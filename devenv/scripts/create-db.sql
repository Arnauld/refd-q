CALL create_role_if_not_exists('busd_r', 'WITH PASSWORD ''busd_p'' LOGIN BYPASSRLS');

CREATE DATABASE busd WITH
    TEMPLATE = template0
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    CONNECTION LIMIT = 255;
