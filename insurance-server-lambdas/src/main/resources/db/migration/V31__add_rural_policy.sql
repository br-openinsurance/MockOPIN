CREATE TABLE rural_policies (
    policy_id                                           UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    insurance_id                                        VARCHAR,
    status                                              VARCHAR,
    account_holder_id                                   UUID,
    created_at                                          TIMESTAMP,
    created_by                                          VARCHAR,
    updated_at                                          TIMESTAMP,
    updated_by                                          VARCHAR,
    hibernate_status                                    VARCHAR,
    FOREIGN KEY (account_holder_id) REFERENCES account_holders (account_holder_id) ON DELETE CASCADE
);

CREATE TABLE rural_policies_aud (
    policy_id                                           UUID,
    insurance_id                                        VARCHAR,
    status                                              VARCHAR,
    account_holder_id                                   UUID,
    rev                                                 INTEGER NOT NULL,
    revtype                                             SMALLINT,
    created_at                                          TIMESTAMP,
    created_by                                          VARCHAR,
    updated_at                                          TIMESTAMP,
    updated_by                                          VARCHAR,
    hibernate_status                                    VARCHAR,
    PRIMARY KEY (policy_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE rural_policy_claims (
    claim_id                                                    UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    policy_id                                                   UUID,
    identification                                              VARCHAR,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    FOREIGN KEY (policy_id) REFERENCES rural_policies (policy_id) ON DELETE CASCADE
);

CREATE TABLE rural_policy_claims_aud (
    claim_id                                                    UUID,
    policy_id                                                   UUID,
    identification                                              VARCHAR,
    rev                                                         INTEGER NOT NULL,
    revtype                                                     SMALLINT,
    created_at                                                  TIMESTAMP,
    created_by                                                  VARCHAR,
    updated_at                                                  TIMESTAMP,
    updated_by                                                  VARCHAR,
    hibernate_status                                            VARCHAR,
    PRIMARY KEY (claim_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE consent_rural_policies
(
    reference_id                                SERIAL PRIMARY KEY NOT NULL,
    consent_id                                  VARCHAR,
    policy_id                                   UUID,
    created_at                                  TIMESTAMP,
    created_by                                  VARCHAR,
    updated_at                                  TIMESTAMP,
    updated_by                                  VARCHAR,
    hibernate_status                            VARCHAR,
    FOREIGN KEY (consent_id) REFERENCES consents (consent_id) ON DELETE CASCADE,
    FOREIGN KEY (policy_id) REFERENCES rural_policies (policy_id) ON DELETE CASCADE
);

CREATE TABLE consent_rural_policies_aud
(
    reference_id                                INTEGER NOT NULL,
    consent_id                                  VARCHAR,
    policy_id                                   UUID,
    created_at                                  TIMESTAMP,
    created_by                                  VARCHAR,
    updated_at                                  TIMESTAMP,
    updated_by                                  VARCHAR,
    hibernate_status                            VARCHAR,
    rev                                         INTEGER NOT NULL,
    revtype                                     SMALLINT,
    PRIMARY KEY (reference_id, rev)
);
