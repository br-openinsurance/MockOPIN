ALTER TABLE life_pension_contract_withdrawals
    ADD COLUMN withdrawal_occurence BOOLEAN,
    ADD COLUMN type VARCHAR,
    ADD COLUMN nature VARCHAR,
    ADD COLUMN request_date VARCHAR,
    ADD COLUMN liquidation_date VARCHAR,
    ADD COLUMN amount VARCHAR,
    ADD COLUMN amount_unit_type VARCHAR,
    ADD COLUMN amount_unit_type_others VARCHAR,
    ADD COLUMN amount_unit_code VARCHAR,
    ADD COLUMN amount_unit_description VARCHAR,
    ADD COLUMN amount_currency VARCHAR,
    ADD COLUMN posted_charged_amount VARCHAR,
    ADD COLUMN posted_charged_unit_type VARCHAR,
    ADD COLUMN posted_charged_unit_type_others VARCHAR,
    ADD COLUMN posted_charged_unit_code VARCHAR,
    ADD COLUMN posted_charged_unit_description VARCHAR,
    ADD COLUMN posted_charged_currency VARCHAR;

ALTER TABLE life_pension_contract_withdrawals_aud
    ADD COLUMN withdrawal_occurence BOOLEAN,
    ADD COLUMN type VARCHAR,
    ADD COLUMN nature VARCHAR,
    ADD COLUMN request_date VARCHAR,
    ADD COLUMN liquidation_date VARCHAR,
    ADD COLUMN amount VARCHAR,
    ADD COLUMN amount_unit_type VARCHAR,
    ADD COLUMN amount_unit_type_others VARCHAR,
    ADD COLUMN amount_unit_code VARCHAR,
    ADD COLUMN amount_unit_description VARCHAR,
    ADD COLUMN amount_currency VARCHAR,
    ADD COLUMN posted_charged_amount VARCHAR,
    ADD COLUMN posted_charged_unit_type VARCHAR,
    ADD COLUMN posted_charged_unit_type_others VARCHAR,
    ADD COLUMN posted_charged_unit_code VARCHAR,
    ADD COLUMN posted_charged_unit_description VARCHAR,
    ADD COLUMN posted_charged_currency VARCHAR;

CREATE TABLE life_pension_contract_withdrawal_fies (
    life_pension_contract_withdrawal_fie_id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    fie_cnpj VARCHAR,
    fie_name VARCHAR,
    fie_trade_name VARCHAR,
    life_pension_contract_withdrawal_id UUID,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (life_pension_contract_withdrawal_id) REFERENCES life_pension_contract_withdrawals (life_pension_contract_withdrawal_id) ON DELETE CASCADE
);

CREATE TABLE life_pension_contract_withdrawal_fies_aud (
    life_pension_contract_withdrawal_fie_id UUID,
    fie_cnpj VARCHAR,
    fie_name VARCHAR,
    fie_trade_name VARCHAR,
    life_pension_contract_withdrawal_id UUID,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (life_pension_contract_withdrawal_fie_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);
