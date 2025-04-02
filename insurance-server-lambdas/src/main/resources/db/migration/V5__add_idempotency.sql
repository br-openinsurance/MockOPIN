CREATE TABLE idempotency_records
(
    idempotency_id                  VARCHAR PRIMARY KEY NOT NULL,
    request                         VARCHAR NOT NULL,
    response                        VARCHAR NOT NULL,
    created_at                      TIMESTAMP,
    created_by                      VARCHAR,
    updated_at                      TIMESTAMP,
    updated_by                      VARCHAR,
    hibernate_status                VARCHAR
);

CREATE TABLE idempotency_records_aud
(
    idempotency_id                  VARCHAR NOT NULL,
    request                         VARCHAR NOT NULL,
    response                        VARCHAR NOT NULL,
    rev                             INTEGER NOT NULL,
    revtype                         SMALLINT,
    created_at                      TIMESTAMP,
    created_by                      VARCHAR,
    updated_at                      TIMESTAMP,
    updated_by                      VARCHAR,
    hibernate_status                VARCHAR,
    PRIMARY KEY (idempotency_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);