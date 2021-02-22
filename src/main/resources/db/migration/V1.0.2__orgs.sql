-- tag::address_type[]
CREATE TYPE address_type AS (
    address1 TEXT,
    address2 TEXT,
    city     TEXT,
    zipcode  TEXT,
    country  TEXT
);
-- end::address_type[]

-- tag::authorities[]
CREATE TABLE IF NOT EXISTS authorities (
    id                   SERIAL NOT NULL PRIMARY KEY,
    code                 TEXT   NOT NULL,
    label                JSONB  NOT NULL,
    legal_name           TEXT   NOT NULL,
    timezone             TEXT   NOT NULL,
    logo_id              INT REFERENCES images(id),
    postal_address       address_type,
    phone_number         TEXT,
    web_site             TEXT,
    contact_email        TEXT,
    social_networks      JSONB
);

CALL add_tenant_meta('authorities');
CALL add_tenant_trigger('authorities');
CALL add_tenant_isolation('authorities');
CALL add_audit_meta('authorities');
CALL add_audit_meta_trigger('authorities');
CALL add_delete_meta('authorities');
CALL add_audit_log_trigger('authorities', 'conf', audit_meta_fields() || audit_delete_fields());

CREATE UNIQUE INDEX authority_code_uniqueness ON authorities (tenant_id, code);
-- end::authorities[]


-- tag::operators[]
CREATE TABLE IF NOT EXISTS operators (
    id                   SERIAL NOT NULL PRIMARY KEY,
    authority_id         INT    NOT NULL REFERENCES authorities(id),
    parent_id            INT             REFERENCES operators(id),
    code                 TEXT   NOT NULL,
    deactivation_date    TIMESTAMP WITH TIME ZONE,
    label                JSONB  NOT NULL,
    legal_name           TEXT   NOT NULL,
    logo_id              INT REFERENCES images(id),
    capital_amount       TEXT,
    registration_number  TEXT,
    vat_number           TEXT,
    head_office_address  address_type,
    postal_address       address_type,
    phone_number         TEXT,
    web_site             TEXT,
    contact_email        TEXT,
    social_networks      JSONB
);

CALL add_tenant_meta('operators');
CALL add_tenant_trigger('operators');
CALL add_tenant_isolation('operators');
CALL add_audit_meta('operators');
CALL add_audit_meta_trigger('operators');
CALL add_delete_meta('operators');
CALL add_audit_log_trigger('operators', 'conf', audit_meta_fields() || audit_delete_fields());

CREATE UNIQUE INDEX operator_code_uniqueness ON operators (tenant_id, code);
-- end::operators[]