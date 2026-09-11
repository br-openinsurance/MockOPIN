ALTER TABLE person_policy_premiums
ADD COLUMN payments_quantity INTEGER,
ADD COLUMN amount VARCHAR,
ADD COLUMN unit_type VARCHAR;

ALTER TABLE person_policy_premiums_aud
ADD COLUMN payments_quantity INTEGER,
ADD COLUMN amount VARCHAR,
ADD COLUMN unit_type VARCHAR;

CREATE TABLE person_premium_coverages (
    person_premium_coverage_id                                  UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    person_policy_premium_id                                    UUID,
    branch                                                      VARCHAR,
    code                                                        VARCHAR,
    premium_amount                                              VARCHAR,
    premium_unit_type                                           VARCHAR,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    FOREIGN KEY (person_policy_premium_id) REFERENCES person_policy_premiums (person_policy_premium_id) ON DELETE CASCADE
);

CREATE TABLE person_premium_coverages_aud (
    person_premium_coverage_id                                  UUID,
    person_policy_premium_id                                    UUID,
    branch                                                      VARCHAR,
    code                                                        VARCHAR,
    premium_amount                                              VARCHAR,
    premium_unit_type                                           VARCHAR,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (person_premium_coverage_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);
