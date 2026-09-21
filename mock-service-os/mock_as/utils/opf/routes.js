/* eslint-disable no-console, camelcase, no-unused-vars */
import { strict as assert } from 'node:assert';
import { urlencoded } from 'express'; // eslint-disable-line import/no-unresolved
import { errors } from 'oidc-provider';
import Debug from 'debug';

import Account from '../account.js';
import {
  isRecurringPayment,
  isPayment,
  isEnrollment,
  getConsentId,
  getPaymentConsentId,
  getRecurringConsentId,
  getEnrollmentId,
} from './helpers.js';
import { bankAdapter } from './adapter.js';
import { updateEnrollment } from './enrollment.js';
import layout from './layout.js';
import { addWebhookMiddleware } from '../oidc.js';
import { requestSentLock } from '../requestSentLock.js';
import { registerCibaRoutes } from './ciba.js';
import { parseSkippedUrlencodedBody } from '../albUrlencodedBodyCompat.js';
import { debug } from '../debugPanel.js';
import { installLayoutRenderer } from '../layoutRenderer.js';

const body = [urlencoded({ extended: false }), parseSkippedUrlencodedBody];
const log = Debug('raidiam:server:info');
const warn = Debug('raidiam:server:warn');

export { debug };

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
          let data;
          if (isEnrollment(params.scope)) {
            log('handling consent for enrollment');
            let id = getEnrollmentId(params.scope);
            log(`enrollment id: ${id}`);
            let enrollment = await bankAdapter.getEnrollment(id);
            let account = await Account.findAccount(null, session.accountId, null);
            log(`Account: ${JSON.stringify(account)}`);
            log(`National id: ${account.national_id}`);
            if (
              enrollment.data.loggedUser.document.identification &&
              enrollment.data.loggedUser.document.rel &&
              enrollment.data.loggedUser.document.rel == 'CPF' &&
              enrollment.data.loggedUser.document.identification !== account.national_id
            ) {
              try {
                let nowUTCMinus3 = Date.now() - 3 * 60 * 60 * 1000;
                await bankAdapter.updateEnrollment(id, {
                  status: 'REJECTED',
                  cancellation: {
                    reason: {
                      rejectionReason: 'REJEITADO_TITULARIDADE_DIVERGENTE',
                    },
                    cancelledFrom: 'DETENTORA',
                    rejectedAt: `${new Date(nowUTCMinus3).toISOString()}`,
                  },
                });
                const result = {
                  error: 'access_denied',
                  error_description: 'Enrollment belongs to another customer',
                };
                return await provider.interactionFinished(req, res, result, {
                  mergeWithLastSubmission: false,
                });
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
            }

            const dataConsentId = getConsentId(params.scope);
            if (dataConsentId != undefined) {
              log(`Data consent ID found in scope: ${dataConsentId}`);
              try {
                const dataConsent = await bankAdapter.getConsent(dataConsentId);
                log(`Data consent journey.isLinked: ${dataConsent.data.journey?.isLinked}`);
                log(`Enrollment journey.isLinked: ${enrollment.data.journey?.isLinked}`);

                if (
                  dataConsent.data.journey?.isLinked &&
                  (!enrollment.data.journey || !enrollment.data.journey.isLinked)
                ) {
                  log('Data consent expects optimised journey but enrollment is not linked - rejecting both');

                  await bankAdapter.updateEnrollment(id, {
                    status: 'REJECTED',
                    cancellation: {
                      reason: {
                        rejectionReason: 'REJEITADO_SEGURANCA_INTERNA',
                      },
                      cancelledFrom: 'DETENTORA',
                      rejectedAt: new Date().toISOString(),
                    },
                  });

                  await bankAdapter.updateConsent(dataConsentId, {
                    status: 'REJECTED',
                    rejection: {
                      rejectedBy: 'ASPSP',
                      reason: {
                        code: 'INTERNAL_SECURITY_REASON',
                        additionalInformation: 'Enrollment is not properly linked for optimised journey',
                      },
                    },
                  });

                  const result = {
                    error: 'access_denied',
                    error_description: 'Enrollment is not linked to data consent for optimised journey',
                  };
                  return await provider.interactionFinished(req, res, result, {
                    mergeWithLastSubmission: false,
                  });
                }
              } catch (error) {
                log(`Error checking data consent ${dataConsentId}: ${error}`);
              }
            }

            data = enrollment.data;
          } else if (isPayment(params.scope)) {
            log('handling consent for payment');
            let id = getPaymentConsentId(params.scope);
            var dataConsentId = getConsentId(params.scope);
            if (dataConsentId != undefined) {
              log('Data consent ID found - rejecting in case of optimised journey');
              console.log(`data consent ID: ${dataConsentId}`);

              const detailMessage = 'Optimised journey not supported for payment consents';
              const rejectedConsent = await bankAdapter.updateConsent(dataConsentId, {
                status: 'REJECTED',
                rejection: {
                  rejectedBy: 'ASPSP',
                  reason: {
                    code: 'INTERNAL_SECURITY_REASON',
                    additionalInformation: detailMessage,
                  },
                },
              });

              console.log(`rejected data consent: ${JSON.stringify(rejectedConsent)}`);
              const result = {
                error: 'access_denied',
                error_description: detailMessage,
              };
              return await provider.interactionFinished(req, res, result, {
                mergeWithLastSubmission: false,
              });
            }
            try {
              log(`payment consent id: ${id}`);
              let paymentConsent = await bankAdapter.getPaymentConsent(id);
              data = paymentConsent.data;
              if (data.sub && data.sub != session.accountId) {
                // For payments_api_unmatching-loggedUser_test-module
                await bankAdapter.updatePaymentConsent(data.consentId, {
                  status: 'REJECTED',
                  rejection: {
                    rejectedBy: 'DETENTORA',
                    rejectedFrom: 'DETENTORA',
                    rejectedAt: new Date().toISOString(),
                    reason: {
                      code: 'AUTENTICACAO_DIVERGENTE',
                      detail: 'Consent belongs to another customer',
                    },
                  },
                });
                const result = {
                  error: 'access_denied',
                  error_description: 'Consent belongs to another customer',
                };
                return await provider.interactionFinished(req, res, result, {
                  mergeWithLastSubmission: false,
                });
              }
            } catch (error) {
              console.log(error);
              const result = {
                error: 'access_denied',
                error_description: 'Unable to get consent',
              };
              return await provider.interactionFinished(req, res, result, {
                mergeWithLastSubmission: false,
              });
            }
          } else if (isRecurringPayment(params.scope)) {
            log('handling consent for recurring payment');
            let id = getRecurringConsentId(params.scope);
            try {
              log(`recurring payment consent id: ${id}`);
              let recurringPaymentConsent = await bankAdapter.getRecurringPaymentConsent(id);
              data = recurringPaymentConsent.data;
              if ((!data.journey || !data.journey.isLinked) && getConsentId(params.scope) != undefined) {
                log('recurring consent is not linked to supplied data consent ID - rejecting both');

                const detailMessage = 'Consent is not linked to the provided data consent ID';
                await bankAdapter.updateRecurringPaymentConsent(id, {
                  status: 'REJECTED',
                  rejection: {
                    rejectedBy: 'DETENTORA',
                    rejectedFrom: 'DETENTORA',
                    rejectedAt: new Date().toISOString(),
                    reason: {
                      code: 'FLUXO_NAO_SUPORTADO_PRODUTO',
                      detail: detailMessage,
                    },
                  },
                });
                await bankAdapter.updateConsent(getConsentId(params.scope), {
                  status: 'REJECTED',
                  rejection: {
                    rejectedBy: 'ASPSP',
                    reason: {
                      code: 'INTERNAL_SECURITY_REASON',
                      additionalInformation: detailMessage,
                    },
                  },
                });
                log('rejected data');
                const result = {
                  error: 'access_denied',
                  error_description: detailMessage,
                };

                return await provider.interactionFinished(req, res, result, {
                  mergeWithLastSubmission: false,
                });
              }
            } catch (error) {
              console.log(error);
              const result = {
                error: 'access_denied',
                error_description: 'Unable to get consent',
              };
              return await provider.interactionFinished(req, res, result, {
                mergeWithLastSubmission: false,
              });
            }
          } else {
            log('handling simple consent');
            let id = getConsentId(params.scope);
            try {
              log(`consent id: ${id}`);
              let consent = await bankAdapter.getConsent(id);
              data = consent.data;
              const recurringId = getRecurringConsentId(params.scope);
              const enrollmentId = getEnrollmentId(params.scope);
              log(`recurring consent data isLinked: ${data.journey != null}`);
              log(`recurringId valid: ${recurringId == undefined}`);
              log(`enrollmentId valid: ${enrollmentId == undefined}`);

              // Check if enrollment ID is not supplied for linked data consent
              if (
                data.journey != null &&
                data.journey.isLinked != null &&
                data.journey.isLinked &&
                data.journey.linkId != undefined &&
                enrollmentId == undefined
              ) {
                // Verify the linkId is actually an enrollment (not a recurring payment)
                try {
                  const linkedEnrollment = await bankAdapter.getEnrollment(data.journey.linkId);
                  if (linkedEnrollment) {
                    log('Enrollment ID is not supplied for linked data consent - Rejecting consent and enrollment');
                    const detailMessage = 'Enrollment ID not provided for linked data consent';

                    await bankAdapter.updateEnrollment(data.journey.linkId, {
                      status: 'REJECTED',
                      cancellation: {
                        reason: {
                          rejectionReason: 'REJEITADO_SEGURANCA_INTERNA',
                        },
                        cancelledFrom: 'DETENTORA',
                        rejectedAt: new Date().toISOString(),
                      },
                    });

                    await bankAdapter.updateConsent(id, {
                      status: 'REJECTED',
                      rejection: {
                        rejectedBy: 'ASPSP',
                        reason: {
                          code: 'INTERNAL_SECURITY_REASON',
                          additionalInformation: detailMessage,
                        },
                      },
                    });

                    const result = {
                      error: 'access_denied',
                      error_description: detailMessage,
                    };

                    return await provider.interactionFinished(req, res, result, {
                      mergeWithLastSubmission: false,
                    });
                  }
                } catch (enrollmentError) {
                  log(`linkId ${data.journey.linkId} is not an enrollment, skipping enrollment check`);
                }
              }

              // Check if recurring consent ID is not supplied for linked data consent
              if (
                data.journey != null &&
                data.journey.isLinked != null &&
                data.journey.isLinked &&
                recurringId == undefined
              ) {
                log('Recurring consent ID is not supplied for linked data consent - Rejecting consent');
                const detailMessage = 'Recurring consent ID not provided for linked data consent';
                await bankAdapter.updateConsent(id, {
                  status: 'REJECTED',
                  rejection: {
                    rejectedBy: 'ASPSP',
                    reason: {
                      code: 'INTERNAL_SECURITY_REASON',
                      additionalInformation: detailMessage,
                    },
                  },
                });
                const result = {
                  error: 'access_denied',
                  error_description: detailMessage,
                };

                return await provider.interactionFinished(req, res, result, {
                  mergeWithLastSubmission: false,
                });
              } else {
                log(`consent is not linked: ${data}`);
              }
            } catch (error) {
              console.log(error);
              const result = {
                error: 'access_denied',
                error_description: 'Unable to get consent',
              };
              return await provider.interactionFinished(req, res, result, {
                mergeWithLastSubmission: false,
              });
            }
          }

          if (data == undefined) {
            const result = {
              error: 'access_denied',
              error_description: 'Unable to locate reference scope',
            };
            log(result);
            return await provider.interactionFinished(req, res, result, {
              mergeWithLastSubmission: false,
            });
          }

          log(`data found: ${JSON.stringify(data)}`);

          if (!data.status || data.status === 'REJECTED') {
            const result = {
              error: 'access_denied',
              error_description: 'Consent record is rejected',
            };
            return await provider.interactionFinished(req, res, result, {
              mergeWithLastSubmission: false,
            });
          }

          if (data.sub && data.sub != session.accountId) {
            // For automatic-pix-unmatching-loggedUser_test-module and sweeping consents -
            // Rejects the consent in a PUT for any recurring configuration type (automatic or sweeping)
            // when the consent's sub does not match the authenticated user.
            await bankAdapter.updateRecurringPaymentConsent(data.consentId, {
              status: 'REJECTED',
              rejection: {
                rejectedBy: 'DETENTORA',
                rejectedFrom: 'DETENTORA',
                rejectedAt: new Date().toISOString(),
                reason: {
                  code: 'AUTENTICACAO_DIVERGENTE',
                  detail: 'Consent belongs to another customer',
                },
              },
            });

            const result = {
              error: 'access_denied',
              error_description: 'Consent belongs to another customer',
            };
            return await provider.interactionFinished(req, res, result, {
              mergeWithLastSubmission: false,
            });
          }

          const scopesInformations = await bankAdapter.getUserInformation(session.accountId, data);
          return res.render('interaction', {
            client,
            uid,
            details: {
              consent: data,
              scopes: scopesInformations,
              prompt: prompt.details,
            },
            params,
            title: 'Authorize',
            session: session ? debug(session) : undefined,
            dbg: {
              params: debug(params),
              prompt: debug(prompt),
              consent: debug(data),
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
          acr: 'urn:brasil:openbanking:loa3',
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
        grant = new provider.Grant({ accountId, clientId: params.client_id });
      }

      let consentRecord;
      let consentId;
      // Authorize the consent record.
      try {
        if (isEnrollment(params.scope)) {
          consentRecord = await updateEnrollment(params, req);
        } else if (isPayment(params.scope)) {
          const authPayload = getAuthorizedPaymentConsentData(req, accountId);
          consentId = getPaymentConsentId(params.scope);
          consentRecord = await bankAdapter.updatePaymentConsent(consentId, authPayload);
        } else if (isRecurringPayment(params.scope)) {
          const authPayload = getAuthorizedPaymentConsentData(req, accountId);
          consentId = getRecurringConsentId(params.scope);
          consentRecord = await bankAdapter.updateRecurringPaymentConsent(consentId, authPayload);
        } else if (getConsentId(params.scope)) {
          const authPayload = getAuthorizedConsentData(req, accountId);
          consentId = getConsentId(params.scope);
          consentRecord = await bankAdapter.updateConsent(consentId, authPayload);
        }
      } catch (error) {
        log(`error updating consent ${consentId}: ${error}`);
        const result = {
          error: 'access_denied',
          error_description: 'Unable to update consent status',
        };
        return await provider.interactionFinished(req, res, result, {
          mergeWithLastSubmission: false,
        });
      }

      if (!consentRecord) {
        log(`error: no consent record found`);
        return;
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

      const consentExpiry = consentRecord.data?.expirationDateTime ?? consentRecord.expirationDateTime;

      if (consentExpiry) {
        let nowUTCMinus3 = Date.now() - 3 * 60 * 60 * 1000;
        const ttl = Math.round((Date.parse(consentExpiry) - nowUTCMinus3) / 1000);
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
    } catch (err) {
      next(err);
    }
  });

  app.get('/interaction/:uid/abort', setNoCache, requestSentLock.isSent, async (req, res, next) => {
    const { prompt, params } = await provider.interactionDetails(req, res);
    let consentId = getConsentId(params.scope);
    if (!consentId) {
      consentId = getPaymentConsentId(params.scope);
    }
    if (!consentId) {
      consentId = getRecurringConsentId(params.scope);
    }
    if (!consentId) {
      consentId = getEnrollmentId(params.scope);
    }
    if (consentId === undefined) {
      const result = {
        error: 'access_denied',
        error_description: 'Unable to locate consent reference scope',
      };

      return await provider.interactionFinished(req, res, result, {
        mergeWithLastSubmission: false,
      });
    }

    if (isEnrollment(params.scope)) {
      await bankAdapter.updateEnrollment(consentId, { status: 'REJECTED' });
    } else if (isPayment(params.scope)) {
      await bankAdapter.updatePaymentConsent(consentId, { status: 'REJECTED' });
    } else if (isRecurringPayment(params.scope)) {
      await bankAdapter.updateRecurringPaymentConsent(consentId, {
        status: 'REJECTED',
        rejection: {
          rejectedBy: 'USUARIO',
          rejectedFrom: 'DETENTORA',
          rejectedAt: new Date().toISOString(),
          reason: {
            code: 'REJEITADO_USUARIO',
            detail: 'O usuário rejeitou a autorização do consentimento',
          },
        },
      });
    } else {
      await bankAdapter.updateConsent(consentId, { status: 'REJECTED' });
    }

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
    if (err instanceof errors.SessionNotFound) {
      warn(`interaction session not found or expired uid=${req.params.uid} path=${req.path}`);
      return res.status(400).render('error', {
        title: 'Error',
        message: 'No session found. Either the token was already used or it is expired. Please try again.',
      });
    }
    log(`unhandled interaction error: ${err.message}`);
    next(err);
  });

  addWebhookMiddleware(provider, bankAdapter);

  // Decoupled CIBA approval screens (no browser redirect happens in CIBA).
  registerCibaRoutes(app, provider);
};

export function getAuthorizedConsentData(req, accountId) {
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
  //The update will conditionally add the debtor account based on what was selected
  return {
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
    status: 'AUTHORISED',
  };
}

function getAuthorizedPaymentConsentData(req, accountId) {
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

  return {
    //user gabriel.nunes will simulate multiple consents and will send different status
    ...(accountId !== 'gabriel.nunes@email.com' && {
      status: 'AUTHORISED',
    }),
    ...(accountId === 'gabriel.nunes@email.com' && {
      status: 'PARTIALLY_ACCEPTED',
    }),
    ...(acceptedPaymentsAccounts && {
      debtorAccount: {
        number: acceptedPaymentsAccounts[0],
        issuer: acceptedPaymentsAccountsBranch[0],
        ispb: '12345678',
        accountType: 'CACC',
      },
    }),
    optimisedJourney: !!req.body.optimisedConsent,
  };
}
