document.addEventListener("DOMContentLoaded", function () {
  const consentToggle = document.getElementById("consent-toggle");
  const continueButton = document.getElementById("continue-button");

  if (consentToggle) {
    consentToggle.addEventListener("change", function () {
      if (this.checked) {
        continueButton.disabled = false;
        continueButton.classList.remove(
          "bg-gray-300",
          "text-gray-500",
          "cursor-not-allowed"
        );
        continueButton.classList.add(
          `bg-[${layout.buttonColor}]`,
          "text-white",
          "hover:bg-[#1e2a3b]",
          "cursor-pointer"
        );
      } else {
        continueButton.disabled = true;
        continueButton.classList.remove(
          `bg-[${layout.buttonColor}]`,
          "text-white",
          "hover:bg-[#1e2a3b]",
          "cursor-pointer"
        );
        continueButton.classList.add(
          "bg-gray-300",
          "text-gray-500",
          "cursor-not-allowed"
        );
      }
    });
  }

  // Expand or collapse scopes
  document.querySelectorAll(".scope-toggle").forEach((toggle) => {
    toggle.addEventListener("click", function () {
      const targetId = this.getAttribute("data-target");
      const target = document.getElementById(targetId);
      const icon = this.querySelector(".toggle-icon");

      if (target.classList.contains("hidden")) {
        target.classList.remove("hidden");
        icon.classList.add("rotate-90");
      } else {
        target.classList.add("hidden");
        icon.classList.remove("rotate-90");
      }
    });
  });

  document
    .querySelectorAll("input[type='checkbox'].sr-only")
    .forEach((checkbox) => {
      const li = checkbox.closest("li");
      if (!li) return;

      const hiddenInputs = li.querySelectorAll("input[type='hidden']");

      if (!checkbox.checked) {
        hiddenInputs.forEach((hi) => (hi.disabled = true));
      }

      checkbox.addEventListener("change", () => {
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
