import { errors } from 'oidc-provider';
import { urlencoded } from 'express';
import { Agent, fetch as undiciFetch } from 'undici';
import tls from 'node:tls';
import Debug from 'debug';

import Account from '../account.js';
import { bankAdapter } from './adapter.js';
import { getConsentId } from './helpers.js';
import { getAuthorizedConsentData } from './routes.js';
import layout from './layout.js';
import { parseSkippedUrlencodedBody } from '../albUrlencodedBodyCompat.js';

const log = Debug('raidiam:server:info');
const err = Debug('raidiam:server:error');
const body = [urlencoded({ extended: false }), parseSkippedUrlencodedBody];

// oidc-provider delivers the ping once. Retries run inline in whichever request carried
// the decision, so they must fit that Lambda invocation's 30s - hence a budget.
const PING_ATTEMPTS = Number(process.env.CIBA_PING_ATTEMPTS ?? 3);
const PING_RETRY_DELAY_MS = Number(process.env.CIBA_PING_RETRY_DELAY_MS ?? 2000);
const PING_BUDGET_MS = Number(process.env.CIBA_PING_BUDGET_MS ?? 20000);
const PING_ATTEMPT_MS = Number(process.env.CIBA_PING_ATTEMPT_MS ?? 10000);

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

function isRetryable(error) {
  const status = error?.response?.status;
  return status === undefined || status >= 500;
}

const PENDING_COLLECTION = 'backchannel_authentication_request';
const PENDING_LIST_LIMIT = 50;

/**
 * Everything the approval screens need, derived from the persisted request.
 */
export function toApprovalEntry(request) {
  const scope = request.scope ?? request.params?.scope;
  return {
    id: request.jti,
    request,
    consentId: getConsentId(scope) || String(request.params?.login_hint || ''),
    clientId: request.clientId,
    accountId: request.accountId,
    scope,
  };
}

/**
 * Undecided requests are approvable. backchannelResult stamps grantId or error.
 */
export function isApprovable(request) {
  return Boolean(request) && !request.grantId && !request.error;
}

/**
 * Load a pending request by auth_req_id, or undefined if it is unknown, expired or
 * already decided. find() honours expiry - unlike backchannelResult, we want it to.
 */
export async function findApprovableRequest(provider, id) {
  const request = await provider.BackchannelAuthenticationRequest.find(id);
  return isApprovable(request) ? toApprovalEntry(request) : undefined;
}

/**
 * Requests awaiting a decision, for the /ciba landing page. The adapter API has no
 * "find all", so this reads the collection directly; best effort, since the in-memory
 * adapter has nothing to read and the tester can use the direct approval URL.
 */
export async function listApprovableRequests(provider) {
  try {
    const { default: MongoAdapter } = await import('../mongodb.js');
    const docs = await MongoAdapter.coll(PENDING_COLLECTION)
      .find({ 'payload.grantId': { $exists: false }, 'payload.error': { $exists: false } })
      .limit(PENDING_LIST_LIMIT)
      .toArray();
    const requests = await Promise.all(docs.map((doc) => provider.BackchannelAuthenticationRequest.find(doc._id)));
    return requests.filter(isApprovable).map(toApprovalEntry);
  } catch (error) {
    err(`CIBA pending list unavailable: ${error}`);
    return [];
  }
}

// OFB CIBA 2.1.0 6.2.3: the profile's own error, not CIBA-Core's unknown_user_id.
const INVALID_LOGIN_HINT = 'invalid_login_hint';

function invalidLoginHint(description) {
  return new errors.CustomOIDCProviderError(INVALID_LOGIN_HINT, description);
}

export async function processLoginHint(ctx, loginHint) {
  const consentId = String(loginHint || '').replace(/^consent:/, '');
  log(`CIBA processLoginHint for ${consentId}`);
  let consent;
  try {
    consent = await bankAdapter.getConsent(consentId);
  } catch (error) {
    err(`CIBA login_hint ${consentId} could not be resolved to a consent: ${error}`);
    throw invalidLoginHint('login_hint could not be associated with a consent');
  }

  const status = consent?.data?.status;
  if (!['AWAITING_AUTHORISATION', 'AUTHORISED'].includes(status)) {
    err(`CIBA login_hint ${consentId}: consent is ${status}`);
    throw invalidLoginHint('login_hint is not associated with an active consent');
  }

  const cpf = consent?.data?.loggedUser?.document?.identification;
  const account = await Account.findByNationalId(cpf);
  if (!account) {
    err(`CIBA login_hint ${consentId}: no account for national_id ${cpf}`);
    throw invalidLoginHint('login_hint could not be associated with a known user');
  }
  log(`CIBA login_hint ${consentId} resolved to account ${account.accountId}`);
  return account.accountId;
}

// OFB sends none of these, but the library's defaults throw and it calls
// verifyUserCode on every backchannel request, so all three must be supplied.
export async function validateRequestContext(ctx, requestContext) {
  return undefined;
}

export async function validateBindingMessage(ctx, bindingMessage) {
  return undefined;
}

export async function verifyUserCode(ctx, account, userCode) {
  return undefined;
}

export async function triggerAuthenticationDevice(ctx, request, account, client) {
  const { id, consentId } = toApprovalEntry(request);
  log(`CIBA request ${id} awaiting approval for consent ${consentId} (client ${client.clientId})`);
}

export function mtlsFetch(clientCert, clientCertKey, caCert) {
  if (!clientCert || !clientCertKey) {
    log('CIBA mtlsFetch: transport cert/key missing — ping will use plain TLS');
    return undefined;
  }
  const connect = { ca: caCert ? [...tls.rootCertificates, caCert] : undefined };
  const pingDispatcher = new Agent({ connect: { ...connect, cert: clientCert, key: clientCertKey } });
  const defaultDispatcher = new Agent({ connect });

  return (url, options = {}) => {
    const isPing =
      String(options.method).toUpperCase() === 'POST' && Boolean(new Headers(options.headers).get('authorization'));
    if (!isPing) {
      return undiciFetch(url, { ...options, dispatcher: defaultDispatcher });
    }
    return undiciFetch(url, {
      ...options,
      dispatcher: pingDispatcher,
      redirect: 'manual',
      signal: options.signal ?? AbortSignal.timeout(PING_ATTEMPT_MS),
    });
  };
}

/**
 * Re-attempt a ping that backchannelResult already tried once and failed to deliver.
 *
 * Counts from 1 (backchannelResult's attempt) up to `attempts`. Stops on acceptance, on
 * a permanent rejection, or when another attempt would not fit the budget. Pass
 * `startedAt` from the top of the request so the budget covers the whole invocation.
 *
 * @returns {Promise<{delivered: boolean, attempts: number}>}
 */
export async function retryPing(
  client,
  request,
  {
    attempts = PING_ATTEMPTS,
    delayMs = PING_RETRY_DELAY_MS,
    budgetMs = PING_BUDGET_MS,
    attemptMs = PING_ATTEMPT_MS,
    wait = sleep,
    now = Date.now,
    lastError,
    startedAt = now(),
  } = {},
) {
  let made = 1;
  let failure = lastError;
  while (made < attempts) {
    if (!isRetryable(failure)) {
      log(`CIBA ping for ${request.jti} not retried: ${failure?.response?.status} is a permanent rejection`);
      return { delivered: false, attempts: made };
    }
    const elapsed = now() - startedAt;
    if (elapsed + delayMs + attemptMs > budgetMs) {
      log(
        `CIBA ping for ${request.jti} not retried: ${elapsed}ms spent, another attempt would not fit the ${budgetMs}ms budget`,
      );
      return { delivered: false, attempts: made };
    }
    await wait(delayMs);
    made += 1;
    try {
      await client.backchannelPing(request);
      log(`CIBA ping for ${request.jti} accepted on attempt ${made}`);
      return { delivered: true, attempts: made };
    } catch (error) {
      failure = error;
      err(`CIBA ping for ${request.jti} failed on attempt ${made}/${attempts}: ${error}`);
    }
  }
  return { delivered: false, attempts: made };
}

// Mirrors `acrValues` in configuration.js.
const SUPPORTED_ACRS = ['urn:brasil:openbanking:loa2', 'urn:brasil:openbanking:loa3'];
const DEFAULT_ACR = 'urn:brasil:openbanking:loa3';

/**
 * CIBA-7.1 / FAPI-CIBA-5.2.2-8: the id_token carries the acr the client requested.
 * acr_values is space-separated in preference order.
 */
export function resolveAcr(request) {
  const requested = String(request?.params?.acr_values || '')
    .split(' ')
    .filter(Boolean);
  return requested.find((acr) => SUPPORTED_ACRS.includes(acr)) ?? DEFAULT_ACR;
}

/**
 * Authorise the bank consent, build the Grant and resolve the CIBA request. Shared by
 * the approval screen and the automatic decision so the two cannot drift.
 *
 * `startedAt` seeds the ping retry budget - pass the top of the request.
 */
export async function approvePending(provider, entry, authPayload, startedAt = Date.now()) {
  const consentRecord = await bankAdapter.updateConsent(entry.consentId, authPayload);

  const grant = new provider.Grant({ accountId: entry.accountId, clientId: entry.clientId });
  grant.addOIDCScope(entry.scope);

  const consentExpiry = consentRecord?.data?.expirationDateTime ?? consentRecord?.expirationDateTime;
  if (consentExpiry) {
    const nowUTCMinus3 = Date.now() - 3 * 60 * 60 * 1000;
    const ttl = Math.round((Date.parse(consentExpiry) - nowUTCMinus3) / 1000);
    Object.defineProperty(grant, 'grantTtl', { value: ttl, writable: false });
  }

  await grant.save();
  try {
    await provider.backchannelResult(entry.request, grant, {
      acr: resolveAcr(entry.request),
      amr: ['mfa'],
      authTime: Math.floor(Date.now() / 1000),
    });
    log(`CIBA request ${entry.id} approved; ping accepted by ${entry.clientId}`);
  } catch (error) {
    if (!entry.request.grantId) {
      throw error;
    }
    err(`CIBA ping for ${entry.id} failed on attempt 1/${PING_ATTEMPTS}: ${error}`);
    try {
      const client = await provider.Client.find(entry.clientId);
      const { delivered, attempts } = await retryPing(client, entry.request, { lastError: error, startedAt });
      log(`CIBA ping for ${entry.id}: ${delivered ? 'delivered' : 'undelivered'} after ${attempts} attempt(s)`);
    } catch (retryError) {
      err(`CIBA ping retry for ${entry.id} aborted: ${retryError}`);
    }
  }
}

export async function rejectPending(provider, entry) {
  try {
    await bankAdapter.updateConsent(entry.consentId, { status: 'REJECTED' });
  } catch (error) {
    err(`reject updateConsent failed: ${error}`);
  }
  await provider.backchannelResult(entry.request, new errors.AccessDenied('end-user rejected the request'));
  log(`CIBA request ${entry.id} rejected`);
}

const AUTOMATED_APPROVAL_ENABLED = process.env.CIBA_AUTOMATED_APPROVAL !== 'false';

const LINKED_IDS_BY_SCOPE = {
  accounts: 'linkedAccountIds',
  'credit-cards-accounts': 'linkedCreditCardAccountIds',
  loans: 'linkedLoanAccountIds',
  financings: 'linkedFinancingAccountIds',
  'invoice-financings': 'linkedInvoiceFinancingAccountIds',
  'unarranged-accounts-overdraft': 'linkedUnarrangedOverdraftAccountIds',
  exchanges: 'linkedExchangeOperationIds',
};

const resourceIdOf = (resource) => resource?.accountId ?? resource?.creditCardAccountId ?? resource?.resourceId;

/**
 * The consent PUT payload for "the user accepted everything", matching the shape
 * getAuthorizedConsentData() builds from the form.
 */
export function authorisedConsentDataForAll(accountId, scopesInformation) {
  const payload = { sub: accountId, status: 'AUTHORISED' };
  for (const { scope, accounts } of scopesInformation ?? []) {
    const key = LINKED_IDS_BY_SCOPE[scope];
    if (key) {
      payload[key] = (accounts ?? []).map(resourceIdOf).filter(Boolean);
    }
  }
  return payload;
}

/** The suite sends `allow` or `deny`; be liberal about the synonyms. */
export function normaliseAction(action) {
  const value = String(action ?? '').toLowerCase();
  if (['allow', 'approve', 'accept'].includes(value)) {
    return 'allow';
  }
  return ['deny', 'reject', 'refuse'].includes(value) ? 'deny' : undefined;
}

/**
 * Decide a pending request on behalf of the absent banking app. Approving selects
 * every resource the user holds - the equivalent of ticking every box on the screen.
 *
 * `startedAt` seeds the ping retry budget; pass the top of the request.
 *
 * @returns {Promise<'allow'|'deny'|undefined>} undefined if there was nothing to decide
 */
export async function decideRequest(provider, id, action, startedAt = Date.now()) {
  const entry = await findApprovableRequest(provider, id);
  if (!entry) {
    log(`CIBA automated decision for ${id} skipped - already decided or expired`);
    return undefined;
  }
  log(`CIBA automated decision for ${id}: ${action}`);

  if (action === 'deny') {
    await rejectPending(provider, entry);
    return action;
  }
  const consent = await bankAdapter.getConsent(entry.consentId);
  const scopesInformation = await bankAdapter.getUserInformation(entry.accountId, consent?.data);
  await approvePending(provider, entry, authorisedConsentDataForAll(entry.accountId, scopesInformation), startedAt);
  return action;
}

export function registerCibaRoutes(app, provider) {
  app.post('/ciba/automated', async (req, res, next) => {
    const startedAt = Date.now();
    try {
      if (!AUTOMATED_APPROVAL_ENABLED) {
        return res.status(404).json({ error: 'automated approval is disabled' });
      }
      const id = req.query.auth_req_id ?? req.body?.auth_req_id;
      const action = normaliseAction(req.query.action ?? req.body?.action);
      if (!id || !action) {
        return res.status(400).json({ error: 'auth_req_id and action (allow|deny) are required' });
      }

      const outcome = await decideRequest(provider, String(id), action, startedAt);
      if (!outcome) {
        return res.status(404).json({ error: 'request not found, already decided or expired' });
      }
      return res.json({ auth_req_id: id, action: outcome });
    } catch (error) {
      return next(error);
    }
  });

  app.get('/ciba', async (req, res) => {
    res.render('ciba', {
      title: 'Pending authorizations',
      pending: await listApprovableRequests(provider),
      id: undefined,
      error: undefined,
    });
  });

  app.get('/ciba/authorize/:id', async (req, res, next) => {
    try {
      const entry = await findApprovableRequest(provider, req.params.id);
      if (!entry) {
        return res.render('ciba', {
          title: 'Pending authorizations',
          pending: await listApprovableRequests(provider),
          id: undefined,
          error: 'Request not found or already handled',
        });
      }
      const client = await provider.Client.find(entry.clientId);
      return res.render('login', {
        client,
        uid: entry.id,
        authBase: `/ciba/authorize/${entry.id}`,
        details: {},
        params: { client_id: entry.clientId, scope: entry.scope },
        title: 'Sign-in',
        session: undefined,
        dbg: {},
        layout,
      });
    } catch (e) {
      next(e);
    }
  });

  app.post('/ciba/authorize/:id/login', body, async (req, res, next) => {
    try {
      const entry = await findApprovableRequest(provider, req.params.id);
      if (!entry) {
        return res.render('ciba', {
          title: 'Pending authorizations',
          pending: await listApprovableRequests(provider),
          id: undefined,
          error: 'Request not found or already handled',
        });
      }
      const client = await provider.Client.find(entry.clientId);

      const account = await Account.authenticate(req.body.login, req.body.password).catch(() => undefined);
      if (!account || account.accountId !== entry.accountId) {
        return res.render('login', {
          client,
          uid: entry.id,
          authBase: `/ciba/authorize/${entry.id}`,
          details: {},
          params: { client_id: entry.clientId, scope: entry.scope },
          title: 'Sign-in',
          error: account ? 'This request belongs to a different user' : 'login / password invalid',
          session: undefined,
          dbg: {},
          layout,
        });
      }

      const consent = await bankAdapter.getConsent(entry.consentId);
      const data = consent.data;
      const scopesInformations = await bankAdapter.getUserInformation(entry.accountId, data);

      return res.render('interaction', {
        client,
        uid: entry.id,
        authBase: `/ciba/authorize/${entry.id}`,
        details: { consent: data, scopes: scopesInformations, prompt: [] },
        params: { client_id: entry.clientId, scope: entry.scope },
        title: 'Authorize',
        session: undefined,
        dbg: {},
        layout,
      });
    } catch (e) {
      next(e);
    }
  });

  app.post('/ciba/authorize/:id/confirm', body, async (req, res, next) => {
    const startedAt = Date.now();
    try {
      const entry = await findApprovableRequest(provider, req.params.id);
      if (!entry) {
        return res.render('ciba', {
          title: 'Pending authorizations',
          pending: await listApprovableRequests(provider),
          id: undefined,
          error: 'Request not found or already handled',
        });
      }

      await approvePending(provider, entry, getAuthorizedConsentData(req, entry.accountId), startedAt);

      return res.render('ciba', {
        title: 'Authorization complete',
        pending: await listApprovableRequests(provider),
        id: undefined,
        error: undefined,
        message: 'Consent authorised. You may return to the client application.',
      });
    } catch (e) {
      err(`CIBA approval failed for ${req.params.id}: ${e}`);
      next(e);
    }
  });

  app.get('/ciba/authorize/:id/abort', async (req, res, next) => {
    try {
      const entry = await findApprovableRequest(provider, req.params.id);
      if (!entry) {
        return res.render('ciba', {
          title: 'Pending authorizations',
          pending: await listApprovableRequests(provider),
          id: undefined,
          error: 'Request not found or already handled',
        });
      }

      await rejectPending(provider, entry);

      return res.render('ciba', {
        title: 'Authorization rejected',
        pending: await listApprovableRequests(provider),
        id: undefined,
        error: undefined,
        message: 'Consent rejected.',
      });
    } catch (e) {
      next(e);
    }
  });
}
