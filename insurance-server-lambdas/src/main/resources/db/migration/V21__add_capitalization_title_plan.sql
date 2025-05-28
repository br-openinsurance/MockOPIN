CREATE TABLE capitalization_title_plans (
    capitalization_title_plan_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    capitalization_title_id VARCHAR,
    status VARCHAR,
    account_holder_id                      UUID,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (account_holder_id) REFERENCES account_holders (account_holder_id) ON DELETE CASCADE
);

CREATE TABLE capitalization_title_plans_aud (
    capitalization_title_plan_id             UUID,
    capitalization_title_id VARCHAR,
    status VARCHAR,
    account_holder_id UUID,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (capitalization_title_plan_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE capitalization_title_plan_series (
    capitalization_title_plan_series_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    capitalization_title_plan_id                      UUID,
    susep_process_number VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (capitalization_title_plan_id) REFERENCES capitalization_title_plans (capitalization_title_plan_id) ON DELETE CASCADE
);

CREATE TABLE capitalization_title_plan_series_aud (
    capitalization_title_plan_series_id             UUID,
    capitalization_title_plan_id UUID,
    susep_process_number VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (capitalization_title_plan_series_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE capitalization_title_plan_events (
    capitalization_title_plan_event_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    capitalization_title_plan_id                      UUID,
    event_type VARCHAR,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (capitalization_title_plan_id) REFERENCES capitalization_title_plans (capitalization_title_plan_id) ON DELETE CASCADE
);

CREATE TABLE capitalization_title_plan_events_aud (
    capitalization_title_plan_event_id             UUID,
    capitalization_title_plan_id UUID,
    event_type VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (capitalization_title_plan_event_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE capitalization_title_plan_settlements (
    capitalization_title_plan_settlement_id             UUID PRIMARY KEY DEFAULT uuid_generate_v4() NOT NULL,
    capitalization_title_plan_id                      UUID,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (capitalization_title_plan_id) REFERENCES capitalization_title_plans (capitalization_title_plan_id) ON DELETE CASCADE
);

CREATE TABLE capitalization_title_plan_settlements_aud (
    capitalization_title_plan_settlement_id             UUID,
    capitalization_title_plan_id UUID,
    settlement_id VARCHAR,
    rev                  INTEGER NOT NULL,
    revtype              SMALLINT,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    PRIMARY KEY (capitalization_title_plan_settlement_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE consent_capitalization_title_plans
(
    reference_id SERIAL PRIMARY KEY NOT NULL,
    consent_id   varchar,
    capitalization_title_plan_id   uuid,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    FOREIGN KEY (consent_id) REFERENCES consents (consent_id) ON DELETE CASCADE,
    FOREIGN KEY (capitalization_title_plan_id) REFERENCES capitalization_title_plans (capitalization_title_plan_id) ON DELETE CASCADE
);

CREATE TABLE consent_capitalization_title_plans_aud
(
    reference_id integer NOT NULL,
    consent_id   varchar,
    capitalization_title_plan_id   uuid,
    created_at           TIMESTAMP,
    created_by           VARCHAR,
    updated_at           TIMESTAMP,
    updated_by           VARCHAR,
    hibernate_status     VARCHAR,
    rev          integer NOT NULL,
    revtype              SMALLINT,
    PRIMARY KEY (reference_id, rev)
);
