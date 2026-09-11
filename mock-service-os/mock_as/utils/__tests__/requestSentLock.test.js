import { createRequestSentLock, requestSentLock } from '../requestSentLock.js';

describe('requestSentLock', () => {
  it('lets the first request through for a given key', () => {
    // Given a fresh lock, and a key nobody has claimed yet
    const lock = createRequestSentLock();

    // When the first request claims that key
    const claimed = lock.claim('interaction-1');

    // Then the claim succeeds
    expect(claimed).toBe(true);
  });

  it('blocks a second request for a key that was already claimed', () => {
    // Given a lock where a key has already been claimed once
    const lock = createRequestSentLock();
    lock.claim('interaction-1');

    // When a second request tries to claim that same key
    const claimed = lock.claim('interaction-1');

    // Then the claim is refused
    expect(claimed).toBe(false);
  });

  it('treats different keys independently', () => {
    // Given a lock where one key has already been claimed
    const lock = createRequestSentLock();
    lock.claim('interaction-1');

    // When a different key is claimed
    const claimed = lock.claim('interaction-2');

    // Then that unrelated key succeeds, since it belongs to a different API call
    expect(claimed).toBe(true);
  });

  it('forgets a key once its hold time has passed, so it can be claimed again', () => {
    // Given a lock that only holds a key for a very short time
    const lock = createRequestSentLock(0.05);
    lock.claim('interaction-1');

    // When enough time passes for that hold to expire
    return new Promise((resolve) => {
      setTimeout(() => {
        // Then the same key can be claimed again
        expect(lock.claim('interaction-1')).toBe(true);
        resolve();
      }, 100);
    });
  });

  it('exposes one shared, ready-to-use lock for every API to share', () => {
    // Given the shared lock every API endpoint imports
    // Then it behaves like any other lock: first claim succeeds, second is refused
    expect(requestSentLock.claim('shared-key-1')).toBe(true);
    expect(requestSentLock.claim('shared-key-1')).toBe(false);
  });

  describe('isSent', () => {
    function fakeResponse() {
      return {
        statusCode: null,
        ended: false,
        status(code) {
          this.statusCode = code;
          return this;
        },
        end() {
          this.ended = true;
        },
      };
    }

    it('lets a first-time request through to the API handler', () => {
      // Given an API endpoint guarded by a fresh lock
      const lock = createRequestSentLock();
      const req = { params: { uid: 'interaction-1' } };
      const res = fakeResponse();
      let calledNext = false;

      // When a request arrives for the first time
      lock.isSent(req, res, () => {
        calledNext = true;
      });

      // Then it is allowed through to the API's own handler
      expect(calledNext).toBe(true);
      expect(res.ended).toBe(false);
    });

    it('immediately rejects a repeated request', () => {
      // Given an API endpoint that has already received one request for this interaction
      const lock = createRequestSentLock();
      const req = { params: { uid: 'interaction-1' } };
      lock.isSent(req, fakeResponse(), () => {});

      // When another request arrives for that same interaction
      const res = fakeResponse();
      let calledNext = false;

      lock.isSent(req, res, () => {
        calledNext = true;
      });

      // Then it never reaches the API handler, and gets an immediate Conflict response
      expect(calledNext).toBe(false);
      expect(res.ended).toBe(true);
      expect(res.statusCode).toBe(409);
    });
  });
});
