const { sendMock, gotMock } = vi.hoisted(() => ({
  sendMock: vi.fn(),
  gotMock: vi.fn(),
}));

vi.mock('@aws-sdk/client-ssm', () => ({
  SSMClient: vi.fn().mockImplementation(function () {
    return { send: sendMock };
  }),
  GetParametersCommand: vi.fn().mockImplementation(function (input) {
    return { input };
  }),
}));

vi.mock('got', () => ({ default: gotMock }));

vi.mock('../opin/configuration.js', () => ({
  default: vi.fn((mtlsIssuer, ssaJwks) => ({ mtlsIssuer, ssaJwks })),
}));

import { GetParametersCommand } from '@aws-sdk/client-ssm';
import configFuncMock from '../opin/configuration.js';
import { getSsmParameters, loadParameters, prepareOidcConfiguration } from '../startup.js';

describe('getSsmParameters', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('returns an empty object without calling SSM when no names are requested', async () => {
    const result = await getSsmParameters([]);

    expect(result).toEqual({});
    expect(sendMock).not.toHaveBeenCalled();
  });

  it('fetches all requested names in a single batched call', async () => {
    sendMock.mockResolvedValue({
      Parameters: [
        { Name: '/prefix/issuer', Value: 'auth.local' },
        { Name: '/prefix/transport_certificate', Value: 'cert-value' },
      ],
    });

    const result = await getSsmParameters(['/prefix/issuer', '/prefix/transport_certificate']);

    expect(sendMock).toHaveBeenCalledTimes(1);
    expect(GetParametersCommand).toHaveBeenCalledWith({
      Names: ['/prefix/issuer', '/prefix/transport_certificate'],
      WithDecryption: true,
    });
    expect(result).toEqual({
      '/prefix/issuer': 'auth.local',
      '/prefix/transport_certificate': 'cert-value',
    });
  });

  it('throws when SSM reports invalid parameters', async () => {
    sendMock.mockResolvedValue({
      Parameters: [],
      InvalidParameters: ['/prefix/missing'],
    });

    await expect(getSsmParameters(['/prefix/missing'])).rejects.toThrow(/Invalid SSM parameters/);
  });

  it('propagates errors from the SSM client', async () => {
    sendMock.mockRejectedValue(new Error('boom'));

    await expect(getSsmParameters(['/prefix/issuer'])).rejects.toThrow('boom');
  });
});

describe('loadParameters', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('loads issuer/cert/key from a single SSM batch call', async () => {
    sendMock.mockResolvedValue({
      Parameters: [
        { Name: '/local/op_fapi_client_config/issuer', Value: 'auth.local' },
        { Name: '/local/op_fapi_client_config/transport_certificate', Value: 'cert-value' },
        { Name: '/local/op_fapi_client_config/transport_key', Value: 'key-value' },
      ],
    });

    const result = await loadParameters();

    expect(sendMock).toHaveBeenCalledTimes(1);
    expect(GetParametersCommand).toHaveBeenCalledWith({
      Names: [
        '/local/op_fapi_client_config/issuer',
        '/local/op_fapi_client_config/transport_certificate',
        '/local/op_fapi_client_config/transport_key',
      ],
      WithDecryption: true,
    });
    expect(result).toEqual({
      issuer: 'https://auth.local',
      clientCert: 'cert-value',
      clientCertKey: 'key-value',
    });
  });

  it('does not add a scheme prefix when SSM already returns one', async () => {
    sendMock.mockResolvedValue({
      Parameters: [
        { Name: '/local/op_fapi_client_config/issuer', Value: 'https://auth.local' },
        { Name: '/local/op_fapi_client_config/transport_certificate', Value: 'cert-value' },
        { Name: '/local/op_fapi_client_config/transport_key', Value: 'key-value' },
      ],
    });

    const result = await loadParameters();

    expect(result.issuer).toBe('https://auth.local');
  });
});

describe('prepareOidcConfiguration', () => {
  const validJwksBody = JSON.stringify({ keys: [] });

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('starts the JWKS fetch without waiting for the brand config import to resolve first', async () => {
    let resolveGot;
    gotMock.mockReturnValue(
      new Promise((resolve) => {
        resolveGot = resolve;
      }),
    );

    const factoryPromise = prepareOidcConfiguration('opin');

    // If got() were only called after awaiting the import, this call count would
    // still be 0 here — Promise.all() firing both synchronously proves otherwise.
    expect(gotMock).toHaveBeenCalledTimes(1);

    resolveGot({ statusCode: 200, body: validJwksBody });
    await factoryPromise;
  });

  it('returns a factory that builds the oidc config once mtlsIssuer is known', async () => {
    gotMock.mockResolvedValue({ statusCode: 200, body: validJwksBody });

    const factory = await prepareOidcConfiguration('opin');
    expect(configFuncMock).not.toHaveBeenCalled();

    const configuration = factory('https://matls-auth.local');

    expect(configFuncMock).toHaveBeenCalledTimes(1);
    expect(configuration.mtlsIssuer).toBe('https://matls-auth.local');
    expect(configuration.findAccount).toBeDefined();
  });

  // Without these the brand config cannot build its mTLS fetch, and requests go
  // out with no client certificate - silently.
  it('passes the transport cert and key through to the brand config', async () => {
    gotMock.mockResolvedValue({ statusCode: 200, body: validJwksBody });

    const factory = await prepareOidcConfiguration('opin');
    factory('https://matls-auth.local', 'CERT', 'KEY');

    expect(configFuncMock).toHaveBeenCalledWith('https://matls-auth.local', expect.anything(), 'CERT', 'KEY');
  });

  it('throws when the JWKS endpoint does not return 200', async () => {
    gotMock.mockResolvedValue({ statusCode: 500, body: '' });

    await expect(prepareOidcConfiguration('opin')).rejects.toThrow(/Failed to load JWKS/);
  });
});
