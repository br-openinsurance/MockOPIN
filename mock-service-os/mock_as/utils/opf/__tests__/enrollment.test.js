vi.mock('../adapter.js', () => ({
  bankAdapter: { updateEnrollment: vi.fn() },
}));

vi.mock('../helpers.js', () => ({
  getEnrollmentId: vi.fn(),
}));

import { updateEnrollment } from '../enrollment.js';
import { bankAdapter } from '../adapter.js';
import { getEnrollmentId } from '../helpers.js';
import {
  AN_ENROLLMENT_ID,
  AN_EXPIRATION,
  A_DAILY_LIMIT,
  A_TRANSACTION_LIMIT,
  capturedPayload,
  params,
  request,
} from './enrollmentTestData.js';

describe('updateEnrollment', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    getEnrollmentId.mockReturnValue(AN_ENROLLMENT_ID);
    bankAdapter.updateEnrollment.mockResolvedValue({ data: { enrollmentId: AN_ENROLLMENT_ID } });
  });

  describe('enrollment targeting', () => {
    it('updates the enrollment identified by the scope', async () => {
      // Given a request scoped to a known enrollment
      let req = request();

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the adapter is called for that enrollment
      expect(bankAdapter.updateEnrollment).toHaveBeenCalledWith(AN_ENROLLMENT_ID, expect.any(Object));
    });
  });

  describe('enrollment status', () => {
    it('always places the enrollment in AWAITING_ENROLLMENT status', async () => {
      // Given any valid enrollment update request
      let req = request();

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the payload status is AWAITING_ENROLLMENT
      expect(bankAdapter.updateEnrollment).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({ status: 'AWAITING_ENROLLMENT' }),
      );
    });
  });

  describe('optimised journey', () => {
    it('marks the journey as optimised when the user opted in', async () => {
      // Given the user opted into the optimised consent journey
      let req = request({ optimisedConsent: 'on' });

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the payload marks the journey as optimised
      expect(bankAdapter.updateEnrollment).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({ optimisedJourney: true }),
      );
    });

    it('marks the journey as non-optimised when the user did not opt in', async () => {
      // Given the request has no optimised consent flag
      let req = request();

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the payload marks the journey as non-optimised
      expect(bankAdapter.updateEnrollment).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({ optimisedJourney: false }),
      );
    });
  });

  describe('expiration date', () => {
    it('includes a formatted expiration timestamp when one is provided', async () => {
      // Given a request with an expiration date/time
      let req = request({ expirationDateTime: AN_EXPIRATION });

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the payload carries it as an ISO UTC timestamp
      expect(bankAdapter.updateEnrollment).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({ expirationDateTime: new Date(`${AN_EXPIRATION}Z`).toISOString() }),
      );
    });

    it('sends null for expiration when none is provided', async () => {
      // Given a request with no expiration date/time
      let req = request();

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the payload expiration is null
      expect(capturedPayload(bankAdapter.updateEnrollment)).toHaveProperty('expirationDateTime', null);
    });

    it('sends null for expiration when an empty value is submitted', async () => {
      // Given a request with an empty expiration date/time
      let expirationDateTime = '';

      // When updating the enrollment
      await updateEnrollment(params(), request({ expirationDateTime }));

      // Then the payload expiration is null
      expect(capturedPayload(bankAdapter.updateEnrollment)).toHaveProperty('expirationDateTime', null);
    });

    describe('UTC round-trip', () => {
      const originalTZ = process.env.TZ;

      afterEach(() => {
        process.env.TZ = originalTZ;
      });

      it('does not drift when the server runs in a non-UTC timezone', async () => {
        // Given the server runs in a non-UTC timezone
        process.env.TZ = 'America/Sao_Paulo';

        // And the expiration is stored as UTC
        const storedUtc = '2027-06-30T15:00:00.000Z';

        // And the form displays it the same way interaction.ejs does
        const displayedInForm = new Date(storedUtc).toISOString().slice(0, 16);

        // When resubmitting the displayed value unchanged
        await updateEnrollment(params(), request({ expirationDateTime: displayedInForm }));

        // Then the stored expiration is still the original UTC instant
        expect(capturedPayload(bankAdapter.updateEnrollment)).toHaveProperty('expirationDateTime', storedUtc);
      });
    });
  });

  describe('transaction limit', () => {
    it('includes the transaction limit when one is provided', async () => {
      // Given a request with a transaction limit
      let req = request({ transactionLimit: A_TRANSACTION_LIMIT });

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the payload carries it unchanged, for the bank to store as-is
      expect(bankAdapter.updateEnrollment).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({ transactionLimit: A_TRANSACTION_LIMIT }),
      );
    });

    it('sends null for the transaction limit when none is provided', async () => {
      // Given a request with no transaction limit
      let req = request();

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the payload transaction limit is null, so the bank leaves it unset
      expect(capturedPayload(bankAdapter.updateEnrollment)).toHaveProperty('transactionLimit', null);
    });

    it('sends null for the transaction limit when an empty value is submitted', async () => {
      // Given a request where the limit input was left blank
      let transactionLimit = '';

      // When updating the enrollment
      await updateEnrollment(params(), request({ transactionLimit }));

      // Then the payload transaction limit is null
      expect(capturedPayload(bankAdapter.updateEnrollment)).toHaveProperty('transactionLimit', null);
    });

    it('does not cap a limit above the previously hardcoded 500.00', async () => {
      // Given a limit well above the old hardcoded default
      let transactionLimit = '75000.00';

      // When updating the enrollment
      await updateEnrollment(params(), request({ transactionLimit }));

      // Then it is passed through untouched
      expect(capturedPayload(bankAdapter.updateEnrollment)).toHaveProperty('transactionLimit', '75000.00');
    });
  });

  describe('daily limit', () => {
    it('includes the daily limit when one is provided', async () => {
      // Given a request with a daily limit
      let req = request({ dailyLimit: A_DAILY_LIMIT });

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the payload carries it unchanged, for the bank to store as-is
      expect(bankAdapter.updateEnrollment).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({ dailyLimit: A_DAILY_LIMIT }),
      );
    });

    it('sends null for the daily limit when none is provided', async () => {
      // Given a request with no daily limit
      let req = request();

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the payload daily limit is null, so the bank leaves it unset
      expect(capturedPayload(bankAdapter.updateEnrollment)).toHaveProperty('dailyLimit', null);
    });

    it('sends null for the daily limit when an empty value is submitted', async () => {
      // Given a request where the limit input was left blank
      let dailyLimit = '';

      // When updating the enrollment
      await updateEnrollment(params(), request({ dailyLimit }));

      // Then the payload daily limit is null
      expect(capturedPayload(bankAdapter.updateEnrollment)).toHaveProperty('dailyLimit', null);
    });

    it('carries the daily limit independently of the transaction limit', async () => {
      // Given a request setting both limits to different amounts
      let req = request({ transactionLimit: A_TRANSACTION_LIMIT, dailyLimit: A_DAILY_LIMIT });

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then each limit reaches the bank under its own key, neither overwriting the other
      expect(bankAdapter.updateEnrollment).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({
          transactionLimit: A_TRANSACTION_LIMIT,
          dailyLimit: A_DAILY_LIMIT,
        }),
      );
    });
  });

  describe('enrollment name', () => {
    it('includes the name when provided', async () => {
      // Given a request with an enrollment name
      let req = request({ name: 'Home Banking' });

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the payload carries that name
      expect(bankAdapter.updateEnrollment).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({ name: 'Home Banking' }),
      );
    });

    it('sends null for name when not provided', async () => {
      // Given a request with no name
      let req = request();

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the payload name is null
      expect(capturedPayload(bankAdapter.updateEnrollment)).toHaveProperty('name', null);
    });

    it('sends null for name when an empty value is submitted', async () => {
      // Given a request with an empty name
      let req = request({ name: '' });

      // When updating the enrollment
      await updateEnrollment(params(), req);

      // Then the payload name is null
      expect(capturedPayload(bankAdapter.updateEnrollment)).toHaveProperty('name', null);
    });
  });

  describe('adapter response', () => {
    it('returns the adapter response to the caller', async () => {
      // Given the adapter resolves with a specific response
      const adapterResponse = { data: { enrollmentId: AN_ENROLLMENT_ID, status: 'AWAITING_ENROLLMENT' } };

      bankAdapter.updateEnrollment.mockResolvedValue(adapterResponse);

      // When updating the enrollment
      const result = await updateEnrollment(params(), request());

      // Then that same response is returned
      expect(result).toBe(adapterResponse);
    });
  });
});
