vi.mock('oidc-provider', async () => {
  const errorsModule = await import('oidc-provider/lib/helpers/errors.js');
  return { errors: errorsModule };
});

import * as oidcErrors from 'oidc-provider/lib/helpers/errors.js';
import buildConfig from '../configuration.js';

// Signature verification happens ahead of time in addSoftwareStatementVerificationMiddleware
// (utils/oidc.js), which stashes its outcome on ctx.state. The validator itself only reads it.
// Unlike OPF, OPIN's validator also requires ctx.get('X-BANK-Certificate-DN') to match the
// software_id in the software statement, so ctx needs a working ctx.get().
function makeCtx({ payload = makePayload(), certDn = `CN=${payload.software_id}` } = {}) {
  return {
    state: { softwareStatementPayload: payload },
    get: (header) => (header === 'X-BANK-Certificate-DN' ? certDn : undefined),
  };
}

function makeCtxWithError(error) {
  return {
    state: { softwareStatementError: error },
    get: () => undefined,
  };
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

describe('OPIN extraClientMetadata validator', () => {
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
      ctx = { state: {}, get: () => undefined };

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).toThrow(
        oidcErrors.InvalidSoftwareStatement,
      );
    });
  });

  // Unique to OPIN: the presented client mTLS certificate's DN must carry the same
  // software_id as the verified software statement.
  describe('mTLS certificate DN validation', () => {
    it('throws UnapprovedSoftwareStatement when no client certificate is presented', () => {
      ctx = makeCtx({ certDn: null });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).toThrow(
        oidcErrors.UnapprovedSoftwareStatement,
      );
    });

    it('throws UnapprovedSoftwareStatement when the certificate DN does not carry the software_id', () => {
      ctx = makeCtx({ certDn: 'CN=some-other-software-id' });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).toThrow(
        oidcErrors.UnapprovedSoftwareStatement,
      );
    });

    it('accepts a certificate DN with a matching CN', () => {
      ctx = makeCtx({ certDn: 'CN=software-abc' });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).not.toThrow();
    });

    it('accepts a certificate DN with a matching UID', () => {
      ctx = makeCtx({ certDn: 'UID=software-abc' });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).not.toThrow();
    });
  });

  describe('SSA payload validations', () => {
    it('throws UnapprovedSoftwareStatement when org is not Active', () => {
      const payload = makePayload({ org_status: 'Pending' });
      ctx = makeCtx({ payload });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).toThrow(
        oidcErrors.UnapprovedSoftwareStatement,
      );
    });

    it('throws UnapprovedSoftwareStatement when software_roles is empty', () => {
      const payload = makePayload({ software_roles: [] });
      ctx = makeCtx({ payload });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', makeMetadata())).toThrow(
        oidcErrors.UnapprovedSoftwareStatement,
      );
    });

    it('throws UnapprovedSoftwareStatement when software_redirect_uris is empty', () => {
      const payload = makePayload({ software_redirect_uris: [] });
      ctx = makeCtx({ payload });

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
      const payload = makePayload({ software_api_webhook_uris: undefined });
      ctx = makeCtx({ payload });
      const metadata = makeMetadata({ webhook_uris: ['https://tpp.example.com/webhook'] });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', metadata)).toThrow(oidcErrors.InvalidClientMetadata);
    });

    it('throws InvalidClientMetadata when a requested webhook_uri is not in the SSA', () => {
      const payload = makePayload({ software_api_webhook_uris: ['https://tpp.example.com/webhook'] });
      ctx = makeCtx({ payload });
      const metadata = makeMetadata({ webhook_uris: ['https://evil.example.com/webhook'] });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', metadata)).toThrow(oidcErrors.InvalidClientMetadata);
    });

    it('accepts webhook_uris that are all listed in the SSA', () => {
      const payload = makePayload({ software_api_webhook_uris: ['https://tpp.example.com/webhook'] });
      ctx = makeCtx({ payload });
      const metadata = makeMetadata({ webhook_uris: ['https://tpp.example.com/webhook'] });

      expect(() => validator(ctx, 'software_statement', 'a.b.c', metadata)).not.toThrow();
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
      expect(metadata.client_uri).toBe('https://tpp.example.com');
      expect(metadata.org_id).toBe('org-123');
      expect(metadata.org_name).toBe('Test Org');
      expect(metadata.org_number).toBe('12345678');
      expect(metadata.software_id).toBe('software-abc');
      expect(metadata.client_description).toBe('A test TPP client');
      expect(metadata.application_type).toBe('web');
      expect(metadata.id_token_signed_response_alg).toBe('PS256');
      expect(metadata.tls_client_certificate_bound_access_tokens).toBe(true);
      expect(metadata.tos_uri).toBe('https://tpp.example.com/tos');
      expect(metadata.policy_uri).toBe('https://tpp.example.com/policy');
    });

    it('defaults tos_uri/policy_uri when the SSA has none, as OPF does', () => {
      const payload = makePayload({ software_tos_uri: undefined, software_policy_uri: undefined });
      ctx = makeCtx({ payload });
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.tos_uri).toBe('https://example.com/tos');
      expect(metadata.policy_uri).toBe('https://example.com/policy');
    });

    it('defaults client_uri, logo_uri and client_name when the SSA and the body carry none', () => {
      const payload = makePayload({
        software_client_uri: '',
        software_logo_uri: '',
        software_client_name: '',
        org_name: '',
      });
      ctx = makeCtx({ payload });
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.client_uri).toBe('https://example.com');
      expect(metadata.logo_uri).toBe('https://example.com/logo.png');
      expect(metadata.client_name).toBe('Client');
    });

    it('prefers the request body over the defaults when the SSA carries nothing', () => {
      const payload = makePayload({
        software_client_uri: undefined,
        software_logo_uri: undefined,
        software_client_name: undefined,
      });
      ctx = makeCtx({ payload });
      const metadata = makeMetadata({
        client_uri: 'https://body.example.com',
        logo_uri: 'https://body.example.com/logo.png',
        client_name: 'Body App',
      });

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.client_uri).toBe('https://body.example.com');
      expect(metadata.logo_uri).toBe('https://body.example.com/logo.png');
      expect(metadata.client_name).toBe('Body App');
    });

    it('always sets the fixed OPIN grant_types and response_types', () => {
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.grant_types).toEqual(['client_credentials', 'authorization_code', 'refresh_token', 'implicit']);
      expect(metadata.response_types).toEqual(['code id_token', 'code']);
    });

    it('defaults to all permitted scopes when none are requested', () => {
      const metadata = makeMetadata();

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.scope).toContain('openid');
      expect(metadata.scope).toContain('consents');
      expect(metadata.scope).toContain('insurance-auto');
    });

    it('preserves requested scope when provided', () => {
      const metadata = makeMetadata({ scope: 'openid consents' });

      validator(ctx, 'software_statement', 'a.b.c', metadata);

      expect(metadata.scope).toBe('openid consents');
    });
  });
});
