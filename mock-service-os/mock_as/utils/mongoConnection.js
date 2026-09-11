import { MongoClient } from 'mongodb'; // eslint-disable-line import/no-unresolved

// A Lambda execution environment handles invocations essentially one at a time, so a large
// pool just holds idle sockets open. serverSelectionTimeoutMS/connectTimeoutMS are lowered
// from the driver's 30s defaults so a connect() call fails fast instead of hanging when the
// replica set is under connection contention (e.g. many execution environments cold-starting
// at once during a traffic burst).
export const MONGO_CONNECTION_OPTIONS = {
  maxPoolSize: 5,
  minPoolSize: 0,
  serverSelectionTimeoutMS: 5000,
  connectTimeoutMS: 5000,
  socketTimeoutMS: 10000,
};

let clientPromise;

// The oidc-provider adapter (mongodb.js) and the account store (account.js) both talk to the
// same replica set. Sharing a single MongoClient here means only one connection/auth/
// topology-discovery round trip happens per cold start instead of one per caller, and any
// caller that awaits connect() before it resolves shares the same in-flight promise rather
// than starting a second connection attempt.
export function connect(uri = process.env.MONGODB_URI) {
  if (!clientPromise) {
    // If the connection attempt fails, drop the cached promise so the next call retries
    // instead of every future call rejecting for the rest of this execution environment's life.
    clientPromise = MongoClient.connect(uri, MONGO_CONNECTION_OPTIONS).catch((err) => {
      clientPromise = undefined;
      throw err;
    });
  }
  return clientPromise;
}

// Test-only escape hatch: clears the cached client so the next connect() call reconnects.
export function reset() {
  clientPromise = undefined;
}
