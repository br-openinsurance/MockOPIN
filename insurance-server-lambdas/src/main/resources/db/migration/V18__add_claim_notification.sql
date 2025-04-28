CREATE TABLE claim_notification_damages
(
    claim_id                        UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    client_id                       VARCHAR NOT NULL,
    consent_id                      VARCHAR NOT NULL,
    expiration_date_time            TIMESTAMP,
    data                 JSONB  NOT NULL,
    created_at                      TIMESTAMP,
    created_by                      VARCHAR,
    updated_at                      TIMESTAMP,
    updated_by                      VARCHAR,
    hibernate_status                VARCHAR
);

CREATE TABLE claim_notification_damages_aud
(
    claim_id                        UUID,
    client_id                       VARCHAR NOT NULL,
    consent_id                      VARCHAR NOT NULL,
    expiration_date_time            TIMESTAMP,
    data                 JSONB  NOT NULL,
    rev                             INTEGER NOT NULL,
    revtype                         SMALLINT,
    created_at                      TIMESTAMP,
    created_by                      VARCHAR,
    updated_at                      TIMESTAMP,
    updated_by                      VARCHAR,
    hibernate_status                VARCHAR,
    PRIMARY KEY (claim_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE claim_notification_persons
(
    claim_id                        UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    client_id                       VARCHAR NOT NULL,
    consent_id                      VARCHAR NOT NULL,
    expiration_date_time            TIMESTAMP,
    data                 JSONB  NOT NULL,
    created_at                      TIMESTAMP,
    created_by                      VARCHAR,
    updated_at                      TIMESTAMP,
    updated_by                      VARCHAR,
    hibernate_status                VARCHAR
);

CREATE TABLE claim_notification_persons_aud
(
    claim_id                        UUID,
    client_id                       VARCHAR NOT NULL,
    consent_id                      VARCHAR NOT NULL,
    expiration_date_time            TIMESTAMP,
    data                 JSONB  NOT NULL,
    rev                             INTEGER NOT NULL,
    revtype                         SMALLINT,
    created_at                      TIMESTAMP,
    created_by                      VARCHAR,
    updated_at                      TIMESTAMP,
    updated_by                      VARCHAR,
    hibernate_status                VARCHAR,
    PRIMARY KEY (claim_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

ALTER TABLE consents ADD COLUMN claim_notification_information JSONB;
ALTER TABLE consents_aud ADD COLUMN claim_notification_information JSONB;