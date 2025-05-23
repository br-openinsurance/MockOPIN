package com.raidiam.trustframework.mockinsurance.utils;

import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentPermission;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public enum PermissionPhase {
    PHASE2(EnumSet.of(
            EnumConsentPermission.RESOURCES_READ,
            EnumConsentPermission.CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ,
            EnumConsentPermission.CUSTOMERS_PERSONAL_QUALIFICATION_READ,
            EnumConsentPermission.CUSTOMERS_PERSONAL_ADDITIONALINFO_READ,
            EnumConsentPermission.CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ,
            EnumConsentPermission.CUSTOMERS_BUSINESS_QUALIFICATION_READ,
            EnumConsentPermission.CUSTOMERS_BUSINESS_ADDITIONALINFO_READ,
            EnumConsentPermission.CAPITALIZATION_TITLE_READ,
            EnumConsentPermission.CAPITALIZATION_TITLE_PLANINFO_READ,
            EnumConsentPermission.CAPITALIZATION_TITLE_EVENTS_READ,
            EnumConsentPermission.CAPITALIZATION_TITLE_SETTLEMENTS_READ,
            EnumConsentPermission.PENSION_PLAN_READ,
            EnumConsentPermission.PENSION_PLAN_CONTRACTINFO_READ,
            EnumConsentPermission.PENSION_PLAN_MOVEMENTS_READ,
            EnumConsentPermission.PENSION_PLAN_PORTABILITIES_READ,
            EnumConsentPermission.PENSION_PLAN_WITHDRAWALS_READ,
            EnumConsentPermission.PENSION_PLAN_CLAIM,
            EnumConsentPermission.LIFE_PENSION_READ,
            EnumConsentPermission.LIFE_PENSION_CONTRACTINFO_READ,
            EnumConsentPermission.LIFE_PENSION_MOVEMENTS_READ,
            EnumConsentPermission.LIFE_PENSION_PORTABILITIES_READ,
            EnumConsentPermission.LIFE_PENSION_WITHDRAWALS_READ,
            EnumConsentPermission.LIFE_PENSION_CLAIM,
            EnumConsentPermission.FINANCIAL_ASSISTANCE_READ,
            EnumConsentPermission.FINANCIAL_ASSISTANCE_CONTRACTINFO_READ,
            EnumConsentPermission.FINANCIAL_ASSISTANCE_MOVEMENTS_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_POLICYINFO_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_PREMIUM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_CLAIM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_POLICYINFO_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_PREMIUM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_CLAIM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_POLICYINFO_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_PREMIUM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_CLAIM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_POLICYINFO_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_PREMIUM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_CLAIM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_POLICYINFO_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_PREMIUM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_CLAIM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_POLICYINFO_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_PREMIUM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_CLAIM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_POLICYINFO_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_PREMIUM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_CLAIM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_POLICYINFO_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_PREMIUM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_CLAIM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_POLICYINFO_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_PREMIUM_READ,
            EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_CLAIM_READ
    )),

    PHASE3(EnumSet.of(
            EnumConsentPermission.CLAIM_NOTIFICATION_REQUEST_DAMAGE_CREATE,
            EnumConsentPermission.CLAIM_NOTIFICATION_REQUEST_PERSON_CREATE,
            EnumConsentPermission.ENDORSEMENT_REQUEST_CREATE,
            EnumConsentPermission.QUOTE_PATRIMONIAL_LEAD_CREATE,
            EnumConsentPermission.QUOTE_PATRIMONIAL_LEAD_UPDATE,
            EnumConsentPermission.QUOTE_PATRIMONIAL_HOME_READ,
            EnumConsentPermission.QUOTE_PATRIMONIAL_HOME_CREATE,
            EnumConsentPermission.QUOTE_PATRIMONIAL_HOME_UPDATE,
            EnumConsentPermission.QUOTE_PATRIMONIAL_CONDOMINIUM_READ,
            EnumConsentPermission.QUOTE_PATRIMONIAL_CONDOMINIUM_CREATE,
            EnumConsentPermission.QUOTE_PATRIMONIAL_CONDOMINIUM_UPDATE,
            EnumConsentPermission.QUOTE_PATRIMONIAL_BUSINESS_READ,
            EnumConsentPermission.QUOTE_PATRIMONIAL_BUSINESS_CREATE,
            EnumConsentPermission.QUOTE_PATRIMONIAL_BUSINESS_UPDATE,
            EnumConsentPermission.QUOTE_PATRIMONIAL_DIVERSE_RISKS_READ,
            EnumConsentPermission.QUOTE_PATRIMONIAL_DIVERSE_RISKS_CREATE,
            EnumConsentPermission.QUOTE_PATRIMONIAL_DIVERSE_RISKS_UPDATE,
            EnumConsentPermission.QUOTE_ACCEPTANCE_AND_BRANCHES_ABROAD_LEAD_CREATE,
            EnumConsentPermission.QUOTE_ACCEPTANCE_AND_BRANCHES_ABROAD_LEAD_UPDATE,
            EnumConsentPermission.QUOTE_AUTO_LEAD_CREATE,
            EnumConsentPermission.QUOTE_AUTO_LEAD_UPDATE,
            EnumConsentPermission.QUOTE_AUTO_READ,
            EnumConsentPermission.QUOTE_AUTO_CREATE,
            EnumConsentPermission.QUOTE_AUTO_UPDATE,
            EnumConsentPermission.QUOTE_FINANCIAL_RISK_LEAD_CREATE,
            EnumConsentPermission.QUOTE_FINANCIAL_RISK_LEAD_UPDATE,
            EnumConsentPermission.QUOTE_HOUSING_LEAD_CREATE,
            EnumConsentPermission.QUOTE_HOUSING_LEAD_UPDATE,
            EnumConsentPermission.QUOTE_RESPONSIBILITY_LEAD_CREATE,
            EnumConsentPermission.QUOTE_RESPONSIBILITY_LEAD_UPDATE,
            EnumConsentPermission.QUOTE_RURAL_LEAD_CREATE,
            EnumConsentPermission.QUOTE_RURAL_LEAD_UPDATE,
            EnumConsentPermission.QUOTE_TRANSPORT_LEAD_CREATE,
            EnumConsentPermission.QUOTE_TRANSPORT_LEAD_UPDATE,
            EnumConsentPermission.QUOTE_PERSON_LEAD_CREATE,
            EnumConsentPermission.QUOTE_PERSON_LEAD_UPDATE,
            EnumConsentPermission.QUOTE_PERSON_LIFE_READ,
            EnumConsentPermission.QUOTE_PERSON_LIFE_CREATE,
            EnumConsentPermission.QUOTE_PERSON_LIFE_UPDATE,
            EnumConsentPermission.QUOTE_PERSON_TRAVEL_READ,
            EnumConsentPermission.QUOTE_PERSON_TRAVEL_CREATE,
            EnumConsentPermission.QUOTE_PERSON_TRAVEL_UPDATE,
            EnumConsentPermission.QUOTE_CAPITALIZATION_TITLE_LEAD_CREATE,
            EnumConsentPermission.QUOTE_CAPITALIZATION_TITLE_LEAD_UPDATE,
            EnumConsentPermission.QUOTE_CAPITALIZATION_TITLE_READ,
            EnumConsentPermission.QUOTE_CAPITALIZATION_TITLE_CREATE,
            EnumConsentPermission.QUOTE_CAPITALIZATION_TITLE_UPDATE,
            EnumConsentPermission.QUOTE_CAPITALIZATION_TITLE_RAFFLE_CREATE,
            EnumConsentPermission.CONTRACT_PENSION_PLAN_LEAD_CREATE,
            EnumConsentPermission.CONTRACT_PENSION_PLAN_LEAD_UPDATE,
            EnumConsentPermission.CONTRACT_PENSION_PLAN_LEAD_PORTABILITY_CREATE,
            EnumConsentPermission.CONTRACT_PENSION_PLAN_LEAD_PORTABILITY_UPDATE,
            EnumConsentPermission.CONTRACT_LIFE_PENSION_LEAD_CREATE,
            EnumConsentPermission.CONTRACT_LIFE_PENSION_LEAD_UPDATE,
            EnumConsentPermission.CONTRACT_LIFE_PENSION_LEAD_PORTABILITY_CREATE,
            EnumConsentPermission.CONTRACT_LIFE_PENSION_LEAD_PORTABILITY_UPDATE,
            EnumConsentPermission.PENSION_WITHDRAWAL_CREATE,
            EnumConsentPermission.CAPITALIZATION_TITLE_WITHDRAWAL_CREATE
    ));

    private final Set<EnumConsentPermission> permissions;

    PermissionPhase(Set<EnumConsentPermission> permissions) {
        this.permissions = permissions;
    }

    public boolean containsAny(List<EnumConsentPermission> permissions) {
        return permissions.stream().anyMatch(this.permissions::contains);
    }
}
