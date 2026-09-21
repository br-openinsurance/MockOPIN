vi.mock('oidc-provider', async () => {
  const errorsModule = await import('oidc-provider/lib/helpers/errors.js');
  return { errors: errorsModule };
});

import * as oidcErrors from 'oidc-provider/lib/helpers/errors.js';
import buildConfig from '../configuration.js';

function makeCtx(payload = makePayload(), certDn = `CN=${payload.software_id}`) {
  return {
    state: { softwareStatementPayload: payload },
    get: (header) => (header === 'X-BANK-Certificate-DN' ? certDn : undefined),
  };
}

function makeCtxWithError(error) {
  return { state: { softwareStatementError: error } };
}

function makePayload(overrides = {}) {
  return {
    org_status: 'Active',
    software_roles: ['DADOS'],
    software_redirect_uris: ['https://tpp.example.com/callback'],
    software_id: 'software-abc',
    software_jwks_uri: 'https://tpp.example.com/jwks',
    software_client_name: 'Test TPP',
    software_client_uri: 'https://tpp.example.com',
    software_policy_uri: 'https://tpp.example.com/policy',
    software_tos_uri: 'https://tpp.example.com/tos',
    software_logo_uri: 'https://tpp.example.com/logo.png',
    software_client_description: 'A test TPP client',
    org_id: 'org-123',
    org_name: 'Test Org',
    org_number: '12345678',
    ...overrides,
  };
}

function makeMetadata(overrides = {}) {
  return {
    redirect_uris: ['https://tpp.example.com/callback'],
    ...overrides,
  };
}

describe('OPF extraClientMetadata validator', () => {
  let validator;
  let ctx;

  beforeEach(() => {
    const config = buildConfig('https://matls-auth.example.com', {});
    validator = config.extraClientMetadata.validator;
    ctx = makeCtx();
  });

  describe('non-software_statement keys', () => {
    it('does nothing for other metadata keys', () => {
      const metadata = makeMetadata();
      validator(ctx, 'client_name', 'My App', metadata);
      expect(metadata).toEqual(makeMetadata());
    });
  });

  describe('undefined software_statement with undefined ctx', () => {
    it('returns early without error', () => {
      expect(() => validator(undefined, 'software_statement', undefined, makeMetadata())).not.toThrow();
    });
  });

  describe('jwks by value', () => {
    it('throws InvalidClientMetadata when metadata contains jwks', () => {
      const metadata = makeMetadata({ jwks: { keys: [] } });
      expect(() => validator(ctx, 'software_statement', 'a.b.c', metadata)).toThrow(oidcErrors.InvalidClientMetadata);
    });
  });

  describe('JWT verification failures', () => {
    it('throws InvalidSoftwareStatement on expired token', () => {
      const error = Object.assign(new Error('token expired'), { code: 'ERR_JWT_EXPIRED' });
      ctx = makeCtxWithError(error);

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).toThrow(
        oidcErrors.InvalidSoftwareStatement,
      );
    });

    it('throws InvalidSoftwareStatement on signature verification failure', () => {
      const error = Object.assign(new Error('verification failed'), { code: 'ERR_JWS_SIGNATURE_VERIFICATION_FAILED' });
      ctx = makeCtxWithError(error);

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).toThrow(
        oidcErrors.InvalidSoftwareStatement,
      );
    });

    it('throws InvalidClientMetadata on unknown JWT error', () => {
      ctx = makeCtxWithError(new Error('something unexpected'));

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).toThrow(
        oidcErrors.InvalidClientMetadata,
      );
    });

    it('throws InvalidSoftwareStatement when no verification result is available on ctx.state', () => {
      ctx = { state: {} };

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).toThrow(
        oidcErrors.InvalidSoftwareStatement,
      );
    });
  });

  describe('mTLS certificate DN validation', () => {
    it('throws UnapprovedSoftwareStatement when no client certificate is presented', () => {
      ctx = makeCtx(makePayload(), null);

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).toThrow(
        oidcErrors.UnapprovedSoftwareStatement,
      );
    });

    it('throws UnapprovedSoftwareStatement when the certificate DN does not carry the software_id', () => {
      ctx = makeCtx(makePayload(), 'CN=some-other-software-id');

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).toThrow(
        oidcErrors.UnapprovedSoftwareStatement,
      );
    });

    it('accepts a certificate DN with a matching CN', () => {
      ctx = makeCtx(makePayload(), 'CN=software-abc');

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).not.toThrow();
    });

    it('accepts a certificate DN with a matching UID', () => {
      ctx = makeCtx(makePayload(), 'UID=software-abc');

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).not.toThrow();
    });
  });

  describe('SSA payload validations', () => {
    it('throws UnapprovedSoftwareStatement when org is not Active', () => {
      ctx = makeCtx(makePayload({ org_status: 'Pending' }));

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).toThrow(
        oidcErrors.UnapprovedSoftwareStatement,
      );
    });

    it('throws UnapprovedSoftwareStatement when software_roles is empty', () => {
      ctx = makeCtx(makePayload({ software_roles: [] }));

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).toThrow(
        oidcErrors.UnapprovedSoftwareStatement,
      );
    });

    it('throws UnapprovedSoftwareStatement when software_redirect_uris is empty', () => {
      ctx = makeCtx(makePayload({ software_redirect_uris: [] }));

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).toThrow(
        oidcErrors.UnapprovedSoftwareStatement,
      );
    });

    it('throws InvalidSoftwareStatement when software_id does not match metadata', () => {
      const metadata = makeMetadata({ software_id: 'a-different-software-id' });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', metadata)).toThrow(
        oidcErrors.InvalidSoftwareStatement,
      );
    });

    it('throws InvalidClientMetadata when jwks_uri does not match SSA', () => {
      const metadata = makeMetadata({ jwks_uri: 'https://evil.example.com/jwks' });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', metadata)).toThrow(oidcErrors.InvalidClientMetadata);
    });

    it('throws InvalidSoftwareStatement when org_id does not match metadata', () => {
      const metadata = makeMetadata({ org_id: 'a-different-org-id' });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', metadata)).toThrow(
        oidcErrors.InvalidSoftwareStatement,
      );
    });
  });

  describe('redirect URI validation', () => {
    it('throws InvalidClientMetadata when redirect_uris is empty', () => {
      const metadata = makeMetadata({ redirect_uris: [] });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', metadata)).toThrow(oidcErrors.InvalidClientMetadata);
    });

    it('throws InvalidClientMetadata when no redirect_uri matches the SSA', () => {
      const metadata = makeMetadata({ redirect_uris: ['https://evil.example.com/callback'] });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', metadata)).toThrow(oidcErrors.InvalidClientMetadata);
    });

    it('filters redirect_uris to only those present in software_redirect_uris', () => {
      const metadata = makeMetadata({
        redirect_uris: ['https://tpp.example.com/callback', 'https://not-in-ssa.example.com/callback'],
      });

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.redirect_uris).toEqual(['https://tpp.example.com/callback']);
    });
  });

  describe('webhook URI validation', () => {
    it('throws InvalidClientMetadata when webhook_uris are requested but SSA has none', () => {
      ctx = makeCtx(makePayload({ software_api_webhook_uris: undefined }));
      const metadata = makeMetadata({ webhook_uris: ['https://tpp.example.com/webhook'] });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', metadata)).toThrow(oidcErrors.InvalidClientMetadata);
    });

    it('throws InvalidClientMetadata when a requested webhook_uri is not in the SSA', () => {
      ctx = makeCtx(makePayload({ software_api_webhook_uris: ['https://tpp.example.com/webhook'] }));
      const metadata = makeMetadata({ webhook_uris: ['https://evil.example.com/webhook'] });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', metadata)).toThrow(oidcErrors.InvalidClientMetadata);
    });

    it('accepts webhook_uris that are all listed in the SSA', () => {
      ctx = makeCtx(makePayload({ software_api_webhook_uris: ['https://tpp.example.com/webhook'] }));
      const metadata = makeMetadata({ webhook_uris: ['https://tpp.example.com/webhook'] });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', metadata)).not.toThrow();
    });
  });

  describe('policy_uri', () => {
    it('falls back to default when SSA has no policy_uri', () => {
      ctx = makeCtx(makePayload({ software_policy_uri: undefined }));
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.policy_uri).toBe('https://example.com/policy');
    });

    it('falls back to default when SSA has null policy_uri', () => {
      ctx = makeCtx(makePayload({ software_policy_uri: null }));
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.policy_uri).toBe('https://example.com/policy');
    });

    it('falls back to default when SSA has empty policy_uri', () => {
      ctx = makeCtx(makePayload({ software_policy_uri: '' }));
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.policy_uri).toBe('https://example.com/policy');
    });

    it('uses policy_uri from SSA when present', () => {
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.policy_uri).toBe('https://tpp.example.com/policy');
    });

    it('enables encrypted id_token and PAR when policy_uri is the FAPI test sentinel', () => {
      ctx = makeCtx(makePayload({ software_policy_uri: 'https://www.fapi.new' }));
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.policy_uri).toBe('https://www.fapi.new');
      expect(metadata.id_token_encrypted_response_alg).toBe('RSA-OAEP');
      expect(metadata.id_token_encrypted_response_enc).toBe('A256GCM');
      expect(metadata.require_pushed_authorization_request).toBe(true);
    });
  });

  describe('tos_uri', () => {
    it('falls back to default when SSA has no tos_uri', () => {
      ctx = makeCtx(makePayload({ software_tos_uri: undefined }));
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.tos_uri).toBe('https://example.com/tos');
    });

    it('falls back to default when SSA has empty string tos_uri', () => {
      ctx = makeCtx(makePayload({ software_tos_uri: '' }));
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.tos_uri).toBe('https://example.com/tos');
    });

    it('uses tos_uri from SSA when present', () => {
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.tos_uri).toBe('https://tpp.example.com/tos');
    });
  });

  describe('client_uri', () => {
    it('uses client_uri from SSA when present', () => {
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.client_uri).toBe('https://tpp.example.com');
    });

    it('falls back to the request body value when SSA has no client_uri', () => {
      ctx = makeCtx(makePayload({ software_client_uri: undefined }));
      const metadata = makeMetadata({ client_uri: 'https://body.example.com' });

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.client_uri).toBe('https://body.example.com');
    });

    it('falls back to default when SSA has no client_uri and the body has none', () => {
      ctx = makeCtx(makePayload({ software_client_uri: undefined }));
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.client_uri).toBe('https://example.com');
    });

    it('falls back to default when SSA has empty string client_uri and the body has none', () => {
      ctx = makeCtx(makePayload({ software_client_uri: '' }));
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.client_uri).toBe('https://example.com');
    });
  });

  describe('logo_uri', () => {
    it('uses logo_uri from SSA when present', () => {
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.logo_uri).toBe('https://tpp.example.com/logo.png');
    });

    it('falls back to the request body value when SSA has no logo_uri', () => {
      ctx = makeCtx(makePayload({ software_logo_uri: undefined }));
      const metadata = makeMetadata({ logo_uri: 'https://body.example.com/logo.png' });

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.logo_uri).toBe('https://body.example.com/logo.png');
    });

    it('falls back to default when SSA has empty string logo_uri and the body has none', () => {
      ctx = makeCtx(makePayload({ software_logo_uri: '' }));
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.logo_uri).toBe('https://example.com/logo.png');
    });
  });

  describe('client_name', () => {
    it('uses client_name from SSA when present', () => {
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.client_name).toBe('Test TPP');
    });

    it('falls back to the request body value when SSA has no client_name', () => {
      ctx = makeCtx(makePayload({ software_client_name: undefined }));
      const metadata = makeMetadata({ client_name: 'Body App' });

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.client_name).toBe('Body App');
    });

    it('falls back to org_name when SSA has empty string client_name and the body has none', () => {
      ctx = makeCtx(makePayload({ software_client_name: '' }));
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.client_name).toBe('Test Org');
    });

    it('falls back to a fixed name when neither the SSA, the body nor the org carry one', () => {
      ctx = makeCtx(makePayload({ software_client_name: '', org_name: '' }));
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.client_name).toBe('Client');
    });
  });

  describe('metadata mutation on success', () => {
    it('removes software_statement from metadata after processing', () => {
      const metadata = makeMetadata({ software_statement: 'a.b.c' });

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.software_statement).toBeUndefined();
    });

    it('copies fields from SSA payload into metadata', () => {
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.jwks_uri).toBe('https://tpp.example.com/jwks');
      expect(metadata.client_name).toBe('Test TPP');
      expect(metadata.org_id).toBe('org-123');
      expect(metadata.org_name).toBe('Test Org');
      expect(metadata.org_number).toBe('12345678');
      expect(metadata.software_id).toBe('software-abc');
      expect(metadata.client_description).toBe('A test TPP client');
      expect(metadata.application_type).toBe('web');
      expect(metadata.id_token_signed_response_alg).toBe('PS256');
      expect(metadata.tls_client_certificate_bound_access_tokens).toBe(true);
    });

    it('defaults to all permitted scopes when none are requested', () => {
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.scope).toContain('openid');
      expect(metadata.scope).toContain('accounts');
      expect(metadata.scope).toContain('payments');
    });

    it('preserves requested scope when provided', () => {
      const metadata = makeMetadata({ scope: 'openid accounts' });

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.scope).toBe('openid accounts');
    });
  });

  // OFB CIBA 2.1.0 6.2.2 / 6.3.5: clients register for ping mode only, and must
  // declare the notification endpoint the AS will POST the ping to.
  describe('CIBA client registration', () => {
    const CIBA_GRANT_TYPE = 'urn:openid:params:grant-type:ciba';
    const NOTIFICATION_ENDPOINT = 'https://web.conformance.directory.openbankingbrasil.org.br/test-mtls/a/mock';

    function makeCibaMetadata(overrides = {}) {
      return makeMetadata({
        grant_types: ['client_credentials', CIBA_GRANT_TYPE],
        backchannel_token_delivery_mode: 'ping',
        backchannel_client_notification_endpoint: NOTIFICATION_ENDPOINT,
        ...overrides,
      });
    }

    it('keeps the CIBA grant type and backchannel metadata on the registration', () => {
      const metadata = makeCibaMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.grant_types).toContain(CIBA_GRANT_TYPE);
      expect(metadata.backchannel_token_delivery_mode).toBe('ping');
      expect(metadata.backchannel_client_notification_endpoint).toBe(NOTIFICATION_ENDPOINT);
      expect(metadata.backchannel_authentication_request_signing_alg).toBe('PS256');
    });

    it('takes the first entry when the notification endpoint is sent as an array', () => {
      const metadata = makeCibaMetadata({ backchannel_client_notification_endpoint: [NOTIFICATION_ENDPOINT] });

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.backchannel_client_notification_endpoint).toBe(NOTIFICATION_ENDPOINT);
    });

    it('falls back to the endpoint declared in the software statement', () => {
      ctx = makeCtx(makePayload({ software_client_notification_endpoint: NOTIFICATION_ENDPOINT }));
      const metadata = makeMetadata({ grant_types: ['client_credentials', CIBA_GRANT_TYPE] });

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.backchannel_client_notification_endpoint).toBe(NOTIFICATION_ENDPOINT);
    });

    // The local directory publishes software_client_notification_endpoint on every
    // SSA, so it must not by itself make a registration a CIBA registration.
    it('does not turn a plain registration into a CIBA one because the SSA advertises an endpoint', () => {
      ctx = makeCtx(makePayload({ software_client_notification_endpoint: NOTIFICATION_ENDPOINT }));
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.grant_types).not.toContain(CIBA_GRANT_TYPE);
      expect(metadata.backchannel_token_delivery_mode).toBeUndefined();
      expect(metadata.backchannel_client_notification_endpoint).toBeUndefined();
    });

    it('registers a CIBA client that only declared the delivery mode', () => {
      const metadata = makeMetadata({
        backchannel_token_delivery_mode: 'ping',
        backchannel_client_notification_endpoint: NOTIFICATION_ENDPOINT,
      });

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.grant_types).toContain(CIBA_GRANT_TYPE);
    });

    it('rejects a delivery mode other than ping', () => {
      const metadata = makeCibaMetadata({ backchannel_token_delivery_mode: 'poll' });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', metadata)).toThrow(oidcErrors.InvalidClientMetadata);
    });

    it('rejects a CIBA registration with no notification endpoint', () => {
      const metadata = makeMetadata({ grant_types: ['client_credentials', CIBA_GRANT_TYPE] });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', metadata)).toThrow(oidcErrors.InvalidClientMetadata);
    });

    it('leaves a non-CIBA registration without the CIBA grant or backchannel metadata', () => {
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.grant_types).not.toContain(CIBA_GRANT_TYPE);
      expect(metadata.backchannel_token_delivery_mode).toBeUndefined();
      expect(metadata.backchannel_client_notification_endpoint).toBeUndefined();
    });

    // The redirect-fallback scenario runs a FAPI hybrid authorization first and only
    // then falls back to CIBA, so one and the same client has to be able to do both.
    describe('the same client can also run the FAPI hybrid redirect flow', () => {
      it('keeps the hybrid grant types alongside the CIBA one', () => {
        const metadata = makeCibaMetadata();

        validator(ctx, 'software_statement', 'a.b.c', metadata);

        expect(metadata.grant_types).toEqual(
          expect.arrayContaining(['authorization_code', 'implicit', 'refresh_token', CIBA_GRANT_TYPE]),
        );
      });

      it('keeps code id_token available as a response type', () => {
        const metadata = makeCibaMetadata();

        validator(ctx, 'software_statement', 'a.b.c', metadata);

        // 'implicit' above is what makes the id_token half of the hybrid response legal.
        expect(metadata.response_types).toContain('code id_token');
      });

      it('preserves the redirect_uris the client registered', () => {
        const metadata = makeCibaMetadata();

        validator(ctx, 'software_statement', 'a.b.c', metadata);

        expect(metadata.redirect_uris).toEqual(['https://tpp.example.com/callback']);
      });
    });
  });
});

describe('OPF CIBA provider configuration', () => {
  let config;

  beforeEach(() => {
    config = buildConfig('https://matls-auth.example.com', {}, 'CERT', 'KEY');
  });

  it('enables CIBA with ping as the only delivery mode', () => {
    // 6.2.2: the AS must not advertise or accept any mode other than ping. Poll
    // remains available to ping-registered clients via CIBA-Core, as a fallback.
    expect(config.features.ciba.enabled).toBe(true);
    expect(config.features.ciba.deliveryModes).toEqual(['ping']);
  });

  it('provides every CIBA hook oidc-provider requires', () => {
    for (const hook of [
      'processLoginHint',
      'triggerAuthenticationDevice',
      'validateRequestContext',
      'validateBindingMessage',
      'verifyUserCode',
    ]) {
      expect(typeof config.features.ciba[hook], hook).toBe('function');
    }
  });

  it('advertises the backchannel authentication endpoint on the mTLS host', () => {
    expect(config.discovery.mtls_endpoint_aliases.backchannel_authentication_endpoint).toBe(
      'https://matls-auth.example.com/backchannel',
    );
  });

  it('overrides fetch so the ping notification is sent over mTLS', () => {
    // 6.3.4: the client notification endpoint is mTLS protected.
    expect(typeof config.fetch).toBe('function');
  });

  it('leaves fetch untouched when no transport cert is available', () => {
    expect(buildConfig('https://matls-auth.example.com', {}).fetch).toBeUndefined();
  });

  // 6.2.4: expires_in is the consent approval window, and the AS is the one that
  // fixes it. Keeping it a plain number - rather than a function reading
  // ctx.oidc.params - is what makes a client-supplied requested_expiry inert.
  it('gives the backchannel authentication request the 10 minute approval window', () => {
    expect(config.ttl.BackchannelAuthenticationRequest).toBe(600);
  });

  it('does not let the client influence that window', () => {
    expect(typeof config.ttl.BackchannelAuthenticationRequest).toBe('number');
  });

  it('enables pushed authorization requests, which the hybrid leg of the fallback uses', () => {
    expect(config.features.pushedAuthorizationRequests.enabled).toBe(true);
  });

  // BrazilCIBA-5.2.2 / FAPIBrazilValidateIdTokenExp: at least 180 days for an
  // id_token issued off a CIBA authentication, but not for everyone else.
  describe('id_token lifetime', () => {
    const ttl = (client) => config.ttl.IdToken({}, {}, client);
    const DAYS = 24 * 60 * 60;

    it('gives a CIBA ping client more than the 180 days the profile requires', () => {
      expect(ttl({ backchannelTokenDeliveryMode: 'ping' })).toBeGreaterThan(180 * DAYS);
    });

    it('stays well under the 50 year ceiling the same check enforces', () => {
      expect(ttl({ backchannelTokenDeliveryMode: 'ping' })).toBeLessThan(50 * 365 * DAYS);
    });

    it('leaves a redirect-only client on the ordinary hour', () => {
      expect(ttl({})).toBe(3600);
      expect(ttl(undefined)).toBe(3600);
    });
  });
});
