CREATE TABLE transport_policies (
    transport_policy_id             VARCHAR PRIMARY KEY NOT NULL,
    status VARCHAR,
    document_type VARCHAR,
    proposal_id VARCHAR,
    account_holder_id                      UUID,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (account_holder_id) REFERENCES account_holders (account_holder_id) ON DELETE CASCADE
);

CREATE TABLE transport_policies_aud (
    transport_policy_id             VARCHAR,
    status VARCHAR,
    document_type VARCHAR,
    proposal_id VARCHAR,
    account_holder_id UUID,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (transport_policy_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE transport_policy_claims (
    transport_policy_claim_id             VARCHAR PRIMARY KEY NOT NULL,
    status VARCHAR,
    transport_policy_id                      VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (transport_policy_id) REFERENCES transport_policies (transport_policy_id) ON DELETE CASCADE
);

CREATE TABLE transport_policy_claims_aud (
    transport_policy_claim_id             VARCHAR,
    status VARCHAR,
    transport_policy_id VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (transport_policy_claim_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE consent_transport_policies
(
    reference_id SERIAL PRIMARY KEY NOT NULL,
    consent_id   varchar,
    transport_policy_id   varchar,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (consent_id) REFERENCES consents (consent_id) ON DELETE CASCADE,
    FOREIGN KEY (transport_policy_id) REFERENCES transport_policies (transport_policy_id) ON DELETE CASCADE
);

CREATE TABLE consent_transport_policies_aud
(
    reference_id integer NOT NULL,
    consent_id   varchar,
    transport_policy_id   varchar,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    rev          integer NOT NULL,
    revtype              SMALLINT,
    PRIMARY KEY (reference_id, rev)
);