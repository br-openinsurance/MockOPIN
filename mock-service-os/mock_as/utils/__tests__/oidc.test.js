const { jwtVerifyMock } = vi.hoisted(() => ({
  jwtVerifyMock: vi.fn(),
}));

vi.mock('jose', () => ({
  jwtVerify: jwtVerifyMock,
}));

import { addSoftwareStatementVerificationMiddleware } from '../oidc.js';

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

function makeCtx({ path = '/reg', method = 'POST', body = {} } = {}) {
  return {
    path,
    method,
    req: { body },
    request: { body },
    state: {},
  };
}

function makeUnparsedCtx({ path = '/reg', method = 'POST', rawBody } = {}) {
  return {
    path,
    method,
    req: {
      readable: true,
      body: undefined,
      async *[Symbol.asyncIterator]() {
        yield Buffer.from(rawBody);
      },
    },
    request: { body: undefined },
    state: {},
  };
}

describe('addSoftwareStatementVerificationMiddleware', () => {
  const ssaJwks = Symbol('ssaJwks');
  let provider;
  let next;

  beforeEach(() => {
    vi.clearAllMocks();
    provider = makeProvider();
    addSoftwareStatementVerificationMiddleware(provider, ssaJwks);
    next = vi.fn().mockResolvedValue(undefined);
  });

  it('reads software_statement off the /reg request body and verifies it against the SSA JWKS', async () => {
    const softwareStatement = 'header.payload.signature';
    jwtVerifyMock.mockResolvedValue({ payload: { software_id: 'abc-123' } });

    const ctx = makeCtx({ body: { software_statement: softwareStatement } });
    await provider.middleware(ctx, next);

    expect(jwtVerifyMock).toHaveBeenCalledWith(
      softwareStatement,
      ssaJwks,
      expect.objectContaining({
        algorithms: ['PS256'],
        maxTokenAge: '5 days',
        typ: 'JWT',
      }),
    );
    expect(ctx.state.softwareStatementPayload).toEqual({ software_id: 'abc-123' });
    expect(next).toHaveBeenCalled();
  });

  it('falls back to ctx.request.body when ctx.req.body is not set', async () => {
    const softwareStatement = 'header.payload.signature';
    jwtVerifyMock.mockResolvedValue({ payload: { software_id: 'abc-123' } });

    const ctx = makeCtx({ body: { software_statement: softwareStatement } });
    delete ctx.req.body;

    await provider.middleware(ctx, next);

    expect(jwtVerifyMock).toHaveBeenCalledWith(softwareStatement, ssaJwks, expect.anything());
    expect(ctx.state.softwareStatementPayload).toEqual({ software_id: 'abc-123' });
  });

  it('reads the raw request stream when ctx.req.body/ctx.request.body are both empty but the stream is still readable', async () => {
    const softwareStatement = 'header.payload.signature';
    jwtVerifyMock.mockResolvedValue({ payload: { software_id: 'abc-123' } });

    const ctx = makeUnparsedCtx({ rawBody: JSON.stringify({ software_statement: softwareStatement }) });
    await provider.middleware(ctx, next);

    expect(jwtVerifyMock).toHaveBeenCalledWith(softwareStatement, ssaJwks, expect.anything());
    expect(ctx.state.softwareStatementPayload).toEqual({ software_id: 'abc-123' });
    expect(ctx.req.body).toEqual({ software_statement: softwareStatement });
    expect(next).toHaveBeenCalled();
  });

  it('leaves software_statement unverified without throwing when the readable stream body fails to parse as JSON', async () => {
    const ctx = makeUnparsedCtx({ rawBody: 'not-json' });
    await provider.middleware(ctx, next);

    expect(jwtVerifyMock).not.toHaveBeenCalled();
    expect(ctx.state.softwareStatementPayload).toBeUndefined();
    expect(ctx.state.softwareStatementError).toBeUndefined();
    expect(next).toHaveBeenCalled();
  });

  it('stashes the verification error on ctx.state instead of throwing, so next() still runs', async () => {
    const error = Object.assign(new Error('signature verification failed'), {
      code: 'ERR_JWS_SIGNATURE_VERIFICATION_FAILED',
    });
    jwtVerifyMock.mockRejectedValue(error);

    const ctx = makeCtx({ body: { software_statement: 'header.payload.signature' } });
    await provider.middleware(ctx, next);

    expect(ctx.state.softwareStatementError).toBe(error);
    expect(ctx.state.softwareStatementPayload).toBeUndefined();
    expect(next).toHaveBeenCalled();
  });

  it('does not attempt verification when the /reg body has no software_statement', async () => {
    const ctx = makeCtx({ body: { client_name: 'My App' } });
    await provider.middleware(ctx, next);

    expect(jwtVerifyMock).not.toHaveBeenCalled();
    expect(ctx.state.softwareStatementPayload).toBeUndefined();
    expect(ctx.state.softwareStatementError).toBeUndefined();
    expect(next).toHaveBeenCalled();
  });

  it('verifies on PUT /reg as well as POST', async () => {
    jwtVerifyMock.mockResolvedValue({ payload: { software_id: 'abc-123' } });

    const ctx = makeCtx({ method: 'PUT', body: { software_statement: 'header.payload.signature' } });
    await provider.middleware(ctx, next);

    expect(jwtVerifyMock).toHaveBeenCalled();
    expect(ctx.state.softwareStatementPayload).toEqual({ software_id: 'abc-123' });
  });

  it('skips verification entirely for requests outside /reg', async () => {
    const ctx = makeCtx({ path: '/token', body: { software_statement: 'header.payload.signature' } });
    await provider.middleware(ctx, next);

    expect(jwtVerifyMock).not.toHaveBeenCalled();
    expect(next).toHaveBeenCalled();
  });

  it('skips verification for GET requests to /reg', async () => {
    const ctx = makeCtx({ method: 'GET', body: { software_statement: 'header.payload.signature' } });
    await provider.middleware(ctx, next);

    expect(jwtVerifyMock).not.toHaveBeenCalled();
    expect(next).toHaveBeenCalled();
  });
});
