import { renderEnrollmentInteraction } from './renderInteraction.js';

describe('interaction.ejs — enrollment expiration input', () => {
  it('rejects a non-date string via native constraint validation', () => {
    // Given the enrollment expiration input rendered by the view
    const document = renderEnrollmentInteraction();
    const expirationInput = document.getElementById('enrollment-expiration');

    // When a user types a value that is not a real date/time
    expirationInput.value = 'abcdefghijklmnop';

    // Then the browser reports it as invalid
    expect(expirationInput.checkValidity()).toBe(false);
    expect(expirationInput.validity.patternMismatch).toBe(true);
  });

  it('accepts a well-formatted date/time string', () => {
    // Given the enrollment expiration input rendered by the view
    const document = renderEnrollmentInteraction();
    const expirationInput = document.getElementById('enrollment-expiration');

    // When a user types a value matching the expected format
    expirationInput.value = '2027-06-30T15:00';

    // Then the browser reports it as valid
    expect(expirationInput.checkValidity()).toBe(true);
  });

  it('accepts an empty value, since expiration is optional', () => {
    // Given the enrollment expiration input rendered by the view
    const document = renderEnrollmentInteraction();
    const expirationInput = document.getElementById('enrollment-expiration');

    // When the field is left empty
    expirationInput.value = '';

    // Then the browser does not enforce the pattern on an optional empty field
    expect(expirationInput.checkValidity()).toBe(true);
  });

  it('lets the user cancel even when the expiration field is invalid', () => {
    // Given the expiration field currently holds an invalid value
    const document = renderEnrollmentInteraction();
    const expirationInput = document.getElementById('enrollment-expiration');
    expirationInput.value = 'abcdefghijklmnop';

    // When checking whether the Cancel button skips constraint validation
    const cancelButton = document.getElementById('cancel-button');

    // Then it is marked to bypass validation, so cancelling is never blocked
    expect(cancelButton.formNoValidate).toBe(true);
  });

  it('still validates the expiration field when confirming consent', () => {
    // Given the enrollment interaction form
    const document = renderEnrollmentInteraction();

    // When checking whether the Confirm Consent button skips constraint validation
    const continueButton = document.getElementById('continue-button');

    // Then it does not bypass validation, so invalid input still blocks confirmation
    expect(continueButton.formNoValidate).toBe(false);
  });
});

describe('interaction.ejs — enrollment name input', () => {
  it('loads a previously persisted name from the response field, enrollmentName', () => {
    // Given the enrollment response carries a persisted name under enrollmentName
    const document = renderEnrollmentInteraction({ enrollmentName: 'Home Banking' });

    // When the view renders the name field
    const nameInput = document.getElementById('enrollment-name');

    // Then the input is pre-filled with that value
    expect(nameInput.value).toBe('Home Banking');
  });

  it('does not fall back to the unrelated update-schema field, name', () => {
    // Given only the update-schema field, name, is present — not the response field
    const document = renderEnrollmentInteraction({ name: 'Should Not Be Used' });

    // When the view renders the name field
    const nameInput = document.getElementById('enrollment-name');

    // Then it stays empty, since name is not a valid source for display
    expect(nameInput.value).toBe('');
  });

  it('renders an empty value when no name has been persisted', () => {
    // Given the enrollment has no persisted name
    const document = renderEnrollmentInteraction();

    // When the view renders the name field
    const nameInput = document.getElementById('enrollment-name');

    // Then the input is empty
    expect(nameInput.value).toBe('');
  });

  it('still submits the field under the update-schema key, name', () => {
    // Given the enrollment response carries a persisted name under enrollmentName
    const document = renderEnrollmentInteraction({ enrollmentName: 'Home Banking' });

    // When checking how the field is wired for submission
    const nameInput = document.getElementById('enrollment-name');

    // Then it still posts as "name", matching the PUT /enrollments update schema
    expect(nameInput.name).toBe('name');
  });
});

describe('interaction.ejs — enrollment transaction limit input', () => {
  it('rejects a whole number, since the bank requires two decimal places', () => {
    // Given the transaction limit input rendered by the view
    const document = renderEnrollmentInteraction();
    const limitInput = document.getElementById('enrollment-transaction-limit');

    // When a user types an amount without cents
    limitInput.value = '450';

    // Then the browser reports it as invalid, before the bank rejects it for
    // failing its own ^\d{1,16}\.\d{2}$ constraint
    expect(limitInput.checkValidity()).toBe(false);
    expect(limitInput.validity.patternMismatch).toBe(true);
  });

  it('rejects a non-numeric value', () => {
    // Given the transaction limit input rendered by the view
    const document = renderEnrollmentInteraction();
    const limitInput = document.getElementById('enrollment-transaction-limit');

    // When a user types something that is not an amount
    limitInput.value = 'abcdefghijklmnop';

    // Then the browser reports it as invalid
    expect(limitInput.checkValidity()).toBe(false);
    expect(limitInput.validity.patternMismatch).toBe(true);
  });

  it('accepts a well-formatted amount', () => {
    // Given the transaction limit input rendered by the view
    const document = renderEnrollmentInteraction();
    const limitInput = document.getElementById('enrollment-transaction-limit');

    // When a user types an amount with two decimal places
    limitInput.value = '450.00';

    // Then the browser reports it as valid
    expect(limitInput.checkValidity()).toBe(true);
  });

  it('accepts an empty value, since the transaction limit is optional', () => {
    // Given the transaction limit input rendered by the view
    const document = renderEnrollmentInteraction();
    const limitInput = document.getElementById('enrollment-transaction-limit');

    // When the field is left empty
    limitInput.value = '';

    // Then the browser does not enforce the pattern on an optional empty field
    expect(limitInput.checkValidity()).toBe(true);
  });

  it('does not enforce the 500 BRL ceiling, which depends on businessEntity', () => {
    // Given the transaction limit input rendered by the view
    const document = renderEnrollmentInteraction();
    const limitInput = document.getElementById('enrollment-transaction-limit');

    // When a user types an amount above 500
    limitInput.value = '75000.00';

    // Then the form still accepts it: the ceiling only applies when the enrollment
    // carries no businessEntity, which this page cannot know
    expect(limitInput.checkValidity()).toBe(true);
  });

  it('loads a previously persisted transaction limit', () => {
    // Given the enrollment response carries a persisted limit
    const document = renderEnrollmentInteraction({ transactionLimit: '450.00' });

    // When the view renders the transaction limit field
    const limitInput = document.getElementById('enrollment-transaction-limit');

    // Then the input is pre-filled with that value
    expect(limitInput.value).toBe('450.00');
  });

  it('renders an empty value when no transaction limit has been persisted', () => {
    // Given the enrollment has no persisted limit
    const document = renderEnrollmentInteraction();

    // When the view renders the transaction limit field
    const limitInput = document.getElementById('enrollment-transaction-limit');

    // Then the input is empty
    expect(limitInput.value).toBe('');
  });

  it('submits the field under the update-schema key, transactionLimit', () => {
    // Given the enrollment interaction form
    const document = renderEnrollmentInteraction();

    // When checking how the field is wired for submission
    const limitInput = document.getElementById('enrollment-transaction-limit');

    // Then it posts as "transactionLimit", matching the PUT /enrollments update schema
    expect(limitInput.name).toBe('transactionLimit');
  });
});

describe('interaction.ejs — enrollment daily limit input', () => {
  it('rejects a whole number, since the bank requires two decimal places', () => {
    // Given the daily limit input rendered by the view
    const document = renderEnrollmentInteraction();
    const limitInput = document.getElementById('enrollment-daily-limit');

    // When a user types an amount without cents
    limitInput.value = '1500';

    // Then the browser reports it as invalid, before the bank rejects it for
    // failing its own ^\d{1,16}\.\d{2}$ constraint
    expect(limitInput.checkValidity()).toBe(false);
    expect(limitInput.validity.patternMismatch).toBe(true);
  });

  it('rejects a non-numeric value', () => {
    // Given the daily limit input rendered by the view
    const document = renderEnrollmentInteraction();
    const limitInput = document.getElementById('enrollment-daily-limit');

    // When a user types something that is not an amount
    limitInput.value = 'abcdefghijklmnop';

    // Then the browser reports it as invalid
    expect(limitInput.checkValidity()).toBe(false);
    expect(limitInput.validity.patternMismatch).toBe(true);
  });

  it('accepts a well-formatted amount', () => {
    // Given the daily limit input rendered by the view
    const document = renderEnrollmentInteraction();
    const limitInput = document.getElementById('enrollment-daily-limit');

    // When a user types an amount with two decimal places
    limitInput.value = '1500.00';

    // Then the browser reports it as valid
    expect(limitInput.checkValidity()).toBe(true);
  });

  it('accepts an empty value, since the daily limit is optional', () => {
    // Given the daily limit input rendered by the view
    const document = renderEnrollmentInteraction();
    const limitInput = document.getElementById('enrollment-daily-limit');

    // When the field is left empty
    limitInput.value = '';

    // Then the browser does not enforce the pattern on an optional empty field
    expect(limitInput.checkValidity()).toBe(true);
  });

  it('loads a previously persisted daily limit', () => {
    // Given the enrollment response carries a persisted limit
    const document = renderEnrollmentInteraction({ dailyLimit: '1500.00' });

    // When the view renders the daily limit field
    const limitInput = document.getElementById('enrollment-daily-limit');

    // Then the input is pre-filled with that value
    expect(limitInput.value).toBe('1500.00');
  });

  it('renders an empty value when no daily limit has been persisted', () => {
    // Given the enrollment has no persisted limit
    const document = renderEnrollmentInteraction();

    // When the view renders the daily limit field
    const limitInput = document.getElementById('enrollment-daily-limit');

    // Then the input is empty
    expect(limitInput.value).toBe('');
  });

  it('submits the field under the update-schema key, dailyLimit', () => {
    // Given the enrollment interaction form
    const document = renderEnrollmentInteraction();

    // When checking how the field is wired for submission
    const limitInput = document.getElementById('enrollment-daily-limit');

    // Then it posts as "dailyLimit", matching the PUT /enrollments update schema
    expect(limitInput.name).toBe('dailyLimit');
  });

  it('renders as a field separate from the transaction limit', () => {
    // Given an enrollment carrying different values for each limit
    const document = renderEnrollmentInteraction({ transactionLimit: '450.00', dailyLimit: '1500.00' });

    // When the view renders both limit fields
    const dailyInput = document.getElementById('enrollment-daily-limit');
    const transactionInput = document.getElementById('enrollment-transaction-limit');

    // Then each shows its own persisted value, so the two are never conflated
    expect(dailyInput.value).toBe('1500.00');
    expect(transactionInput.value).toBe('450.00');
  });
});
