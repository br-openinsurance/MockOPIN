

CREATE TABLE dynamic_fields (
    dynamic_field_id UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    api VARCHAR,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR
);

CREATE TABLE dynamic_fields_aud (
    dynamic_field_id UUID,
    api VARCHAR,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    created_at TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP,
    updated_by VARCHAR,
    hibernate_status VARCHAR,
    PRIMARY KEY (dynamic_field_id, rev)
);
