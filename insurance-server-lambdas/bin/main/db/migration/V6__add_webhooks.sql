CREATE TABLE client_webhook_uri
(
    client_id                       VARCHAR PRIMARY KEY NOT NULL,
    webhook_uri                     VARCHAR,
    created_at                      TIMESTAMP,
    created_by                      VARCHAR,
    updated_at                      TIMESTAMP,
    updated_by                      VARCHAR,
    hibernate_status                VARCHAR
);

CREATE TABLE client_webhook_uri_aud
(
    client_id                       VARCHAR NOT NULL,
    webhook_uri                     VARCHAR,
    rev                             INTEGER NOT NULL,
    revtype                         SMALLINT,
    created_at                      TIMESTAMP,
    created_by                      VARCHAR,
    updated_at                      TIMESTAMP,
    updated_by                      VARCHAR,
    hibernate_status                VARCHAR,
    PRIMARY KEY (client_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);