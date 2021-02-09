CREATE OR REPLACE PROCEDURE create_role_if_not_exists(role_name text, stmt text)
    LANGUAGE plpgsql AS
$proc$
BEGIN
    IF NOT EXISTS (
            SELECT FROM pg_catalog.pg_roles  -- SELECT list can be empty for this
            WHERE  rolname = role_name) THEN
        EXECUTE format('CREATE ROLE %I %S', role_name, stmt);
    END IF;
END
$proc$;
-- ;;

CREATE OR REPLACE PROCEDURE create_database_if_not_exists(database_name text, stmt text)
    LANGUAGE plpgsql AS
$proc$
BEGIN
    IF NOT EXISTS (
            SELECT FROM pg_database WHERE datname = database_name) THEN
        EXECUTE format('CREATE DATABASE %I %S', database_name, stmt);
    END IF;
END
$proc$;
-- ;;


