import { Base64 } from 'js-base64';
import { decodeAlbFormUrlEncodedBody, JSON_BODY_PATHS } from '../albEvent.js';

function albEvent({ path, body, isBase64Encoded = true, headers = {} }) {
  return {
    requestContext: { elb: { targetGroupArn: 'arn:aws:elasticloadbalancing:us-east-1:0:targetgroup/local/0' } },
    path,
    headers,
    isBase64Encoded,
    body: isBase64Encoded ? Base64.encode(body) : body,
  };
}

describe('decodeAlbFormUrlEncodedBody', () => {
  it('decodes the /interaction/:uid/login body (the regression this fixes)', () => {
    const event = albEvent({
      path: '/interaction/abc123/login',
      body: 'login=ralph.bragg%40gmail.com&password=secret',
    });

    decodeAlbFormUrlEncodedBody(event);

    expect(event.body).toBe('login=ralph.bragg%40gmail.com&password=secret');
    expect(event.isBase64Encoded).toBe(false);
  });

  it('still decodes /token requests (no regression from the previous hardcoded path check)', () => {
    const event = albEvent({ path: '/token', body: 'grant_type=client_credentials' });

    decodeAlbFormUrlEncodedBody(event);

    expect(event.body).toBe('grant_type=client_credentials');
    expect(event.isBase64Encoded).toBe(false);
  });

  it('decodes other form-post routes (CIBA login/confirm, revocation, introspection, PAR, backchannel)', () => {
    const paths = [
      '/ciba/authorize/abc/login',
      '/ciba/authorize/abc/confirm',
      '/interaction/abc123/confirm',
      '/token/revocation',
      '/token/introspection',
      '/request',
      '/backchannel',
    ];

    paths.forEach((path) => {
      const event = albEvent({ path, body: 'foo=bar' });

      decodeAlbFormUrlEncodedBody(event);

      expect(event.body).toBe('foo=bar');
      expect(event.isBase64Encoded).toBe(false);
    });
  });

  it('decodes even when the content-type header is missing entirely (observed on real ALB events)', () => {
    const event = albEvent({ path: '/interaction/abc123/login', body: 'login=foo&password=bar', headers: undefined });

    decodeAlbFormUrlEncodedBody(event);

    expect(event.body).toBe('login=foo&password=bar');
    expect(event.isBase64Encoded).toBe(false);
  });

  it('leaves JSON body paths untouched (/reg, /ciba/automated)', () => {
    JSON_BODY_PATHS.forEach((path) => {
      const event = albEvent({ path, body: '{"client_name":"test"}', headers: { 'content-type': 'application/json' } });
      const originalBody = event.body;

      decodeAlbFormUrlEncodedBody(event);

      expect(event.body).toBe(originalBody);
      expect(event.isBase64Encoded).toBe(true);
    });
  });

  it('leaves non-ALB events untouched', () => {
    const event = { path: '/token', headers: {}, body: 'foo=bar', isBase64Encoded: true };

    decodeAlbFormUrlEncodedBody(event);

    expect(event.body).toBe('foo=bar');
    expect(event.isBase64Encoded).toBe(true);
  });

  it('is a no-op when there is no body', () => {
    const event = {
      requestContext: { elb: {} },
      path: '/.well-known/openid-configuration',
      headers: {},
    };

    expect(() => decodeAlbFormUrlEncodedBody(event)).not.toThrow();
    expect(event.body).toBeUndefined();
  });
});
