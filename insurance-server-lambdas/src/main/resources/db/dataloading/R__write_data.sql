-- ${flyway:timestamp}
-- First, drop *all* existing data. Yes all of it. But try not to step on flyway
-- and delete the records from 'revinfo' at last.
DO $$DECLARE
    r RECORD;
BEGIN
    FOR r IN (SELECT tablename
              FROM pg_tables
              WHERE schemaname = current_schema()
              AND tablename NOT LIKE '%flyway%') LOOP
        EXECUTE 'TRUNCATE TABLE ' || quote_ident(r.tablename) || ' CASCADE;';
    END LOOP;
END $$;

-- Now insert the people and their accounts - Moved to separate files for each CPF