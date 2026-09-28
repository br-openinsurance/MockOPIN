import { debug } from '../debugPanel.js';

describe('debugPanel.js — debug() HTML escaping (INCM-149)', () => {
  it('escapes markup injected via an OIDC request parameter, e.g. client_id', () => {
    const html = debug({ client_id: '"><img src=x onerror=alert(1)>' });

    expect(html).not.toContain('<img src=x onerror=alert(1)>');
    expect(html).toContain('&lt;img src=x onerror=alert(1)&gt;');
  });

  it('escapes a script tag injected via login_hint', () => {
    const html = debug({ login_hint: '<script>alert(document.cookie)</script>' });

    expect(html).not.toContain('<script>alert(document.cookie)</script>');
    expect(html).toContain('&lt;script&gt;alert(document.cookie)&lt;/script&gt;');
  });

  it('escapes ampersands and quotes so attributes cannot be broken out of', () => {
    const html = debug({ redirect_uri: 'https://evil.example/?a=1&b="x" onload=\'y\'' });

    expect(html).toContain('&amp;b=&quot;x&quot; onload=&#39;y&#39;');
  });

  it('still bolds known keys for readability, unaffected by escaping', () => {
    const html = debug({ client_id: 'safe-value' });

    expect(html).toContain('<strong>client_id</strong>');
    expect(html).toContain('safe-value');
  });

  it('omits empty values, matching existing debug-panel behaviour', () => {
    const html = debug({ client_id: undefined, scope: '' });

    expect(html).toBe('');
  });
});
