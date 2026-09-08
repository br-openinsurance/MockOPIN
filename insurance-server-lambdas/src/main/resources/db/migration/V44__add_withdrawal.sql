ALTER TABLE consents ADD COLUMN withdrawal_captalization_information JSONB;
ALTER TABLE consents ADD COLUMN withdrawal_life_pension_information JSONB;
ALTER TABLE consents ADD COLUMN raffle_captalization_title_information JSONB;

ALTER TABLE consents_aud ADD COLUMN withdrawal_captalization_information JSONB;
ALTER TABLE consents_aud ADD COLUMN withdrawal_life_pension_information JSONB;
ALTER TABLE consents_aud ADD COLUMN raffle_captalization_title_information JSONB;

CREATE TABLE pension_withdrawals (
    withdrawal_id        UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    consent_id           VARCHAR NOT NULL,
    client_id            VARCHAR NOT NULL,
    data                 JSONB  NOT NULL,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR
);

CREATE TABLE pension_withdrawals_aud (
    withdrawal_id        UUID,
    consent_id           VARCHAR NOT NULL,
    client_id            VARCHAR NOT NULL,
    data                 JSONB  NOT NULL,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (withdrawal_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE pension_withdrawal_leads (
    withdrawal_lead_id   UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    consent_id           VARCHAR NOT NULL,
    client_id            VARCHAR NOT NULL,
    data                 JSONB  NOT NULL,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR
);

CREATE TABLE pension_withdrawal_leads_aud (
    withdrawal_lead_id   UUID,
    consent_id           VARCHAR NOT NULL,
    client_id            VARCHAR NOT NULL,
    data                 JSONB  NOT NULL,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (withdrawal_lead_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE capitalization_title_withdrawals (
    withdrawal_id        UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    consent_id           VARCHAR NOT NULL,
    client_id            VARCHAR NOT NULL,
    data                 JSONB  NOT NULL,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR
);

CREATE TABLE capitalization_title_withdrawals_aud (
    withdrawal_id        UUID,
    consent_id           VARCHAR NOT NULL,
    client_id            VARCHAR NOT NULL,
    data                 JSONB  NOT NULL,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (withdrawal_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);
