const { connectMock } = vi.hoisted(() => ({
  connectMock: vi.fn(),
}));

vi.mock('mongodb', () => ({
  MongoClient: { connect: connectMock },
}));

import { connect, reset, MONGO_CONNECTION_OPTIONS } from '../mongoConnection.js';

describe('mongoConnection', () => {
  const fakeClient = { db: vi.fn() };

  beforeEach(() => {
    vi.clearAllMocks();
    reset();
    connectMock.mockResolvedValue(fakeClient);
  });

  it('opens a single MongoClient connection for concurrent callers', async () => {
    const [a, b] = await Promise.all([
      connect('mongodb://example/?replicaSet=rs0'),
      connect('mongodb://example/?replicaSet=rs0'),
    ]);

    expect(connectMock).toHaveBeenCalledTimes(1);
    expect(a).toBe(fakeClient);
    expect(b).toBe(fakeClient);
  });

  it('reuses the cached client on subsequent sequential calls', async () => {
    await connect('mongodb://example/?replicaSet=rs0');
    await connect('mongodb://example/?replicaSet=rs0');

    expect(connectMock).toHaveBeenCalledTimes(1);
  });

  it('reconnects after reset()', async () => {
    await connect('mongodb://example/?replicaSet=rs0');
    reset();
    await connect('mongodb://example/?replicaSet=rs0');

    expect(connectMock).toHaveBeenCalledTimes(2);
  });

  it('defaults to process.env.MONGODB_URI when no uri argument is passed', async () => {
    process.env.MONGODB_URI = 'mongodb://from-env/?replicaSet=rs0';

    await connect();

    expect(connectMock).toHaveBeenCalledWith('mongodb://from-env/?replicaSet=rs0', MONGO_CONNECTION_OPTIONS);

    delete process.env.MONGODB_URI;
  });

  it('passes Lambda-tuned options that fail fast instead of the driver 30s defaults', async () => {
    await connect('mongodb://example/?replicaSet=rs0');

    expect(connectMock).toHaveBeenCalledWith('mongodb://example/?replicaSet=rs0', MONGO_CONNECTION_OPTIONS);
    expect(MONGO_CONNECTION_OPTIONS.serverSelectionTimeoutMS).toBeLessThan(30000);
    expect(MONGO_CONNECTION_OPTIONS.connectTimeoutMS).toBeLessThan(30000);
  });

  it('propagates connection errors to every waiting caller', async () => {
    connectMock.mockReset();
    connectMock.mockRejectedValue(new Error('connection refused'));

    await expect(connect('mongodb://example/?replicaSet=rs0')).rejects.toThrow('connection refused');
  });

  it('allows a retry on the next call after a failed connection attempt', async () => {
    connectMock.mockReset();
    connectMock.mockRejectedValueOnce(new Error('connection refused'));
    connectMock.mockResolvedValueOnce(fakeClient);

    await expect(connect('mongodb://example/?replicaSet=rs0')).rejects.toThrow('connection refused');

    const client = await connect('mongodb://example/?replicaSet=rs0');

    expect(client).toBe(fakeClient);
    expect(connectMock).toHaveBeenCalledTimes(2);
  });
});
