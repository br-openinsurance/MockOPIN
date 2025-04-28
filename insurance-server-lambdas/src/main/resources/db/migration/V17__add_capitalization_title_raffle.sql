CREATE TABLE capitalization_title_raffles (
    raffle_id            UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    client_id            VARCHAR NOT NULL,
    data                 JSONB  NOT NULL,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR
);

CREATE TABLE capitalization_title_raffles_aud (
    raffle_id            UUID,
    client_id            VARCHAR NOT NULL,
    data                 JSONB  NOT NULL,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (raffle_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);