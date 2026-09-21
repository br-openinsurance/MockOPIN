export const AN_ENROLLMENT_ID = 'urn:raidiambank:enrollment:enroll-abc-123';
export const AN_EXPIRATION = '2027-06-30T12:00:00';
// Two decimal places, as the bank's transactionLimit pattern requires.
export const A_TRANSACTION_LIMIT = '1500.00';
// Deliberately different from the transaction limit, so tests catch the two being swapped.
export const A_DAILY_LIMIT = '3000.00';

export const ENROLLMENT_SCOPE_STRING = 'openid nrp-consents enrollment:urn:raidiambank:enrollment:enroll-abc-123';

export const ENROLLMENT_SCOPE_ARRAY = [
  'openid',
  'nrp-consents',
  'enrollment:urn:raidiambank:enrollment:enroll-abc-123',
];

export function params(scope = ENROLLMENT_SCOPE_STRING) {
  return { scope };
}

export function request(body = {}) {
  return { body };
}

export function capturedPayload(mockFn) {
  return mockFn.mock.calls[0][1];
}
