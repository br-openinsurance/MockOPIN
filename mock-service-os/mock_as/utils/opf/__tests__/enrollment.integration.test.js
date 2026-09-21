/**
 * Integration tests for the enrollment update flow.
 *
 * These tests use the real scope-parsing logic (helpers.js + oidc.js) and only
 * mock the external bank adapter. The goal is to verify that the full chain —
 * OIDC scope string → enrollment ID extraction → update payload → adapter call —
 * works correctly as an integrated unit.
 */
vi.mock('../adapter.js', () => ({
  bankAdapter: { updateEnrollment: vi.fn() },
}));

import { updateEnrollment } from '../enrollment.js';
import { bankAdapter } from '../adapter.js';
import {
  AN_ENROLLMENT_ID,
  AN_EXPIRATION,
  A_DAILY_LIMIT,
  A_TRANSACTION_LIMIT,
  ENROLLMENT_SCOPE_STRING,
  ENROLLMENT_SCOPE_ARRAY,
  params,
  request,
} from './enrollmentTestData.js';

describe('enrollment update flow — scope parsing to adapter (integration)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    bankAdapter.updateEnrollment.mockResolvedValue({
      data: { enrollmentId: AN_ENROLLMENT_ID, status: 'AWAITING_ENROLLMENT' },
    });
  });

  describe('scope formats', () => {
    it('resolves the enrollment ID from a space-separated scope string', async () => {
      // Given a scope provided as a space-separated string
      let scope = params(ENROLLMENT_SCOPE_STRING);

      // When updating the enrollment
      await updateEnrollment(scope, request());

      // Then the adapter is called for the enrollment resolved from that scope
      expect(bankAdapter.updateEnrollment).toHaveBeenCalledWith(AN_ENROLLMENT_ID, expect.any(Object));
    });

    it('resolves the enrollment ID from a scope array', async () => {
      // Given a scope provided as an array
      let scope = params(ENROLLMENT_SCOPE_ARRAY);

      // When updating the enrollment
      await updateEnrollment(scope, request());

      // Then the adapter is called for the enrollment resolved from that scope
      expect(bankAdapter.updateEnrollment).toHaveBeenCalledWith(AN_ENROLLMENT_ID, expect.any(Object));
    });
  });

  describe('complete update payload', () => {
    it('sends the full payload to the adapter when all optional fields are provided', async () => {
      // Given a request with all optional fields provided
      let req = request({
        optimisedConsent: 'on',
        expirationDateTime: AN_EXPIRATION,
        name: 'Home Banking Enrollment',
        transactionLimit: A_TRANSACTION_LIMIT,
        dailyLimit: A_DAILY_LIMIT,
      });

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the payload carries every field through to the adapter
      expect(bankAdapter.updateEnrollment).toHaveBeenCalledWith(AN_ENROLLMENT_ID, {
        status: 'AWAITING_ENROLLMENT',
        optimisedJourney: true,
        expirationDateTime: new Date(`${AN_EXPIRATION}Z`).toISOString(),
        name: 'Home Banking Enrollment',
        transactionLimit: A_TRANSACTION_LIMIT,
        dailyLimit: A_DAILY_LIMIT,
      });
    });

    it('sends null for every optional field when none are provided', async () => {
      // Given a request with no optional fields
      let req = request();

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the payload nulls out each optional field
      expect(bankAdapter.updateEnrollment).toHaveBeenCalledWith(AN_ENROLLMENT_ID, {
        status: 'AWAITING_ENROLLMENT',
        optimisedJourney: false,
        expirationDateTime: null,
        name: null,
        transactionLimit: null,
        dailyLimit: null,
      });
    });
  });
});
