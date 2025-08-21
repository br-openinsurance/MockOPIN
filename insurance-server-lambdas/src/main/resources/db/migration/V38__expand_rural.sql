ALTER TABLE rural_policies
    RENAME COLUMN policy_id TO rural_policy_id;

ALTER TABLE rural_policies
    ADD COLUMN product_name VARCHAR,
    ADD COLUMN document_type VARCHAR,
    ADD COLUMN policy_id VARCHAR,
    ADD COLUMN susep_process_number VARCHAR,
    ADD COLUMN group_certificate_id VARCHAR,
    ADD COLUMN issuance_type VARCHAR,
    ADD COLUMN issuance_date DATE,
    ADD COLUMN term_start_date DATE,
    ADD COLUMN term_end_date DATE,
    ADD COLUMN lead_insurer_code VARCHAR,
    ADD COLUMN lead_insurer_policy_id VARCHAR,
    ADD COLUMN max_lmg_amount VARCHAR,
    ADD COLUMN max_lmg_unit_type VARCHAR,
    ADD COLUMN max_lmg_unit_type_others VARCHAR,
    ADD COLUMN max_lmg_unit_code VARCHAR,
    ADD COLUMN max_lmg_unit_description VARCHAR,
    ADD COLUMN proposal_id VARCHAR,
    ADD COLUMN coinsurance_retained_percentage VARCHAR;

ALTER TABLE rural_policies_aud
    RENAME COLUMN policy_id TO rural_policy_id;

ALTER TABLE rural_policies_aud
    ADD COLUMN product_name VARCHAR,
    ADD COLUMN document_type VARCHAR,
    ADD COLUMN policy_id VARCHAR,
    ADD COLUMN susep_process_number VARCHAR,
    ADD COLUMN group_certificate_id VARCHAR,
    ADD COLUMN issuance_type VARCHAR,
    ADD COLUMN issuance_date DATE,
    ADD COLUMN term_start_date DATE,
    ADD COLUMN term_end_date DATE,
    ADD COLUMN lead_insurer_code VARCHAR,
    ADD COLUMN lead_insurer_policy_id VARCHAR,
    ADD COLUMN max_lmg_amount VARCHAR,
    ADD COLUMN max_lmg_unit_type VARCHAR,
    ADD COLUMN max_lmg_unit_type_others VARCHAR,
    ADD COLUMN max_lmg_unit_code VARCHAR,
    ADD COLUMN max_lmg_unit_description VARCHAR,
    ADD COLUMN proposal_id VARCHAR,
    ADD COLUMN coinsurance_retained_percentage VARCHAR;

CREATE TABLE rural_policy_insured_objects (
    rural_policy_insured_object_id                              UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    rural_policy_id                                             UUID,
    identification                                              VARCHAR,
    type                                                        VARCHAR,
    type_additional_info                                        VARCHAR,
    description                                                 VARCHAR,
    amount                                                      VARCHAR,
    unit_type                                                   VARCHAR,
    unit_type_others                                            VARCHAR,
    unit_code                                                   VARCHAR,
    unit_description                                            VARCHAR,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    FOREIGN KEY (rural_policy_id) REFERENCES rural_policies (rural_policy_id) ON DELETE CASCADE
);

CREATE TABLE rural_policy_insured_objects_aud (
    rural_policy_insured_object_id                              UUID,
    rural_policy_id                                             UUID,
    identification                                              VARCHAR,
    type                                                        VARCHAR,
    type_additional_info                                        VARCHAR,
    description                                                 VARCHAR,
    amount                                                      VARCHAR,
    unit_type                                                   VARCHAR,
    unit_type_others                                            VARCHAR,
    unit_code                                                   VARCHAR,
    unit_description                                            VARCHAR,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (rural_policy_insured_object_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE rural_policy_insured_object_coverages (
    rural_policy_insured_object_coverage_id                     UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    rural_policy_insured_object_id                              UUID,
    branch                                                      VARCHAR,
    code                                                        VARCHAR,
    description                                                 VARCHAR,
    internal_code                                               VARCHAR,
    susep_process_number                                        VARCHAR,
    lmi_amount                                                  VARCHAR,
    lmi_unit_type                                               VARCHAR,
    lmi_unit_type_others                                        VARCHAR,
    lmi_unit_code                                               VARCHAR,
    lmi_unit_description                                        VARCHAR,
    lmi_sublimit                                                BOOLEAN,
    term_start_date                                             DATE,
    term_end_date                                               DATE,
    main_coverage                                               BOOLEAN,
    feature                                                     VARCHAR,
    type                                                        VARCHAR,
    grace_period                                                INTEGER,
    grace_periodicity                                           VARCHAR,
    grace_period_counting_method                                VARCHAR,
    grace_period_start_date                                     DATE,
    grace_period_end_date                                       DATE,
    premium_periodicity                                         VARCHAR,
    premium_periodicity_others                                  VARCHAR,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    FOREIGN KEY (rural_policy_insured_object_id) REFERENCES rural_policy_insured_objects (rural_policy_insured_object_id) ON DELETE CASCADE
);

CREATE TABLE rural_policy_insured_object_coverages_aud (
    rural_policy_insured_object_coverage_id                     UUID,
    rural_policy_insured_object_id                              UUID,
    branch                                                      VARCHAR,
    code                                                        VARCHAR,
    description                                                 VARCHAR,
    internal_code                                               VARCHAR,
    susep_process_number                                        VARCHAR,
    lmi_amount                                                  VARCHAR,
    lmi_unit_type                                               VARCHAR,
    lmi_unit_type_others                                        VARCHAR,
    lmi_unit_code                                               VARCHAR,
    lmi_unit_description                                        VARCHAR,
    lmi_sublimit                                                BOOLEAN,
    term_start_date                                             DATE,
    term_end_date                                               DATE,
    main_coverage                                               BOOLEAN,
    feature                                                     VARCHAR,
    type                                                        VARCHAR,
    grace_period                                                INTEGER,
    grace_periodicity                                           VARCHAR,
    grace_period_counting_method                                VARCHAR,
    grace_period_start_date                                     DATE,
    grace_period_end_date                                       DATE,
    premium_periodicity                                         VARCHAR,
    premium_periodicity_others                                  VARCHAR,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (rural_policy_insured_object_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE rural_policy_coverages (
    rural_policy_coverage_id                                    UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    rural_policy_id                                             UUID,
    branch                                                      VARCHAR,
    code                                                        VARCHAR,
    description                                                 VARCHAR,
    deductible_id                                               UUID,
    pos_id                                                      UUID,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    FOREIGN KEY (rural_policy_id) REFERENCES rural_policies (rural_policy_id) ON DELETE CASCADE
);

CREATE TABLE rural_policy_coverages_aud (
    rural_policy_coverage_id                                    UUID,
    rural_policy_id                                             UUID,
    branch                                                      VARCHAR,
    code                                                        VARCHAR,
    description                                                 VARCHAR,
    deductible_id                                               UUID,
    pos_id                                                      UUID,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (rural_policy_coverage_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE rural_policy_branch_insured_objects (
    rural_policy_branch_insured_object_id                       UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    rural_policy_id                                             UUID,
    identification                                              VARCHAR,
    fesr_participant                                            BOOLEAN,
    amount                                                      VARCHAR,
    unit_type                                                   VARCHAR,
    unit_type_others                                            VARCHAR,
    unit_code                                                   VARCHAR,
    unit_description                                            VARCHAR,
    subvention_type                                             VARCHAR,
    safe_area                                                   VARCHAR,
    unit_measure                                                VARCHAR,
    unit_measure_others                                         VARCHAR,
    culture_code                                                VARCHAR,
    flock_code                                                  VARCHAR,
    flock_code_others                                           VARCHAR,
    forest_code                                                 VARCHAR,
    forest_code_others                                          VARCHAR,
    survey_date                                                 DATE,
    survey_address                                              VARCHAR,
    survey_country_sub_division                                 VARCHAR,
    survey_postcode                                             VARCHAR,
    survey_country_code                                         VARCHAR,
    surveyor_id_type                                            VARCHAR,
    surveyor_id_others                                          VARCHAR,
    surveyor_id                                                 VARCHAR,
    surveyor_name                                               VARCHAR,
    model_type                                                  VARCHAR,
    model_type_others                                           VARCHAR,
    assets_covered                                              BOOLEAN,
    covered_animal_destination                                  VARCHAR,
    animal_type                                                 VARCHAR,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    FOREIGN KEY (rural_policy_id) REFERENCES rural_policies (rural_policy_id) ON DELETE CASCADE
);

CREATE TABLE rural_policy_branch_insured_objects_aud (
    rural_policy_branch_insured_object_id                       UUID,
    rural_policy_id                                             UUID,
    identification                                              VARCHAR,
    fesr_participant                                            VARCHAR,
    amount                                                      VARCHAR,
    unit_type                                                   VARCHAR,
    unit_type_others                                            VARCHAR,
    unit_code                                                   VARCHAR,
    unit_description                                            VARCHAR,
    subvention_type                                             VARCHAR,
    safe_area                                                   VARCHAR,
    unit_measure                                                VARCHAR,
    unit_measure_others                                         VARCHAR,
    culture_code                                                VARCHAR,
    flock_code                                                  VARCHAR,
    flock_code_others                                           VARCHAR,
    forest_code                                                 VARCHAR,
    forest_code_others                                          VARCHAR,
    survey_date                                                 DATE,
    survey_address                                              VARCHAR,
    survey_country_sub_division                                 VARCHAR,
    survey_postcode                                             VARCHAR,
    survey_country_code                                         VARCHAR,
    surveyor_id_type                                            VARCHAR,
    surveyor_id_others                                          VARCHAR,
    surveyor_id                                                 VARCHAR,
    surveyor_name                                               VARCHAR,
    model_type                                                  VARCHAR,
    model_type_others                                           VARCHAR,
    assets_covered                                              BOOLEAN,
    covered_animal_destination                                  VARCHAR,
    animal_type                                                 VARCHAR,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (rural_policy_branch_insured_object_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

ALTER TABLE rural_policy_claims
    DROP CONSTRAINT rural_policy_claims_policy_id_fkey;

ALTER TABLE rural_policy_claims
    RENAME COLUMN claim_id TO rural_policy_claim_id;

ALTER TABLE rural_policy_claims
    RENAME COLUMN policy_id TO rural_policy_id;

ALTER TABLE rural_policy_claims
    ADD CONSTRAINT rural_policy_claims_rural_policy_id_fkey FOREIGN KEY (rural_policy_id) REFERENCES rural_policies (rural_policy_id) ON DELETE CASCADE;

ALTER TABLE rural_policy_claims
    ADD COLUMN documentation_delivery_date DATE,
    ADD COLUMN status VARCHAR,
    ADD COLUMN status_alteration_date DATE,
    ADD COLUMN ocurrance_date DATE,
    ADD COLUMN warning_date DATE,
    ADD COLUMN third_party_date DATE,
    ADD COLUMN amount VARCHAR,
    ADD COLUMN unit_type VARCHAR,
    ADD COLUMN unit_type_others VARCHAR,
    ADD COLUMN unit_code VARCHAR,
    ADD COLUMN unit_description VARCHAR,
    ADD COLUMN denial_justification VARCHAR,
    ADD COLUMN denial_justification_description VARCHAR,
    ADD COLUMN survey_date DATE,
    ADD COLUMN survey_address VARCHAR,
    ADD COLUMN survey_country_sub_division VARCHAR,
    ADD COLUMN survey_postcode VARCHAR,
    ADD COLUMN survey_country_code VARCHAR;

ALTER TABLE rural_policy_claims_aud
    RENAME COLUMN claim_id TO rural_policy_claim_id;

ALTER TABLE rural_policy_claims_aud
    RENAME COLUMN policy_id TO rural_policy_id;

ALTER TABLE rural_policy_claims_aud
    ADD COLUMN documentation_delivery_date DATE,
    ADD COLUMN status VARCHAR,
    ADD COLUMN status_alteration_date DATE,
    ADD COLUMN ocurrance_date DATE,
    ADD COLUMN warning_date DATE,
    ADD COLUMN third_party_date DATE,
    ADD COLUMN amount VARCHAR,
    ADD COLUMN unit_type VARCHAR,
    ADD COLUMN unit_type_others VARCHAR,
    ADD COLUMN unit_code VARCHAR,
    ADD COLUMN unit_description VARCHAR,
    ADD COLUMN denial_justification VARCHAR,
    ADD COLUMN denial_justification_description VARCHAR,
    ADD COLUMN survey_date DATE,
    ADD COLUMN survey_address VARCHAR,
    ADD COLUMN survey_country_sub_division VARCHAR,
    ADD COLUMN survey_postcode VARCHAR,
    ADD COLUMN survey_country_code VARCHAR;

CREATE TABLE rural_policy_claim_coverages (
    rural_policy_claim_coverage_id                              UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    rural_policy_claim_id                                       UUID,
    insured_object_id                                           VARCHAR,
    branch                                                      VARCHAR,
    code                                                        VARCHAR,
    description                                                 VARCHAR,
    warning_date                                                DATE,
    third_party_claim_date                                      DATE,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    FOREIGN KEY (rural_policy_claim_id) REFERENCES rural_policy_claims (rural_policy_claim_id) ON DELETE CASCADE
);

CREATE TABLE rural_policy_claim_coverages_aud (
    rural_policy_claim_coverage_id                              UUID,
    rural_policy_claim_id                                       UUID,
    insured_object_id                                           VARCHAR,
    branch                                                      VARCHAR,
    code                                                        VARCHAR,
    description                                                 VARCHAR,
    warning_date                                                DATE,
    third_party_claim_date                                      DATE,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (rural_policy_claim_coverage_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE rural_policy_premiums (
    rural_policy_premium_id                                     UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    rural_policy_id                                             UUID,
    payments_quantity                                           INTEGER,
    amount                                                      VARCHAR,
    unit_type                                                   VARCHAR,
    unit_type_others                                            VARCHAR,
    unit_code                                                   VARCHAR,
    unit_description                                            VARCHAR,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    FOREIGN KEY (rural_policy_id) REFERENCES rural_policies (rural_policy_id) ON DELETE CASCADE
);

CREATE TABLE rural_policy_premiums_aud (
    rural_policy_premium_id                                     UUID,
    rural_policy_id                                             UUID,
    payments_quantity                                           INTEGER,
    amount                                                      VARCHAR,
    unit_type                                                   VARCHAR,
    unit_type_others                                            VARCHAR,
    unit_code                                                   VARCHAR,
    unit_description                                            VARCHAR,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (rural_policy_premium_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE rural_policy_premium_coverages (
    rural_policy_premium_coverage_id                            UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    rural_policy_premium_id                                     UUID,
    branch                                                      VARCHAR,
    code                                                        VARCHAR,
    description                                                 VARCHAR,
    amount                                                      VARCHAR,
    unit_type                                                   VARCHAR,
    unit_type_others                                            VARCHAR,
    unit_code                                                   VARCHAR,
    unit_description                                            VARCHAR,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    FOREIGN KEY (rural_policy_premium_id) REFERENCES rural_policy_premiums (rural_policy_premium_id) ON DELETE CASCADE
);

CREATE TABLE rural_policy_premium_coverages_aud (
    rural_policy_premium_coverage_id                            UUID,
    rural_policy_premium_id                                     UUID,
    branch                                                      VARCHAR,
    code                                                        VARCHAR,
    description                                                 VARCHAR,
    amount                                                      VARCHAR,
    unit_type                                                   VARCHAR,
    unit_type_others                                            VARCHAR,
    unit_code                                                   VARCHAR,
    unit_description                                            VARCHAR,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (rural_policy_premium_coverage_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

ALTER TABLE consent_rural_policies
    DROP CONSTRAINT consent_rural_policies_policy_id_fkey;

ALTER TABLE consent_rural_policies
    RENAME COLUMN policy_id TO rural_policy_id;

ALTER TABLE consent_rural_policies_aud
    RENAME COLUMN policy_id TO rural_policy_id;

ALTER TABLE consent_rural_policies
    ADD CONSTRAINT consent_rural_policies_rural_policy_id_fkey FOREIGN KEY (rural_policy_id) REFERENCES rural_policies (rural_policy_id) ON DELETE CASCADE;
