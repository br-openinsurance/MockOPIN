--mock_read_user needs write permission to be able to update status when reading (mock behaviour)
--temp solution to asses a fix and then remove this
GRANT INSERT, UPDATE, SELECT, DELETE ON ALL TABLES IN SCHEMA public TO mock_read_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO mock_read_user;
GRANT SELECT ON ALL SEQUENCES IN SCHEMA public TO mock_read_user;