import { strict as assert } from 'node:assert';

import { urlencoded } from 'express';

import Account from '../account.js';
import { errors } from 'oidc-provider';
import { getConsentId } from './helpers.js';
import { insurerAdapter } from './adapter.js';
import layout from './layout.js';
import { addWebhookMiddleware } from '../oidc.js';
import Debug from 'debug';
import { requestSentLock } from '../requestSentLock.js';
import { parseSkippedUrlencodedBody } from '../albUrlencodedBodyCompat.js';
import { debug } from '../debugPanel.js';
import { installLayoutRenderer } from '../layoutRenderer.js';

const body = [urlencoded({ extended: false }), parseSkippedUrlencodedBody];
const log = Debug('raidiam:server:info');
const warn = Debug('raidiam:server:warn');

export { debug };
const { SessionNotFound } = errors;
export default (app, provider) => {
  installLayoutRenderer(app, layout);

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
          const scopesInformations = await insurerAdapter.getUserInformation(session.accountId, consent.data);
          log(`scopesInformations: ${JSON.stringify(scopesInformations)}`);
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

  app.post('/interaction/:uid/confirm', setNoCache, body, requestSentLock.isSent, async (req, res, next) => {
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

  app.get('/interaction/:uid/abort', setNoCache, requestSentLock.isSent, async (req, res, next) => {
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
      warn(`interaction session not found or expired uid=${req.params.uid} path=${req.path}`);
      return res.status(400).render('error', {
        title: 'Error',
        message: 'No session found. Either the token was already used or it is expired. Please try again.',
      });
    }
    log(`unhandled interaction error: ${err.message}`);
    next(err);
  });

  addWebhookMiddleware(provider, insurerAdapter);
};

function getAuthorizedConsentData(req, accountId) {
  let acceptedCapitalizationTitlePlans = req.body['capitalization-title-accounts']
    ? Array.isArray(req.body['capitalization-title-accounts'])
      ? req.body['capitalization-title-accounts']
      : req.body['capitalization-title-accounts'].split(' ')
    : [];

  let acceptedFinancialRiskPolicies = req.body['financial-risk-accounts']
    ? Array.isArray(req.body['financial-risk-accounts'])
      ? req.body['financial-risk-accounts']
      : req.body['financial-risk-accounts'].split(' ')
    : [];

  let acceptedHousingPolicies = req.body['housing-accounts']
    ? Array.isArray(req.body['housing-accounts'])
      ? req.body['housing-accounts']
      : req.body['housing-accounts'].split(' ')
    : [];

  let acceptedResponsibilityPolicies = req.body['responsibility-accounts']
    ? Array.isArray(req.body['responsibility-accounts'])
      ? req.body['responsibility-accounts']
      : req.body['responsibility-accounts'].split(' ')
    : [];

  let acceptedPersonPolicies = req.body['person-accounts']
    ? Array.isArray(req.body['person-accounts'])
      ? req.body['person-accounts']
      : req.body['person-accounts'].split(' ')
    : [];

  let acceptedLifePensionContracts = req.body['life-pension-accounts']
    ? Array.isArray(req.body['life-pension-accounts'])
      ? req.body['life-pension-accounts']
      : req.body['life-pension-accounts'].split(' ')
    : [];

  let acceptedPensionPlanContracts = req.body['pension-plan-accounts']
    ? Array.isArray(req.body['pension-plan-accounts'])
      ? req.body['pension-plan-accounts']
      : req.body['pension-plan-accounts'].split(' ')
    : [];

  let acceptedFinancialAssistanceContracts = req.body['financial-assistance-accounts']
    ? Array.isArray(req.body['financial-assistance-accounts'])
      ? req.body['financial-assistance-accounts']
      : req.body['financial-assistance-accounts'].split(' ')
    : [];

  let acceptedAcceptanceAndBranchesAbroadPolicies = req.body['insurance-acceptance-and-branches-abroad-accounts']
    ? Array.isArray(req.body['insurance-acceptance-and-branches-abroad-accounts'])
      ? req.body['insurance-acceptance-and-branches-abroad-accounts']
      : req.body['insurance-acceptance-and-branches-abroad-accounts'].split(' ')
    : [];

  let patrimonialPolicies = req.body['patrimonial-accounts']
    ? Array.isArray(req.body['patrimonial-accounts'])
      ? req.body['patrimonial-accounts']
      : req.body['patrimonial-accounts'].split(' ')
    : [];

  let ruralPolicies = req.body['rural-accounts']
    ? Array.isArray(req.body['rural-accounts'])
      ? req.body['rural-accounts']
      : req.body['rural-accounts'].split(' ')
    : [];

  let autoPolicies = req.body['auto-accounts']
    ? Array.isArray(req.body['auto-accounts'])
      ? req.body['auto-accounts']
      : req.body['auto-accounts'].split(' ')
    : [];

  let transportPolicies = req.body['transport-accounts']
    ? Array.isArray(req.body['transport-accounts'])
      ? req.body['transport-accounts']
      : req.body['transport-accounts'].split(' ')
    : [];

  return {
    status: 'AUTHORISED',
    sub: accountId,
    ...(acceptedCapitalizationTitlePlans && {
      linkedCapitalizationTilePlanIds: acceptedCapitalizationTitlePlans,
    }),
    ...(acceptedFinancialRiskPolicies && {
      linkedFinancialRiskPolicyIds: acceptedFinancialRiskPolicies,
    }),
    ...(acceptedHousingPolicies && {
      linkedHousingPolicyIds: acceptedHousingPolicies,
    }),
    ...(acceptedResponsibilityPolicies && {
      linkedResponsibilityPolicyIds: acceptedResponsibilityPolicies,
    }),
    ...(acceptedPersonPolicies && {
      linkedPersonPolicyIds: acceptedPersonPolicies,
    }),
    ...(acceptedLifePensionContracts && {
      linkedLifePensionContractIds: acceptedLifePensionContracts,
    }),
    ...(acceptedPensionPlanContracts && {
      linkedPensionPlanContractIds: acceptedPensionPlanContracts,
    }),
    ...(acceptedAcceptanceAndBranchesAbroadPolicies && {
      linkedAcceptanceAndBranchesAbroadPolicyIds: acceptedAcceptanceAndBranchesAbroadPolicies,
    }),
    ...(acceptedFinancialAssistanceContracts && {
      linkedFinancialAssistanceContractIds: acceptedFinancialAssistanceContracts,
    }),
    ...(patrimonialPolicies && {
      linkedPatrimonialPolicyIds: patrimonialPolicies,
    }),
    ...(ruralPolicies && {
      linkedRuralPolicyIds: ruralPolicies,
    }),
    ...(autoPolicies && {
      linkedAutoPolicyIds: autoPolicies,
    }),
    ...(transportPolicies && {
      linkedTransportPolicyIds: transportPolicies,
    }),
  };
}
