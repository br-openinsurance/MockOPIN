import { errors } from 'oidc-provider';
import axios from 'axios';
import { randomUUID } from 'crypto';
import https from 'https';
import NodeCache from 'node-cache';
import crypto from 'crypto';
import Debug from 'debug';

const log = Debug('raidiam:server:info');
const nodeCash = new NodeCache();

export class BankAdapter {
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
      response = await this.httpClient.get(`/open-banking/consents/v3/consents/${consentId}`, config);
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
      response = await this.httpClient.put(`/open-banking/consents/v2/consents/${consentId}`, { data: data }, config);
      log(`consent ${consentId} updated`);
    } catch (error) {
      log(`error updating the consent ${consentId} - ${error}`);
      throw new errors.InvalidGrant(`consent ${consentId} not updated`);
    }
    return response.data;
  }

  async getPaymentConsent(consentId) {
    const config = await this.getRequestConfig('payments op:payments');
    let response;
    try {
      response = await this.httpClient.get(`/open-banking/payments/v4/consents/${consentId}`, config);
    } catch (error) {
      log(`error fetching the payment consent ${consentId} - ${error} - ${error.response?.data}`);
      throw new errors.InvalidGrant(`payment consent ${consentId} not found`);
    }
    return response.data;
  }

  async updatePaymentConsent(consentId, data) {
    const config = await this.getRequestConfig('payments op:payments');
    let response;
    try {
      response = await this.httpClient.put(`/open-banking/payments/v4/consents/${consentId}`, { data: data }, config);
      log(`payment consent ${consentId} updated`);
    } catch (error) {
      log(`error updating the payment consent ${consentId} - ${error}`);
      throw new errors.InvalidGrant(`payment consent ${consentId} not updated`);
    }
    return response.data;
  }

  async getRecurringPaymentConsent(consentId) {
    const config = await this.getRequestConfig('recurring-payments op:recurring-payments op:payments');
    let response;
    try {
      response = await this.httpClient.get(
        `/open-banking/automatic-payments/v1/recurring-consents/${consentId}`,
        config,
      );
    } catch (error) {
      log(`error fetching the recurring payment consent ${consentId} - ${error} - ${error.response?.data}`);
      throw new errors.InvalidGrant(`recurring consent ${consentId} not found`);
    }
    return response.data;
  }

  async updateRecurringPaymentConsent(consentId, data) {
    const config = await this.getRequestConfig('recurring-payments op:recurring-payments op:payments');
    let response;
    try {
      response = await this.httpClient.put(
        `/open-banking/automatic-payments/v1/recurring-consents/${consentId}`,
        { data: data },
        config,
      );
      log(`recurring payment consent ${consentId} updated`);
    } catch (error) {
      log(`error updating the recurring payment consent ${consentId} - ${error}`);
      throw new errors.InvalidGrant(`recurring consent ${consentId} not updated`);
    }
    return response.data;
  }

  async getEnrollment(enrollmentId) {
    const config = await this.getRequestConfig('payments op:payments');
    let response;
    try {
      response = await this.httpClient.get(`/open-banking/enrollments/v1/enrollments/${enrollmentId}`, config);
    } catch (error) {
      log(`error fetching the enrollment ${enrollmentId} - ${error} - ${error.response?.data}`);
      throw new errors.InvalidGrant(`enrollment ${enrollmentId} not found`);
    }
    return response.data;
  }

  async updateEnrollment(enrollmentId, data) {
    const config = await this.getRequestConfig('payments op:payments');
    let response;
    try {
      response = await this.httpClient.put(
        `/open-banking/enrollments/v1/enrollments/${enrollmentId}`,
        { data: data },
        config,
      );
      log(`enrollment ${enrollmentId} updated`);
    } catch (error) {
      log(`error updating the enrollment ${enrollmentId} - ${error}`);
      throw new errors.InvalidGrant(`enrollment ${enrollmentId} not updated`);
    }
    return response.data;
  }

  async updateWebhook(clientId, webhookUri) {
    await this.deleteWebhook(clientId);

    const config = await this.getRequestConfig('op:admin');
    let response;
    try {
      response = await this.httpClient.post(`/admin/webhook`, { clientId, webhookUri }, config);
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
    const config = await this.getRequestConfig('op:admin');
    let response;
    try {
      response = await this.httpClient.delete(`/admin/webhook/${clientId}`, config);
    } catch (error) {
      log(`Error deleting the client ${clientId} webhook - ${error}`);
      return;
    }

    if (response.status < 200 || response.status >= 300) {
      log(`Error deleting the client ${clientId} webhook - ${JSON.stringify(response)}`);
      return;
    }
    log(`Client ${clientId} webhook URI deleted`);
  }

  async getUserInformation(userId, consentRecord) {
    var result = [];
    if ('permissions' in consentRecord) {
      var arr2 = ['ACCOUNTS_READ', 'ACCOUNTS_BALANCES_READ', 'ACCOUNTS_TRANSACTIONS_READ'];
      var hasPermission = consentRecord.permissions.some((i) => arr2.includes(i));
      if (hasPermission) {
        const userAccounts = await this.getUserAccountsInformation(userId);
        result.push({ scope: 'accounts', accounts: userAccounts.data.data });
      }

      arr2 = [
        'CREDIT_CARDS_ACCOUNTS_READ',
        'CREDIT_CARDS_ACCOUNTS_LIMITS_READ',
        'CREDIT_CARDS_ACCOUNTS_TRANSACTIONS_READ',
        'CREDIT_CARDS_ACCOUNTS_BILLS_READ',
        'CREDIT_CARDS_ACCOUNTS_BILLS_TRANSACTIONS_READ',
      ];
      hasPermission = consentRecord.permissions.some((i) => arr2.includes(i));
      if (hasPermission) {
        const userAccounts = await this.getUserCreditCardAccountsInformation(userId);
        result.push({ scope: 'credit-cards-accounts', accounts: userAccounts.data.data });
      }

      arr2 = ['LOANS_READ', 'LOANS_WARRANTIES_READ', 'LOANS_SCHEDULED_INSTALMENTS_READ', 'LOANS_PAYMENTS_READ'];
      hasPermission = consentRecord.permissions.some((i) => arr2.includes(i));
      if (hasPermission) {
        const userAccounts = await this.getUserLoanAccountsInformation(userId);
        result.push({ scope: 'loans', accounts: userAccounts.data.data });
      }

      arr2 = [
        'FINANCINGS_READ',
        'FINANCINGS_WARRANTIES_READ',
        'FINANCINGS_SCHEDULED_INSTALMENTS_READ',
        'FINANCINGS_PAYMENTS_READ',
      ];
      hasPermission = consentRecord.permissions.some((i) => arr2.includes(i));
      if (hasPermission) {
        const userAccounts = await this.getUserFinancingAccountsInformation(userId);
        result.push({ scope: 'financings', accounts: userAccounts.data.data });
      }

      arr2 = [
        'INVOICE_FINANCINGS_READ',
        'INVOICE_FINANCINGS_WARRANTIES_READ',
        'INVOICE_FINANCINGS_SCHEDULED_INSTALMENTS_READ',
        'INVOICE_FINANCINGS_PAYMENTS_READ',
      ];
      hasPermission = consentRecord.permissions.some((i) => arr2.includes(i));
      if (hasPermission) {
        const userAccounts = await this.getUserInvoiceFinancingAccountsInformation(userId);
        result.push({ scope: 'invoice-financings', accounts: userAccounts.data.data });
      }

      arr2 = [
        'UNARRANGED_ACCOUNTS_OVERDRAFT_READ',
        'UNARRANGED_ACCOUNTS_OVERDRAFT_WARRANTIES_READ',
        'UNARRANGED_ACCOUNTS_OVERDRAFT_SCHEDULED_INSTALMENTS_READ',
        'UNARRANGED_ACCOUNTS_OVERDRAFT_PAYMENTS_READ',
      ];
      hasPermission = consentRecord.permissions.some((i) => arr2.includes(i));
      if (hasPermission) {
        const userAccounts = await this.getUserUnarrangedOverdraftAccountsInformation(userId);
        result.push({ scope: 'unarranged-accounts-overdraft', accounts: userAccounts.data.data });
      }

      arr2 = ['EXCHANGES_READ'];
      hasPermission = consentRecord.permissions.some((i) => arr2.includes(i));
      if (hasPermission) {
        const userAccounts = await this.getUserUnarrangedOverdraftAccountsInformation(userId);
        result.push({ scope: 'exchanges', accounts: userAccounts.data.data });
      }
    }
    if ('payment' in consentRecord) {
      const userAccounts = await this.getUserAccountsInformation(userId);
      // log(userAccounts.data.data);
      // userAccounts.data.data = userAccounts.data.data.filter(
      //     (acc) => consentRecord.debtorAccount && acc.number === consentRecord.debtorAccount.number);
      //   log(userAccounts.data.data);
      result.push({ scope: 'payments', accounts: userAccounts.data.data });
    }

    return result;
  }

  async getUserAccountsInformation(userId) {
    const config = await this.getRequestConfig();
    try {
      return await this.httpClient.get(`/user/${userId}/accounts`, config);
    } catch (error) {
      log(`error getting the user accounts ${userId} - ${error}`);
      throw new errors.InvalidGrant(`user accounts ${userId} not found`);
    }
  }

  async getUserLoanAccountsInformation(userId) {
    const config = await this.getRequestConfig();
    try {
      return await this.httpClient.get(`/user/${userId}/loans`, config);
    } catch (error) {
      log(`error getting the user loans ${userId} - ${error}`);
      throw new errors.InvalidGrant(`user loans ${userId} not found`);
    }
  }

  async getUserFinancingAccountsInformation(userId) {
    const config = await this.getRequestConfig();
    try {
      return await this.httpClient.get(`/user/${userId}/financings`, config);
    } catch (error) {
      log(`error getting the user financings ${userId} - ${error}`);
      throw new errors.InvalidGrant(`user financings ${userId} not found`);
    }
  }

  async getUserInvoiceFinancingAccountsInformation(userId) {
    const config = await this.getRequestConfig();
    try {
      return await this.httpClient.get(`/user/${userId}/invoice-financings`, config);
    } catch (error) {
      log(`error getting the user invoice-financings ${userId} - ${error}`);
      throw new errors.InvalidGrant(`user invoice-financings ${userId} not found`);
    }
  }

  async getUserUnarrangedOverdraftAccountsInformation(userId) {
    const config = await this.getRequestConfig();
    try {
      return await this.httpClient.get(`/user/${userId}/unarranged-accounts-overdraft`, config);
    } catch (error) {
      log(`error getting the user unarranged-accounts-overdraft ${userId} - ${error}`);
      throw new errors.InvalidGrant(`user unarranged-accounts-overdraft ${userId} not found`);
    }
  }

  async getUserCreditCardAccountsInformation(userId) {
    const config = await this.getRequestConfig();
    try {
      return await this.httpClient.get(`/user/${userId}/credit-card-accounts`, config);
    } catch (error) {
      log(`error getting the user credit-card-accounts ${userId} - ${error}`);
      throw new errors.InvalidGrant(`user credit-card-accounts ${userId} not found`);
    }
  }

  async getUserExchangesAccountsInformation(userId) {
    const config = await this.getRequestConfig();
    try {
      await this.httpClient.get(`/user/${userId}/exchanges`, config);
    } catch (error) {
      log(`error getting the user exchanges ${userId} - ${error}`);
      throw new errors.InvalidGrant(`user exchanges ${userId} not found`);
    }
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

export let bankAdapter = undefined;

export function init(baseUrl, oidcProvider, clientId, clientCert, clientCertKey) {
  const httpsAgent = new https.Agent({
    key: clientCertKey,
    cert: clientCert,
    rejectUnauthorized: false,
  });

  const instance = axios.create({ baseURL: baseUrl, httpsAgent: httpsAgent });
  bankAdapter = new BankAdapter(instance, oidcProvider, clientId);

  return bankAdapter;
}
