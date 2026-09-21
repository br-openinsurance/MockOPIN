import * as querystring from 'node:querystring';

export function parseSkippedUrlencodedBody(req, res, next) {
  if (Buffer.isBuffer(req.body)) {
    req.body = querystring.parse(req.body.toString('utf8'));
  }
  next();
}
