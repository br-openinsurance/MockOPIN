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

CREATE OR REPLACE FUNCTION addPersonalIdentification(docId varchar) RETURNS uuid AS $$
    INSERT INTO personal_identifications (account_holder_id, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING personal_identification_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPersonalQualification(docId varchar) RETURNS uuid AS $$
    INSERT INTO personal_qualifications (account_holder_id, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING personal_qualification_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPersonalComplimentaryInfo(docId varchar) RETURNS uuid AS $$
    INSERT INTO personal_complimentary_information (account_holder_id, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING personal_complimentary_information_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addBusinessIdentification(docId varchar, cnpj varchar) RETURNS uuid AS $$
    INSERT INTO business_identifications (account_holder_id, cnpj_number, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), cnpj, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING business_identification_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addBusinessQualification(docId varchar) RETURNS uuid AS $$
    INSERT INTO business_qualifications (account_holder_id, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING business_qualification_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addBusinessComplimentaryInfo(docId varchar) RETURNS uuid AS $$
    INSERT INTO business_complimentary_information (account_holder_id, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING business_complimentary_information_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addCapitalizationTitlePlan(docId varchar, titleId varchar, s varchar) RETURNS uuid AS $$
    INSERT INTO capitalization_title_plans (account_holder_id, capitalization_title_id, status, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), titleId, s, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING capitalization_title_plan_id
$$ LANGUAGE SQL;


CREATE OR REPLACE FUNCTION addCapitalizationTitlePlanSeries(planId uuid, susepProcessNumber varchar) RETURNS uuid AS $$
    INSERT INTO capitalization_title_plan_series(capitalization_title_plan_id, susep_process_number, created_at, created_by, updated_at, updated_by)
    VALUES (planId, susepProcessNumber, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING capitalization_title_plan_series_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addCapitalizationTitlePlanEvent(planId uuid, eventType varchar) RETURNS uuid AS $$
    INSERT INTO capitalization_title_plan_events(capitalization_title_plan_id, event_type, created_at, created_by, updated_at, updated_by)
    VALUES (planId, eventType, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING capitalization_title_plan_event_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addCapitalizationTitlePlanSettlement(planId uuid) RETURNS uuid AS $$
    INSERT INTO capitalization_title_plan_settlements(capitalization_title_plan_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING capitalization_title_plan_settlement_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addFinancialRiskPolicy(docId varchar, titleId varchar, s varchar) RETURNS uuid AS $$
    INSERT INTO financial_risk_policies (account_holder_id, financial_risk_id, status, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), titleId, s, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_risk_policy_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addFinancialRiskPolicyClaim(planId uuid) RETURNS uuid AS $$
    INSERT INTO financial_risk_policy_claims(financial_risk_policy_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_risk_policy_claim_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addFinancialRiskPolicyPremium(planId uuid) RETURNS uuid AS $$
    INSERT INTO financial_risk_policy_premiums(financial_risk_policy_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_risk_policy_premium_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addHousingPolicy(docId varchar, titleId varchar, s varchar) RETURNS uuid AS $$
    INSERT INTO housing_policies (account_holder_id, housing_id, status, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), titleId, s, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING housing_policy_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addHousingPolicyClaim(planId uuid) RETURNS uuid AS $$
    INSERT INTO housing_policy_claims(housing_policy_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING housing_policy_claim_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addHousingPolicyPremium(planId uuid) RETURNS uuid AS $$
    INSERT INTO housing_policy_premiums(housing_policy_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING housing_policy_premium_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addResponsibilityPolicy(docId varchar, titleId varchar, s varchar) RETURNS uuid AS $$
    INSERT INTO responsibility_policies (account_holder_id, responsibility_id, status, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), titleId, s, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING responsibility_policy_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addResponsibilityPolicyClaim(planId uuid) RETURNS uuid AS $$
    INSERT INTO responsibility_policy_claims(responsibility_policy_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING responsibility_policy_claim_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addResponsibilityPolicyPremium(planId uuid) RETURNS uuid AS $$
    INSERT INTO responsibility_policy_premiums(responsibility_policy_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING responsibility_policy_premium_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPersonPolicy(docId varchar, titleId varchar, s varchar) RETURNS uuid AS $$
    INSERT INTO person_policies (account_holder_id, person_id, status, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), titleId, s, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING person_policy_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPersonPolicyClaim(planId uuid) RETURNS uuid AS $$
    INSERT INTO person_policy_claims(person_policy_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING person_policy_claim_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPersonPolicyPremium(planId uuid) RETURNS uuid AS $$
    INSERT INTO person_policy_premiums(person_policy_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING person_policy_premium_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addLifePensionContract(docId varchar, titleId varchar, s varchar) RETURNS uuid AS $$
    INSERT INTO life_pension_contracts (account_holder_id, life_pension_id, status, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), titleId, s, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING life_pension_contract_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addLifePensionContractClaim(planId uuid) RETURNS uuid AS $$
    INSERT INTO life_pension_contract_claims(life_pension_contract_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING life_pension_contract_claim_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addLifePensionContractWithdrawal(planId uuid) RETURNS uuid AS $$
    INSERT INTO life_pension_contract_withdrawals(life_pension_contract_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING life_pension_contract_withdrawal_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addLifePensionContractPortability(planId uuid) RETURNS uuid AS $$
    INSERT INTO life_pension_contract_portabilities(life_pension_contract_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING life_pension_contract_portability_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addLifePensionContractMovementBenefit(planId uuid) RETURNS uuid AS $$
    INSERT INTO life_pension_contract_movement_benefits(life_pension_contract_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING life_pension_contract_movement_benefit_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addLifePensionContractMovementContribution(planId uuid) RETURNS uuid AS $$
    INSERT INTO life_pension_contract_movement_contributions(life_pension_contract_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING life_pension_contract_movement_contribution_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPensionPlanContract(planId varchar, docId varchar, s varchar) RETURNS varchar AS $$
    INSERT INTO pension_plan_contracts (pension_plan_contract_id, account_holder_id, status, created_at, created_by, updated_at, updated_by)
    VALUES (planId, getAccountHolderId(docId), s, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING pension_plan_contract_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPensionPlanContractClaim(planId varchar) RETURNS uuid AS $$
    INSERT INTO pension_plan_contract_claims(pension_plan_contract_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING pension_plan_contract_claim_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPensionPlanContractWithdrawal(planId varchar) RETURNS uuid AS $$
    INSERT INTO pension_plan_contract_withdrawals(pension_plan_contract_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING pension_plan_contract_withdrawal_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPensionPlanContractPortability(planId varchar) RETURNS uuid AS $$
    INSERT INTO pension_plan_contract_portabilities(pension_plan_contract_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING pension_plan_contract_portability_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPensionPlanContractMovementBenefit(planId varchar) RETURNS uuid AS $$
    INSERT INTO pension_plan_contract_movement_benefits(pension_plan_contract_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING pension_plan_contract_movement_benefit_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPensionPlanContractMovementContribution(planId varchar) RETURNS uuid AS $$
    INSERT INTO pension_plan_contract_movement_contributions(pension_plan_contract_id, created_at, created_by, updated_at, updated_by)
    VALUES (planId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING pension_plan_contract_movement_contribution_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addAcceptanceAndBranchesAbroadPolicy(docId varchar, insuranceId varchar, s varchar) RETURNS uuid AS $$
    INSERT INTO acceptance_and_branches_abroad_policies (account_holder_id, insurance_id, status, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), insuranceId, s, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING policy_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addAcceptanceAndBranchesAbroadClaim(policyId uuid, identification varchar) RETURNS uuid AS $$
    INSERT INTO acceptance_and_branches_abroad_policy_claims(policy_id, identification, created_at, created_by, updated_at, updated_by)
    VALUES (policyId, identification, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING claim_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPatrimonialPolicy(docId varchar, insuranceId varchar, s varchar, branch varchar) RETURNS uuid AS $$
    INSERT INTO patrimonial_policies (account_holder_id, insurance_id, status, branch, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), insuranceId, s, branch, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING policy_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPatrimonialClaim(policyId uuid, identification varchar) RETURNS uuid AS $$
    INSERT INTO patrimonial_policy_claims(policy_id, identification, created_at, created_by, updated_at, updated_by)
    VALUES (policyId, identification, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING claim_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addRuralPolicy(docId varchar, insuranceId varchar, s varchar) RETURNS uuid AS $$
    INSERT INTO rural_policies (account_holder_id, insurance_id, status, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), insuranceId, s, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING policy_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addRuralClaim(policyId uuid, identification varchar) RETURNS uuid AS $$
    INSERT INTO rural_policy_claims(policy_id, identification, created_at, created_by, updated_at, updated_by)
    VALUES (policyId, identification, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING claim_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addFinancialAssistanceContract(docId varchar, contractId varchar, s varchar) RETURNS varchar AS $$
    INSERT INTO financial_assistance_contracts (account_holder_id, financial_assistance_contract_id, status, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), contractId, s, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_assistance_contract_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addFinancialAssistanceContractMovement(contractId varchar) RETURNS uuid AS $$
    INSERT INTO financial_assistance_contract_movements(financial_assistance_contract_id, created_at, created_by, updated_at, updated_by)
    VALUES (contractId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_assistance_contract_movement_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addAutoPolicy(docId varchar, policyId varchar, s varchar, docType varchar, proposalId varchar) RETURNS varchar AS $$
    INSERT INTO auto_policies(account_holder_id, auto_policy_id, status, document_type, proposal_id, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), policyId, s, docType, proposalId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING auto_policy_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addAutoPolicyClaim(policyId varchar, s varchar) RETURNS varchar AS $$
    INSERT INTO auto_policy_claims(auto_policy_claim_id, status, created_at, created_by, updated_at, updated_by)
    VALUES (policyId, s, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING auto_policy_claim_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addTransportPolicy(docId varchar, policyId varchar, s varchar, docType varchar, proposalId varchar) RETURNS varchar AS $$
    INSERT INTO transport_policies(account_holder_id, transport_policy_id, status, document_type, proposal_id, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), policyId, s, docType, proposalId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING transport_policy_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addTransportPolicyClaim(policyId varchar, s varchar) RETURNS varchar AS $$
    INSERT INTO transport_policy_claims(transport_policy_claim_id, status, created_at, created_by, updated_at, updated_by)
    VALUES (policyId, s, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING transport_policy_claim_id
$$ LANGUAGE SQL;
