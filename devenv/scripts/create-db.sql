CALL create_role_if_not_exists('busd_r', 'WITH PASSWORD ''busd_p'' LOGIN BYPASSRLS');
CALL create_database_if_not_exists('busd',
                                   'WITH TEMPLATE = template0' ||
                                   'ENCODING = ''UTF8''' ||
                                   'TABLESPACE = pg_default' ||
                                   'LC_COLLATE = ''en_US.utf8''' ||
                                   'LC_CTYPE = ''en_US.utf8''' ||
                                   'CONNECTION LIMIT = 255');
ALTER DATABASE busd OWNER TO busd_r;
GRANT CONNECT ON DATABASE busd TO busd_r;
