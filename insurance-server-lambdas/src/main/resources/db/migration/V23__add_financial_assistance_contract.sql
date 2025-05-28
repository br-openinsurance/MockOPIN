CREATE TABLE financial_assistance_contracts (
    financial_assistance_contract_id             VARCHAR PRIMARY KEY NOT NULL,
    status VARCHAR,
    account_holder_id                      UUID,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (account_holder_id) REFERENCES account_holders (account_holder_id) ON DELETE CASCADE
);

CREATE TABLE financial_assistance_contracts_aud (
    financial_assistance_contract_id             VARCHAR,
    status VARCHAR,
    account_holder_id UUID,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (financial_assistance_contract_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE financial_assistance_contract_movements (
    financial_assistance_contract_movement_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    financial_assistance_contract_id                      VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (financial_assistance_contract_id) REFERENCES financial_assistance_contracts (financial_assistance_contract_id) ON DELETE CASCADE
);

CREATE TABLE financial_assistance_contract_movements_aud (
    financial_assistance_contract_movement_id             UUID,
    financial_assistance_contract_id VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (financial_assistance_contract_movement_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE consent_financial_assistance_contracts
(
    reference_id SERIAL PRIMARY KEY NOT NULL,
    consent_id   varchar,
    financial_assistance_contract_id   VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (consent_id) REFERENCES consents (consent_id) ON DELETE CASCADE,
    FOREIGN KEY (financial_assistance_contract_id) REFERENCES financial_assistance_contracts (financial_assistance_contract_id) ON DELETE CASCADE
);

CREATE TABLE consent_financial_assistance_contracts_aud
(
    reference_id integer NOT NULL,
    consent_id   varchar,
    financial_assistance_contract_id   VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    rev          integer NOT NULL,
    revtype              SMALLINT,
    PRIMARY KEY (reference_id, rev)
);