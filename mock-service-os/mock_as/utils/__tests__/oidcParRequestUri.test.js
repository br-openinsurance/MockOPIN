import { UnsecuredJWT, decodeJwt } from 'jose';
import { consumeRequestUri, enforceParRequestUriLifecycle } from '../oidc.js';

const now = () => Math.floor(Date.now() / 1000);

const b64 = (value) => Buffer.from(JSON.stringify(value)).toString('base64url');
// Signature is junk on purpose: the middleware only decodes the payload to read exp.
const clientSignedJwt = (payload) => `${b64({ alg: 'PS256' })}.${b64(payload)}.signature`;

function makeProvider() {
  let middleware;
  return {
    use: (fn) => {
      middleware = fn;
    },
    get middleware() {
      return middleware;
    },
  };
}

function makeCtx({ request, clientPushed = false, expiresIn = 60, route = 'pushed_authorization_request' }) {
  const par = { request, save: vi.fn().mockResolvedValue(undefined) };
  const ctx = {
    oidc: {
      route,
      entities: { PushedAuthorizationRequest: par },
      body: clientPushed ? { request } : {},
    },
    body: { expires_in: expiresIn, request_uri: 'urn:ietf:params:oauth:request_uri:abc123' },
  };
  return { ctx, par };
}

describe('enforceParRequestUriLifecycle, request object minted by the library', () => {
  let provider;
  let next;

  beforeEach(() => {
    provider = makeProvider();
    enforceParRequestUriLifecycle(provider);
    next = vi.fn().mockResolvedValue(undefined);
  });

  it('declares at least 120 seconds', async () => {
    const { ctx, par } = makeCtx({
      request: new UnsecuredJWT({ iss: 'client-one', exp: now() + 60 }).encode(),
    });

    await provider.middleware(ctx, next);

    expect(ctx.body.expires_in).toBe(120);
    expect(par.save).toHaveBeenCalledWith(120);
  });

  // /auth re-validates the JWT's exp, so a record outliving its payload would fail early.
  it('re-mints the request object so its exp matches the declared validity', async () => {
    const issued = now();
    const { ctx, par } = makeCtx({
      request: new UnsecuredJWT({
        iss: 'client-one',
        scope: 'openid',
        iat: issued,
        exp: issued + 60,
      }).encode(),
    });

    await provider.middleware(ctx, next);

    const payload = decodeJwt(par.request);
    expect(payload.exp).toBeGreaterThanOrEqual(issued + 120);
    expect(payload).toMatchObject({ iss: 'client-one', scope: 'openid', iat: issued });
  });

  it('leaves the request_uri untouched so the handle already returned stays usable', async () => {
    const { ctx } = makeCtx({ request: new UnsecuredJWT({ exp: now() + 60 }).encode() });

    await provider.middleware(ctx, next);

    expect(ctx.body.request_uri).toBe('urn:ietf:params:oauth:request_uri:abc123');
  });

  it('ignores routes other than the PAR endpoint', async () => {
    const { ctx, par } = makeCtx({
      request: new UnsecuredJWT({ exp: now() + 60 }).encode(),
      route: 'token',
    });

    await provider.middleware(ctx, next);

    expect(par.save).not.toHaveBeenCalled();
    expect(ctx.body.expires_in).toBe(60);
  });

  it('does nothing when the PAR request failed and left no entity behind', async () => {
    const ctx = {
      oidc: { route: 'pushed_authorization_request', entities: {}, body: {} },
      body: { error: 'invalid_request' },
    };

    await expect(provider.middleware(ctx, next)).resolves.toBeUndefined();
    expect(next).toHaveBeenCalled();
  });
});

describe('enforceParRequestUriLifecycle, request object signed by the client', () => {
  let provider;
  let next;

  beforeEach(() => {
    provider = makeProvider();
    enforceParRequestUriLifecycle(provider);
    next = vi.fn().mockResolvedValue(undefined);
  });

  it('raises to 120 seconds and keeps the signed request object byte for byte', async () => {
    const request = clientSignedJwt({ exp: now() + 600 });
    const { ctx, par } = makeCtx({ request, clientPushed: true });

    await provider.middleware(ctx, next);

    expect(ctx.body.expires_in).toBe(120);
    expect(par.save).toHaveBeenCalledWith(120);
    expect(par.request).toBe(request);
  });

  it('never declares longer than the request object itself lasts', async () => {
    const request = clientSignedJwt({ exp: now() + 90 });
    const { ctx, par } = makeCtx({ request, clientPushed: true });

    await provider.middleware(ctx, next);

    expect(ctx.body.expires_in).toBe(90);
    expect(par.save).toHaveBeenCalledWith(90);
    expect(par.request).toBe(request);
  });

  it('leaves the response alone when the request object expires first', async () => {
    const { ctx, par } = makeCtx({
      request: clientSignedJwt({ exp: now() + 45 }),
      clientPushed: true,
      expiresIn: 45,
    });

    await provider.middleware(ctx, next);

    expect(ctx.body.expires_in).toBe(45);
    expect(par.save).not.toHaveBeenCalled();
  });

  it('leaves the response alone when the request object has no exp', async () => {
    const { ctx, par } = makeCtx({
      request: clientSignedJwt({ iss: 'client-one' }),
      clientPushed: true,
    });

    await provider.middleware(ctx, next);

    expect(ctx.body.expires_in).toBe(60);
    expect(par.save).not.toHaveBeenCalled();
  });
});

describe('consumeRequestUri, spending the request_uri at a successful login', () => {
  const allowingLedger = { claim: async () => true };
  const refusingLedger = { claim: async () => false };

  function providerStub() {
    return {
      PushedAuthorizationRequest: {
        adapter: { destroy: vi.fn().mockResolvedValue(undefined) },
      },
    };
  }

  it('destroys the pushed request', async () => {
    const provider = providerStub();

    const spent = await consumeRequestUri(provider, 'par-jti', 'interaction-a', allowingLedger);

    expect(spent).toBe(true);
    expect(provider.PushedAuthorizationRequest.adapter.destroy).toHaveBeenCalledWith('par-jti');
  });

  it('leaves storage untouched when the ledger refuses the claim', async () => {
    const provider = providerStub();

    const spent = await consumeRequestUri(provider, 'par-jti', 'interaction-a', refusingLedger);

    expect(spent).toBe(false);
    expect(provider.PushedAuthorizationRequest.adapter.destroy).not.toHaveBeenCalled();
  });
});
