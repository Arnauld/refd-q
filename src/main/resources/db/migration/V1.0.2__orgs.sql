-- tag::address_type[]
CREATE TYPE address_type AS (
    address1 TEXT,
    address2 TEXT,
    city     TEXT,
    zipcode  TEXT,
    country  TEXT
);
-- end::address_type[]

-- tag::organization[]
CREATE TABLE IF NOT EXISTS organizations (
    id                   SERIAL NOT NULL PRIMARY KEY,
    code                 TEXT   NOT NULL,
    label                JSONB  NOT NULL,
    legal_name           TEXT   NOT NULL,
    logo_id              INT REFERENCES images(id),
    postal_address       address_type,
    phone_number         TEXT,
    web_site             TEXT,
    contact_email        TEXT,
    social_networks      JSONB
);

CALL add_tenant_meta('organizations');
CALL add_tenant_trigger('organizations');
CALL add_tenant_isolation('organizations');
CALL add_audit_meta('organizations');
CALL add_audit_meta_trigger('organizations');
CALL add_delete_meta('organizations');
CALL add_audit_log_trigger('organizations', 'conf', audit_meta_fields() || audit_delete_fields());

CREATE UNIQUE INDEX organization_code_uniqueness ON organizations (tenant_id, code);
-- end::organization[]


-- tag::operators[]
CREATE TABLE IF NOT EXISTS operators (
    id                   SERIAL NOT NULL PRIMARY KEY,
    organization_id      INT    NOT NULL REFERENCES organizations(id),
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