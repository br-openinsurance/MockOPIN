ALTER TABLE patrimonial_policies
ADD COLUMN document_type VARCHAR,
ADD COLUMN issuance_type VARCHAR,
ADD COLUMN issuance_date DATE,
ADD COLUMN term_start_date DATE,
ADD COLUMN term_end_date DATE,
ADD COLUMN proposal_id VARCHAR,
ADD COLUMN max_lmg_amount VARCHAR,
ADD COLUMN max_lmg_unit_type VARCHAR,
ADD COLUMN max_lmg_unit_type_others VARCHAR,
ADD COLUMN max_lmg_unit_code VARCHAR,
ADD COLUMN max_lmg_unit_description VARCHAR;

ALTER TABLE patrimonial_policies_aud
ADD COLUMN document_type VARCHAR,
ADD COLUMN issuance_type VARCHAR,
ADD COLUMN issuance_date DATE,
ADD COLUMN term_start_date DATE,
ADD COLUMN term_end_date DATE,
ADD COLUMN proposal_id VARCHAR,
ADD COLUMN max_lmg_amount VARCHAR,
ADD COLUMN max_lmg_unit_type VARCHAR,
ADD COLUMN max_lmg_unit_type_others VARCHAR,
ADD COLUMN max_lmg_unit_code VARCHAR,
ADD COLUMN max_lmg_unit_description VARCHAR;
