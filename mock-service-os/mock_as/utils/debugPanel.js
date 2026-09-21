import * as querystring from 'node:querystring';
import { inspect } from 'node:util';
import isEmpty from 'lodash/isEmpty.js';

// HTML-escapes values before building the debug panel string, since views/_layout.ejs outputs it unescaped via <%- (INCM-149).
const keys = new Set();

const escapeHtml = (str) =>
  String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');

export const debug = (obj) =>
  querystring.stringify(
    Object.entries(obj).reduce((acc, [key, value]) => {
      keys.add(key);
      if (isEmpty(value)) return acc;
      acc[key] = inspect(value, { depth: null });
      return acc;
    }, {}),
    '<br/>',
    ': ',
    {
      encodeURIComponent(value) {
        return keys.has(value) ? `<strong>${value}</strong>` : escapeHtml(value);
      },
    },
  );
