import Debug from 'debug';

const log = Debug('raidiam:server:info');

function getDynamicScopeIdFromArray(scopes, dynamicScopePrefix) {
  // eslint-disable-next-line no-shadow
  const result = scopes.filter((s) => {
    if (s.name && s.name.startsWith(dynamicScopePrefix)) return true;
    if (!s.name && s.startsWith(dynamicScopePrefix)) return true;
    return false;
  });
  if (result.length === 0) {
    return undefined;
  }

  return result[0].name
    ? result[0].name.split(`${dynamicScopePrefix}`)[1]
    : result[0].split(`${dynamicScopePrefix}`)[1];
}

function getDynamicScopeIdFromString(scopes, dynamicScopePrefix) {
  const result = scopes.split(' ').filter((s) => s.startsWith(dynamicScopePrefix));
  return result.length > 0 ? result[0].split(`${dynamicScopePrefix}`)[1] : undefined;
}

// Test if a variable is a string
function isString(variable) {
  return typeof variable === 'string';
}

export function getDynamicScopeId(scopes, dynamicScopePrefix) {
  if (Array.isArray(scopes)) {
    return getDynamicScopeIdFromArray(scopes, dynamicScopePrefix);
  } else if (isString(scopes)) {
    return getDynamicScopeIdFromString(scopes, dynamicScopePrefix);
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
