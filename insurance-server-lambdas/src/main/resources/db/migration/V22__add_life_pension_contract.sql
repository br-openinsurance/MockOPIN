CREATE TABLE life_pension_contracts (
    life_pension_contract_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    life_pension_id VARCHAR,
    status VARCHAR,
    account_holder_id                      UUID,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (account_holder_id) REFERENCES account_holders (account_holder_id) ON DELETE CASCADE
);

CREATE TABLE life_pension_contracts_aud (
    life_pension_contract_id             UUID,
    life_pension_id VARCHAR,
    status VARCHAR,
    account_holder_id UUID,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (life_pension_contract_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE life_pension_contract_movement_benefits (
    life_pension_contract_movement_benefit_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    life_pension_contract_id                      UUID,
    susep_process_number VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (life_pension_contract_id) REFERENCES life_pension_contracts (life_pension_contract_id) ON DELETE CASCADE
);

CREATE TABLE life_pension_contract_movement_benefits_aud (
    life_pension_contract_movement_benefit_id             UUID,
    life_pension_contract_id UUID,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (life_pension_contract_movement_benefit_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE life_pension_contract_movement_contributions (
    life_pension_contract_movement_contribution_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    life_pension_contract_id                      UUID,
    event_type VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (life_pension_contract_id) REFERENCES life_pension_contracts (life_pension_contract_id) ON DELETE CASCADE
);

CREATE TABLE life_pension_contract_movement_contributions_aud (
    life_pension_contract_movement_contribution_id             UUID,
    life_pension_contract_id UUID,
    event_type VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (life_pension_contract_movement_contribution_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE life_pension_contract_portabilities (
    life_pension_contract_portability_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    life_pension_contract_id                      UUID,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (life_pension_contract_id) REFERENCES life_pension_contracts (life_pension_contract_id) ON DELETE CASCADE
);

CREATE TABLE life_pension_contract_portabilities_aud (
    life_pension_contract_portability_id             UUID,
    life_pension_contract_id UUID,
    settlement_id VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (life_pension_contract_portability_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE life_pension_contract_withdrawals (
    life_pension_contract_withdrawal_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    life_pension_contract_id                      UUID,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (life_pension_contract_id) REFERENCES life_pension_contracts (life_pension_contract_id) ON DELETE CASCADE
);

CREATE TABLE life_pension_contract_withdrawals_aud (
    life_pension_contract_withdrawal_id             UUID,
    life_pension_contract_id UUID,
    settlement_id VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (life_pension_contract_withdrawal_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE life_pension_contract_claims (
    life_pension_contract_claim_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    life_pension_contract_id                      UUID,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (life_pension_contract_id) REFERENCES life_pension_contracts (life_pension_contract_id) ON DELETE CASCADE
);

CREATE TABLE life_pension_contract_claims_aud (
    life_pension_contract_claim_id             UUID,
    life_pension_contract_id UUID,
    settlement_id VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (life_pension_contract_claim_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE consent_life_pension_contracts
(
    reference_id SERIAL PRIMARY KEY NOT NULL,
    consent_id   varchar,
    life_pension_contract_id   uuid,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (consent_id) REFERENCES consents (consent_id) ON DELETE CASCADE,
    FOREIGN KEY (life_pension_contract_id) REFERENCES life_pension_contracts (life_pension_contract_id) ON DELETE CASCADE
);

CREATE TABLE consent_life_pension_contracts_aud
(
    reference_id integer NOT NULL,
    consent_id   varchar,
    life_pension_contract_id   uuid,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    rev          integer NOT NULL,
    revtype              SMALLINT,
    PRIMARY KEY (reference_id, rev)
);