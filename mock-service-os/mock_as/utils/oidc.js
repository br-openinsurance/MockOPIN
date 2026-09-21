import Debug from 'debug';
import { jwtVerify } from 'jose';

const log = Debug('raidiam:server:info');

function getDynamicScopeFromArray(scopes, dynamicScopePrefix) {
  // eslint-disable-next-line no-shadow
  const result = scopes.filter((s) => {
    if (s.name && s.name.startsWith(dynamicScopePrefix)) return true;
    if (!s.name && s.startsWith(dynamicScopePrefix)) return true;
    return false;
  });
  if (result.length === 0) {
    return undefined;
  }

  return result[0].name ? result[0].name : result[0];
}

function getDynamicScopeFromString(scopes, dynamicScopePrefix) {
  const result = scopes.split(' ').filter((s) => s.startsWith(dynamicScopePrefix));
  return result.length > 0 ? result[0] : undefined;
}

// Test if a variable is a string
function isString(variable) {
  return typeof variable === 'string';
}

export function getDynamicScope(scopes, dynamicScopePrefix) {
  if (Array.isArray(scopes)) {
    return getDynamicScopeFromArray(scopes, dynamicScopePrefix);
  } else if (isString(scopes)) {
    return getDynamicScopeFromString(scopes, dynamicScopePrefix);
  }
}

export function supportDynamicScopes(oidcProvider, ...scopePrefixes) {
  const requestParamOIDCScopes = Object.getOwnPropertyDescriptor(
    oidcProvider.OIDCContext.prototype,
    'requestParamOIDCScopes',
  ).get;
  Object.defineProperty(oidcProvider.OIDCContext.prototype, 'requestParamOIDCScopes', {
    get() {
      const scopes = this.requestParamScopes;
      const recognizedScopes = requestParamOIDCScopes.call(this);
      const regex = new RegExp(`^${scopePrefixes.join('|')}.*`);
      console.log(`searching for dynamic scopes with the pattern: ${regex}`);
      // eslint-disable-next-line no-restricted-syntax
      for (const scope of scopes) {
        if (regex.exec(scope)) {
          console.log(`dynamic scope found: ${scope}`);
          recognizedScopes.add(scope);
        }
      }
      return recognizedScopes;
    },
  });
}

export function ensureTokenEndpointAsAudience(oidcProvider) {
  const clientJwtAuthExpectedAudience = Object.getOwnPropertyDescriptor(
    oidcProvider.OIDCContext.prototype,
    'clientJwtAuthExpectedAudience',
  ).value;
  Object.defineProperty(oidcProvider.OIDCContext.prototype, 'clientJwtAuthExpectedAudience', {
    value() {
      const acceptedAudiences = clientJwtAuthExpectedAudience.call(this);
      console.log(acceptedAudiences);

      // ensure token endpoint is present in all deployment setups
      acceptedAudiences.add(oidcProvider.urlFor('token'));

      return acceptedAudiences;
    },
  });
}

export function addWebhookMiddleware(oidcProvider, adapter) {
  oidcProvider.use(async (ctx, next) => {
    if (!(ctx.path.startsWith('/reg') && ['PUT', 'POST'].includes(ctx.method))) {
      await next();
      return;
    }

    log('Custom POST/PUT request handling for /reg endpoint');

    await next();

    if (![200, 201].includes(ctx.status)) {
      return;
    }

    const webhookUris = ctx.body.webhook_uris;
    const clientId = ctx.body.client_id;
    log(`Evaluating webhook URIs for client ${clientId}: ${JSON.stringify(webhookUris)}`);
    if (webhookUris !== undefined && webhookUris.length > 0) {
      log('Update webhook URI');
      await adapter.updateWebhook(clientId, webhookUris[0]);
    } else {
      log('Delete webhook URI');
      await adapter.deleteWebhook(clientId);
    }
  });
}

async function readRawJsonBody(ctx) {
  const chunks = [];
  // eslint-disable-next-line no-restricted-syntax
  for await (const chunk of ctx.req) {
    chunks.push(chunk);
  }
  return JSON.parse(Buffer.concat(chunks).toString(ctx.charset || 'utf8'));
}

// jose's jwtVerify is async-only, but extraClientMetadata.validator (opf/opin
// configuration.js) is called synchronously from inside the Client constructor, so it
// can't await it directly. This middleware does the actual signature verification ahead
// of time and stashes the outcome on ctx.state, where the sync validator picks it up.
export function addSoftwareStatementVerificationMiddleware(oidcProvider, ssaJwks) {
  oidcProvider.use(async (ctx, next) => {
    if (!(ctx.path.startsWith('/reg') && ['PUT', 'POST'].includes(ctx.method))) {
      await next();
      return;
    }

    let body = ctx.req.body || ctx.request.body;
    if (typeof body?.software_statement !== 'string' && ctx.req.readable) {
      try {
        body = await readRawJsonBody(ctx);
        ctx.req.body = body;
      } catch (error) {
        log(`Failed to read raw /reg request body: ${error.message}`);
      }
    }
    const softwareStatement = body?.software_statement;

    if (typeof softwareStatement === 'string') {
      try {
        const { payload } = await jwtVerify(softwareStatement, ssaJwks, {
          algorithms: ['PS256'],
          issuer: process.env.TRUSTFRAMEWORK_SSA_ISS,
          maxTokenAge: '5 days',
          typ: 'JWT',
        });
        ctx.state.softwareStatementPayload = payload;
      } catch (error) {
        ctx.state.softwareStatementError = error;
      }
    }

    await next();
  });
}
