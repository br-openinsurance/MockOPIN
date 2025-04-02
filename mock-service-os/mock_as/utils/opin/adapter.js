import { errors } from 'oidc-provider';
import axios from 'axios';
import { randomUUID } from 'crypto';
import https from 'https';
import NodeCache from 'node-cache';
import crypto from 'crypto';
import Debug from 'debug';

const log = Debug('raidiam:server:info');
const nodeCash = new NodeCache();

export class InsurerAdapter {
  oidcProvider;
  httpClient;
  nodeCash;
  clientId;

  constructor(httpClient, oidcProvider, clientId) {
    this.httpClient = httpClient;
    this.oidcProvider = oidcProvider;
    this.nodeCash = nodeCash;
    this.clientId = clientId;
  }

  async getConsent(consentId) {
    const config = await this.getRequestConfig('consents op:consent');
    let response;
    try {
      response = await this.httpClient.get(`/open-insurance/consents/v2/consents/${consentId}`, config);
    } catch (error) {
      log(`error fetching the consent ${consentId} - ${error} - ${error.response?.data}`);
      throw new errors.InvalidGrant(`consent ${consentId} not found`);
    }
    return response.data;
  }

  async updateConsent(consentId, data) {
    const config = await this.getRequestConfig('consents op:consent');
    let response;
    try {
      response = await this.httpClient.put(`/open-insurance/consents/v2/consents/${consentId}`, { data: data }, config);
      log(`consent ${consentId} updated`);
    } catch (error) {
      log(`error updating the consent ${consentId} - ${error}`);
      throw new errors.InvalidGrant(`consent ${consentId} not updated`);
    }
    return response.data;
  }

  async getUserInformation(userId, consentRecord) {
    var result = [];
    return result;
  }

  async updateWebhook(clientId, webhookUri) {
    const config = await this.getRequestConfig('op:admin');
    let response;
    try {
      response = await this.httpClient.put(`/admin/webhook`, { clientId, webhookUri }, config);
    } catch (error) {
      log(`Error updating the client ${clientId} webhook - ${error}`);
      return;
    }

    if (response.status < 200 || response.status >= 300) {
      log(`Error updating the client ${clientId} webhook - ${JSON.stringify(response)}`);
      return;
    }
    log(`Client ${clientId} webhook URI updated`);
  }

  async deleteWebhook(clientId) {
    log(`Deleting client ${clientId} webhook URI`);
    this.updateWebhook(clientId, null);
  }

  async getRequestConfig(scopes) {
    if (!scopes) {
      scopes = '';
    }

    const token = await this.getToken(scopes);
    return {
      headers: {
        Authorization: `Bearer ${token}`,
        'x-fapi-interaction-id': randomUUID(),
      },
    };
  }

  async getToken(scope) {
    log('Retrieving token');
    const hash = crypto.createHash('md5').update(scope).digest('hex');
    let token = this.nodeCash.get(hash);
    if (token) {
      log('Using token from cache');
      return token;
    } else {
      log('Obtaining new token');
      token = await this.makeToken(scope);
      this.nodeCash.set(hash, this.token, 2000);
    }

    return token;
  }

  async makeToken(scope) {
    const token = new this.oidcProvider.ClientCredentials({
      client: {
        clientId: this.clientId,
        responseTypeAllowed: (type) => false,
        responseModeAllowed: (type, responseType, fapiProfile) => false,
        grantTypeAllowed: (type) => false,
        redirectUriAllowed: (redirectUri) => false,
        requestUriAllowed: (requestUri) => false,
        postLogoutRedirectUriAllowed: (postLogoutRedirectUri) => false,
        includeSid: () => false,
        compareClientSecret: (actual) => false,
        metadata: () => {
          return { client_id: this.clientId };
        },
        backchannelPing: async (request) => {},
      },
      scope: scope,
    });

    return await token.save();
  }
}

export let insurerAdapter = undefined;

export function init(baseUrl, oidcProvider, clientId, clientCert, clientCertKey) {
  const httpsAgent = new https.Agent({
    key: clientCertKey,
    cert: clientCert,
    rejectUnauthorized: false,
  });

  const instance = axios.create({ baseURL: baseUrl, httpsAgent: httpsAgent });
  insurerAdapter = new InsurerAdapter(instance, oidcProvider, clientId);

  return insurerAdapter;
}
