/* eslint-disable no-console */
import josePkg from 'jose';
const { JWKS } = josePkg;
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

async function loadParameters() {
  const names = {
    issuer: `${SSM_PARAMETER_PREFIX}/issuer`,
    clientCert: `${SSM_PARAMETER_PREFIX}/transport_certificate`,
    clientCertKey: `${SSM_PARAMETER_PREFIX}/transport_key`,
  };
  const fetched = await getSsmParameters(Object.values(names));

  const rawIssuer = fetched[names.issuer];
  const clientCert = fetched[names.clientCert];
  const clientCertKey = fetched[names.clientCertKey];
  const issuer = rawIssuer.startsWith('https://') ? rawIssuer : `https://${rawIssuer}`;

  return { issuer, clientCert, clientCertKey };
}

// Neither the brand config module nor the JWKS fetch depend on the issuer, so this
// can run concurrently with loadParameters() in main() instead of waiting on it first.
// Returns a factory taking the values that only loadParameters() can supply: the
// mtlsIssuer, and the transport cert/key the brand config needs to send requests
// over mutual TLS.
async function prepareOidcConfiguration(brand) {
  const configPath = `./${brand}/configuration.js`;
  log(`Loading configuration from ${configPath}`);
  log(`Load Directory Key Set: ${TRUSTFRAMEWORK_SSA_KEYSET}`);

  const [configFunc, ssaJwksResponse] = await Promise.all([import(configPath), got(TRUSTFRAMEWORK_SSA_KEYSET)]);

  if (ssaJwksResponse.statusCode !== 200) {
    throw new Error(`Failed to load JWKS: ${ssaJwksResponse.statusCode}`);
  }
  const ssaJwks = JWKS.asKeyStore(JSON.parse(ssaJwksResponse.body));

  return (mtlsIssuer, clientCert, clientCertKey) => {
    const configuration = configFunc.default(mtlsIssuer, ssaJwks, clientCert, clientCertKey);
    // Add the findAccount property
    configuration.findAccount = Account.findAccount;
    return configuration;
  };
}

export { getSsmParameters, loadParameters, prepareOidcConfiguration };
