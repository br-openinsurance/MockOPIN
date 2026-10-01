/* eslint-disable no-console */
import { createLocalJWKSet } from 'jose';
import got from 'got';
import Debug from 'debug';
import { SSMClient, GetParametersCommand } from '@aws-sdk/client-ssm';

import Account from './account.js';

const log = Debug('raidiam:server:info');

const {
  AWS_SSM_REGION = 'us-east-1',
  AWS_LOCAL = false,
  LOCAL_STACK_ENDPOINT,
  TRUSTFRAMEWORK_SSA_KEYSET,
  SSM_PARAMETER_PREFIX = '/local/op_fapi_client_config',
} = process.env;

const ssmClient = new SSMClient({
  ...(AWS_LOCAL && { endpoint: LOCAL_STACK_ENDPOINT }),
  maxAttempts: 3,
  region: AWS_SSM_REGION,
});

async function getSsmParameters(names) {
  if (names.length === 0) return {};

  const command = new GetParametersCommand({
    Names: names,
    WithDecryption: true,
  });

  try {
    const result = await ssmClient.send(command);
    if (result.InvalidParameters?.length) {
      throw new Error(`Invalid SSM parameters: ${result.InvalidParameters.join(', ')}`);
    }
    return Object.fromEntries(result.Parameters.map(({ Name, Value }) => [Name, Value]));
  } catch (error) {
    console.error('Error fetching parameters:', error);
    throw error;
  }
}

function parseCookieKeys(raw) {
  let keys;
  try {
    keys = JSON.parse(raw);
  } catch {
    throw new Error('cookie_keys SSM parameter must be a JSON array of strings');
  }
  if (!Array.isArray(keys) || keys.length === 0 || !keys.every((key) => typeof key === 'string' && key.length > 0)) {
    throw new Error('cookie_keys SSM parameter must be a non-empty JSON array of non-empty strings');
  }
  return keys;
}

async function loadParameters() {
  const names = {
    issuer: `${SSM_PARAMETER_PREFIX}/issuer`,
    clientCert: `${SSM_PARAMETER_PREFIX}/transport_certificate`,
    clientCertKey: `${SSM_PARAMETER_PREFIX}/transport_key`,
    caCert: `${SSM_PARAMETER_PREFIX}/certificate_authority`,
    cookieKeys: `${SSM_PARAMETER_PREFIX}/cookie_keys`,
  };
  const fetched = await getSsmParameters(Object.values(names));

  const rawIssuer = fetched[names.issuer];
  const clientCert = fetched[names.clientCert];
  const clientCertKey = fetched[names.clientCertKey];
  const caCert = fetched[names.caCert];
  const cookieKeys = parseCookieKeys(fetched[names.cookieKeys]);
  const issuer = rawIssuer.startsWith('https://') ? rawIssuer : `https://${rawIssuer}`;

  return { issuer, clientCert, clientCertKey, caCert, cookieKeys };
}

async function prepareOidcConfiguration(brand) {
  const configPath = `./${brand}/configuration.js`;
  log(`Loading configuration from ${configPath}`);
  log(`Load Directory Key Set: ${TRUSTFRAMEWORK_SSA_KEYSET}`);

  const [configFunc, ssaJwksResponse] = await Promise.all([import(configPath), got(TRUSTFRAMEWORK_SSA_KEYSET)]);

  if (ssaJwksResponse.statusCode !== 200) {
    throw new Error(`Failed to load JWKS: ${ssaJwksResponse.statusCode}`);
  }

  const ssaJwks = createLocalJWKSet(JSON.parse(ssaJwksResponse.body));

  return (mtlsIssuer, clientCert, clientCertKey, caCert, cookieKeys) => {
    const configuration = configFunc.default(mtlsIssuer, ssaJwks, clientCert, clientCertKey, caCert);
    configuration.findAccount = Account.findAccount;
    configuration.cookies = { ...configuration.cookies, keys: cookieKeys };
    return { configuration, ssaJwks };
  };
}

export { getSsmParameters, loadParameters, prepareOidcConfiguration };
