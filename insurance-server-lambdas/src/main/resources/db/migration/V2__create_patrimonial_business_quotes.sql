CREATE TABLE patrimonial_business_quotes (
    quote_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    client_id            VARCHAR NOT NULL,
    consent_id           VARCHAR NOT NULL,
    status               VARCHAR NOT NULL,
    person_cpf           VARCHAR,
    business_cnpj        VARCHAR,
    expiration_date_time TIMESTAMP,
    data                 JSONB  NOT NULL,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR
);

CREATE TABLE patrimonial_business_quotes_aud (
    quote_id             UUID,
    client_id            VARCHAR NOT NULL,
    consent_id           VARCHAR NOT NULL,
    status               VARCHAR NOT NULL,
    person_cpf           VARCHAR,
    business_cnpj        VARCHAR,
    expiration_date_time TIMESTAMP,
    data                 JSONB  NOT NULL,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (quote_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE patrimonial_home_quotes (
    quote_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    client_id            VARCHAR NOT NULL,
    consent_id           VARCHAR NOT NULL,
    status               VARCHAR NOT NULL,
    person_cpf           VARCHAR,
    business_cnpj        VARCHAR,
    expiration_date_time TIMESTAMP,
    data                 JSONB  NOT NULL,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR
);

CREATE TABLE patrimonial_home_quotes_aud (
    quote_id             UUID,
    client_id            VARCHAR NOT NULL,
    consent_id           VARCHAR NOT NULL,
    status               VARCHAR NOT NULL,
    person_cpf           VARCHAR,
    business_cnpj        VARCHAR,
    expiration_date_time TIMESTAMP,
    data                 JSONB  NOT NULL,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (quote_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE patrimonial_condominium_quotes (
    quote_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    client_id            VARCHAR NOT NULL,
    consent_id           VARCHAR NOT NULL,
    status               VARCHAR NOT NULL,
    person_cpf           VARCHAR,
    business_cnpj        VARCHAR,
    expiration_date_time TIMESTAMP,
    data                 JSONB  NOT NULL,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR
);

CREATE TABLE patrimonial_condominium_quotes_aud (
    quote_id             UUID,
    client_id            VARCHAR NOT NULL,
    consent_id           VARCHAR NOT NULL,
    status               VARCHAR NOT NULL,
    person_cpf           VARCHAR,
    business_cnpj        VARCHAR,
    expiration_date_time TIMESTAMP,
    data                 JSONB  NOT NULL,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (quote_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE patrimonial_diverse_risks_quotes (
    quote_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    client_id            VARCHAR NOT NULL,
    consent_id           VARCHAR NOT NULL,
    status               VARCHAR NOT NULL,
    person_cpf           VARCHAR,
    business_cnpj        VARCHAR,
    expiration_date_time TIMESTAMP,
    data                 JSONB  NOT NULL,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR
);

CREATE TABLE patrimonial_diverse_risks_quotes_aud (
    quote_id             UUID,
    client_id            VARCHAR NOT NULL,
    consent_id           VARCHAR NOT NULL,
    status               VARCHAR NOT NULL,
    person_cpf           VARCHAR,
    business_cnpj        VARCHAR,
    expiration_date_time TIMESTAMP,
    data                 JSONB  NOT NULL,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (quote_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);