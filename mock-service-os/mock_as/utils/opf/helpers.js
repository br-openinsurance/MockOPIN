import { getDynamicScope } from '../oidc.js';

function containsScopes(scopes, scope) {
  if (Array.isArray(scopes)) {
    return scopes.includes(scope);
  } else {
    return scopes.split(' ').includes(scope);
  }
}

export function isPayment(scopes) {
  return getDynamicScope(scopes, 'consent:urn:raidiambank:payment-consent:') && containsScopes(scopes, 'payments');
}

export function isRecurringPayment(scopes) {
  return (
    getDynamicScope(scopes, 'recurring-consent:urn:raidiambank:payment-consent:') &&
    containsScopes(scopes, 'recurring-payments')
  );
}

export function isEnrollment(scopes) {
  return getDynamicScope(scopes, 'enrollment:urn:raidiambank:enrollment:') && containsScopes(scopes, 'nrp-consents');
}

export function getConsentId(scopes) {
  return getDynamicScope(scopes, 'consent:urn:raidiambank:consent:')?.replace('consent:', '');
}

export function getPaymentConsentId(scopes) {
  return getDynamicScope(scopes, 'consent:urn:raidiambank:payment-consent:')?.replace('consent:', '');
}

export function getRecurringConsentId(scopes) {
  return getDynamicScope(scopes, 'recurring-consent:urn:raidiambank:payment-consent:')?.replace(
    'recurring-consent:',
    '',
  );
}

export function getEnrollmentId(scopes) {
  return getDynamicScope(scopes, 'enrollment:urn:raidiambank:enrollment:')?.replace('enrollment:', '');
}
