ALTER TABLE pension_plan_contract_movement_benefits
    ADD COLUMN benefit_amount VARCHAR,
    ADD COLUMN benefit_unit_type VARCHAR,
    ADD COLUMN benefit_unit_type_others VARCHAR,
    ADD COLUMN benefit_unit_code VARCHAR,
    ADD COLUMN benefit_unit_description VARCHAR,
    ADD COLUMN benefit_currency VARCHAR,
    ADD COLUMN benefit_payment_date DATE;

ALTER TABLE pension_plan_contract_movement_benefits_aud
    ADD COLUMN benefit_amount VARCHAR,
    ADD COLUMN benefit_unit_type VARCHAR,
    ADD COLUMN benefit_unit_type_others VARCHAR,
    ADD COLUMN benefit_unit_code VARCHAR,
    ADD COLUMN benefit_unit_description VARCHAR,
    ADD COLUMN benefit_currency VARCHAR,
    ADD COLUMN benefit_payment_date DATE;

ALTER TABLE pension_plan_contract_movement_contributions
    ADD COLUMN contribution_amount VARCHAR,
    ADD COLUMN contribution_unit_type VARCHAR,
    ADD COLUMN contribution_unit_type_others VARCHAR,
    ADD COLUMN contribution_unit_code VARCHAR,
    ADD COLUMN contribution_unit_description VARCHAR,
    ADD COLUMN contribution_currency VARCHAR,
    ADD COLUMN charged_in_advance_amount VARCHAR,
    ADD COLUMN charged_in_advance_unit_type VARCHAR,
    ADD COLUMN charged_in_advance_unit_type_others VARCHAR,
    ADD COLUMN charged_in_advance_unit_code VARCHAR,
    ADD COLUMN charged_in_advance_unit_description VARCHAR,
    ADD COLUMN charged_in_advance_currency VARCHAR,
    ADD COLUMN periodicity VARCHAR,
    ADD COLUMN contribution_expiration_date DATE,
    ADD COLUMN contribution_payment_date DATE;

ALTER TABLE pension_plan_contract_movement_contributions_aud
    ADD COLUMN contribution_amount VARCHAR,
    ADD COLUMN contribution_unit_type VARCHAR,
    ADD COLUMN contribution_unit_type_others VARCHAR,
    ADD COLUMN contribution_unit_code VARCHAR,
    ADD COLUMN contribution_unit_description VARCHAR,
    ADD COLUMN contribution_currency VARCHAR,
    ADD COLUMN charged_in_advance_amount VARCHAR,
    ADD COLUMN charged_in_advance_unit_type VARCHAR,
    ADD COLUMN charged_in_advance_unit_type_others VARCHAR,
    ADD COLUMN charged_in_advance_unit_code VARCHAR,
    ADD COLUMN charged_in_advance_unit_description VARCHAR,
    ADD COLUMN charged_in_advance_currency VARCHAR,
    ADD COLUMN periodicity VARCHAR,
    ADD COLUMN contribution_expiration_date DATE,
    ADD COLUMN contribution_payment_date DATE;
