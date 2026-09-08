document.addEventListener('DOMContentLoaded', function () {
  const consentToggle = document.getElementById('consent-toggle');
  const continueButton = document.getElementById('continue-button');
  const consentForm = document.getElementById('consentForm');
  const cancelButton = document.getElementById('cancel-button');
  const continueSpinner = document.getElementById('continue-spinner');
  const continueLabel = document.getElementById('continue-label');

  if (consentToggle && continueButton) {
    const color = layout.buttonColor;
    const hoverColor = '#1e2a3b';
    continueButton.style.setProperty('--btn-color', color);
    continueButton.style.setProperty('--btn-hover-color', hoverColor);

    consentToggle.addEventListener('change', function () {
      continueButton.disabled = !this.checked;
    });
  }

  if (consentForm) {
    consentForm.addEventListener('keydown', (event) => {
      if (
        event.key === 'Enter' &&
        !event.isComposing &&
        event.target.matches('input[type="text"], input[type="checkbox"]')
      ) {
        event.preventDefault();
      }
    });
  }

  if (consentForm && cancelButton && continueButton) {
    let hasSubmitted = false;
    let clickedButton = null;

    continueButton.addEventListener('click', () => {
      clickedButton = continueButton;
    });

    cancelButton.addEventListener('click', () => {
      clickedButton = cancelButton;
    });

    consentForm.addEventListener('submit', (event) => {
      if (hasSubmitted) {
        event.preventDefault();
        return;
      }
      hasSubmitted = true;

      continueButton.disabled = true;
      cancelButton.disabled = true;

      if (clickedButton === continueButton) {
        if (continueSpinner) continueSpinner.classList.remove('hidden');
        if (continueLabel) continueLabel.textContent = 'Submitting...';
      } else if (clickedButton === cancelButton) {
        cancelButton.textContent = 'Cancelling...';
      }
    });
  }

  // Expand or collapse scopes
  document.querySelectorAll('.scope-toggle').forEach((toggle) => {
    toggle.addEventListener('click', function () {
      const targetId = this.getAttribute('data-target');
      const target = document.getElementById(targetId);
      const icon = this.querySelector('.toggle-icon');

      if (target.classList.contains('hidden')) {
        target.classList.remove('hidden');
        icon.classList.add('rotate-90');
      } else {
        target.classList.add('hidden');
        icon.classList.remove('rotate-90');
      }
    });
  });

  document.querySelectorAll("input[type='checkbox'].sr-only").forEach((checkbox) => {
    const li = checkbox.closest('li');
    if (!li) return;

    const hiddenInputs = li.querySelectorAll("input[type='hidden']");

    if (!checkbox.checked) {
      hiddenInputs.forEach((hi) => (hi.disabled = true));
    }

    checkbox.addEventListener('change', () => {
      if (checkbox.checked) {
        hiddenInputs.forEach((hi) => {
          hi.disabled = false;
        });
      } else {
        hiddenInputs.forEach((hi) => {
          hi.disabled = true;
        });
      }
    });
  });
});
