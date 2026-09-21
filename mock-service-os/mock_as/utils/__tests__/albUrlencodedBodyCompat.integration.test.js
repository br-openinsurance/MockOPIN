import express from 'express';
import { urlencoded } from 'express';
import serverlessExpress from '@vendia/serverless-express';
import { parseSkippedUrlencodedBody } from '../albUrlencodedBodyCompat.js';

function buildApp(bodyMiddleware) {
  const app = express();
  app.post('/interaction/:uid/login', bodyMiddleware, (req, res) => {
    res.json({ body: req.body });
  });
  return app;
}

function albEvent(body) {
  return {
    requestContext: { elb: { targetGroupArn: 'arn:aws:elasticloadbalancing:us-east-1:0:targetgroup/local/0' } },
    httpMethod: 'POST',
    path: '/interaction/abc123/login',
    headers: { 'content-type': 'application/x-www-form-urlencoded', 'content-length': String(Buffer.byteLength(body)) },
    isBase64Encoded: false,
    body,
  };
}

describe('express.urlencoded() behind @vendia/serverless-express', () => {
  it('demonstrates the regression: body-parser leaves req.body as an unparsed Buffer', async () => {
    const app = buildApp(urlencoded({ extended: false }));
    const server = serverlessExpress({ app });

    const result = await server(albEvent('login=ralph.bragg%40gmail.com&password=secret'), {});

    const { body } = JSON.parse(result.body);
    expect(body).not.toEqual({ login: 'ralph.bragg@gmail.com', password: 'secret' });
  });

  it('parseSkippedUrlencodedBody fixes it', async () => {
    const app = buildApp([urlencoded({ extended: false }), parseSkippedUrlencodedBody]);
    const server = serverlessExpress({ app });

    const result = await server(albEvent('login=ralph.bragg%40gmail.com&password=secret'), {});

    const { body } = JSON.parse(result.body);
    expect(body).toEqual({ login: 'ralph.bragg@gmail.com', password: 'secret' });
  });
});
