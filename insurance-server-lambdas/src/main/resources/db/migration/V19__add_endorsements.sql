CREATE TABLE endorsements (
    endorsement_id       UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    consent_id           VARCHAR NOT NULL,
    client_id            VARCHAR NOT NULL,
    data                 JSONB  NOT NULL,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR
);

CREATE TABLE endorsements_aud (
    endorsement_id       UUID,
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
    PRIMARY KEY (endorsement_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

ALTER TABLE consents ADD COLUMN endorsement_information JSONB;
ALTER TABLE consents_aud ADD COLUMN endorsement_information JSONB;