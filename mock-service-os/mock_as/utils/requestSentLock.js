import NodeCache from 'node-cache';

const DEFAULT_TTL_SECONDS = 15;

export function createRequestSentLock(ttlSeconds = DEFAULT_TTL_SECONDS) {
  const claimed = new NodeCache({ stdTTL: ttlSeconds });

  function claim(key) {
    if (claimed.has(key)) {
      return false;
    }
    claimed.set(key, true);
    return true;
  }

  function isSent(req, res, next) {
    if (claim(req.params.uid)) {
      return next();
    }
    res.status(409).end();
  }

  return { claim, isSent };
}

export const requestSentLock = createRequestSentLock();
