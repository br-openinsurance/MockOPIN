--- RECREATABLE MIGRATION FILE ---
--- This will be run every time flyway detects that its hash has changed ---
--- Needs to be kept up to date with schema changes in the other migrations ---

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Functions we're going to need to get the job done ...
CREATE OR REPLACE FUNCTION addAccountHolder(doc varchar, rel varchar, accountHolderName varchar, userId varchar) RETURNS uuid AS $$
    INSERT INTO account_holders (document_identification, document_rel, account_holder_name, user_id,
                                 created_at, created_by, updated_at, updated_by)
    VALUES (doc, rel, accountHolderName, userId,
            NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING account_holder_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION getAccountHolderId(docId varchar) RETURNS uuid AS $$
    SELECT account_holder_id FROM account_holders WHERE document_identification = docId;
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addAccountWithId(docId varchar, accountId uuid, status varchar) RETURNS uuid AS $$
    INSERT INTO accounts (account_holder_id, account_id, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), accountId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING account_id
$$ LANGUAGE SQL;



