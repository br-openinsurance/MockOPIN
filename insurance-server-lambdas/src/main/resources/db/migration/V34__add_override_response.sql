CREATE TABLE override_responses (
    override_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    path VARCHAR NOT NULL,
    method VARCHAR NOT NULL,
    client_id VARCHAR NOT NULL,
    expires_in INTEGER,
    response                 JSONB  NOT NULL,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR
);

CREATE TABLE override_responses_aud (
    override_id             UUID,
    path VARCHAR NOT NULL,
    method VARCHAR NOT NULL,
    client_id VARCHAR NOT NULL,
    expires_in INTEGER,
    response                 JSONB  NOT NULL,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (override_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);