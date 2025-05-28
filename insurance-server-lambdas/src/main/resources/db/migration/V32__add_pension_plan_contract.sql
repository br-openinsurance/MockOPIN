CREATE TABLE pension_plan_contracts (
    pension_plan_contract_id             VARCHAR PRIMARY KEY,
    status VARCHAR,
    account_holder_id                      UUID,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (account_holder_id) REFERENCES account_holders (account_holder_id) ON DELETE CASCADE
);

CREATE TABLE pension_plan_contracts_aud (
    pension_plan_contract_id             VARCHAR,
    status VARCHAR,
    account_holder_id UUID,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (pension_plan_contract_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE pension_plan_contract_movement_benefits (
    pension_plan_contract_movement_benefit_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    pension_plan_contract_id                      VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (pension_plan_contract_id) REFERENCES pension_plan_contracts (pension_plan_contract_id) ON DELETE CASCADE
);

CREATE TABLE pension_plan_contract_movement_benefits_aud (
    pension_plan_contract_movement_benefit_id             UUID,
    pension_plan_contract_id VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (pension_plan_contract_movement_benefit_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE pension_plan_contract_movement_contributions (
    pension_plan_contract_movement_contribution_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    pension_plan_contract_id                      VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (pension_plan_contract_id) REFERENCES pension_plan_contracts (pension_plan_contract_id) ON DELETE CASCADE
);

CREATE TABLE pension_plan_contract_movement_contributions_aud (
    pension_plan_contract_movement_contribution_id             UUID,
    pension_plan_contract_id VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (pension_plan_contract_movement_contribution_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE pension_plan_contract_portabilities (
    pension_plan_contract_portability_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    pension_plan_contract_id                      VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (pension_plan_contract_id) REFERENCES pension_plan_contracts (pension_plan_contract_id) ON DELETE CASCADE
);

CREATE TABLE pension_plan_contract_portabilities_aud (
    pension_plan_contract_portability_id             UUID,
    pension_plan_contract_id VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (pension_plan_contract_portability_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE pension_plan_contract_withdrawals (
    pension_plan_contract_withdrawal_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    pension_plan_contract_id                      VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (pension_plan_contract_id) REFERENCES pension_plan_contracts (pension_plan_contract_id) ON DELETE CASCADE
);

CREATE TABLE pension_plan_contract_withdrawals_aud (
    pension_plan_contract_withdrawal_id             UUID,
    pension_plan_contract_id VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (pension_plan_contract_withdrawal_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE pension_plan_contract_claims (
    pension_plan_contract_claim_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    pension_plan_contract_id                      VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (pension_plan_contract_id) REFERENCES pension_plan_contracts (pension_plan_contract_id) ON DELETE CASCADE
);

CREATE TABLE pension_plan_contract_claims_aud (
    pension_plan_contract_claim_id             UUID,
    pension_plan_contract_id VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (pension_plan_contract_claim_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE consent_pension_plan_contracts
(
    reference_id SERIAL PRIMARY KEY NOT NULL,
    consent_id   varchar,
    pension_plan_contract_id   VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (consent_id) REFERENCES consents (consent_id) ON DELETE CASCADE,
    FOREIGN KEY (pension_plan_contract_id) REFERENCES pension_plan_contracts (pension_plan_contract_id) ON DELETE CASCADE
);

CREATE TABLE consent_pension_plan_contracts_aud
(
    reference_id integer NOT NULL,
    consent_id   varchar,
    pension_plan_contract_id   VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    rev          integer NOT NULL,
    revtype              SMALLINT,
    PRIMARY KEY (reference_id, rev)
);