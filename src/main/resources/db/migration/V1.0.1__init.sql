--
--
-- AUDIT
--
--

-- tag::audit_caller_type[]
CREATE TYPE caller_type AS ENUM('AGENT', 'CUSTOMER', 'SERVICE');
-- end::audit_caller_type[]

-- tag::audit_meta[]
CREATE OR REPLACE PROCEDURE add_audit_meta(table_name text)
  LANGUAGE plpgsql AS
$proc$
BEGIN
EXECUTE format('ALTER TABLE %I ADD COLUMN created_at TIMESTAMP WITH TIME ZONE NOT NULL', table_name);
EXECUTE format('ALTER TABLE %I ADD COLUMN created_by TEXT                     NOT NULL', table_name);
EXECUTE format('ALTER TABLE %I ADD COLUMN created_by_type caller_type         NOT NULL', table_name);
EXECUTE format('ALTER TABLE %I ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL', table_name);
EXECUTE format('ALTER TABLE %I ADD COLUMN updated_by TEXT                     NOT NULL', table_name);
EXECUTE format('ALTER TABLE %I ADD COLUMN updated_by_type caller_type         NOT NULL', table_name);
END
$proc$;
-- end::audit_meta[]


-- tag::audit_meta_fields[]
CREATE OR REPLACE FUNCTION audit_meta_fields() RETURNS TEXT[]
LANGUAGE plpgsql AS
$proc$
BEGIN
RETURN '{"created_at", "created_by", "created_by_type", "updated_at", "updated_by", "updated_by_type"}'::TEXT[];
END
$proc$;
-- end::audit_meta_fields[]

-- tag::audit_deletable[]
CREATE OR REPLACE PROCEDURE add_delete_meta(table_name text)
  LANGUAGE plpgsql AS
$proc$
BEGIN
EXECUTE format('ALTER TABLE %I ADD COLUMN deleted_at TIMESTAMP WITH TIME ZONE', table_name);
EXECUTE format('ALTER TABLE %I ADD COLUMN deleted_by TEXT                    ', table_name);
EXECUTE format('ALTER TABLE %I ADD COLUMN deleted_by_type caller_type        ', table_name);
END
$proc$;
-- end::audit_deletable[]


-- tag::audit_delete_fields[]
CREATE OR REPLACE FUNCTION audit_delete_fields() RETURNS TEXT[]
LANGUAGE plpgsql AS
$proc$
BEGIN
RETURN '{"deleted_at", "deleted_by", "deleted_by_type"}'::TEXT[];
END
$proc$;
-- end::audit_delete_fields[]


-- tag::audit_meta_trigger[]
CREATE OR REPLACE FUNCTION audit_meta_trigger_func()
RETURNS trigger AS $body$
DECLARE
caller_id   TEXT;
    caller_type caller_type;
    overrides   BOOLEAN;
BEGIN
    overrides = TRUE;
    IF current_setting('var.bypass_audit_meta', 't') IS NOT NULL THEN
        overrides = FALSE;
    END IF;

    caller_id   = current_setting('var.caller_id');
    caller_type = current_setting('var.caller_type')::caller_type;

    IF (TG_OP = 'INSERT') THEN
        IF (overrides OR NEW.created_at IS NULL) THEN
            NEW.created_at = now();
        END IF;
        IF (overrides OR NEW.created_by IS NULL) THEN
            NEW.created_by = caller_id;
        END IF;
        IF (overrides OR NEW.created_by_type IS NULL) THEN
            NEW.created_by_type = caller_type;
        END IF;
    END IF;

--    IF (TG_OP = 'UPDATE') THEN
--        IF (NEW.created_at != OLD.created_at) THEN
--            RAISE ERROR 'Cannot update created_at once entity exists'
--        END IF;
--    END IF;

    IF (overrides OR NEW.updated_at IS NULL) THEN
        NEW.updated_at = now();
    END IF;
    IF (overrides OR NEW.updated_by IS NULL) THEN
        NEW.updated_by = caller_id;
    END IF;
    IF (overrides OR NEW.updated_by_type IS NULL) THEN
        NEW.updated_by_type = caller_type;
    END IF;

    RETURN NEW;
END;
$body$
LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE add_audit_meta_trigger(table_name text)
  LANGUAGE plpgsql AS
$proc$
BEGIN
EXECUTE format('CREATE TRIGGER %1$I_audit_meta_trigger'
                   ' BEFORE INSERT OR UPDATE ON %1$I'
                   ' FOR EACH ROW EXECUTE FUNCTION audit_meta_trigger_func()',
               table_name);
END
$proc$;
-- end::audit_meta_trigger[]

--
--
-- TENANT
--
--

-- tag::tenant[]
CREATE TABLE IF NOT EXISTS tenants (
       id   SERIAL NOT NULL PRIMARY KEY,
       code TEXT   NOT NULL UNIQUE,
       name TEXT   NOT NULL UNIQUE
);

CREATE OR REPLACE PROCEDURE add_tenant_meta(table_name text)
  LANGUAGE plpgsql AS
$proc$
BEGIN
EXECUTE format('ALTER TABLE %I ADD COLUMN tenant_id INT NOT NULL REFERENCES tenants (id) ON DELETE RESTRICT', table_name);
END
$proc$;
-- end::tenant[]

-- tag::tenant_trigger[]
CREATE OR REPLACE FUNCTION tenant_trigger_func()
RETURNS trigger AS $body$
-- DECLARE
--    overrides BOOLEAN;
BEGIN
--    overrides = TRUE;
--    IF current_setting('var.bypass_tenant', 't') IS NOT NULL THEN
--        overrides = FALSE;
--    END IF;

    IF (TG_OP = 'INSERT') THEN
        NEW.tenant_id = current_setting('var.tenant_id');
END IF;

    IF (TG_OP = 'UPDATE' AND OLD.tenant_id != NEW.tenant_id) THEN
        RAISE EXCEPTION 'Tenant cannot be changed';
END IF;

RETURN NEW;
END;
$body$
LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE add_tenant_trigger(table_name text)
  LANGUAGE plpgsql AS
$proc$
BEGIN
EXECUTE format('CREATE TRIGGER %1$I_tenant_trigger'
                   ' BEFORE INSERT OR UPDATE ON %1$I'
                   ' FOR EACH ROW EXECUTE FUNCTION tenant_trigger_func()',
               table_name);
END
$proc$;
-- end::tenant_trigger[]

-- tag::tenant_isolation[]
CREATE OR REPLACE PROCEDURE add_tenant_isolation(table_name text)
  LANGUAGE plpgsql AS
$proc$
BEGIN
EXECUTE format('CREATE POLICY tenant_isolation_policy'
                   ' ON %I'
                   ' USING'
                   ' (tenant_id = current_setting(''var.tenant_id'')::Integer)'
                   ' WITH CHECK '
                   ' (tenant_id = current_setting(''var.tenant_id'')::Integer);', table_name);
EXECUTE format('ALTER TABLE %I ENABLE ROW LEVEL SECURITY;', table_name);
END
$proc$;
-- end::tenant_isolation[]

-- ==================================================================
--
-- AUDIT LOG
--
-- ==================================================================
-- tag:hstore[]
CREATE EXTENSION IF NOT EXISTS hstore;
-- end:hstore[]

-- tag:audit_log_table[]
CREATE TYPE audit_log_category_type AS ENUM ('tenant', 'shared', 'conf', 'topology', 'catalog');

CREATE SEQUENCE audit_log_id_seq;
CREATE TABLE audit_log (
                           id BIGINT NOT NULL DEFAULT nextval('audit_log_id_seq') PRIMARY KEY,
                           transaction_id  BIGINT,
                           category         audit_log_category_type,
    -- WHO
                           tenant_id       INT REFERENCES tenants(id), -- tenant is declared to keep known order for trigger
                           changed_by      TEXT NOT NULL,
                           changed_by_type caller_type NOT NULL,
    -- CHANGES
                           changed_at         TIMESTAMP WITH TIME ZONE NOT NULL,
                           changed_table_name TEXT NOT NULL,
                           changed_id         BIGINT NOT NULL, -- changed primary key
                           changed_type       TEXT NOT NULL CHECK (changed_type IN ('I','D','U','T')),
                           changed_fields     JSONB
);
-- end:audit_log_table[]

-- tag:audit_log_trigger[]
CREATE OR REPLACE FUNCTION audit_log_trigger_func()
RETURNS trigger AS $body$
DECLARE
category        audit_log_category_type;
    excluded_cols   text[] = ARRAY[]::text[];
    audit_row       audit_log;
    changed_fields  HSTORE;
    before_fields   JSONB;
    after_fields    JSONB;
    diff            JSONB;
    tenant_id       INT;
BEGIN
    category = TG_ARGV[0]::audit_log_category_type;
    IF TG_ARGV[1] IS NOT NULL THEN
        excluded_cols = TG_ARGV[1]::text[];
END IF;

    -- special case
    IF (TG_TABLE_NAME != 'tenants') THEN
        tenant_id = current_setting('var.tenant_id');
ELSE
        tenant_id = NEW.id;
END IF;

    audit_row = ROW(
            nextval('audit_log_id_seq'),        -- id
            txid_current(),                     -- transaction_id
            category,
            -- CallingContext...
            tenant_id,                           -- tenant_id
            current_setting('var.caller_id'),    -- caller_id
            current_setting('var.caller_type')::caller_type, -- caller_id_type
            --
            CURRENT_TIMESTAMP,                  -- changed_at
            TG_TABLE_NAME,                      -- changed_table_name
            NEW.id,                             -- changed_id
            substring(TG_OP,1,1),               -- changed_type
            NULL                                -- changed_fields
       );

    IF (TG_OP = 'UPDATE') THEN
        -- removes all matching key/value pairs from the 1st hstore that appear in the 2nd hstore
        -- removes the key/value pairs where the keys are found in the array of strings
        -- then convert the hstore to jsonb
        changed_fields =  (hstore(NEW.*) - hstore(OLD.*)) - excluded_cols;
        IF changed_fields = hstore('') THEN
            -- All changed fields are ignored. Skip this update.
            RETURN NEW;
END IF;
        after_fields = hstore_to_json(changed_fields);
        changed_fields = (hstore(OLD.*) - hstore(NEW.*)) - excluded_cols;
        before_fields = hstore_to_json(changed_fields);

    ELSIF (TG_OP = 'DELETE' AND TG_LEVEL = 'ROW') THEN
        before_fields = hstore_to_json(hstore(OLD.*) - excluded_cols);
        after_fields  = '{}';
    ELSIF (TG_OP = 'INSERT' AND TG_LEVEL = 'ROW') THEN
        before_fields = '{}';
        after_fields = hstore_to_json(hstore(NEW.*) - excluded_cols);
END IF;

    diff = jsonb_set('{}', '{"before"}', before_fields);
    diff = jsonb_set(diff, '{"after"}', after_fields);
    audit_row.changed_fields = diff;

INSERT INTO audit_log VALUES (audit_row.*);
RETURN NEW;
END;
$body$
LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE add_audit_log_trigger(table_name text, category audit_log_category_type, excluded_cols text[] default ARRAY[]::text[])
  LANGUAGE plpgsql AS
$proc$
BEGIN
    -- TODO
    -- ASSERT table has a `id` field
EXECUTE format('CREATE TRIGGER %1$I_audit_trigger'
                   ' AFTER INSERT OR UPDATE OR DELETE ON %1$I'
                   ' FOR EACH ROW EXECUTE FUNCTION audit_log_trigger_func(%2$I, %3$L)',
               table_name, category, excluded_cols);
END
$proc$;
-- end:audit_log_trigger[]


--tag::tenant_post_creation[]
CALL add_audit_meta('tenants');
CALL add_audit_meta_trigger('tenants');
CALL add_audit_log_trigger('tenants', 'tenant');
CALL add_delete_meta('tenants');
--tag::tenant_post_creation[]

-- ==================================================================
--
-- IMAGE
--
-- ==================================================================

-- tag::image[]
CREATE TABLE IF NOT EXISTS images (
                                      id           SERIAL NOT NULL PRIMARY KEY,
                                      name         TEXT   NOT NULL,
                                      content      BYTEA,
                                      content_type TEXT,
                                      metadata     JSONB
);
CALL add_tenant_meta('images');
CALL add_tenant_trigger('images');
CALL add_tenant_isolation('images');
CALL add_audit_meta('images');
CALL add_audit_meta_trigger('images');
CALL add_audit_log_trigger('images', 'shared');
-- end::image[]
