import ejs from 'ejs';
import { JSDOM } from 'jsdom';
import { readFileSync } from 'fs';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';

const __dirname = dirname(fileURLToPath(import.meta.url));
const templatePath = join(__dirname, '..', 'interaction.ejs');
const template = readFileSync(templatePath, 'utf8');

export function renderInteractionHtml(consentOverrides = {}, layoutOverrides = {}, locals = {}) {
  const layout = { buttonColor: '#1F3233', ...layoutOverrides };
  const data = {
    uid: 'test-uid',
    layout,
    ...locals,
    details: {
      consent: {
        enrollmentId: 'urn:raidiambank:enrollment:enroll-abc-123',
        status: 'AWAITING_ENROLLMENT',
        expirationDateTime: null,
        ...consentOverrides,
      },
      scopes: [],
    },
  };

  return { html: ejs.render(template, data, { filename: templatePath }), layout };
}

export function renderEnrollmentInteraction(consentOverrides = {}, layoutOverrides = {}) {
  const { html } = renderInteractionHtml(consentOverrides, layoutOverrides);
  return new JSDOM(html).window.document;
}

export function renderConsentInteraction(consentOverrides = {}, layoutOverrides = {}, locals = {}) {
  const { html } = renderInteractionHtml(
    {
      enrollmentId: undefined,
      consentId: 'urn:raidiambank:consent:abc-123',
      status: 'AWAITING_AUTHORISATION',
      ...consentOverrides,
    },
    layoutOverrides,
    locals,
  );
  return new JSDOM(html).window.document;
}
