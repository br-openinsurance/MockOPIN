/* eslint-disable no-console */

import * as path from 'node:path';
import * as url from 'node:url';
import { dirname } from 'desm';
import express from 'express'; // eslint-disable-line import/no-unresolved
import helmet from 'helmet';
import josePkg from 'jose';
const { JWKS } = josePkg;
import got from 'got';
import Provider from 'oidc-provider';
import { randomUUID, randomBytes } from 'node:crypto';
import Debug from 'debug';
import { SSMClient, GetParameterCommand } from '@aws-sdk/client-ssm';
import bodyParser from 'body-parser';

import Account from './utils/account.js';
import BRAND_NAME from './utils/brandHelper.js';
import { supportDynamicScopes, ensureTokenEndpointAsAudience } from './utils/oidc.js';

const log = Debug('raidiam:server:info');

const __dirname = dirname(import.meta.url);
const {
  BRAND = 'NO_BRAND',
  ISSUER,
  TRANSPORT_CERT,
  TRANSPORT_CERT_KEY,
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

const brandConfig = {
  [BRAND_NAME.OPIN]: {
    brand: 'opin',
    dynamicScopesSupported: ['consent:'],
  },
  default: {
    brand: 'opin',
    dynamicScopesSupported: ['consent:'],
  },
};

async function getSsmParameter(name) {
  const command = new GetParameterCommand({
    Name: name,
    WithDecryption: true,
  });

  try {
    const result = await ssmClient.send(command);
    return result.Parameter.Value;
  } catch (error) {
    console.error('Error fetching parameter:', error);
    throw error;
  }
}

async function loadParameters() {
  let issuer = ISSUER || (await getSsmParameter(`${SSM_PARAMETER_PREFIX}/issuer`));
  issuer = issuer.startsWith('https://') ? issuer : `https://${issuer}`;
  const clientCert = TRANSPORT_CERT || (await getSsmParameter(`${SSM_PARAMETER_PREFIX}/transport_certificate`));
  const clientCertKey = TRANSPORT_CERT_KEY || (await getSsmParameter(`${SSM_PARAMETER_PREFIX}/transport_key`));

  return { issuer, clientCert, clientCertKey };
}

async function loadSupportFunctions(provider, dynamicScopes) {
  log(`Configure support dynamic scopes function - ${dynamicScopes}`);
  supportDynamicScopes(provider, ...dynamicScopes);
  log(`Configure token endpoint as audience function`);
  ensureTokenEndpointAsAudience(provider);
}

async function loadOidcConfiguration(brand, mtlsIssuer) {
  let configPath = `./utils/${brand}/configuration.js`;
  log(`Loading configuration from ${configPath}`);
  // Import the module
  const configFunc = await import(configPath);

  log(`Load Directory Key Set: ${TRUSTFRAMEWORK_SSA_KEYSET}`);
  const ssaJwksResponse = await got(TRUSTFRAMEWORK_SSA_KEYSET);
  if (ssaJwksResponse.statusCode !== 200) {
    throw new Error(`Failed to load JWKS: ${ssaJwksResponse.statusCode}`);
  }
  const ssaJwks = JWKS.asKeyStore(JSON.parse(ssaJwksResponse.body));

  let configuration = configFunc.default(mtlsIssuer, ssaJwks);
  // Add the findAccount property
  configuration.findAccount = Account.findAccount;
  return configuration;
}

async function main() {
  const app = express();

  app.use((req, res, next) => {
    res.locals.nonce = randomBytes(16).toString('base64');
    next();
  });
  const directives = helmet.contentSecurityPolicy.getDefaultDirectives();
  delete directives['form-action'];
  directives['script-src'] = ["'self'", (req, res) => `'nonce-${res.locals.nonce}'`];
  app.use(
    helmet({
      contentSecurityPolicy: {
        useDefaults: false,
        directives,
      },
    }),
  );

  app.set('views', path.join(__dirname, 'views'));
  app.set('view engine', 'ejs');
  app.use(express.static(path.join(__dirname, 'public')));

  app.use(bodyParser.json());

  log(`Load configuration for ${BRAND}`);
  const config = brandConfig[BRAND] || brandConfig.default;
  const { issuer, clientCert, clientCertKey } = await loadParameters();

  let mtlsIssuer = new URL(issuer);
  mtlsIssuer.host = `matls-${mtlsIssuer.host}`;
  mtlsIssuer = mtlsIssuer.toString().replace(/\/$/, ''); // Remove trailing slash.
  let apiUrl = new URL(issuer);
  apiUrl.host = apiUrl.host.replace('auth', 'matls-api');
  apiUrl = apiUrl.toString().replace(/\/$/, ''); // Remove trailing slash.
  log(`Issuer: ${issuer}, mTLS Issuer: ${mtlsIssuer}, API Host: ${apiUrl}`);

  let oidcConfig = await loadOidcConfiguration(config.brand, mtlsIssuer);

  let adapter;
  if (process.env.MONGODB_URI) {
    ({ default: adapter } = await import('./utils/mongodb.js'));
    await adapter.connect('openid-server');
  }

  let provider = new Provider(issuer, {
    adapter,
    ...oidcConfig,
    ...{
      clients: [
        {
          client_id: 'introspection-client',
          client_secret: 'introspection-client-secret',
          redirect_uris: ['https://localhost.emobix.co.uk:8443/test/a/mock/callback'],
          grant_types: ['authorization_code', 'implicit', 'refresh_token'],
          jwks: {
            keys: [
              {
                kty: 'RSA',
                e: 'AQAB',
                use: 'sig',
                kid: 'CfbVPtmKe6h0A29BEyHdLeCOKIs4SxhYwKKQxhidhEE',
                alg: 'PS256',
                n: 'nwBAiEo-6HY1eSt22s1D3GqfWVsco0Tp1ymSgMoDaSxWayr52lOEx0hluB2bKrscuJ0cfLuYrjEOY2f0ZjeHioRjl-1eDzMnHtp5bzCznWxMFEUuBnvRio7ZyuqZVGQLaY95I4sce06_Wd9lQeGytb654aYTuSJrkVwRQLtplPEQuYYMcH3DD8BADztuIC2DrYc4zJuWwXdgU_EPkObZPlu49op5kVwJav9wY-XiK-r6ruUgZ5-MxXNVFf39AnNszVw8urwMXiQ2aHijKNMy2pJHaQSPOyaeICKBcQw9L-mGtXfuT00aota054GkrPVn8V7HXdlYH3tKJY-SKfzRwQ',
              },
            ],
          },
        },
      ],
    },
  });
  log(`Create Account adapter`);
  await Account.initialiseAdapter('accounts');

  log(`Init Adapter for ${BRAND || 'Default'}`);
  const { init } = await import(`./utils/${config.brand}/adapter.js`);
  init(apiUrl, provider, 'openid-provider-client', clientCert, clientCertKey);

  loadSupportFunctions(provider, config.dynamicScopesSupported);

  const routes = await import(`./utils/${config.brand}/routes.js`);
  routes.default(app, provider);

  provider.use(async (ctx, next) => {
    await next(); // Ensure the next middleware

    if (!ctx.get('x-fapi-interaction-id')) {
      ctx.set('x-fapi-interaction-id', randomUUID());
    } else {
      ctx.set('x-fapi-interaction-id', ctx.get('x-fapi-interaction-id'));
    }
  });

  // Implemented as requested by OCI-1785
  provider.on('client_credentials.saved', (token) => {
    if (token.kind == 'ClientCredentials' && token.scope.includes('openid')) {
      log('Remove openId scope at client_credential flow');
      token.scope = token.scope.replace('openid', '').trim();
    }
  });
  // Trust the proxy
  app.enable('trust proxy');
  provider.proxy = true;

  const prod = process.env.NODE_ENV === 'production';
  if (prod) {
    app.use((req, res, next) => {
      if (req.secure) {
        next();
      } else if (req.method === 'GET' || req.method === 'HEAD') {
        res.redirect(
          url.format({
            protocol: 'https',
            host: req.get('host'),
            pathname: req.originalUrl,
          }),
        );
      } else {
        res.status(400).json({
          error: 'invalid_request',
          error_description: 'do yourself a favor and only use https',
        });
      }
    });
  }

  // Serve static files
  app.use('/assets', express.static(path.join(__dirname, 'assets')));
  app.use(provider.callback());

  return {
    app,
  };
}

async function run() {
  const app = await main();
  const port = process.env.PORT ? process.env.PORT : 9000;

  const server = app.app.listen(port, () => {
    log(`Listening on port ${port}`);
  });

  return {
    app,
    server,
  };
}

export { run };
