ALTER TABLE transport_policy_claims
ADD COLUMN identification VARCHAR,
ADD COLUMN documentation_delivery_date DATE,
ADD COLUMN status_alteration_date DATE,
ADD COLUMN occurrence_date DATE,
ADD COLUMN warning_date DATE,
ADD COLUMN third_party_claim_date DATE,
ADD COLUMN amount VARCHAR,
ADD COLUMN unit_type VARCHAR,
ADD COLUMN unit_type_others VARCHAR,
ADD COLUMN unit_code VARCHAR,
ADD COLUMN unit_description VARCHAR,
ADD COLUMN currency VARCHAR,
ADD COLUMN denial_justification VARCHAR,
ADD COLUMN denial_justification_description VARCHAR;

ALTER TABLE transport_policy_claims_aud
ADD COLUMN identification VARCHAR,
ADD COLUMN documentation_delivery_date DATE,
ADD COLUMN status_alteration_date DATE,
ADD COLUMN occurrence_date DATE,
ADD COLUMN warning_date DATE,
ADD COLUMN third_party_claim_date DATE,
ADD COLUMN amount VARCHAR,
ADD COLUMN unit_type VARCHAR,
ADD COLUMN unit_type_others VARCHAR,
ADD COLUMN unit_code VARCHAR,
ADD COLUMN unit_description VARCHAR,
ADD COLUMN currency VARCHAR,
ADD COLUMN denial_justification VARCHAR,
ADD COLUMN denial_justification_description VARCHAR;

CREATE TABLE transport_policy_claim_coverages (
    transport_policy_claim_coverage_id                          UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    transport_policy_claim_id                                   VARCHAR,
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
    FOREIGN KEY (transport_policy_claim_id) REFERENCES transport_policy_claims (transport_policy_claim_id) ON DELETE CASCADE
);

CREATE TABLE transport_policy_claim_coverages_aud (
    transport_policy_claim_coverage_id                          UUID,
    transport_policy_claim_id                                   VARCHAR,
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
    PRIMARY KEY (transport_policy_claim_coverage_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);
