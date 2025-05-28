import { getDynamicScopeId } from '../oidc.js';

function containsScopes(scopes, scope) {
  if (Array.isArray(scopes)) {
    return scopes.includes(scope);
  } else {
    return scopes.split(' ').includes(scope);
  }
}

export function isEnrollment(scopes) {
  return containsScopes(scopes, 'nrp-consents');
}

export function isPayment(scopes) {
  return containsScopes(scopes, 'payments');
}

export function isRecurringPayment(scopes) {
  return containsScopes(scopes, 'recurring-payments');
}

export function getConsentId(scopes) {
  return getDynamicScopeId(scopes, 'consent:');
}

export function getRecurringConsentId(scopes) {
  return getDynamicScopeId(scopes, 'recurring-consent:');
}

export function getEnrollmentId(scopes) {
  return getDynamicScopeId(scopes, 'enrollment:');
}
