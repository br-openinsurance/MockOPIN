import { getDynamicScopeId } from "../oidc.js";

export function getConsentId(scopes) {
    return getDynamicScopeId(scopes, 'consent:')
}