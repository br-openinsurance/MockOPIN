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

CREATE OR REPLACE FUNCTION addCapitalizationTitlePlan(docId varchar, titleId varchar, status varchar,
productName varchar) RETURNS uuid AS $$
    INSERT INTO capitalization_title_plans (account_holder_id, capitalization_title_id, status, product_name,
    created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), titleId, status, productName, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING capitalization_title_plan_id
$$ LANGUAGE SQL;


CREATE OR REPLACE FUNCTION addCapitalizationTitlePlanSeries(planId uuid, susepProcessNumber varchar, modality varchar,
commercialDenomination varchar, serieSize int, gracePeriodRedemption int, gracePeriodForFullRedemption int,
updateIndex varchar, updateIndexOthers varchar, readjustmentIndex varchar, readjustmentIndexOthers varchar,
bonusClause boolean, frequency varchar, frequencyDescription varchar, interestRate varchar) RETURNS uuid AS $$
    INSERT INTO capitalization_title_plan_series(capitalization_title_plan_id, susep_process_number, modality,
    commercial_denomination, serie_size, grace_period_redemption, grace_period_for_full_redemption, update_index,
    update_index_others, readjustment_index, readjustment_index_others, bonus_clause, frequency, frequency_description,
    interest_rate, created_at, created_by, updated_at, updated_by)
    VALUES (planId, susepProcessNumber, modality, commercialDenomination, serieSize, gracePeriodRedemption,
    gracePeriodForFullRedemption, updateIndex, updateIndexOthers, readjustmentIndex, readjustmentIndexOthers,
    bonusClause, frequency, frequencyDescription, interestRate, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING capitalization_title_plan_series_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addCapitalizationTitlePlanEventRaffle(planId uuid, eventType varchar, raffleDate date,
raffleSettlementDate date, raffleAmount varchar, raffleUnitType varchar, raffleUnitTypeOthers varchar, raffleUnitCode varchar,
raffleUnitDescription varchar, raffleCurrency varchar) RETURNS uuid AS $$
    INSERT INTO capitalization_title_plan_events(capitalization_title_plan_id, event_type, raffle_date,
    raffle_settlement_date, raffle_amount, raffle_unit_type, raffle_unit_type_others, raffle_unit_code,
    raffle_unit_description, raffle_currency, created_at, created_by, updated_at, updated_by)
    VALUES (planId, eventType, raffleDate, raffleSettlementDate, raffleAmount, raffleUnitType, raffleUnitTypeOthers, raffleUnitCode, raffleUnitDescription,
    raffleCurrency, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING capitalization_title_plan_event_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addCapitalizationTitlePlanSettlement(planId uuid, settlementPaymentDate date,
settlementDueDate date, settlementAmount varchar, settlementUnitType varchar, settlementUnitTypeOthers varchar,
settlementUnitCode varchar, settlementUnitDescription varchar, settlementCurrency varchar) RETURNS uuid AS $$
    INSERT INTO capitalization_title_plan_settlements(capitalization_title_plan_id, settlement_payment_date,
    settlement_due_date, settlement_financial_amount, settlement_financial_unit_type, settlement_financial_unit_type_others,
    settlement_financial_unit_code, settlement_financial_unit_description, settlement_financial_currency,
    created_at, created_by, updated_at, updated_by)
    VALUES (planId, settlementPaymentDate, settlementDueDate, settlementAmount, settlementUnitType,
    settlementUnitTypeOthers, settlementUnitCode, settlementUnitDescription, settlementCurrency,
    NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING capitalization_title_plan_settlement_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addCapitalizationTitlePlanBroker(seriesId uuid, susepBrokerCode varchar,
brokerDescription varchar) RETURNS uuid AS $$
    INSERT INTO capitalization_title_plan_brokers(capitalization_title_plan_series_id, susep_broker_code,
    broker_description, created_at, created_by, updated_at, updated_by)
    VALUES (seriesId, susepBrokerCode, brokerDescription, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING capitalization_title_plan_broker_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addCapitalizationTitlePlanQuota(seriesId uuid, quota int,
capitalizationQuota varchar, raffleQuota varchar, chargingQuota varchar)  RETURNS uuid AS $$
    INSERT INTO capitalization_title_plan_quotas(capitalization_title_plan_series_id, quota, capitalization_quota,
    raffle_quota, charging_quota, created_at, created_by, updated_at, updated_by)
    VALUES (seriesId, quota, capitalizationQuota, raffleQuota, chargingQuota, NOW(), 'PREPOPULATE', NOW(),
    'PREPOPULATE')
    RETURNING capitalization_title_plan_quota_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addCapitalizationTitlePlanTitle(seriesId uuid, registrationForm varchar, issueTitleDate date,
termStartDate date, termEndDate date, rafflePremiumAmount varchar, rafflePremiumUnitType varchar,
rafflePremiumUnitTypeOthers varchar, rafflePremiumUnitCode varchar, rafflePremiumUnitDescription varchar,
rafflePremiumCurrency varchar, contributionAmount varchar, contributionUnitType varchar,
contributionUnitTypeOthers varchar, contributionUnitCode varchar, contributionUnitDescription varchar,
contributionCurrency varchar)  RETURNS uuid AS $$
    INSERT INTO capitalization_title_plan_titles(capitalization_title_plan_series_id, registration_form,
    issue_title_date, term_start_date, term_end_date, raffle_premium_amount, raffle_premium_unit_type,
    raffle_premium_unit_type_others, raffle_premium_unit_code, raffle_premium_unit_description, raffle_premium_currency,
    contribution_amount, contribution_unit_type, contribution_unit_type_others, contribution_unit_code,
    contribution_unit_description, contribution_currency, created_at, created_by, updated_at, updated_by)
    VALUES (seriesId, registrationForm, issueTitleDate, termStartDate, termEndDate, rafflePremiumAmount, 
    rafflePremiumUnitType, rafflePremiumUnitTypeOthers, rafflePremiumUnitCode, rafflePremiumUnitDescription,
    rafflePremiumCurrency, contributionAmount, contributionUnitType, contributionUnitTypeOthers, contributionUnitCode,
    contributionUnitDescription, contributionCurrency, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING capitalization_title_plan_title_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addCapitalizationTitlePlanSubscriber(titleId uuid, subscriberName varchar,
subscriberDocumentType varchar, subscriberDocumentTypeOthers varchar, subscriberDocumentNumber varchar,
subscriberAddress varchar, subscriberAddressAdditionalInfo varchar, subscriberTownName varchar,
subscriberCountrySubDivision varchar, subscriberCountryCode varchar, subscriberPostcode varchar,
subscriberCountryCallingCode varchar, subscriberAreaCode varchar, subscriberNumber varchar)  RETURNS uuid AS $$
    INSERT INTO capitalization_title_plan_subscribers(capitalization_title_plan_title_id, subscriber_name, subscriber_document_type,
    subscriber_document_type_others, subscriber_document_number, subscriber_address, subscriber_address_additional_info,
    subscriber_town_name, subscriber_country_sub_division, subscriber_country_code, subscriber_postcode,
    subscriber_country_calling_code, subscriber_area_code, subscriber_number, created_at, created_by,
    updated_at, updated_by)
    VALUES (titleId, subscriberName, subscriberDocumentType, subscriberDocumentTypeOthers, subscriberDocumentNumber,
    subscriberAddress, subscriberAddressAdditionalInfo, subscriberTownName, subscriberCountrySubDivision,
    subscriberCountryCode, subscriberPostcode, subscriberCountryCallingCode, subscriberAreaCode, subscriberNumber,
    NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING capitalization_title_plan_subscriber_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addCapitalizationTitlePlanHolder(subscriberId uuid, holderName varchar,
holderDocumentType varchar, holderDocumentTypeOthers varchar, holderDocumentNumber varchar,
holderAddress varchar, holderAddressAdditionalInfo varchar, holderTownName varchar,
holderCountrySubdivision varchar, holderCountryCode varchar, holderPostcode varchar, holderRedemption boolean,
holderRaffle boolean, holderCountryCallingCode varchar, holderAreaCode varchar,
holderNumber varchar)  RETURNS uuid AS $$
    INSERT INTO capitalization_title_plan_holders(capitalization_title_plan_subscriber_id, holder_name, holder_document_type,
    holder_document_type_others, holder_document_number, holder_address, holder_address_additional_info,
    holder_town_name, holder_country_subdivision, holder_country_code, holder_postcode, holder_redemption, holder_raffle,
    holder_country_calling_code, holder_area_code, holder_number, created_at, created_by,
    updated_at, updated_by)
    VALUES (subscriberId, holderName, holderDocumentType, holderDocumentTypeOthers, holderDocumentNumber,
    holderAddress, holderAddressAdditionalInfo, holderTownName, holderCountrySubDivision,
    holderCountryCode, holderPostcode, holderRedemption, holderRaffle, holderCountryCallingCode,
    holderAreaCode, holderNumber, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING capitalization_title_plan_holder_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addCapitalizationTitlePlanTechnicalProvisions(titleId uuid, pmcAmount varchar,
pmcUnitType varchar, pmcUnitTypeOthers varchar, pmcUnitCode varchar, pmcUnitDescription varchar, pmcCurrency varchar,
pdbAmount varchar, pdbUnitType varchar, pdbUnitTypeOthers varchar, pdbUnitCode varchar, pdbUnitDescription varchar,
pdbCurrency varchar, prAmount varchar, prUnitType varchar, prUnitTypeOthers varchar, prUnitCode varchar,
prUnitDescription varchar, prCurrency varchar, pspAmount varchar, pspUnitType varchar, pspUnitTypeOthers varchar,
pspUnitCode varchar, pspUnitDescription varchar, pspCurrency varchar)  RETURNS uuid AS $$
    INSERT INTO capitalization_title_plan_technical_provisions(capitalization_title_plan_title_id, pmc_amount, pmc_unit_type, pmc_unit_type_others, pmc_unit_code,
    pmc_unit_description, pmc_currency, pdb_amount, pdb_unit_type, pdb_unit_type_others, pdb_unit_code,
    pdb_unit_description, pdb_currency, pr_amount, pr_unit_type, pr_unit_type_others, pr_unit_code, pr_unit_description,
    pr_currency, psp_amount, psp_unit_type, psp_unit_type_others, psp_unit_code, psp_unit_description, psp_currency,
    created_at, created_by, updated_at, updated_by)
    VALUES (titleId, pmcAmount, pmcUnitType, pmcUnitTypeOthers, pmcUnitCode, pmcUnitDescription, pmcCurrency,
                     pdbAmount, pdbUnitType, pdbUnitTypeOthers, pdbUnitCode, pdbUnitDescription,
                     pdbCurrency, prAmount, prUnitType, prUnitTypeOthers, prUnitCode,
                     prUnitDescription, prCurrency, pspAmount, pspUnitType, pspUnitTypeOthers,
                     pspUnitCode, pspUnitDescription, pspCurrency, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING capitalization_title_plan_technical_provision_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addFinancialRiskPolicy(docId varchar, titleId varchar, status varchar, productName varchar,
documentType varchar, susepProcessNumber varchar, groupCertificateId varchar, issuanceType varchar, issuanceDate date,
termStartDate date, termEndDate date, leadInsurerCode varchar, leadInsurerPolicyId varchar, maxLMGAmount varchar,
maxLMGUnitType varchar, maxLMGUnitTypeOthers varchar, maxLMGUnitCode varchar, maxLMGUnitDescription varchar,
proposalId varchar, coinsuranceRetainedPercentage varchar, branchInfoIdentification varchar,
branchInfoUserGroup varchar, branchInfoTechnicalSurplus varchar) RETURNS uuid AS $$
    INSERT INTO financial_risk_policies (account_holder_id, financial_risk_id, status, product_name, document_type,
    susep_process_number, group_certificate_id, issuance_type, issuance_date, term_start_date, term_end_date,
    lead_insurer_code, lead_insurer_policy_id, max_lmg_amount, max_lmg_unit_type, max_lmg_unit_type_others, max_lmg_unit_code,
    max_lmg_unit_description, proposal_id, coinsurance_retained_percentage, identification,
    user_group, technical_surplus, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), titleId, status, productName, documentType, susepProcessNumber, groupCertificateId,
    issuanceType, issuanceDate, termStartDate, termEndDate, leadInsurerCode, leadInsurerPolicyId, maxLMGAmount,
    maxLMGUnitType, maxLMGUnitTypeOthers, maxLMGUnitCode, maxLMGUnitDescription, proposalId,
    coinsuranceRetainedPercentage, branchInfoIdentification, branchInfoUserGroup, branchInfoTechnicalSurplus,
    NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_risk_policy_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPersonalInfo(identification varchar,
identificationType varchar, identificationTypeOthers varchar, name varchar, birthDate date, postCode varchar,
email varchar, city varchar, state varchar, country varchar, address varchar, addressAdditionalInfo varchar) RETURNS uuid AS $$
    INSERT INTO personal_info(identification, identification_type, identification_type_others,
    name, birth_date, post_code, email, city, state, country, address, address_additional_info, created_at, created_by, updated_at, updated_by)
    VALUES(identification, identificationType, identificationTypeOthers, name, birthDate, postCode,
    email, city, state, country, address, addressAdditionalInfo, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING reference_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPersonalInfoIds(reference_id UUID, ids UUID[])
RETURNS void AS $$
BEGIN
  INSERT INTO personal_info_ids(reference_id, personal_id)
  SELECT reference_id, unnest(ids);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION addBeneficiaryInfo(identification varchar,
identificationType varchar, identificationTypeOthers varchar, name varchar) RETURNS uuid AS $$
    INSERT INTO beneficiary_info(identification, identification_type, identification_type_others,
    name, created_at, created_by, updated_at, updated_by)
    VALUES(identification, identificationType, identificationTypeOthers, name,
    NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING reference_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addBeneficiaryInfoIds(reference_id UUID, ids UUID[])
RETURNS void AS $$
BEGIN
  INSERT INTO beneficiary_info_ids(reference_id, beneficiary_id)
  SELECT reference_id, unnest(ids);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION addPrincipalInfo(identification varchar,
identificationType varchar, identificationTypeOthers varchar, name varchar, postCode varchar, email varchar, city varchar,
state varchar, country varchar, address varchar, addressAdditionalInfo varchar) RETURNS uuid AS $$
    INSERT INTO principal_info(identification, identification_type, identification_type_others,
    name, post_code, email, city, state, country, address, address_additional_info, created_at, created_by, updated_at,
    updated_by)
    VALUES(identification, identificationType, identificationTypeOthers, name, postCode, email, city,
           state, country, address, addressAdditionalInfo, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING reference_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPrincipalInfoIds(reference_id UUID, ids UUID[])
RETURNS void AS $$
BEGIN
  INSERT INTO principal_info_ids(reference_id, principal_id)
  SELECT reference_id, unnest(ids);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION addIntermediaryInfo(identification varchar,
identificationType varchar, identificationTypeOthers varchar, name varchar, postCode varchar,
city varchar, state varchar, country varchar, address varchar, type varchar) RETURNS uuid AS $$
    INSERT INTO intermediaries(identification, identification_type, identification_type_others,
    name, post_code, city, state, country, address, type, created_at, created_by, updated_at, updated_by)
    VALUES(identification, identificationType, identificationTypeOthers, name, postCode,
    city, state, country, address, type, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING reference_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addIntermediaryInfoIds(reference_id UUID, ids UUID[])
RETURNS void AS $$
BEGIN
  INSERT INTO intermediary_info_ids(reference_id, intermediary_id)
  SELECT reference_id, unnest(ids);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION addFinancialRiskPolicyInsuredObject(financialRiskPolicyId UUID, identification varchar,
type varchar, typeAdditionalInfo varchar, description varchar, amount varchar, unitType varchar, unitTypeOthers varchar,
unitCode varchar, unitDescription varchar) RETURNS uuid AS $$
    INSERT INTO financial_risk_policy_insured_objects(financial_risk_policy_id, identification, type, type_additional_info,
    description, amount, unit_type, unit_type_others, unit_code, unit_description, created_at, created_by, updated_at, updated_by)
    VALUES(financialRiskPolicyId, identification, type, typeAdditionalInfo, description, amount, unitType, unitTypeOthers,
    unitCode, unitDescription, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_risk_policy_insured_object_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addFinancialRiskPolicyInsuredObjectCoverage(financialRiskPolicyInsuredObjectId UUID, branch varchar,
code varchar, description varchar, internalCode varchar, susepProcessNumber varchar, lmiAmount varchar, lmiUnitType varchar,
lmiUnitTypeOthers varchar, lmiUnitCode varchar, lmiUnitDescription varchar, isLMISublimit boolean, termStartDate date,
termEndDate date, isMainCoverage boolean, feature varchar, type varchar, gracePeriod integer, gracePeriodicity varchar,
gracePeriodCountingMethod varchar, gracePeriodStartDate date, gracePeriodEndDate date, premiumPeriodicity varchar,
premiumPeriodicityOthers varchar) RETURNS uuid AS $$
    INSERT INTO financial_risk_policy_insured_object_coverages(financial_risk_policy_insured_object_id, branch, code,
    description, internal_code, susep_process_number, lmi_amount, lmi_unit_type, lmi_unit_type_others, lmi_unit_code,
    lmi_unit_description, is_lmi_sublimit, term_start_date, term_end_date, is_main_coverage, feature, type, grace_period,
    grace_periodicity, grace_period_counting_method, grace_period_start_date, grace_period_end_date, premium_periodicity,
    premium_periodicity_others, created_at, created_by, updated_at, updated_by)
    VALUES(financialRiskPolicyInsuredObjectId, branch, code, description, internalCode, susepProcessNumber, lmiAmount,
    lmiUnitType, lmiUnitTypeOthers, lmiUnitCode, lmiUnitDescription, isLMISublimit, termStartDate, termEndDate,
    isMainCoverage, feature, type, gracePeriod, gracePeriodicity, gracePeriodCountingMethod, gracePeriodStartDate,
    gracePeriodEndDate, premiumPeriodicity, premiumPeriodicityOthers, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_risk_policy_insured_object_coverage_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addFinancialRiskPolicyCoverage(financialRiskPolicyId UUID, deductibleId UUID, posId UUID, branch varchar, code varchar,
description varchar) RETURNS uuid AS $$
    INSERT INTO financial_risk_policy_coverages(financial_risk_policy_id, deductible_id, pos_id, branch, code, description, created_at,
    created_by, updated_at, updated_by)
    VALUES(financialRiskPolicyId, deductibleId, posId, branch, code, description, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_risk_policy_coverage_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addDeductible(type varchar,
typeAdditionalInfo varchar, amount varchar, unitType varchar, unitTypeOthers varchar, unitCode varchar, unitDescription varchar,
period integer, periodicity varchar, periodCountingMethod varchar, periodStartDate date, periodEndDate date,
description varchar) RETURNS uuid AS $$
    INSERT INTO deductibles(type, type_additional_info, amount, unit_type, unit_type_others,
    unit_code, unit_description,period, periodicity, period_counting_method, period_start_date, period_end_date, description,
    created_at, created_by, updated_at, updated_by)
    VALUES(type, typeAdditionalInfo, amount, unitType, unitTypeOthers, unitCode, unitDescription,
    period, periodicity, periodCountingMethod, periodStartDate, periodEndDate, description, NOW(), 'PREPOPULATE', NOW(),
    'PREPOPULATE')
    RETURNING reference_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPos(applicationType varchar,
description varchar, minValueAmount varchar, minValueUnitType varchar, minValueUnitTypeOthers varchar, minValueUnitCode varchar,
minValueUnitDescription varchar, maxValueAmount varchar, maxValueUnitType varchar, maxValueUnitTypeOthers varchar,
maxValueUnitCode varchar, maxValueUnitDescription varchar, percentageAmount varchar, percentageUnitType varchar,
percentageUnitTypeOthers varchar, percentageUnitCode varchar, percentageUnitDescription varchar, valueOthersAmount varchar,
valueOthersUnitType varchar, valueOthersUnitTypeOthers varchar, valueOthersUnitCode varchar,
valueOthersUnitDescription varchar) RETURNS uuid AS $$
    INSERT INTO pos(application_type, description, min_value_amount, min_value_unit_type,
    min_value_unit_type_others, min_value_unit_code, min_value_unit_description, max_value_amount, max_value_unit_type,
    max_value_unit_type_others, max_value_unit_code, max_value_unit_description, percentage_amount, percentage_unit_type,
    percentage_unit_type_others, percentage_unit_code, percentage_unit_description, value_others_amount,
    value_others_unit_type, value_others_unit_type_others, value_others_unit_code, value_others_unit_description,
    created_at, created_by, updated_at, updated_by)
    VALUES(applicationType, description, minValueAmount, minValueUnitType,
    minValueUnitTypeOthers, minValueUnitCode, minValueUnitDescription, maxValueAmount, maxValueUnitType,
    maxValueUnitTypeOthers, maxValueUnitCode, maxValueUnitDescription, percentageAmount, percentageUnitType,
    percentageUnitTypeOthers, percentageUnitCode, percentageUnitDescription, valueOthersAmount, valueOthersUnitType,
    valueOthersUnitTypeOthers, valueOthersUnitCode, valueOthersUnitDescription, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING pos_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addCoinsurer(identification varchar,
cededPercentage varchar) RETURNS uuid AS $$
    INSERT INTO coinsurers(identification, ceded_percentage, created_at, created_by, updated_at,
    updated_by)
    VALUES(identification, cededPercentage, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING coinsurer_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addCoinsurerIds(reference_id UUID, ids UUID[])
RETURNS void AS $$
BEGIN
  INSERT INTO coinsurer_ids(reference_id, coinsurer_id)
  SELECT reference_id, unnest(ids);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION addFinancialRiskPolicyClaim(financialRiskPolicyId UUID, identification varchar,
documentationDeliveryDate date, status varchar, statusAlterationDate date, occurrenceDate date, warningDate date,
thirdPartyClaimDate date, amount varchar, unitType varchar, unitTypeOthers varchar, unitCode varchar, unitDescription varchar,
denialJustification varchar, denialJustificationDescription varchar) RETURNS uuid AS $$
    INSERT INTO financial_risk_policy_claims(financial_risk_policy_id, identification, documentation_delivery_date, status,
    status_alteration_date, occurrence_date, warning_date, third_party_claim_date, amount, unit_type, unit_type_others,
    unit_code, unit_description, denial_justification,denial_justification_description, created_at, created_by, updated_at,
    updated_by)
    VALUES (financialRiskPolicyId, identification, documentationDeliveryDate, status, statusAlterationDate, occurrenceDate,
    warningDate, thirdPartyClaimDate, amount, unitType, unitTypeOthers, unitCode, unitDescription, denialJustification,
    denialJustificationDescription, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_risk_policy_claim_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addFinancialRiskPolicyClaimCoverage(financialRiskPolicyClaimId UUID, insuredObjectId varchar,
branch varchar, code varchar, description varchar, warningDate date, thirdPartyClaimDate date) RETURNS uuid AS $$
    INSERT INTO financial_risk_policy_claim_coverages(financial_risk_policy_claim_id, insured_object_id, branch, code,
    description, warning_date, third_party_claim_date, created_at, created_by, updated_at, updated_by)
    VALUES(financialRiskPolicyClaimId, insuredObjectId, branch, code, description, warningDate, thirdPartyClaimDate,
    NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_risk_policy_claim_coverage_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addFinancialRiskPolicyPremium(financialRiskPolicyId uuid, paymentsQuantity integer, amount varchar,
unitType varchar, unitTypeOthers varchar, unitCode varchar, unitDescription varchar) RETURNS uuid AS $$
    INSERT INTO financial_risk_policy_premiums(financial_risk_policy_id, payments_quantity, amount, unit_type, unit_type_others,
    unit_code, unit_description, created_at, created_by, updated_at, updated_by)
    VALUES (financialRiskPolicyId, paymentsQuantity, amount, unitType, unitTypeOthers, unitCode, unitDescription,
    NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_risk_policy_premium_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPaymentIds(reference_id UUID, ids UUID[])
RETURNS void AS $$
BEGIN
  INSERT INTO payment_ids(reference_id, payment_id)
  SELECT reference_id, unnest(ids);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION addFinancialRiskPolicyPremiumCoverage(financialRiskPolicyPremiumId UUID, branch varchar,
code varchar, description varchar, premiumAmount varchar, premiumUnitType varchar, premiumUnitTypeOthers varchar,
premiumUnitCode varchar, premiumUnitDescription varchar) RETURNS uuid AS $$
    INSERT INTO financial_risk_policy_premium_coverages(financial_risk_policy_premium_id, branch, code, description,
    premium_amount, premium_unit_type, premium_unit_type_others, premium_unit_code, premium_unit_description,
    created_at, created_by, updated_at, updated_by)
    VALUES(financialRiskPolicyPremiumId, branch, code, description, premiumAmount, premiumUnitType, premiumUnitTypeOthers,
    premiumUnitCode, premiumUnitDescription, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_risk_policy_premium_coverage_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addPayment(movementDate date,
movementType varchar, movementOrigin varchar, movementPaymentsNumber varchar, amount varchar, unitType varchar,
unitTypeOthers varchar, unitCode varchar, unitDescription varchar, maturityDate date, tellerId varchar, tellerIdType varchar,
tellerIdOthers varchar, tellerName varchar, financialInstitutionCode varchar, paymentType varchar) RETURNS uuid AS $$
    INSERT INTO payments(movement_date, movement_type, movement_origin,
    movement_payments_number, amount, unit_type, unit_type_others, unit_code, unit_description, maturity_date, teller_id,
    teller_id_type, teller_id_type_others, teller_name, financial_institution_code, payment_type, created_at, created_by,
    updated_at, updated_by)
    VALUES(movementDate, movementType, movementOrigin, movementPaymentsNumber, amount,
    unitType, unitTypeOthers, unitCode, unitDescription, maturityDate, tellerId, tellerIdType, tellerIdOthers, tellerName,
    financialInstitutionCode, paymentType, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING payment_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addHousingPolicy(docId varchar, titleId varchar, s varchar, productName VARCHAR, documentType VARCHAR,
policyId VARCHAR, susepProcessNumber VARCHAR, groupCertificateId VARCHAR, issuanceType VARCHAR, issuanceDate DATE, termStartDate DATE,
termEndDate DATE, leadInsurerCode VARCHAR, leadInsurerPolicyId VARCHAR, maxLmgAmount VARCHAR, maxLmgUnitType VARCHAR, maxLmgUnitCode VARCHAR,
maxLmgUnitDescription VARCHAR, proposalId VARCHAR) RETURNS uuid AS $$
    INSERT INTO housing_policies (account_holder_id, housing_id, status, product_name, document_type, policy_id, susep_process_number,
    group_certificate_id, issuance_type, issuance_date, term_start_date, term_end_date, lead_insurer_code, lead_insurer_policy_id,
    max_lmg_amount, max_lmg_unit_type, max_lmg_unit_code, max_lmg_unit_description, proposal_id,
    created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), titleId, s, productName, documentType, policyId, susepProcessNumber, groupCertificateId, issuanceType,
    issuanceDate, termStartDate, termEndDate, leadInsurerCode, leadInsurerPolicyId, maxLmgAmount, maxLmgUnitType, maxLmgUnitCode, maxLmgUnitDescription,
    proposalId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING housing_policy_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addHousingPolicyInsuredObject(planId uuid, identification VARCHAR, type VARCHAR, description VARCHAR, amount VARCHAR,
unitType VARCHAR, unitCode VARCHAR, unitDescription VARCHAR) RETURNS uuid AS $$
    INSERT INTO housing_policy_insured_objects(housing_policy_id, identification, type, description, amount, unit_type, unit_code, unit_description,
    created_at, created_by, updated_at, updated_by)
    VALUES (planId, identification, type, description, amount, unitType, unitCode, unitDescription, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING housing_policy_insured_object_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addHousingPolicyInsuredObjectCoverage(insuredObjectId uuid, branch VARCHAR, code VARCHAR, description VARCHAR,
internalCode VARCHAR, susepProcessNumber VARCHAR, lmiAmount VARCHAR, lmiUnitType VARCHAR, lmiSubLimit BOOLEAN, termStartDate DATE, termEndDate DATE,
mainCoverage BOOLEAN, feature VARCHAR, type VARCHAR, gracePeriod INTEGER, gracePeriodicity VARCHAR,
gracePeriodCountingMethod VARCHAR, premiumPeriodicity VARCHAR) RETURNS uuid AS $$
    INSERT INTO housing_policy_insured_object_coverages(housing_policy_insured_object_id, branch, code, description, internal_code, susep_process_number,
    lmi_amount, lmi_unit_type, lmi_sub_limit, term_start_date, term_end_date, main_coverage, feature, type, grace_period, grace_periodicity,
    grace_period_counting_method, premium_periodicity, created_at, created_by, updated_at, updated_by)
    VALUES (insuredObjectId, branch, code, description, internalCode, susepProcessNumber, lmiAmount, lmiUnitType, lmiSubLimit, termStartDate,
    termEndDate, mainCoverage, feature, type, gracePeriod, gracePeriodicity, gracePeriodCountingMethod, premiumPeriodicity,
    NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING housing_policy_insured_object_coverage_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addHousingPolicyBranchInsuredObject(planId uuid, identification VARCHAR, propertyType VARCHAR, postcode VARCHAR,
interestRate VARCHAR, costRate VARCHAR, updateIndex VARCHAR) RETURNS uuid AS $$
    INSERT INTO housing_policy_branch_insured_objects(housing_policy_id, identification, property_type, postcode, interest_rate, cost_rate,
    update_index, created_at, created_by, updated_at, updated_by)
    VALUES (planId, identification, propertyType, postcode, interestRate, costRate, updateIndex, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING housing_policy_branch_insured_object_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addHousingPolicyBranchInsuredObjectLender(branchInsuredObjectId uuid, companyName VARCHAR, cnpjNumber VARCHAR) RETURNS uuid AS $$
    INSERT INTO housing_policy_branch_insured_object_lenders(housing_policy_branch_insured_object_id, company_name, cnpj_number, created_at, created_by, updated_at, updated_by)
    VALUES (branchInsuredObjectId, companyName, cnpjNumber, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING housing_policy_branch_insured_object_lender_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addHousingPolicyBranchInsured(planId uuid, identification VARCHAR, identificationType VARCHAR) RETURNS uuid AS $$
    INSERT INTO housing_policy_branch_insureds(housing_policy_id, identification, identification_type, created_at, created_by, updated_at, updated_by)
    VALUES (planId, identification, identificationType, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING housing_policy_branch_insured_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addHousingPolicyClaim(planId uuid, identification VARCHAR, documentationDeliveryDate DATE, status VARCHAR,
statusAlterationDate DATE, occurrenceDate DATE, warningDate DATE, thirdPartyClaimDate DATE, amount VARCHAR, unitType VARCHAR,
denialJustification VARCHAR, denialJustificationDescription VARCHAR) RETURNS uuid AS $$
    INSERT INTO housing_policy_claims(housing_policy_id, identification, documentation_delivery_date, status, status_alteration_date,
    occurrence_date, warning_date, third_party_claim_date, amount, unit_type, denial_justification, denial_justification_description,
    created_at, created_by, updated_at, updated_by)
    VALUES (planId, identification, documentationDeliveryDate, status, statusAlterationDate, occurrenceDate, warningDate, thirdPartyClaimDate,
    amount, unitType, denialJustification, denialJustificationDescription, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING housing_policy_claim_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addHousingPolicyClaimCoverage(claimId uuid, insuredObjectId VARCHAR, branch VARCHAR, code VARCHAR, description VARCHAR,
warningDate DATE, thirdPartyClaimDate DATE) RETURNS uuid AS $$
    INSERT INTO housing_policy_claim_coverages(housing_policy_claim_id, insured_object_id, branch, code, description, warning_date, third_party_claim_date,
    created_at, created_by, updated_at, updated_by)
    VALUES (claimId, insuredObjectId, branch, code, description, warningDate, thirdPartyClaimDate, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING housing_policy_claim_coverage_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addHousingPolicyPremium(planId uuid, paymentsQuantity INTEGER, amount VARCHAR, unitType VARCHAR) RETURNS uuid AS $$
    INSERT INTO housing_policy_premiums(housing_policy_id, payments_quantity, amount, unit_type, created_at, created_by, updated_at, updated_by)
    VALUES (planId, paymentsQuantity, amount, unitType, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING housing_policy_premium_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addHousingPolicyPremiumCoverage(premiumId uuid, branch VARCHAR, code VARCHAR, amount VARCHAR, unitType VARCHAR) RETURNS uuid AS $$
    INSERT INTO housing_policy_premium_coverages(housing_policy_premium_id, branch, code, amount, unit_type, created_at, created_by, updated_at, updated_by)
    VALUES (premiumId, branch, code, amount, unitType, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING housing_policy_premium_coverage_id
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

CREATE OR REPLACE FUNCTION addRuralPolicy(docId VARCHAR, insuranceId VARCHAR, s VARCHAR, productName VARCHAR, documentType VARCHAR, policyId VARCHAR, susepProcessNumber VARCHAR, groupCertificateId VARCHAR, issuanceType VARCHAR, issuanceDate DATE, termStartDate DATE, termEndDate DATE, leadInsurerCode VARCHAR, leadInsurerPolicyId VARCHAR, maxLmgAmount VARCHAR, maxLmgUnitType VARCHAR, maxLmgUnitTypeOthers VARCHAR, maxLmgUnitCode VARCHAR, maxLmgUnitDescription VARCHAR, proposalId VARCHAR, coinsuranceRetainedPercentage VARCHAR ) RETURNS UUID AS $$
    INSERT INTO rural_policies (account_holder_id, insurance_id, status, product_name, document_type, policy_id, susep_process_number, group_certificate_id, issuance_type, issuance_date, term_start_date, term_end_date, lead_insurer_code, lead_insurer_policy_id, max_lmg_amount, max_lmg_unit_type, max_lmg_unit_type_others, max_lmg_unit_code, max_lmg_unit_description, proposal_id, coinsurance_retained_percentage, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), insuranceId, s, productName, documentType, policyId, susepProcessNumber, groupCertificateId, issuanceType, issuanceDate, termStartDate, termEndDate, leadInsurerCode, leadInsurerPolicyId, maxLmgAmount, maxLmgUnitType, maxLmgUnitTypeOthers, maxLmgUnitCode, maxLmgUnitDescription, proposalId, coinsuranceRetainedPercentage, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING rural_policy_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addRuralPolicyInsuredObject(ruralPolicyId UUID, identification VARCHAR, type VARCHAR, typeAdditionalInfo VARCHAR, description VARCHAR, amount VARCHAR, unitType VARCHAR, unitTypeOthers VARCHAR, unitCode VARCHAR, unitDescription VARCHAR) RETURNS UUID AS $$
    INSERT INTO rural_policy_insured_objects (rural_policy_id, identification, type, type_additional_info, description, amount, unit_type, unit_type_others, unit_code, unit_description, created_at, created_by, updated_at, updated_by)
    VALUES (ruralPolicyId, identification, type, typeAdditionalInfo, description, amount, unitType, unitTypeOthers, unitCode, unitDescription, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING rural_policy_insured_object_id
$$ LANGUAGE SQL;
   
CREATE OR REPLACE FUNCTION addRuralPolicyInsuredObjectCoverage(ruralPolicyInsuredObjectId UUID, branch VARCHAR, code VARCHAR, description VARCHAR, internalCode VARCHAR, susepProcessNumber VARCHAR, lmiAmount VARCHAR, lmiUnitType VARCHAR, lmiUnitTypeOthers VARCHAR, lmiUnitCode VARCHAR, lmiUnitDescription VARCHAR, lmiSublimit BOOLEAN, termStartDate DATE, termEndDate DATE, mainCoverage BOOLEAN, feature VARCHAR, type VARCHAR, gracePeriod INTEGER, gracePeriodicity VARCHAR, gracePeriodCountingMethod VARCHAR, gracePeriodStartDate DATE, gracePeriodEndDate DATE, premiumPeriodicity VARCHAR, premiumPeriodicityOthers VARCHAR) RETURNS UUID AS $$
    INSERT INTO rural_policy_insured_object_coverages (rural_policy_insured_object_id, branch, code, description, internal_code, susep_process_number, lmi_amount, lmi_unit_type, lmi_unit_type_others, lmi_unit_code, lmi_unit_description, lmi_sublimit, term_start_date, term_end_date, main_coverage, feature, type, grace_period, grace_periodicity, grace_period_counting_method, grace_period_start_date, grace_period_end_date, premium_periodicity, premium_periodicity_others, created_at, created_by, updated_at, updated_by)
    VALUES (ruralPolicyInsuredObjectId, branch, code, description, internalCode, susepProcessNumber, lmiAmount, lmiUnitType, lmiUnitTypeOthers, lmiUnitCode, lmiUnitDescription, lmiSublimit, termStartDate, termEndDate, mainCoverage, feature, type, gracePeriod, gracePeriodicity, gracePeriodCountingMethod, gracePeriodStartDate, gracePeriodEndDate, premiumPeriodicity, premiumPeriodicityOthers, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING rural_policy_insured_object_coverage_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addRuralPolicyCoverage(ruralPolicyId UUID, branch VARCHAR, code VARCHAR, description VARCHAR, deductibleId UUID, posId UUID) RETURNS UUID AS $$
    INSERT INTO rural_policy_coverages(rural_policy_id, branch, code, description, deductible_id, pos_id, created_at, created_by, updated_at, updated_by)
    VALUES (ruralPolicyId, branch, code, description, deductibleId, posId, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING rural_policy_coverage_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addRuralPolicyBranchInsuredObject(ruralPolicyId UUID, identification VARCHAR, fesrParticipant BOOLEAN, amount VARCHAR, unitType VARCHAR, unitTypeOthers VARCHAR, unitCode VARCHAR, unitDescription VARCHAR, subventionType VARCHAR, safeArea VARCHAR, unitMeasure VARCHAR, unitMeasureOthers VARCHAR, cultureCode VARCHAR, flockCode VARCHAR, flockCodeOthers VARCHAR, forestCode VARCHAR, forestCodeOthers VARCHAR, surveyDate DATE, surveyAddress VARCHAR, surveyCountrySubDivision VARCHAR, surveyPostcode VARCHAR, surveyCountryCode VARCHAR, surveyorIdType VARCHAR, surveyorIdOthers VARCHAR, surveyorId VARCHAR, surveyorName VARCHAR, modelType VARCHAR, modelTypeOthers VARCHAR, assetsCovered BOOLEAN, coveredAnimalDestination VARCHAR, animalType VARCHAR) RETURNS UUID AS $$
    INSERT INTO rural_policy_branch_insured_objects(rural_policy_id, identification, fesr_participant, amount, unit_type, unit_type_others, unit_code, unit_description, subvention_type, safe_area, unit_measure, unit_measure_others, culture_code, flock_code, flock_code_others, forest_code, forest_code_others, survey_date, survey_address, survey_country_sub_division, survey_postcode, survey_country_code, surveyor_id_type, surveyor_id_others, surveyor_id, surveyor_name, model_type, model_type_others, assets_covered, covered_animal_destination, animal_type, created_at, created_by, updated_at, updated_by)
    VALUES (ruralPolicyId, identification, fesrParticipant, amount, unitType, unitTypeOthers, unitCode, unitDescription, subventionType, safeArea, unitMeasure, unitMeasureOthers, cultureCode, flockCode, flockCodeOthers, forestCode, forestCodeOthers, surveyDate, surveyAddress, surveyCountrySubDivision, surveyPostcode, surveyCountryCode, surveyorIdType, surveyorIdOthers, surveyorId, surveyorName, modelType, modelTypeOthers, assetsCovered, coveredAnimalDestination, animalType, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING rural_policy_branch_insured_object_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addRuralPolicyClaim(ruralPolicyId UUID, identification VARCHAR, documentationDeliveryDate DATE, status VARCHAR, statusAlterationDate DATE, ocurranceDate DATE, warningDate DATE, thirdPartyDate DATE, amount VARCHAR, unitType VARCHAR, unitTypeOthers VARCHAR, unitCode VARCHAR, unitDescription VARCHAR, denialJustification VARCHAR, denialJustificationDescription VARCHAR, surveyDate DATE, surveyAddress VARCHAR, surveyCountrySubDivision VARCHAR, surveyPostcode VARCHAR, surveyCountryCode VARCHAR) RETURNS UUID AS $$
    INSERT INTO rural_policy_claims(rural_policy_id, identification, documentation_delivery_date, status, status_alteration_date, ocurrance_date, warning_date, third_party_date, amount, unit_type, unit_type_others, unit_code, unit_description, denial_justification, denial_justification_description, survey_date, survey_address, survey_country_sub_division, survey_postcode, survey_country_code, created_at, created_by, updated_at, updated_by)
    VALUES (ruralPolicyId, identification, documentationDeliveryDate, status, statusAlterationDate, ocurranceDate, warningDate, thirdPartyDate, amount, unitType, unitTypeOthers, unitCode, unitDescription, denialJustification, denialJustificationDescription, surveyDate, surveyAddress, surveyCountrySubDivision, surveyPostcode, surveyCountryCode, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING rural_policy_claim_id
$$ LANGUAGE SQL; 

CREATE OR REPLACE FUNCTION addRuralPolicyClaimCoverage(ruralPolicyClaimId UUID, insuredObjectId VARCHAR, branch VARCHAR, code VARCHAR, description VARCHAR, warningDate DATE, thirdPartyClaimDate DATE) RETURNS UUID AS $$
    INSERT INTO rural_policy_claim_coverages(rural_policy_claim_id, insured_object_id, branch, code, description, warning_date, third_party_claim_date, created_at, created_by, updated_at, updated_by)
    VALUES (ruralPolicyClaimId, insuredObjectId, branch, code, description, warningDate, thirdPartyClaimDate, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING rural_policy_claim_coverage_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addRuralPolicyPremium(ruralPolicyId UUID, paymentsQuantity INTEGER, amount VARCHAR, unitType VARCHAR, unitTypeOthers VARCHAR, unitCode VARCHAR, unitDescription VARCHAR) RETURNS UUID AS $$
    INSERT INTO rural_policy_premiums(rural_policy_id, payments_quantity, amount, unit_type, unit_type_others, unit_code, unit_description, created_at, created_by, updated_at, updated_by)
    VALUES (ruralPolicyId, paymentsQuantity, amount, unitType, unitTypeOthers, unitCode, unitDescription, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING rural_policy_premium_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addRuralPolicyPremiumCoverage(ruralPolicyPremiumId UUID, branch VARCHAR, code VARCHAR, description VARCHAR, amount VARCHAR, unitType VARCHAR,  unitTypeOthers VARCHAR, unitCode VARCHAR, unitDescription VARCHAR) RETURNS UUID AS $$
    INSERT INTO rural_policy_premium_coverages(rural_policy_premium_id, branch, code, description, amount, unit_type, unit_type_others, unit_code, unit_description, created_at, created_by, updated_at, updated_by)
    VALUES (ruralPolicyPremiumId, branch, code, description, amount, unitType, unitTypeOthers, unitCode, unitDescription, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING rural_policy_premium_coverage_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addFinancialAssistanceContract(docId varchar, contractId varchar, status varchar,
certificateId varchar, groupContractId varchar, susepProcessNumber varchar, conceivedCreditValueAmount varchar,
conceivedCreditValueUnitType varchar, conceivedCreditValueUnitTypeOthers varchar, conceivedCreditValueUnitCode varchar,
conceivedCreditValueUnitDescription varchar, conceivedCreditValueCurrency varchar, creditedLiquidValueAmount varchar,
creditedLiquidValueUnitType varchar, creditedLiquidValueUnitTypeOthers varchar, creditedLiquidValueUnitCode varchar,
creditedLiquidValueUnitDescription varchar, creditedLiquidValueCurrency varchar, counterInstallmentAmount varchar,
counterInstallmentUnitType varchar, counterInstallmentUnitTypeOthers varchar, counterInstallmentUnitCode varchar,
counterInstallmentUnitDescription varchar, counterInstallmentCurrency varchar, counterInstallmentPeriodicity varchar,
counterInstallmentQuantity int, counterInstallmentFirst_date date, counterInstallmentLastDate date,
interestRateAmount varchar, interestRateUnitType varchar, interestRateUnitTypeOthers varchar, interestRateUnitCode varchar,
interestRateUnitDescription varchar, interestRate_currency varchar, effectiveCostRateAmount varchar,
effectiveCostRateUnitType varchar, effectiveCostRateUnitTypeOthers varchar, effectiveCostRateUnitCode varchar,
effectiveCostRateUnitDescription varchar, effectiveCostRateCurrency varchar, amortizationPeriod int,
acquittanceValueAmount varchar, acquittanceValueUnitType varchar, acquittanceValueUnitTypeOthers varchar,
acquittanceValueUnitCode varchar, acquittanceValueUnitDescription varchar, acquittanceValueCurrency varchar,
acquittanceDate date, taxesValueAmount varchar, taxesValueUnitType varchar, taxesValueUnitTypeOthers varchar,
taxesValueUnitCode varchar, taxesValueUnitDescription varchar, taxesValueCurrency varchar, expensesValueAmount varchar,
expensesValueUnitType varchar, expensesValueUnitTypeOthers varchar, expensesValueUnitCode varchar,
expensesValueUnitDescription varchar, expensesValueCurrency varchar, finesValueAmount varchar, finesValueUnitType varchar,
finesValueUnitTypeOthers varchar, finesValueUnitCode varchar, finesValueUnitDescription varchar, finesValueCurrency varchar,
monetaryUpdatesValueAmount varchar, monetaryUpdatesValueUnitType varchar, monetaryUpdatesValueUnitTypeOthers varchar,
monetaryUpdatesValueUnitCode varchar, monetaryUpdatesValueUnitDescription varchar, monetaryUpdatesValueCurrency varchar,
administrativeFeesValueAmount varchar, administrativeFeesValueUnitType varchar, administrativeFeesValueUnitTypeOthers varchar,
administrativeFeesValueUnitCode varchar, administrativeFeesValueUnitDescription varchar, administrativeFeesValueCurrency varchar,
interestValueAmount varchar, interestValueUnitType varchar, interestValueUnitTypeOthers varchar, interestValueUnitCode varchar,
interestValueUnitDescription varchar, interestValueCurrency varchar) RETURNS varchar AS $$
    INSERT INTO financial_assistance_contracts (account_holder_id, financial_assistance_contract_id, status, certificate_id,
    group_contract_id, susep_process_number, conceived_credit_value_amount, conceived_credit_value_unit_type,
    conceived_credit_value_unit_type_others, conceived_credit_value_unit_code, conceived_credit_value_unit_description,
    conceived_credit_value_currency, credited_liquid_value_amount, credited_liquid_value_unit_type,
    credited_liquid_value_unit_type_others, credited_liquid_value_unit_code, credited_liquid_value_unit_description,
    credited_liquid_value_currency, counter_installment_amount, counter_installment_unit_type,
    counter_installment_unit_type_others, counter_installment_unit_code, counter_installment_unit_description,
    counter_installment_currency, counter_installment_periodicity, counter_installment_quantity, counter_installment_first_date,
    counter_installment_last_date, interest_rate_amount, interest_rate_unit_type, interest_rate_unit_type_others,
    interest_rate_unit_code, interest_rate_unit_description, interest_rate_currency, effective_cost_rate_amount,
    effective_cost_rate_unit_type, effective_cost_rate_unit_type_others, effective_cost_rate_unit_code,
    effective_cost_rate_unit_description, effective_cost_rate_currency, amortization_period, acquittance_value_amount,
    acquittance_value_unit_type, acquittance_value_unit_type_others, acquittance_value_unit_code,
    acquittance_value_unit_description, acquittance_value_currency, acquittance_date, taxes_value_amount, taxes_value_unit_type,
    taxes_value_unit_type_others, taxes_value_unit_code, taxes_value_unit_description, taxes_value_currency,
    expenses_value_amount, expenses_value_unit_type, expenses_value_unit_type_others, expenses_value_unit_code,
    expenses_value_unit_description, expenses_value_currency, fines_value_amount, fines_value_unit_type,
    fines_value_unit_type_others, fines_value_unit_code, fines_value_unit_description, fines_value_currency,
    monetary_updates_value_amount, monetary_updates_value_unit_type, monetary_updates_value_unit_type_others,
    monetary_updates_value_unit_code, monetary_updates_value_unit_description, monetary_updates_value_currency,
    administrative_fees_value_amount, administrative_fees_value_unit_type, administrative_fees_value_unit_type_others,
    administrative_fees_value_unit_code, administrative_fees_value_unit_description, administrative_fees_value_currency,
    interest_value_amount, interest_value_unit_type, interest_value_unit_type_others, interest_value_unit_code,
    interest_value_unit_description, interest_value_currency, created_at, created_by, updated_at, updated_by)
    VALUES (getAccountHolderId(docId), contractId, status, certificateId, groupContractId, susepProcessNumber,
    conceivedCreditValueAmount, conceivedCreditValueUnitType, conceivedCreditValueUnitTypeOthers,
    conceivedCreditValueUnitCode, conceivedCreditValueUnitDescription, conceivedCreditValueCurrency,
    creditedLiquidValueAmount, creditedLiquidValueUnitType, creditedLiquidValueUnitTypeOthers, creditedLiquidValueUnitCode,
    creditedLiquidValueUnitDescription, creditedLiquidValueCurrency, counterInstallmentAmount, counterInstallmentUnitType,
    counterInstallmentUnitTypeOthers, counterInstallmentUnitCode, counterInstallmentUnitDescription, counterInstallmentCurrency,
    counterInstallmentPeriodicity, counterInstallmentQuantity, counterInstallmentFirst_date, counterInstallmentLastDate,
    interestRateAmount, interestRateUnitType, interestRateUnitTypeOthers, interestRateUnitCode, interestRateUnitDescription,
    interestRate_currency, effectiveCostRateAmount, effectiveCostRateUnitType, effectiveCostRateUnitTypeOthers,
    effectiveCostRateUnitCode, effectiveCostRateUnitDescription, effectiveCostRateCurrency, amortizationPeriod,
    acquittanceValueAmount, acquittanceValueUnitType, acquittanceValueUnitTypeOthers, acquittanceValueUnitCode,
    acquittanceValueUnitDescription, acquittanceValueCurrency, acquittanceDate, taxesValueAmount, taxesValueUnitType,
    taxesValueUnitTypeOthers, taxesValueUnitCode, taxesValueUnitDescription, taxesValueCurrency, expensesValueAmount,
    expensesValueUnitType, expensesValueUnitTypeOthers, expensesValueUnitCode, expensesValueUnitDescription,
    expensesValueCurrency, finesValueAmount, finesValueUnitType, finesValueUnitTypeOthers, finesValueUnitCode,
    finesValueUnitDescription, finesValueCurrency,  monetaryUpdatesValueAmount, monetaryUpdatesValueUnitType,
    monetaryUpdatesValueUnitTypeOthers,  monetaryUpdatesValueUnitCode, monetaryUpdatesValueUnitDescription,
    monetaryUpdatesValueCurrency,  administrativeFeesValueAmount, administrativeFeesValueUnitType,
    administrativeFeesValueUnitTypeOthers,  administrativeFeesValueUnitCode, administrativeFeesValueUnitDescription,
    administrativeFeesValueCurrency,  interestValueAmount, interestValueUnitType, interestValueUnitTypeOthers,
    interestValueUnitCode,  interestValueUnitDescription, interestValueCurrency, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_assistance_contract_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addFinancialAssistanceContractMovement(contractId varchar, updatedDebitAmount varchar,
updatedDebitUnitType varchar, updatedDebitUnitTypeOthers varchar, updatedDebitUnitCode varchar, updatedDebitUnitDescription varchar,
updatedDebitCurrency varchar, remainingCounterInstallmentsQuantity integer, remainingUnpaidCounterInstallmentsQuantity integer,
lifePensionPmBacAmount varchar, lifePensionPmBacUnitType varchar, lifePensionPmBacUnitTypeOthers varchar,
lifePensionPmBacUnitCode varchar, lifePensionPmBacUnitDescription varchar, lifePensionPmBacCurrency varchar,
pensionPlanPmBacAmount varchar, pensionPlanPmBacUnitType varchar, pensionPlanPmBacUnitTypeOthers varchar,
pensionPlanPmBacUnitCode varchar, pensionPlanPmBacUnitDescription varchar, pensionPlanPmBacUnitCurrency varchar) RETURNS uuid AS $$
    INSERT INTO financial_assistance_contract_movements(financial_assistance_contract_id, updated_debit_amount, updated_debit_unit_type,
    updated_debit_unit_type_others, updated_debit_unit_code, updated_debit_unit_description, updated_debit_currency,
    remaining_counter_installments_quantity , remaining_unpaid_counter_installments_quantity , life_pension_pm_bac_amount,
    life_pension_pm_bac_unit_type, life_pension_pm_bac_unit_type_others, life_pension_pm_bac_unit_code,
    life_pension_pm_bac_unit_description, life_pension_pm_bac_currency, pension_plan_pm_bac_amount, pension_plan_pm_bac_unit_type,
    pension_plan_pm_bac_unit_type_others, pension_plan_pm_bac_unit_code, pension_plan_pm_bac_unit_description, pension_plan_pm_bac_currency,
    created_at, created_by, updated_at, updated_by)
    VALUES (contractId, updatedDebitAmount, updatedDebitUnitType, updatedDebitUnitTypeOthers, updatedDebitUnitCode, 
    updatedDebitUnitDescription, updatedDebitCurrency, remainingCounterInstallmentsQuantity, remainingUnpaidCounterInstallmentsQuantity,
    lifePensionPmBacAmount, lifePensionPmBacUnitType, lifePensionPmBacUnitTypeOthers, lifePensionPmBacUnitCode,
    lifePensionPmBacUnitDescription, lifePensionPmBacCurrency, pensionPlanPmBacAmount, pensionPlanPmBacUnitType,
    pensionPlanPmBacUnitTypeOthers, pensionPlanPmBacUnitCode, pensionPlanPmBacUnitDescription, pensionPlanPmBacUnitCurrency,
    NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_assistance_contract_movement_id
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION addFinancialAssistanceContractInsured(contractId varchar, documentType varchar, documentTypeOthers varchar, documentNumber varchar, name varchar) RETURNS uuid AS $$
    INSERT INTO financial_assistance_contract_insureds (financial_assistance_contract_id, document_type, document_type_others, document_number, name, created_at, created_by, updated_at, updated_by)
    VALUES (contractId, documentType, documentTypeOthers, documentNumber, name, NOW(), 'PREPOPULATE', NOW(), 'PREPOPULATE')
    RETURNING financial_assistance_contract_insured_id
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
