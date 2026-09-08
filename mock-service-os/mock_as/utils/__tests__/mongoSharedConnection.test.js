const { connectMock } = vi.hoisted(() => ({
  connectMock: vi.fn(),
}));

vi.mock('mongodb', () => ({
  MongoClient: { connect: connectMock },
}));

import MongoAdapter from '../mongodb.js';
import Account from '../account.js';
import { reset as resetMongoConnection } from '../mongoConnection.js';

// mongodb.js (the oidc-provider adapter store) and account.js (the account store) each used
// to call MongoClient.connect() independently, doubling the connection/auth/topology-discovery
// work every cold start. These tests prove they now share a single underlying connection.
describe('shared Mongo connection between the oidc-provider adapter and the account store', () => {
  const dbHandles = {};

  function fakeDb(name) {
    if (!dbHandles[name]) {
      dbHandles[name] = { name, collection: vi.fn(() => ({ createIndexes: vi.fn().mockResolvedValue() })) };
    }
    return dbHandles[name];
  }

  const fakeClient = { db: vi.fn(fakeDb) };

  beforeEach(() => {
    vi.clearAllMocks();
    resetMongoConnection();
    Object.keys(dbHandles).forEach((key) => delete dbHandles[key]);
    connectMock.mockResolvedValue(fakeClient);
  });

  it('opens the underlying MongoClient only once for both stores', async () => {
    await MongoAdapter.connect('openid-server');
    await Account.initialiseAdapter('accounts');

    expect(connectMock).toHaveBeenCalledTimes(1);
  });

  it('still resolves each store against its own database namespace on the shared client', async () => {
    await MongoAdapter.connect('openid-server');
    await Account.initialiseAdapter('accounts');

    expect(fakeClient.db).toHaveBeenCalledWith('openid-server');
    expect(fakeClient.db).toHaveBeenCalledWith('accounts');

    // Constructing an adapter for a given collection kicks off index creation against the
    // openid-server db handle specifically, not the accounts one.
    new MongoAdapter('session'); // eslint-disable-line no-new
    expect(dbHandles['openid-server'].collection).toHaveBeenCalledWith('session');

    Account.coll();
    expect(dbHandles.accounts.collection).toHaveBeenCalledWith('accounts');
  });

  it('shares one connection even when both stores connect concurrently', async () => {
    await Promise.all([MongoAdapter.connect('openid-server'), Account.initialiseAdapter('accounts')]);

    expect(connectMock).toHaveBeenCalledTimes(1);
  });
});
