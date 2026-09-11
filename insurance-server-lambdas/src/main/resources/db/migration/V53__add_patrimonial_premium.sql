CREATE TABLE patrimonial_premiums (
    patrimonial_premium_id                                      UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    policy_id                                                   UUID,
    payments_quantity                                           INTEGER,
    amount                                                      VARCHAR,
    unit_type                                                   VARCHAR,
    unit_code                                                   VARCHAR,
    unit_description                                            VARCHAR,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    FOREIGN KEY (policy_id) REFERENCES patrimonial_policies (policy_id) ON DELETE CASCADE
);

CREATE TABLE patrimonial_premiums_aud (
    patrimonial_premium_id                                      UUID,
    policy_id                                                   UUID,
    payments_quantity                                           INTEGER,
    amount                                                      VARCHAR,
    unit_type                                                   VARCHAR,
    unit_code                                                   VARCHAR,
    unit_description                                            VARCHAR,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (patrimonial_premium_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE patrimonial_premium_coverages (
    patrimonial_premium_coverage_id                             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    patrimonial_premium_id                                      UUID,
    branch                                                      VARCHAR,
    code                                                        VARCHAR,
    amount                                                      VARCHAR,
    unit_type                                                   VARCHAR,
    unit_code                                                   VARCHAR,
    unit_description                                            VARCHAR,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    FOREIGN KEY (patrimonial_premium_id) REFERENCES patrimonial_premiums (patrimonial_premium_id) ON DELETE CASCADE
);

CREATE TABLE patrimonial_premium_coverages_aud (
    patrimonial_premium_coverage_id                             UUID,
    patrimonial_premium_id                                      UUID,
    branch                                                      VARCHAR,
    code                                                        VARCHAR,
    amount                                                      VARCHAR,
    unit_type                                                   VARCHAR,
    unit_code                                                   VARCHAR,
    unit_description                                            VARCHAR,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (patrimonial_premium_coverage_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);
