-- ${flyway:timestamp}
DO $$DECLARE
    docId varchar := '76109277673';
    accountHolderName varchar := 'Ralph Bragg';
    accountHolderEmail varchar := 'ralph.bragg@gmail.com';
    account1Id varchar;
BEGIN
    PERFORM addAccountHolder(docId::varchar, 'CPF'::varchar, accountHolderName, accountHolderEmail);
    account1Id := addAccountWithId(docId, '291e5a29-49ed-401f-a583-193caa7aceee', 'AVAILABLE');
END $$;