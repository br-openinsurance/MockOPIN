import { Base64 } from 'js-base64';

export const JSON_BODY_PATHS = ['/reg', '/ciba/automated'];

export function decodeAlbFormUrlEncodedBody(event) {
  if (!event?.requestContext?.elb || !event.body) {
    return event;
  }

  if (JSON_BODY_PATHS.includes(event.path)) {
    return event;
  }

  event.body = Base64.decode(event.body);
  event.isBase64Encoded = false;
  return event;
}
