ALTER TABLE transport_policies
ADD COLUMN product_name VARCHAR,
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
ADD COLUMN max_lmg_currency VARCHAR,
ADD COLUMN coinsurance_retained_percentage VARCHAR;

ALTER TABLE transport_policies_aud
ADD COLUMN product_name VARCHAR,
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
ADD COLUMN max_lmg_currency VARCHAR,
ADD COLUMN coinsurance_retained_percentage VARCHAR;

-- Transport-specific link tables: the shared *_ids tables key on reference_id UUID,
-- but transport_policies.transport_policy_id is VARCHAR. The referenced rows themselves
-- still live in the shared personal_info / beneficiary_info / principal_info /
-- intermediaries / coinsurers tables.
CREATE TABLE transport_personal_info_ids (
    reference_id                                                VARCHAR NOT NULL,
    personal_id                                                 UUID,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (reference_id, personal_id),
    FOREIGN KEY (reference_id) REFERENCES transport_policies (transport_policy_id) ON DELETE CASCADE
);

CREATE TABLE transport_personal_info_ids_aud (
    reference_id                                                VARCHAR NOT NULL,
    personal_id                                                 UUID,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (reference_id, personal_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE transport_beneficiary_info_ids (
    reference_id                                                VARCHAR NOT NULL,
    beneficiary_id                                              UUID,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (reference_id, beneficiary_id),
    FOREIGN KEY (reference_id) REFERENCES transport_policies (transport_policy_id) ON DELETE CASCADE
);

CREATE TABLE transport_beneficiary_info_ids_aud (
    reference_id                                                VARCHAR NOT NULL,
    beneficiary_id                                              UUID,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (reference_id, beneficiary_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE transport_principal_info_ids (
    reference_id                                                VARCHAR NOT NULL,
    principal_id                                                UUID,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (reference_id, principal_id),
    FOREIGN KEY (reference_id) REFERENCES transport_policies (transport_policy_id) ON DELETE CASCADE
);

CREATE TABLE transport_principal_info_ids_aud (
    reference_id                                                VARCHAR NOT NULL,
    principal_id                                                UUID,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (reference_id, principal_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE transport_intermediary_info_ids (
    reference_id                                                VARCHAR NOT NULL,
    intermediary_id                                             UUID,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (reference_id, intermediary_id),
    FOREIGN KEY (reference_id) REFERENCES transport_policies (transport_policy_id) ON DELETE CASCADE
);

CREATE TABLE transport_intermediary_info_ids_aud (
    reference_id                                                VARCHAR NOT NULL,
    intermediary_id                                             UUID,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (reference_id, intermediary_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE transport_coinsurer_ids (
    reference_id                                                VARCHAR NOT NULL,
    coinsurer_id                                                UUID,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (reference_id, coinsurer_id),
    FOREIGN KEY (reference_id) REFERENCES transport_policies (transport_policy_id) ON DELETE CASCADE
);

CREATE TABLE transport_coinsurer_ids_aud (
    reference_id                                                VARCHAR NOT NULL,
    coinsurer_id                                                UUID,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (reference_id, coinsurer_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE transport_policy_coverages (
    transport_policy_coverage_id                                UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    transport_policy_id                                         VARCHAR,
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
    FOREIGN KEY (transport_policy_id) REFERENCES transport_policies (transport_policy_id) ON DELETE CASCADE
);

CREATE TABLE transport_policy_coverages_aud (
    transport_policy_coverage_id                                UUID,
    transport_policy_id                                         VARCHAR,
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
    PRIMARY KEY (transport_policy_coverage_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE transport_policy_endorsements (
    transport_policy_endorsement_id                             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    transport_policy_id                                         VARCHAR,
    travel_type                                                 VARCHAR,
    transport_type                                              VARCHAR,
    shipments_number                                            INTEGER,
    branch                                                      VARCHAR,
    shipments_premium_amount                                    VARCHAR,
    shipments_premium_unit_type                                 VARCHAR,
    shipments_premium_unit_type_others                          VARCHAR,
    shipments_premium_unit_code                                 VARCHAR,
    shipments_premium_unit_description                          VARCHAR,
    shipments_premium_currency                                  VARCHAR,
    shipments_premium_brl                                       VARCHAR,
    shipments_insureds_amount                                   VARCHAR,
    shipments_insureds_unit_type                                VARCHAR,
    shipments_insureds_unit_type_others                         VARCHAR,
    shipments_insureds_unit_code                                VARCHAR,
    shipments_insureds_unit_description                         VARCHAR,
    shipments_insureds_currency                                 VARCHAR,
    min_insured_amount                                          VARCHAR,
    min_insured_unit_type                                       VARCHAR,
    min_insured_unit_type_others                                VARCHAR,
    min_insured_unit_code                                       VARCHAR,
    min_insured_unit_description                                VARCHAR,
    min_insured_currency                                        VARCHAR,
    max_insured_amount                                          VARCHAR,
    max_insured_unit_type                                       VARCHAR,
    max_insured_unit_type_others                                VARCHAR,
    max_insured_unit_code                                       VARCHAR,
    max_insured_unit_description                                VARCHAR,
    max_insured_currency                                        VARCHAR,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    FOREIGN KEY (transport_policy_id) REFERENCES transport_policies (transport_policy_id) ON DELETE CASCADE
);

CREATE TABLE transport_policy_endorsements_aud (
    transport_policy_endorsement_id                             UUID,
    transport_policy_id                                         VARCHAR,
    travel_type                                                 VARCHAR,
    transport_type                                              VARCHAR,
    shipments_number                                            INTEGER,
    branch                                                      VARCHAR,
    shipments_premium_amount                                    VARCHAR,
    shipments_premium_unit_type                                 VARCHAR,
    shipments_premium_unit_type_others                          VARCHAR,
    shipments_premium_unit_code                                 VARCHAR,
    shipments_premium_unit_description                          VARCHAR,
    shipments_premium_currency                                  VARCHAR,
    shipments_premium_brl                                       VARCHAR,
    shipments_insureds_amount                                   VARCHAR,
    shipments_insureds_unit_type                                VARCHAR,
    shipments_insureds_unit_type_others                         VARCHAR,
    shipments_insureds_unit_code                                VARCHAR,
    shipments_insureds_unit_description                         VARCHAR,
    shipments_insureds_currency                                 VARCHAR,
    min_insured_amount                                          VARCHAR,
    min_insured_unit_type                                       VARCHAR,
    min_insured_unit_type_others                                VARCHAR,
    min_insured_unit_code                                       VARCHAR,
    min_insured_unit_description                                VARCHAR,
    min_insured_currency                                        VARCHAR,
    max_insured_amount                                          VARCHAR,
    max_insured_unit_type                                       VARCHAR,
    max_insured_unit_type_others                                VARCHAR,
    max_insured_unit_code                                       VARCHAR,
    max_insured_unit_description                                VARCHAR,
    max_insured_currency                                        VARCHAR,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (transport_policy_endorsement_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);
