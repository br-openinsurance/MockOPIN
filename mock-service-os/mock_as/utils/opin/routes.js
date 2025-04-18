/* eslint-disable no-console, camelcase, no-unused-vars */
import { strict as assert } from 'node:assert';
import * as querystring from 'node:querystring';
import { inspect } from 'node:util';

import isEmpty from 'lodash/isEmpty.js';
import { urlencoded } from 'express'; // eslint-disable-line import/no-unresolved

import Account from '../account.js';
import { errors } from 'oidc-provider';
import { getConsentId } from './helpers.js';
import { insurerAdapter } from './adapter.js';
import layout from './layout.js';
import { addWebhookMiddleware } from '../oidc.js';

const body = urlencoded({ extended: false });

const keys = new Set();
const debug = (obj) =>
  querystring.stringify(
    Object.entries(obj).reduce((acc, [key, value]) => {
      keys.add(key);
      if (isEmpty(value)) return acc;
      acc[key] = inspect(value, { depth: null });
      return acc;
    }, {}),
    '<br/>',
    ': ',
    {
      encodeURIComponent(value) {
        return keys.has(value) ? `<strong>${value}</strong>` : value;
      },
    },
  );
const { SessionNotFound } = errors;
export default (app, provider) => {
  app.use((req, res, next) => {
    const orig = res.render;
    // you'll probably want to use a full blown render engine capable of layouts
    res.render = (view, locals) => {
      app.render(view, locals, (err, html) => {
        if (err) throw err;
        orig.call(res, '_layout', {
          ...locals,
          layout,
          body: html,
        });
      });
    };
    next();
  });

  function setNoCache(req, res, next) {
    res.set('cache-control', 'no-store');
    next();
  }

  app.get('/interaction/:uid', setNoCache, async (req, res, next) => {
    try {
      const { uid, prompt, params, session } = await provider.interactionDetails(req, res);

      const client = await provider.Client.find(params.client_id);

      switch (prompt.name) {
        case 'login': {
          return res.render('login', {
            client,
            uid,
            details: prompt.details,
            params,
            title: 'Sign-in',
            session: session ? debug(session) : undefined,
            dbg: {
              params: debug(params),
              prompt: debug(prompt),
            },
            layout,
          });
        }
        case 'consent': {
          let consentId = getConsentId(prompt.details.missingOIDCScope);
          if (consentId == undefined) {
            const result = {
              error: 'access_denied',
              error_description: 'Unable to locate consent reference scope',
            };
            console.log(result);
            return await provider.interactionFinished(req, res, result, {
              mergeWithLastSubmission: false,
            });
          }

          let consent = await insurerAdapter.getConsent(consentId);
          console.log(consent);
          const scopesInformations = await insurerAdapter.getUserInformation(session.accountId, consent.data);
          return res.render('interaction', {
            client,
            uid,
            details: {
              consent: consent.data,
              scopes: scopesInformations,
              prompt: prompt.details,
            },
            params,
            title: 'Authorize',
            session: session ? debug(session) : undefined,
            dbg: {
              params: debug(params),
              prompt: debug(prompt),
            },
            layout,
          });
        }
        default:
          return undefined;
      }
    } catch (err) {
      return next(err);
    }
  });

  app.post('/interaction/:uid/login', setNoCache, body, async (req, res, next) => {
    try {
      const { uid, prompt, params, session } = await provider.interactionDetails(req, res);

      assert.equal(prompt.name, 'login');

      const account = await Account.authenticate(req.body.login, req.body.password).catch((err) => {
        console.log(req.body.username);
        console.log(err);
      });

      if (!account) {
        console.log('login / password invalid');
        const client = await provider.Client.find(params.client_id);
        return res.render('login', {
          client,
          uid,
          details: prompt.details,
          params,
          title: 'Sign-in',
          error: 'login / password invalid',
          session: session ? debug(session) : undefined,
          dbg: {
            params: debug(params),
            prompt: debug(prompt),
          },
          layout,
        });
      }

      const result = {
        login: {
          accountId: account.accountId,
          acr: 'urn:brasil:openinsurance:loa3',
        },
      };

      await provider.interactionFinished(req, res, result, {
        mergeWithLastSubmission: false,
      });
    } catch (err) {
      next(err);
    }
  });

  app.post('/interaction/:uid/confirm', setNoCache, body, async (req, res, next) => {
    try {
      const interactionDetails = await provider.interactionDetails(req, res);
      const {
        prompt: { name, details },
        params,
        session: { accountId },
      } = interactionDetails;
      assert.equal(name, 'consent');

      let { grantId } = interactionDetails;
      let grant;

      if (grantId) {
        // we'll be modifying existing grant in existing session
        grant = await provider.Grant.find(grantId);
      } else {
        // we're establishing a new grant
        grant = new provider.Grant({
          accountId,
          clientId: params.client_id,
        });
      }

      let consentId = getConsentId(details.missingOIDCScope);
      console.log(`consent ID: ${consentId}`);
      if (consentId) {
        let consentRecord;
        // Authorize the consent record.
        try {
          let authPayload = getAuthorizedConsentData(req, accountId);
          consentRecord = await insurerAdapter.updateConsent(consentId, authPayload);
        } catch (error) {
          console.log(error);
          const result = {
            error: 'access_denied',
            error_description: 'Unable to update consent status',
          };
          return await provider.interactionFinished(req, res, result, {
            mergeWithLastSubmission: false,
          });
        }

        if (details.missingOIDCScope) {
          grant.addOIDCScope(details.missingOIDCScope.join(' '));
        }
        if (details.missingOIDCClaims) {
          grant.addOIDCClaims(details.missingOIDCClaims);
        }
        if (details.missingResourceScopes) {
          for (const [indicator, scopes] of Object.entries(details.missingResourceScopes)) {
            grant.addResourceScope(indicator, scopes.join(' '));
          }
        }

        if (consentRecord.expirationDateTime) {
          let nowUTCMinus3 = Date.now() - 3 * 60 * 60 * 1000;
          const ttl = Math.round((Date.parse(consentRecord.expirationDateTime) - nowUTCMinus3) / 1000);
          Object.defineProperty(grant, 'grantTtl', {
            value: ttl,
            writable: false,
          });
        }

        // Save the grant
        grantId = await grant.save();

        const consent = {};
        if (!interactionDetails.grantId) {
          // we don't have to pass grantId to consent, we're just modifying existing one
          consent.grantId = grantId;
        }

        const result = { consent };
        return await provider.interactionFinished(req, res, result, {
          mergeWithLastSubmission: true,
        });
      }
    } catch (err) {
      next(err);
    }
  });

  app.get('/interaction/:uid/abort', setNoCache, async (req, res, next) => {
    const { prompt } = await provider.interactionDetails(req, res);

    let consentId = getConsentId(prompt.details.missingOIDCScope);
    if (consentId === undefined) {
      const result = {
        error: 'access_denied',
        error_description: 'Unable to locate consent reference scope',
      };

      return await provider.interactionFinished(req, res, result, {
        mergeWithLastSubmission: false,
      });
    }

    await insurerAdapter.updateConsent(consentId, { status: 'REJECTED' });

    try {
      const result = {
        error: 'access_denied',
        error_description: 'End-User aborted interaction',
      };
      await provider.interactionFinished(req, res, result, {
        mergeWithLastSubmission: false,
      });
    } catch (err) {
      next(err);
    }
  });

  app.use((err, req, res, next) => {
    if (err instanceof SessionNotFound) {
      // handle interaction expired / session not found error
    }
    next(err);
  });

  addWebhookMiddleware(provider, insurerAdapter);
};

function getAuthorizedConsentData(req, accountId) {
  // Accounts could be an array (if there is more than one)
  let acceptedAccounts = req.body['accounts-accounts']
    ? Array.isArray(req.body['accounts-accounts'])
      ? req.body['accounts-accounts']
      : req.body['accounts-accounts'].split(' ')
    : [];

  let acceptedCreditCardsAccounts = req.body['credit-cards-accounts']
    ? Array.isArray(req.body['credit-cards-accounts'])
      ? req.body['credit-cards-accounts']
      : req.body['credit-cards-accounts'].split(' ')
    : [];

  if (!acceptedAccounts.length) {
    acceptedAccounts = req.body['customers-accounts']
      ? Array.isArray(req.body['customers-accounts'])
        ? req.body['customers-accounts']
        : req.body['customers-accounts'].split(' ')
      : [];
  }

  const acceptedFinancingsAccounts = req.body['financings-accounts']
    ? Array.isArray(req.body['financings-accounts'])
      ? req.body['financings-accounts']
      : req.body['financings-accounts'].split(' ')
    : [];

  const acceptedInvoiceFinancingsAccounts = req.body['invoice-financings-accounts']
    ? Array.isArray(req.body['invoice-financings-accounts'])
      ? req.body['invoice-financings-accounts']
      : req.body['invoice-financings-accounts'].split(' ')
    : [];

  const acceptedLoansAccounts = req.body['loans-accounts']
    ? Array.isArray(req.body['loans-accounts'])
      ? req.body['loans-accounts']
      : req.body['loans-accounts'].split(' ')
    : [];

  const acceptedOverdraftAccounts = req.body['unarranged-accounts-overdraft-accounts']
    ? Array.isArray(req.body['unarranged-accounts-overdraft-accounts'])
      ? req.body['unarranged-accounts-overdraft-accounts']
      : req.body['unarranged-accounts-overdraft-accounts'].split(' ')
    : [];

  const acceptedExchangesOperationsAccounts = req.body['exchanges-accounts']
    ? Array.isArray(req.body['exchanges-accounts'])
      ? req.body['exchanges-accounts']
      : req.body['exchanges-accounts'].split(' ')
    : [];

  const acceptedPaymentsAccounts = req.body['payments-accounts']
    ? Array.isArray(req.body['payments-accounts'])
      ? req.body['payments-accounts']
      : req.body['payments-accounts'].split(' ')
    : [];

  const acceptedPaymentsAccountsBranch = req.body['payments-accounts-branch']
    ? Array.isArray(req.body['payments-accounts-branch'])
      ? req.body['payments-accounts-branch']
      : req.body['payments-accounts-branch'].split(' ')
    : [];

  //The update will conditionally add the debtor account based on what was selected
  return {
    status: 'AUTHORISED',
    ...(acceptedFinancingsAccounts && {
      linkedFinancingAccountIds: acceptedFinancingsAccounts,
    }),
    ...(acceptedLoansAccounts && {
      linkedLoanAccountIds: acceptedLoansAccounts,
    }),
    ...(acceptedInvoiceFinancingsAccounts && {
      linkedInvoiceFinancingAccountIds: acceptedInvoiceFinancingsAccounts,
    }),
    ...(acceptedOverdraftAccounts && {
      linkedUnarrangedOverdraftAccountIds: acceptedOverdraftAccounts,
    }),
    sub: accountId,
    ...(acceptedAccounts && {
      linkedAccountIds: acceptedAccounts,
    }),
    ...(acceptedCreditCardsAccounts && {
      linkedCreditCardAccountIds: acceptedCreditCardsAccounts,
    }),
    ...(acceptedExchangesOperationsAccounts && {
      linkedExchangeOperationIds: acceptedExchangesOperationsAccounts,
    }),
    ...(acceptedPaymentsAccounts && {
      debtorAccount: {
        number: acceptedPaymentsAccounts[0],
        issuer: acceptedPaymentsAccountsBranch[0],
        ispb: '12345678',
        accountType: 'CACC',
      },
    }),
  };
}
