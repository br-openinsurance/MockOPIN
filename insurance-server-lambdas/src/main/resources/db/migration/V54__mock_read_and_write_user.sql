--mock read user
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_catalog.pg_user WHERE usename = 'mock_read_user') THEN
        IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'rds_iam') THEN
            CREATE USER mock_read_user WITH LOGIN;
GRANT rds_iam TO mock_read_user;
ELSE
            CREATE USER mock_read_user WITH PASSWORD 'password';
END IF;
END IF;
END $$;

--GRANT SELECT
GRANT SELECT ON ALL TABLES IN SCHEMA public TO mock_read_user;
GRANT SELECT ON ALL SEQUENCES IN SCHEMA public TO mock_read_user;
--mock write user
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_catalog.pg_user WHERE usename = 'mock_write_user') THEN
        IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'rds_iam') THEN
            CREATE USER mock_write_user WITH LOGIN;
GRANT rds_iam TO mock_write_user;
ELSE
            CREATE USER mock_write_user WITH PASSWORD 'password';
END IF;
END IF;
END $$;

GRANT INSERT, UPDATE, SELECT, DELETE ON ALL TABLES IN SCHEMA public TO mock_write_user;

--mock_write_user needs sequence privileges to insert into tables with serial/identity columns
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO mock_write_user;
