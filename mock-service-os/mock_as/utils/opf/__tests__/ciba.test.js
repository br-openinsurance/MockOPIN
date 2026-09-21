import { describe, it, expect, vi, beforeEach } from 'vitest';

vi.mock('../adapter.js', () => ({
  bankAdapter: {
    getConsent: vi.fn(),
    getUserInformation: vi.fn(),
    updateConsent: vi.fn(),
  },
}));
vi.mock('../../account.js', () => ({
  default: {
    findByNationalId: vi.fn(),
    authenticate: vi.fn(),
  },
}));
vi.mock('../routes.js', () => ({
  getAuthorizedConsentData: vi.fn(() => ({ sub: 'x', status: 'AUTHORISED' })),
}));
vi.mock('../layout.js', () => ({ default: {} }));
vi.mock('undici', () => ({
  Agent: vi.fn(function Agent(options) {
    this.options = options;
  }),
  fetch: vi.fn(async () => ({ status: 204 })),
}));

import {
  processLoginHint,
  triggerAuthenticationDevice,
  verifyUserCode,
  mtlsFetch,
  retryPing,
  resolveAcr,
  toApprovalEntry,
  isApprovable,
  findApprovableRequest,
  authorisedConsentDataForAll,
  normaliseAction,
  decideRequest,
} from '../ciba.js';
import { bankAdapter } from '../adapter.js';
import Account from '../../account.js';
import { Agent, fetch as undiciFetch } from 'undici';
import tls from 'node:tls';

const CONSENT_ID = 'urn:raidiambank:consent:abc-123';
const CPF = '76109277673';
const SUB = 'ralph.bragg@gmail.com';
const aConsent = (status = 'AWAITING_AUTHORISATION') => ({
  data: { status, loggedUser: { document: { identification: CPF } } },
});

beforeEach(() => {
  vi.clearAllMocks();
});

describe('processLoginHint', () => {
  it('resolves the data consentId to the account sub via the consent CPF', async () => {
    bankAdapter.getConsent.mockResolvedValue(aConsent());
    Account.findByNationalId.mockResolvedValue({ accountId: SUB });

    const result = await processLoginHint({}, CONSENT_ID);

    expect(bankAdapter.getConsent).toHaveBeenCalledWith(CONSENT_ID);
    expect(Account.findByNationalId).toHaveBeenCalledWith(CPF);
    expect(result).toBe(SUB);
  });

  it('strips a leading "consent:" prefix before resolving the consent', async () => {
    bankAdapter.getConsent.mockResolvedValue(aConsent());
    Account.findByNationalId.mockResolvedValue({ accountId: SUB });

    await processLoginHint({}, `consent:${CONSENT_ID}`);

    expect(bankAdapter.getConsent).toHaveBeenCalledWith(CONSENT_ID);
  });

  it('accepts a consent that is already authorised', async () => {
    bankAdapter.getConsent.mockResolvedValue(aConsent('AUTHORISED'));
    Account.findByNationalId.mockResolvedValue({ accountId: SUB });

    await expect(processLoginHint({}, CONSENT_ID)).resolves.toBe(SUB);
  });

  it('rejects with invalid_login_hint when the consent cannot be fetched', async () => {
    bankAdapter.getConsent.mockRejectedValue(new Error('not found'));

    await expect(processLoginHint({}, CONSENT_ID)).rejects.toMatchObject({ error: 'invalid_login_hint' });
    expect(Account.findByNationalId).not.toHaveBeenCalled();
  });

  it('rejects with invalid_login_hint when the consent is no longer active', async () => {
    bankAdapter.getConsent.mockResolvedValue(aConsent('REJECTED'));

    await expect(processLoginHint({}, CONSENT_ID)).rejects.toMatchObject({ error: 'invalid_login_hint' });
    expect(Account.findByNationalId).not.toHaveBeenCalled();
  });

  it('rejects with invalid_login_hint when no account maps to the consent CPF', async () => {
    bankAdapter.getConsent.mockResolvedValue(aConsent());
    Account.findByNationalId.mockResolvedValue(undefined);

    await expect(processLoginHint({}, CONSENT_ID)).rejects.toMatchObject({ error: 'invalid_login_hint' });
  });
});

const aRequest = (overrides = {}) => ({
  jti: 'req-1',
  clientId: 'ciba-client',
  accountId: SUB,
  params: { scope: `openid consent:${CONSENT_ID}` },
  ...overrides,
});

describe('toApprovalEntry', () => {
  it('derives everything the approval screens need from the persisted request', () => {
    const request = aRequest();

    expect(toApprovalEntry(request)).toEqual({
      id: 'req-1',
      request,
      consentId: CONSENT_ID,
      clientId: 'ciba-client',
      accountId: SUB,
      scope: `openid consent:${CONSENT_ID}`,
    });
  });

  it('reads the scope off the request when it is not under params', () => {
    const request = aRequest({ scope: `openid consent:${CONSENT_ID}`, params: {} });

    expect(toApprovalEntry(request).consentId).toBe(CONSENT_ID);
  });

  it('falls back to login_hint when the scope carries no consent id', () => {
    const request = aRequest({ params: { scope: 'openid', login_hint: CONSENT_ID } });

    expect(toApprovalEntry(request).consentId).toBe(CONSENT_ID);
  });
});

describe('isApprovable', () => {
  it('accepts a request nobody has decided yet', () => {
    expect(isApprovable(aRequest())).toBe(true);
  });

  it('rejects one that was already approved', () => {
    expect(isApprovable(aRequest({ grantId: 'grant-1' }))).toBe(false);
  });

  it('rejects one that was already declined', () => {
    expect(isApprovable(aRequest({ error: 'access_denied' }))).toBe(false);
  });

  it('rejects a request that is not there at all', () => {
    expect(isApprovable(undefined)).toBe(false);
  });
});

describe('findApprovableRequest', () => {
  const providerReturning = (request) => ({
    BackchannelAuthenticationRequest: { find: vi.fn(async () => request) },
  });

  it('loads the request from the adapter by auth_req_id', async () => {
    const provider = providerReturning(aRequest());

    const entry = await findApprovableRequest(provider, 'req-1');

    expect(provider.BackchannelAuthenticationRequest.find).toHaveBeenCalledWith('req-1');
    expect(entry).toMatchObject({ id: 'req-1', consentId: CONSENT_ID, accountId: SUB });
  });

  // The default find() honours expiry, so an expired auth_req_id simply is not there.
  it('returns undefined when the request is unknown or expired', async () => {
    await expect(findApprovableRequest(providerReturning(undefined), 'req-1')).resolves.toBeUndefined();
  });

  it('returns undefined when the request was already decided', async () => {
    const provider = providerReturning(aRequest({ grantId: 'grant-1' }));

    await expect(findApprovableRequest(provider, 'req-1')).resolves.toBeUndefined();
  });
});

describe('triggerAuthenticationDevice', () => {
  it('stores nothing - oidc-provider has already persisted the request', async () => {
    await expect(
      triggerAuthenticationDevice({}, aRequest(), { accountId: SUB }, { clientId: 'ciba-client' }),
    ).resolves.toBeUndefined();
  });
});

describe('mtlsFetch', () => {
  it('returns undefined when the transport cert/key are missing', () => {
    expect(mtlsFetch(undefined, undefined)).toBeUndefined();
    expect(mtlsFetch('cert', undefined)).toBeUndefined();
  });

  it('sends the request through an undici Agent carrying the transport cert', async () => {
    const fetchImpl = mtlsFetch('-----BEGIN CERT-----', '-----BEGIN KEY-----', '-----BEGIN CA CERT-----');
    expect(typeof fetchImpl).toBe('function');
    expect(Agent).toHaveBeenCalledWith({
      connect: {
        cert: '-----BEGIN CERT-----',
        key: '-----BEGIN KEY-----',
        ca: [...tls.rootCertificates, '-----BEGIN CA CERT-----'],
      },
    });

    const response = await fetchImpl('https://web.conformance.example/cb', {
      method: 'POST',
      headers: { authorization: 'Bearer notification-token' },
      body: '{"auth_req_id":"req-1"}',
    });

    expect(response.status).toBe(204);
    const [url, options] = undiciFetch.mock.calls.at(-1);
    expect(url).toBe('https://web.conformance.example/cb');
    expect(options.method).toBe('POST');
    expect(options.headers.authorization).toBe('Bearer notification-token');
    expect(options.dispatcher).toBeInstanceOf(Agent);
    expect(options.dispatcher.options.connect.cert).toBe('-----BEGIN CERT-----');
    expect(options.signal).toBeInstanceOf(AbortSignal);
  });

  it.each([
    ['jwks_uri GET', { method: 'GET', headers: { Accept: 'application/json' } }],
    ['backchannel logout POST', { method: 'POST', headers: { 'content-type': 'application/json' } }],
  ])('does not present the transport cert on a %s', async (_name, requestOptions) => {
    const fetchImpl = mtlsFetch('-----BEGIN CERT-----', '-----BEGIN KEY-----', '-----BEGIN CA CERT-----');

    await fetchImpl('https://directory/client_one/application.jwks', requestOptions);

    const [, options] = undiciFetch.mock.calls.at(-1);
    expect(options.dispatcher.options.connect.ca).toEqual([...tls.rootCertificates, '-----BEGIN CA CERT-----']);
    expect(options.dispatcher.options.connect.cert).toBeUndefined();
    expect(options.signal).toBeUndefined();
  });

  it('does not follow redirects returned by the notification endpoint', async () => {
    const fetchImpl = mtlsFetch('-----BEGIN CERT-----', '-----BEGIN KEY-----');

    await fetchImpl('https://web.conformance.example/cb', {
      method: 'POST',
      headers: { authorization: 'Bearer notification-token' },
      body: '{"auth_req_id":"req-1"}',
    });

    const [, options] = undiciFetch.mock.calls.at(-1);
    expect(options.redirect).toBe('manual');
  });
});

describe('verifyUserCode', () => {
  // OFB never sends a user_code, but oidc-provider implementation throws.
  it('accepts a request with no user_code', async () => {
    await expect(verifyUserCode({}, { accountId: SUB }, undefined)).resolves.toBeUndefined();
  });
});

describe('authorisedConsentDataForAll', () => {
  it('links every resource the user has, grouped the way the bank expects', () => {
    const payload = authorisedConsentDataForAll(SUB, [
      { scope: 'accounts', accounts: [{ accountId: 'acc-1' }, { accountId: 'acc-2' }] },
      { scope: 'credit-cards-accounts', accounts: [{ creditCardAccountId: 'cc-1' }] },
      { scope: 'loans', accounts: [{ resourceId: 'loan-1' }] },
    ]);

    expect(payload).toEqual({
      sub: SUB,
      status: 'AUTHORISED',
      linkedAccountIds: ['acc-1', 'acc-2'],
      linkedCreditCardAccountIds: ['cc-1'],
      linkedLoanAccountIds: ['loan-1'],
    });
  });

  it('authorises with no links when the user has nothing to share', () => {
    expect(authorisedConsentDataForAll(SUB, [])).toEqual({ sub: SUB, status: 'AUTHORISED' });
    expect(authorisedConsentDataForAll(SUB, undefined)).toEqual({ sub: SUB, status: 'AUTHORISED' });
  });

  it('ignores scopes the consent PUT has no field for', () => {
    const payload = authorisedConsentDataForAll(SUB, [{ scope: 'payments', accounts: [{ accountId: 'x' }] }]);

    expect(payload).toEqual({ sub: SUB, status: 'AUTHORISED' });
  });

  it('drops resources with no usable id rather than linking undefined', () => {
    const payload = authorisedConsentDataForAll(SUB, [
      { scope: 'accounts', accounts: [{ accountId: 'acc-1' }, { somethingElse: 'x' }] },
    ]);

    expect(payload.linkedAccountIds).toEqual(['acc-1']);
  });
});

describe('normaliseAction', () => {
  it('accepts what the conformance suite sends', () => {
    expect(normaliseAction('allow')).toBe('allow');
    expect(normaliseAction('deny')).toBe('deny');
    expect(normaliseAction('ALLOW')).toBe('allow');
  });

  it('rejects anything it does not recognise', () => {
    expect(normaliseAction('maybe')).toBeUndefined();
    expect(normaliseAction('')).toBeUndefined();
    expect(normaliseAction(undefined)).toBeUndefined();
  });
});

describe('decideRequest', () => {
  const pending = { jti: 'req-1', clientId: 'c1', accountId: SUB, params: { scope: `openid consent:${CONSENT_ID}` } };

  const providerFor = (request) => {
    const grant = { addOIDCScope: vi.fn(), save: vi.fn(async () => {}) };
    return {
      __grant: grant,
      BackchannelAuthenticationRequest: { find: vi.fn(async () => request) },
      Grant: vi.fn(function Grant() {
        return grant;
      }),
      backchannelResult: vi.fn(async () => {}),
      Client: { find: vi.fn(async () => ({ backchannelPing: vi.fn() })) },
    };
  };

  it('approves and links every resource, then resolves the request', async () => {
    const provider = providerFor(pending);
    bankAdapter.getConsent.mockResolvedValue(aConsent());
    bankAdapter.getUserInformation.mockResolvedValue([{ scope: 'accounts', accounts: [{ accountId: 'acc-1' }] }]);

    await expect(decideRequest(provider, 'req-1', 'allow')).resolves.toBe('allow');

    expect(bankAdapter.updateConsent).toHaveBeenCalledWith(CONSENT_ID, {
      sub: SUB,
      status: 'AUTHORISED',
      linkedAccountIds: ['acc-1'],
    });
    expect(provider.__grant.save).toHaveBeenCalled();
    expect(provider.backchannelResult).toHaveBeenCalledWith(pending, provider.__grant, expect.anything());
  });

  it('declines on deny without building a Grant or reading the consent', async () => {
    const provider = providerFor(pending);

    await expect(decideRequest(provider, 'req-1', 'deny')).resolves.toBe('deny');

    expect(bankAdapter.getConsent).not.toHaveBeenCalled();
    expect(bankAdapter.updateConsent).toHaveBeenCalledWith(CONSENT_ID, { status: 'REJECTED' });
    expect(provider.Grant).not.toHaveBeenCalled();
    expect(provider.backchannelResult).toHaveBeenCalledWith(
      pending,
      expect.objectContaining({ error: 'access_denied' }),
    );
  });

  it('does nothing when the tester already decided it in the browser', async () => {
    const provider = providerFor({ ...pending, grantId: 'grant-1' });

    await expect(decideRequest(provider, 'req-1', 'allow')).resolves.toBeUndefined();

    expect(bankAdapter.updateConsent).not.toHaveBeenCalled();
    expect(provider.backchannelResult).not.toHaveBeenCalled();
  });
});

describe('resolveAcr', () => {
  // CIBA-7.1 / FAPI-CIBA-5.2.2-8. The conformance two-client test asks for loa2 on
  // its second client, so asserting a fixed loa3 fails FAPICIBAValidateIdTokenACRClaims.
  const LOA2 = 'urn:brasil:openbanking:loa2';
  const LOA3 = 'urn:brasil:openbanking:loa3';

  it('honours the acr the client asked for', () => {
    expect(resolveAcr({ params: { acr_values: LOA2 } })).toBe(LOA2);
    expect(resolveAcr({ params: { acr_values: LOA3 } })).toBe(LOA3);
  });

  it('takes the first supported value from the preference-ordered list', () => {
    expect(resolveAcr({ params: { acr_values: `${LOA2} ${LOA3}` } })).toBe(LOA2);
    expect(resolveAcr({ params: { acr_values: `${LOA3} ${LOA2}` } })).toBe(LOA3);
  });

  it('skips values it cannot assert', () => {
    expect(resolveAcr({ params: { acr_values: `urn:made:up ${LOA2}` } })).toBe(LOA2);
  });

  it('falls back to loa3 when nothing usable was requested', () => {
    expect(resolveAcr({ params: { acr_values: 'urn:made:up' } })).toBe(LOA3);
    expect(resolveAcr({ params: { acr_values: '' } })).toBe(LOA3);
    expect(resolveAcr({ params: {} })).toBe(LOA3);
    expect(resolveAcr({})).toBe(LOA3);
    expect(resolveAcr(undefined)).toBe(LOA3);
  });
});

describe('retryPing', () => {
  // oidc-provider delivers the ping exactly once and rejects on any non-2xx, so a
  // receiver whose endpoint is briefly down never learns its request was decided.
  // These count backchannelResult's own attempt as attempt 1.
  const REQUEST = { jti: 'req-1' };
  const noWait = vi.fn(async () => {});

  const clientThatAlwaysFails = () => ({
    backchannelPing: vi.fn(async () => {
      throw new Error('expected 204 No Content, got: 529');
    }),
  });

  it('tops the total up to three attempts when the endpoint keeps failing', async () => {
    const client = clientThatAlwaysFails();

    const result = await retryPing(client, REQUEST, { wait: noWait });

    expect(client.backchannelPing).toHaveBeenCalledTimes(2);
    expect(result).toEqual({ delivered: false, attempts: 3 });
  });

  it('stops as soon as the endpoint accepts', async () => {
    const client = {
      backchannelPing: vi.fn().mockRejectedValueOnce(new Error('529')).mockResolvedValueOnce(undefined),
    };

    const result = await retryPing(client, REQUEST, { attempts: 5, wait: noWait });

    expect(client.backchannelPing).toHaveBeenCalledTimes(2);
    expect(result).toEqual({ delivered: true, attempts: 3 });
  });

  it('waits between attempts', async () => {
    const wait = vi.fn(async () => {});

    await retryPing(clientThatAlwaysFails(), REQUEST, { attempts: 3, delayMs: 5000, wait });

    expect(wait).toHaveBeenCalledTimes(2);
    expect(wait).toHaveBeenCalledWith(5000);
  });

  it('makes no retry when a single attempt is configured', async () => {
    const client = clientThatAlwaysFails();

    const result = await retryPing(client, REQUEST, { attempts: 1, wait: noWait });

    expect(client.backchannelPing).not.toHaveBeenCalled();
    expect(result).toEqual({ delivered: false, attempts: 1 });
  });

  const failWithStatus = (status) => {
    const error = new Error(`expected 204 No Content, got: ${status}`);
    error.response = { status };
    return error;
  };

  it.each([
    ['401', 401],
    ['403', 403],
    ['a 301 redirect', 301],
  ])('does not retry when the endpoint answered %s', async (_label, status) => {
    const client = clientThatAlwaysFails();
    const result = await retryPing(client, REQUEST, { wait: noWait, lastError: failWithStatus(status) });

    expect(client.backchannelPing).not.toHaveBeenCalled();
    expect(result).toEqual({ delivered: false, attempts: 1 });
  });

  it('stops retrying as soon as a retry comes back 4xx', async () => {
    const client = { backchannelPing: vi.fn().mockRejectedValue(failWithStatus(400)) };

    const result = await retryPing(client, REQUEST, { wait: noWait, lastError: failWithStatus(529) });

    expect(client.backchannelPing).toHaveBeenCalledTimes(1);
    expect(result).toEqual({ delivered: false, attempts: 2 });
  });

  it('retries a 529, which is what the ping-to-poll module answers', async () => {
    const client = { backchannelPing: vi.fn().mockRejectedValue(failWithStatus(529)) };

    const result = await retryPing(client, REQUEST, { wait: noWait, lastError: failWithStatus(529) });

    expect(client.backchannelPing).toHaveBeenCalledTimes(2);
    expect(result).toEqual({ delivered: false, attempts: 3 });
  });

  it('retries a network error, which carries no response at all', async () => {
    const client = { backchannelPing: vi.fn().mockRejectedValue(new Error('ECONNREFUSED')) };

    const result = await retryPing(client, REQUEST, { wait: noWait, lastError: new Error('ECONNREFUSED') });

    expect(client.backchannelPing).toHaveBeenCalledTimes(2);
    expect(result).toEqual({ delivered: false, attempts: 3 });
  });

  describe('time budget', () => {
    const fastFailure = failWithStatus(529);

    it('makes all three attempts when the endpoint refuses quickly', async () => {
      const client = { backchannelPing: vi.fn().mockRejectedValue(fastFailure) };

      const result = await retryPing(client, REQUEST, {
        wait: noWait,
        lastError: fastFailure,
        startedAt: 0,
        now: () => 100,
      });

      expect(client.backchannelPing).toHaveBeenCalledTimes(2);
      expect(result).toEqual({ delivered: false, attempts: 3 });
    });

    it('stops when another attempt would not fit, rather than risking the invocation', async () => {
      const client = { backchannelPing: vi.fn().mockRejectedValue(fastFailure) };
      // 10s already gone on a black-holed first attempt: 10000 + 2000 delay + 10000
      // worst-case attempt overruns the 20000 budget, so we do not start it.
      const result = await retryPing(client, REQUEST, {
        wait: noWait,
        lastError: fastFailure,
        startedAt: 0,
        now: () => 10000,
      });

      expect(client.backchannelPing).not.toHaveBeenCalled();
      expect(result).toEqual({ delivered: false, attempts: 1 });
    });

    it('re-checks the budget before every attempt, not just the first', async () => {
      const client = { backchannelPing: vi.fn().mockRejectedValue(fastFailure) };
      const now = vi.fn().mockReturnValueOnce(100).mockReturnValue(9000);

      const result = await retryPing(client, REQUEST, {
        wait: noWait,
        lastError: fastFailure,
        startedAt: 0,
        now,
        attempts: 5,
      });

      expect(client.backchannelPing).toHaveBeenCalledTimes(1);
      expect(result).toEqual({ delivered: false, attempts: 2 });
    });
  });

  it('always pings the same request, so the auth_req_id never changes between attempts', async () => {
    const client = clientThatAlwaysFails();

    await retryPing(client, REQUEST, { wait: noWait });

    for (const call of client.backchannelPing.mock.calls) {
      expect(call[0]).toBe(REQUEST);
    }
  });
});
