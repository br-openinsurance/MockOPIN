import { parseSkippedUrlencodedBody } from '../albUrlencodedBodyCompat.js';

describe('parseSkippedUrlencodedBody', () => {
  it('parses req.body when body-parser left it as a raw Buffer (the serverless-express regression this fixes)', () => {
    const req = { body: Buffer.from('login=ralph.bragg%40gmail.com&password=P%40ssword01', 'utf8') };
    const next = vi.fn();

    parseSkippedUrlencodedBody(req, {}, next);

    expect(req.body).toEqual({ login: 'ralph.bragg@gmail.com', password: 'P@ssword01' });
    expect(next).toHaveBeenCalledTimes(1);
  });

  it('leaves req.body untouched when body-parser already parsed it (normal local dev flow)', () => {
    const parsed = { login: 'ralph.bragg@gmail.com', password: 'P@ssword01' };
    const req = { body: parsed };
    const next = vi.fn();

    parseSkippedUrlencodedBody(req, {}, next);

    expect(req.body).toBe(parsed);
    expect(next).toHaveBeenCalledTimes(1);
  });

  it('leaves req.body untouched when there is no body', () => {
    const req = { body: undefined };
    const next = vi.fn();

    parseSkippedUrlencodedBody(req, {}, next);

    expect(req.body).toBeUndefined();
    expect(next).toHaveBeenCalledTimes(1);
  });
});
