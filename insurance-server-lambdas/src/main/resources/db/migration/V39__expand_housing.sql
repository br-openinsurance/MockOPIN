ALTER TABLE housing_policies
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
    ADD COLUMN max_lmg_unit_code VARCHAR,
    ADD COLUMN max_lmg_unit_description VARCHAR,
    ADD COLUMN proposal_id VARCHAR;

ALTER TABLE housing_policies_aud
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
    ADD COLUMN max_lmg_unit_code VARCHAR,
    ADD COLUMN max_lmg_unit_description VARCHAR,
    ADD COLUMN proposal_id VARCHAR;

CREATE TABLE housing_policy_insured_objects (
    housing_policy_insured_object_id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    housing_policy_id UUID,
    identification VARCHAR,
    type VARCHAR,
    description VARCHAR,
    amount VARCHAR,
    unit_type VARCHAR,
    unit_code VARCHAR,
    unit_description VARCHAR,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    FOREIGN KEY (housing_policy_id) REFERENCES housing_policies (housing_policy_id) ON DELETE CASCADE
);

CREATE TABLE housing_policy_insured_objects_aud (
    housing_policy_insured_object_id UUID,
    housing_policy_id UUID,
    identification VARCHAR,
    type VARCHAR,
    description VARCHAR,
    amount VARCHAR,
    unit_type VARCHAR,
    unit_code VARCHAR,
    unit_description VARCHAR,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    PRIMARY KEY (housing_policy_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE housing_policy_insured_object_coverages (
    housing_policy_insured_object_coverage_id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    housing_policy_insured_object_id UUID,
    branch VARCHAR,
    code VARCHAR,
    description VARCHAR,
    internal_code VARCHAR,
    susep_process_number VARCHAR,
    lmi_amount VARCHAR,
    lmi_unit_type VARCHAR,
    lmi_sub_limit BOOLEAN,
    term_start_date DATE,
    term_end_date DATE,
    main_coverage BOOLEAN,
    feature VARCHAR,
    type VARCHAR,
    grace_period VARCHAR,
    grace_periodicity VARCHAR,
    grace_period_counting_method VARCHAR,
    premium_periodicity VARCHAR,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    FOREIGN KEY (housing_policy_insured_object_id) REFERENCES housing_policy_insured_objects (housing_policy_insured_object_id) ON DELETE CASCADE
);

CREATE TABLE housing_policy_insured_object_coverages_aud (
    housing_policy_insured_object_coverage_id UUID,
    housing_policy_insured_object_id UUID,
    branch VARCHAR,
    code VARCHAR,
    description VARCHAR,
    internal_code VARCHAR,
    susep_process_number VARCHAR,
    lmi_amount VARCHAR,
    lmi_unit_type VARCHAR,
    lmi_sub_limit BOOLEAN,
    term_start_date DATE,
    term_end_date DATE,
    main_coverage BOOLEAN,
    feature VARCHAR,
    type VARCHAR,
    grace_period VARCHAR,
    grace_periodicity VARCHAR,
    grace_period_counting_method VARCHAR,
    premium_periodicity VARCHAR,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    PRIMARY KEY (housing_policy_insured_object_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE housing_policy_branch_insured_objects (
    housing_policy_branch_insured_object_id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    housing_policy_id UUID,
    identification VARCHAR,
    property_type VARCHAR,
    postcode VARCHAR,
    interest_rate VARCHAR,
    cost_rate VARCHAR,
    update_index VARCHAR,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    FOREIGN KEY (housing_policy_id) REFERENCES housing_policies (housing_policy_id) ON DELETE CASCADE
);

CREATE TABLE housing_policy_branch_insured_objects_aud (
    housing_policy_branch_insured_object_id UUID,
    housing_policy_id UUID,
    identification VARCHAR,
    property_type VARCHAR,
    postcode VARCHAR,
    interest_rate VARCHAR,
    cost_rate VARCHAR,
    update_index VARCHAR,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    PRIMARY KEY (housing_policy_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE housing_policy_branch_insured_object_lenders (
    housing_policy_branch_insured_object_lender_id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    housing_policy_branch_insured_object_id UUID,
    company_name VARCHAR,
    cnpj_number VARCHAR,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    FOREIGN KEY (housing_policy_branch_insured_object_id) REFERENCES housing_policy_branch_insured_objects (housing_policy_branch_insured_object_id) ON DELETE CASCADE
);

CREATE TABLE housing_policy_branch_insured_object_lenders_aud (
    housing_policy_branch_insured_object_lender_id UUID,
    housing_policy_branch_insured_object_id UUID,
    company_name VARCHAR,
    cnpj_number VARCHAR,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    PRIMARY KEY (housing_policy_branch_insured_object_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE housing_policy_branch_insureds (
    housing_policy_branch_insured_id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    housing_policy_id UUID,
    identification VARCHAR,
    identification_type VARCHAR,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    FOREIGN KEY (housing_policy_id) REFERENCES housing_policies (housing_policy_id) ON DELETE CASCADE
);

CREATE TABLE housing_policy_branch_insureds_aud (
    housing_policy_branch_insured_id UUID,
    housing_policy_id UUID,
    identification VARCHAR,
    identification_type VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    PRIMARY KEY (housing_policy_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

ALTER TABLE housing_policy_claims
    ADD COLUMN identification VARCHAR,
    ADD COLUMN documentation_delivery_date DATE,
    ADD COLUMN status VARCHAR,
    ADD COLUMN status_alteration_date DATE,
    ADD COLUMN occurrence_date DATE,
    ADD COLUMN warning_date DATE,
    ADD COLUMN third_party_claim_date DATE,
    ADD COLUMN amount VARCHAR,
    ADD COLUMN unit_type VARCHAR,
    ADD COLUMN denial_justification VARCHAR,
    ADD COLUMN denial_justification_description VARCHAR;

ALTER TABLE housing_policy_claims_aud
    ADD COLUMN identification VARCHAR,
    ADD COLUMN documentation_delivery_date DATE,
    ADD COLUMN status VARCHAR,
    ADD COLUMN status_alteration_date DATE,
    ADD COLUMN occurrence_date DATE,
    ADD COLUMN warning_date DATE,
    ADD COLUMN third_party_claim_date DATE,
    ADD COLUMN amount VARCHAR,
    ADD COLUMN unit_type VARCHAR,
    ADD COLUMN denial_justification VARCHAR,
    ADD COLUMN denial_justification_description VARCHAR;

CREATE TABLE housing_policy_claim_coverages (
    housing_policy_claim_coverage_id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    housing_policy_claim_id UUID,
    insured_object_id VARCHAR,
    branch VARCHAR,
    code VARCHAR,
    description VARCHAR,
    warning_date DATE,
    third_party_claim_date DATE,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    FOREIGN KEY (housing_policy_claim_id) REFERENCES housing_policy_claims (housing_policy_claim_id) ON DELETE CASCADE
);

CREATE TABLE housing_policy_claim_coverages_aud (
    housing_policy_claim_coverage_id UUID,
    housing_policy_claim_id UUID,
    insured_object_id VARCHAR,
    branch VARCHAR,
    code VARCHAR,
    description VARCHAR,
    warning_date DATE,
    third_party_claim_date DATE,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    PRIMARY KEY (housing_policy_claim_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

ALTER TABLE housing_policy_premiums
    ADD COLUMN payments_quantity INTEGER,
    ADD COLUMN amount VARCHAR,
    ADD COLUMN unit_type VARCHAR;

ALTER TABLE housing_policy_premiums_aud
    ADD COLUMN payments_quantity INTEGER,
    ADD COLUMN amount VARCHAR,
    ADD COLUMN unit_type VARCHAR;

CREATE TABLE housing_policy_premium_coverages (
    housing_policy_premium_coverage_id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    housing_policy_premium_id UUID,
    branch VARCHAR,
    code VARCHAR,
    amount VARCHAR,
    unit_type VARCHAR,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    FOREIGN KEY (housing_policy_premium_id) REFERENCES housing_policy_premiums (housing_policy_premium_id) ON DELETE CASCADE
);

CREATE TABLE housing_policy_premium_coverages_aud (
    housing_policy_premium_coverage_id UUID,
    housing_policy_premium_id UUID,
    branch VARCHAR,
    code VARCHAR,
    amount VARCHAR,
    unit_type VARCHAR,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    PRIMARY KEY (housing_policy_premium_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);