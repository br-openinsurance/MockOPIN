CREATE TABLE housing_policies (
    housing_policy_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    housing_id VARCHAR,
    status VARCHAR,
    account_holder_id                      UUID,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (account_holder_id) REFERENCES account_holders (account_holder_id) ON DELETE CASCADE
);

CREATE TABLE housing_policies_aud (
    housing_policy_id             UUID,
    housing_id VARCHAR,
    status VARCHAR,
    account_holder_id UUID,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (housing_policy_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE housing_policy_claims (
    housing_policy_claim_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    housing_policy_id                      UUID,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (housing_policy_id) REFERENCES housing_policies (housing_policy_id) ON DELETE CASCADE
);

CREATE TABLE housing_policy_claims_aud (
    housing_policy_claim_id             UUID,
    housing_policy_id UUID,
    settlement_id VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (housing_policy_claim_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE housing_policy_premiums (
    housing_policy_premium_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    housing_policy_id                      UUID,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (housing_policy_id) REFERENCES housing_policies (housing_policy_id) ON DELETE CASCADE
);

CREATE TABLE housing_policy_premiums_aud (
    housing_policy_premium_id             UUID,
    housing_policy_id UUID,
    settlement_id VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (housing_policy_premium_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE consent_housing_policies
(
    reference_id SERIAL PRIMARY KEY NOT NULL,
    consent_id   varchar,
    housing_policy_id   uuid,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (consent_id) REFERENCES consents (consent_id) ON DELETE CASCADE,
    FOREIGN KEY (housing_policy_id) REFERENCES housing_policies (housing_policy_id) ON DELETE CASCADE
);

CREATE TABLE consent_housing_policies_aud
(
    reference_id integer NOT NULL,
    consent_id   varchar,
    housing_policy_id   uuid,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    rev          integer NOT NULL,
    revtype              SMALLINT,
    PRIMARY KEY (reference_id, rev)
);