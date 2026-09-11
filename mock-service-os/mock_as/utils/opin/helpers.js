import { getDynamicScope } from '../oidc.js';

export function getConsentId(scopes) {
  return getDynamicScope(scopes, 'consent:urn:raidiaminsurance:')?.replace('consent:', '');
}
