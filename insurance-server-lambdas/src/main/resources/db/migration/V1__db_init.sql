CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE SEQUENCE IF NOT EXISTS hibernate_sequence INCREMENT 1 START 1 MINVALUE 1;

CREATE TABLE revinfo
(
    rev      SERIAL PRIMARY KEY NOT NULL,
    revtstmp bigint
);

CREATE TABLE account_holders
(
    reference_id            SERIAL PRIMARY KEY                     NOT NULL,
    account_holder_id       uuid UNIQUE DEFAULT uuid_generate_v4() NOT NULL,
    user_id                 varchar,
    document_identification varchar(11),
    document_rel            varchar(3),
    account_holder_name     varchar,
    created_at              timestamp,
    created_by              varchar,
    updated_at              timestamp,
    updated_by              varchar,
    hibernate_status        varchar
);

CREATE TABLE account_holders_aud
(
    reference_id            integer NOT NULL,
    account_holder_id       uuid,
    user_id                 varchar,
    document_identification varchar(11),
    document_rel            varchar(3),
    account_holder_name     varchar,
    rev                     integer NOT NULL,
    revtype                                smallint,
    created_at                             timestamp,
    created_by                             varchar,
    updated_at                             timestamp,
    updated_by                             varchar,
    hibernate_status                       varchar,
    PRIMARY KEY (reference_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE accounts
(
    reference_id                           SERIAL PRIMARY KEY                     NOT NULL,
    account_id                             uuid UNIQUE DEFAULT uuid_generate_v4() NOT NULL,
    account_holder_id                      uuid,
    status                                 varchar,
    created_at                             timestamp,
    created_by                             varchar,
    updated_at                             timestamp,
    updated_by                             varchar,
    hibernate_status                       varchar,
    FOREIGN KEY (account_holder_id) REFERENCES account_holders (account_holder_id) ON DELETE CASCADE
);

CREATE TABLE accounts_aud
(
    reference_id                           integer NOT NULL,
    account_id                             uuid,
    status                                 varchar,
    account_holder_id                      uuid,
    rev                                    integer NOT NULL,
    revtype                                smallint,
    created_at                             timestamp,
    created_by                             varchar,
    updated_at                             timestamp,
    updated_by                             varchar,
    hibernate_status                       varchar,
    PRIMARY KEY (reference_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE consents
(
    reference_id                     SERIAL PRIMARY KEY NOT NULL,
    consent_id                       text UNIQUE,
    account_holder_id                uuid,
    expiration_date_time             timestamp,
    creation_date_time               timestamp,
    status_update_date_time          timestamp          NOT NULL,
    status                           varchar            NOT NULL,
    client_id                        varchar,
    business_document_identification varchar,
    business_document_rel            varchar,
    rejected_by                      varchar,
    rejection_code                   varchar,
    rejection_additional_information varchar,
    created_at                       timestamp,
    created_by                       varchar,
    updated_at                       timestamp,
    updated_by                       varchar,
    hibernate_status                 varchar,
    FOREIGN KEY (account_holder_id) REFERENCES account_holders (account_holder_id) ON DELETE CASCADE
);

CREATE TABLE consents_aud
(
    reference_id                     integer NOT NULL,
    consent_id                       text,
    account_holder_id                uuid,
    expiration_date_time             timestamp,
    creation_date_time               timestamp,
    status_update_date_time          timestamp,
    status                           varchar,
    client_id                        varchar,
    business_document_identification varchar,
    business_document_rel            varchar,
    rejected_by                      varchar,
    rejection_code                   varchar,
    rejection_additional_information varchar,
    rev                              integer NOT NULL,
    revtype                          smallint,
    created_at                       timestamp,
    created_by                       varchar,
    updated_at                       timestamp,
    updated_by                       varchar,
    hibernate_status                 varchar,
    PRIMARY KEY (reference_id, rev)
);

CREATE TABLE consent_permissions
(
    reference_id SERIAL PRIMARY KEY NOT NULL,
    consent_id          text,
    permission          varchar            NOT NULL,
    created_at          timestamp,
    created_by          varchar,
    updated_at          timestamp,
    updated_by          varchar,
    hibernate_status    varchar,
    FOREIGN KEY (consent_id) REFERENCES consents (consent_id) ON DELETE CASCADE
);

CREATE TABLE consent_permissions_aud
(
    reference_id integer NOT NULL,
    consent_id          text,
    permission          varchar,
    rev                 integer NOT NULL,
    revtype             smallint,
    created_at          timestamp,
    created_by          varchar,
    updated_at          timestamp,
    updated_by          varchar,
    hibernate_status    varchar,
    PRIMARY KEY (reference_id, rev)
);
