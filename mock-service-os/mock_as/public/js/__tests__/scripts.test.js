import { readFileSync } from 'fs';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';
import { JSDOM, VirtualConsole } from 'jsdom';
import { renderInteractionHtml } from '../../../views/__tests__/renderInteraction.js';

const __dirname = dirname(fileURLToPath(import.meta.url));
const scriptsSource = readFileSync(join(__dirname, '..', 'scripts.js'), 'utf8');

function renderInteractionPage(consentOverrides = {}, layoutOverrides = {}) {
  const { html, layout } = renderInteractionHtml(consentOverrides, layoutOverrides);

  const dom = new JSDOM(`<!DOCTYPE html><html><body>${html}</body></html>`, {
    runScripts: 'dangerously',
    virtualConsole: new VirtualConsole(),
  });

  dom.window.layout = layout;
  dom.window.eval(scriptsSource);
  dom.window.document.dispatchEvent(new dom.window.Event('DOMContentLoaded'));

  return dom.window.document;
}

function turnConsentToggle(document, isOn) {
  const consentToggle = document.getElementById('consent-toggle');
  consentToggle.checked = isOn;
  consentToggle.dispatchEvent(new document.defaultView.Event('change'));
}

describe('consent screen — Confirm Consent button reacts to the consent toggle', () => {
  it('starts out impossible to click, since nothing has been agreed to yet', () => {
    // Given a customer has just opened the consent screen, having agreed to nothing yet
    const document = renderInteractionPage();
    const continueButton = document.getElementById('continue-button');

    // Then the Confirm Consent button cannot be clicked
    expect(continueButton.disabled).toBe(true);
  });

  it("becomes clickable, in the product's own colour, once the customer agrees to consent", () => {
    // Given a customer is on the consent screen for Mock Insurer, which has its own colour scheme
    const document = renderInteractionPage({}, { brand: 'opin', buttonColor: '#0E580D' });
    const continueButton = document.getElementById('continue-button');

    // When the customer switches on "I consent to the above data"
    turnConsentToggle(document, true);

    // Then the Confirm Consent button becomes clickable, shown in that product's own colour
    expect(continueButton.disabled).toBe(false);
    expect(continueButton.style.getPropertyValue('--btn-color')).toBe('#0E580D');
  });

  it('goes back to looking exactly as it did before, if the customer changes their mind', () => {
    // Given a customer is on the consent screen, before agreeing to anything
    const document = renderInteractionPage();
    const continueButton = document.getElementById('continue-button');
    const colorBeforeToggling = continueButton.style.getPropertyValue('--btn-color');

    // When the customer agrees to consent, and then changes their mind and switches it back off
    turnConsentToggle(document, true);
    turnConsentToggle(document, false);

    // Then the button cannot be clicked, and its colour is unaffected by toggling back and forth
    expect(continueButton.disabled).toBe(true);
    expect(continueButton.style.getPropertyValue('--btn-color')).toBe(colorBeforeToggling);
  });
});

describe('consent screen — pressing Enter on a field does not abort the interaction', () => {
  it("stops Enter from triggering the form's default submit button, from a text field", () => {
    // Given a customer is typing into the optional Enrollment Name field
    const document = renderInteractionPage();
    const nameInput = document.getElementById('enrollment-name');

    // When the customer presses Enter
    const enterKeyPress = new document.defaultView.KeyboardEvent('keydown', {
      key: 'Enter',
      cancelable: true,
      bubbles: true,
    });
    nameInput.dispatchEvent(enterKeyPress);

    // Then the browser is told not to treat that as a form submission
    expect(enterKeyPress.defaultPrevented).toBe(true);
  });

  it("stops Enter from triggering the form's default submit button, from a checkbox", () => {
    // Given a customer has tabbed to the "I consent to the above data" checkbox
    const document = renderInteractionPage();
    const consentToggle = document.getElementById('consent-toggle');

    // When the customer presses Enter while it has focus
    const enterKeyPress = new document.defaultView.KeyboardEvent('keydown', {
      key: 'Enter',
      cancelable: true,
      bubbles: true,
    });
    consentToggle.dispatchEvent(enterKeyPress);

    // Then the browser is told not to treat that as a form submission
    expect(enterKeyPress.defaultPrevented).toBe(true);
  });

  it('still lets a customer activate a button by pressing Enter once it has focus', () => {
    // Given a customer has tabbed to the Cancel button itself
    const document = renderInteractionPage();
    const cancelButton = document.getElementById('cancel-button');

    // When the customer presses Enter to activate it
    const enterKeyPress = new document.defaultView.KeyboardEvent('keydown', {
      key: 'Enter',
      cancelable: true,
      bubbles: true,
    });
    cancelButton.dispatchEvent(enterKeyPress);

    // Then that Enter press is left alone, so the button still activates normally
    expect(enterKeyPress.defaultPrevented).toBe(false);
  });

  it('leaves Enter alone while a customer is confirming characters typed via an IME', () => {
    // Given a customer is composing text (e.g. Japanese, Chinese, Korean input) in the Enrollment Name field
    const document = renderInteractionPage();
    const nameInput = document.getElementById('enrollment-name');

    // When they press Enter to confirm the character, not to submit the form
    const composingEnterKeyPress = new document.defaultView.KeyboardEvent('keydown', {
      key: 'Enter',
      isComposing: true,
      cancelable: true,
      bubbles: true,
    });
    nameInput.dispatchEvent(composingEnterKeyPress);

    // Then that Enter press is left alone
    expect(composingEnterKeyPress.defaultPrevented).toBe(false);
  });

  it('leaves normal typing untouched', () => {
    // Given a customer is typing into the optional Enrollment Name field
    const document = renderInteractionPage();
    const nameInput = document.getElementById('enrollment-name');

    // When the customer types an ordinary character
    const letterKeyPress = new document.defaultView.KeyboardEvent('keydown', {
      key: 'a',
      cancelable: true,
      bubbles: true,
    });
    nameInput.dispatchEvent(letterKeyPress);

    // Then nothing is blocked
    expect(letterKeyPress.defaultPrevented).toBe(false);
  });
});

describe('consent screen — a customer cannot submit the form twice', () => {
  it('shows a loading spinner and locks both buttons the moment Confirm Consent is clicked', () => {
    // Given a customer has agreed to consent, so Confirm Consent is clickable
    const document = renderInteractionPage();
    turnConsentToggle(document, true);
    const continueButton = document.getElementById('continue-button');
    const cancelButton = document.getElementById('cancel-button');

    // When the customer clicks Confirm Consent
    continueButton.click();

    // Then the button shows it is working, and neither button can be clicked again
    expect(document.getElementById('continue-label').textContent).toBe('Submitting...');
    expect(document.getElementById('continue-spinner').classList.contains('hidden')).toBe(false);
    expect(continueButton.disabled).toBe(true);
    expect(cancelButton.disabled).toBe(true);
  });

  it('locks Confirm Consent too, if the customer clicks Cancel instead', () => {
    // Given a customer has agreed to consent, so Confirm Consent is clickable
    const document = renderInteractionPage();
    turnConsentToggle(document, true);
    const continueButton = document.getElementById('continue-button');
    const cancelButton = document.getElementById('cancel-button');

    // When the customer clicks Cancel instead of confirming
    cancelButton.click();

    // Then Cancel shows it is working, and Confirm Consent is locked out too
    expect(cancelButton.textContent).toBe('Cancelling...');
    expect(cancelButton.disabled).toBe(true);
    expect(continueButton.disabled).toBe(true);
  });

  it('does not let a stray extra submission (e.g. pressing Enter) go through after the first click', () => {
    // Given a customer has already clicked Confirm Consent once
    const document = renderInteractionPage();
    turnConsentToggle(document, true);
    document.getElementById('continue-button').click();

    // When something triggers another attempt to submit the same form
    const secondAttempt = new document.defaultView.Event('submit', { cancelable: true });
    document.getElementById('consentForm').dispatchEvent(secondAttempt);

    // Then that second attempt is blocked
    expect(secondAttempt.defaultPrevented).toBe(true);
  });
});

describe('consent screen — Confirm Consent button uses the configured product colour', () => {
  it('applies the configured product colour to the enabled Confirm Consent button', () => {
    // Given a customer is on the consent screen, which has a product colour configured
    const document = renderInteractionPage({}, { brand: 'opin', buttonColor: '#2F3A4F' });
    const continueButton = document.getElementById('continue-button');

    // When the customer switches on "I consent to the above data"
    turnConsentToggle(document, true);

    // Then the button becomes clickable in the configured product colour
    expect(continueButton.disabled).toBe(false);
    expect(continueButton.style.getPropertyValue('--btn-color')).toBe('#2F3A4F');
  });

  it('goes back to looking exactly as it did before, if the customer changes their mind', () => {
    // Given a customer is on the consent screen, before agreeing to anything
    const document = renderInteractionPage({}, { brand: 'opin', buttonColor: '#2F3A4F' });
    const continueButton = document.getElementById('continue-button');
    const colorBeforeToggling = continueButton.style.getPropertyValue('--btn-color');

    // When the customer agrees to consent, and then changes their mind and switches it back off
    turnConsentToggle(document, true);
    turnConsentToggle(document, false);

    // Then the button cannot be clicked, and its colour is unaffected by toggling back and forth
    expect(continueButton.disabled).toBe(true);
    expect(continueButton.style.getPropertyValue('--btn-color')).toBe(colorBeforeToggling);
  });
});
