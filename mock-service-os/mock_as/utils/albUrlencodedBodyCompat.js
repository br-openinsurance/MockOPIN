import * as querystring from 'node:querystring';

export function parseSkippedUrlencodedBody(req, res, next) {
  if (Buffer.isBuffer(req.body)) {
    req.body = querystring.parse(req.body.toString('utf8'));
  } else if (req.body === undefined) {
    req.body = {};
  }
  next();
}
