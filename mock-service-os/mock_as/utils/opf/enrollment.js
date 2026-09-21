import { getEnrollmentId } from './helpers.js';
import { bankAdapter } from './adapter.js';

function buildEnrollmentUpdate(req) {
  const update = {
    status: 'AWAITING_ENROLLMENT',
    optimisedJourney: !!req.body.optimisedConsent,
  };
  update.name = req.body.name || null;
  update.expirationDateTime = req.body.expirationDateTime
    ? new Date(`${req.body.expirationDateTime}Z`).toISOString()
    : null;
  update.transactionLimit = req.body.transactionLimit || null;
  update.dailyLimit = req.body.dailyLimit || null;
  return update;
}

export function updateEnrollment(params, req) {
  return bankAdapter.updateEnrollment(getEnrollmentId(params.scope), buildEnrollmentUpdate(req));
}
