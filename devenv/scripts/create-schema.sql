CREATE SCHEMA IF NOT EXISTS dev0;
CALL create_role_if_not_exists('busd_dev0', 'WITH PASSWORD ''busd_p'' SUPERUSER INHERIT NOCREATEROLE NOCREATEDB NOLOGIN NOREPLICATION BYPASSRLS');
ALTER  SCHEMA dev0 OWNER TO busd_dev0;
ALTER  ROLE busd_dev0 IN DATABASE busd SET search_path to dev0;

CALL create_role_if_not_exists('busd_dev0_app', 'WITH PASSWORD ''busd_p'' NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS');

ALTER ROLE busd_dev0_app IN DATABASE busd SET search_path to dev0;

GRANT SELECT,UPDATE,INSERT,DELETE ON ALL TABLES    IN SCHEMA dev0 TO busd_dev0_app;
GRANT SELECT,UPDATE,USAGE         ON ALL SEQUENCES IN SCHEMA dev0 TO busd_dev0_app;
GRANT USAGE                       ON                  SCHEMA dev0 TO busd_dev0_app;
-- limit privileges that will be applied to objects created in the future, e.g. new tables
ALTER DEFAULT PRIVILEGES FOR ROLE busd_dev0 IN SCHEMA dev0 GRANT SELECT,UPDATE,INSERT,DELETE ON TABLES    TO busd_dev0_app;
ALTER DEFAULT PRIVILEGES FOR ROLE busd_dev0 IN SCHEMA dev0 GRANT SELECT,UPDATE,USAGE         ON SEQUENCES TO busd_dev0_app;

CALL create_role_if_not_exists('busd_dev0_mig', 'WITH PASSWORD ''busd_p'' SUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION BYPASSRLS');
ALTER ROLE busd_dev0_mig IN DATABASE busd SET search_path to dev0;
GRANT busd_dev0 TO busd_dev0_mig;
