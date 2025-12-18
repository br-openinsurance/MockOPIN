package com.raidiam.trustframework.mockinsurance.utils;

import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentV3Permission;
import lombok.Getter;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Getter
public enum PermissionV3Group {

    PERSONAL_REGISTRATION(EnumSet.of(
            EnumConsentV3Permission.RESOURCES_READ,
            EnumConsentV3Permission.CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ,
            EnumConsentV3Permission.CUSTOMERS_PERSONAL_QUALIFICATION_READ,
            EnumConsentV3Permission.CUSTOMERS_PERSONAL_ADDITIONALINFO_READ
    )),
    BUSINESS_REGISTRATION(EnumSet.of(
            EnumConsentV3Permission.RESOURCES_READ,
            EnumConsentV3Permission.CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ,
            EnumConsentV3Permission.CUSTOMERS_BUSINESS_QUALIFICATION_READ,
            EnumConsentV3Permission.CUSTOMERS_BUSINESS_ADDITIONALINFO_READ
    )),
    CAPITALIZATION_TITLE(EnumSet.of(
            EnumConsentV3Permission.RESOURCES_READ,
            EnumConsentV3Permission.CAPITALIZATION_TITLE_READ,
            EnumConsentV3Permission.CAPITALIZATION_TITLE_PLANINFO_READ,
            EnumConsentV3Permission.CAPITALIZATION_TITLE_EVENTS_READ,
            EnumConsentV3Permission.CAPITALIZATION_TITLE_SETTLEMENTS_READ
    )),
    PENSION_PLAN(EnumSet.of(
            EnumConsentV3Permission.RESOURCES_READ,
            EnumConsentV3Permission.PENSION_PLAN_READ,
            EnumConsentV3Permission.PENSION_PLAN_CONTRACTINFO_READ,
            EnumConsentV3Permission.PENSION_PLAN_MOVEMENTS_READ,
            EnumConsentV3Permission.PENSION_PLAN_PORTABILITIES_READ,
            EnumConsentV3Permission.PENSION_PLAN_WITHDRAWALS_READ,
            EnumConsentV3Permission.PENSION_PLAN_CLAIM_READ
    )),
    LIFE_PENSION_PLAN(EnumSet.of(
            EnumConsentV3Permission.RESOURCES_READ,
            EnumConsentV3Permission.LIFE_PENSION_READ,
            EnumConsentV3Permission.LIFE_PENSION_CONTRACTINFO_READ,
            EnumConsentV3Permission.LIFE_PENSION_MOVEMENTS_READ,
            EnumConsentV3Permission.LIFE_PENSION_PORTABILITIES_READ,
            EnumConsentV3Permission.LIFE_PENSION_WITHDRAWALS_READ,
            EnumConsentV3Permission.LIFE_PENSION_CLAIM_READ
    )),
    FINANCIAL_ASSISTANCE(EnumSet.of(
            EnumConsentV3Permission.FINANCIAL_ASSISTANCE_READ,
            EnumConsentV3Permission.FINANCIAL_ASSISTANCE_CONTRACTINFO_READ,
            EnumConsentV3Permission.FINANCIAL_ASSISTANCE_MOVEMENTS_READ
    )),
    CLAIM_NOTIFICATION_REQUEST_DAMAGE(EnumSet.of(
            EnumConsentV3Permission.CLAIM_NOTIFICATION_REQUEST_DAMAGE_CREATE
    )),
    CLAIM_NOTIFICATION_REQUEST_PERSON(EnumSet.of(
            EnumConsentV3Permission.CLAIM_NOTIFICATION_REQUEST_PERSON_CREATE
    )),
    ENDORSEMENT_REQUEST(EnumSet.of(
            EnumConsentV3Permission.ENDORSEMENT_REQUEST_CREATE
    )),
    QUOTE_PATRIMONIAL_LEAD(EnumSet.of(
            EnumConsentV3Permission.QUOTE_PATRIMONIAL_LEAD_CREATE,
            EnumConsentV3Permission.QUOTE_PATRIMONIAL_LEAD_UPDATE
    ), false),
    QUOTE_PATRIMONIAL_HOME(EnumSet.of(
            EnumConsentV3Permission.QUOTE_PATRIMONIAL_HOME_READ,
            EnumConsentV3Permission.QUOTE_PATRIMONIAL_HOME_CREATE,
            EnumConsentV3Permission.QUOTE_PATRIMONIAL_HOME_UPDATE
    ), false),
    QUOTE_PATRIMONIAL_CONDOMINIUM(EnumSet.of(
            EnumConsentV3Permission.QUOTE_PATRIMONIAL_CONDOMINIUM_READ,
            EnumConsentV3Permission.QUOTE_PATRIMONIAL_CONDOMINIUM_CREATE,
            EnumConsentV3Permission.QUOTE_PATRIMONIAL_CONDOMINIUM_UPDATE
    ), false),
    QUOTE_PATRIMONIAL_BUSINESS(EnumSet.of(
            EnumConsentV3Permission.QUOTE_PATRIMONIAL_BUSINESS_READ,
            EnumConsentV3Permission.QUOTE_PATRIMONIAL_BUSINESS_CREATE,
            EnumConsentV3Permission.QUOTE_PATRIMONIAL_BUSINESS_UPDATE
    ), false),
    QUOTE_PERSON_LIFE(EnumSet.of(
            EnumConsentV3Permission.QUOTE_PERSON_LIFE_READ,
            EnumConsentV3Permission.QUOTE_PERSON_LIFE_CREATE,
            EnumConsentV3Permission.QUOTE_PERSON_LIFE_UPDATE
    ), false),
    QUOTE_TRAVEL(EnumSet.of(
            EnumConsentV3Permission.QUOTE_PERSON_TRAVEL_READ,
            EnumConsentV3Permission.QUOTE_PERSON_TRAVEL_CREATE,
            EnumConsentV3Permission.QUOTE_PERSON_TRAVEL_UPDATE
    ), false),
    CONTRACT_PENSION_PLAN_LEAD(EnumSet.of(
            EnumConsentV3Permission.CONTRACT_PENSION_PLAN_LEAD_CREATE,
            EnumConsentV3Permission.CONTRACT_PENSION_PLAN_LEAD_UPDATE
    )),
    CONTRACT_LIFE_PENSION_PLAN_LEAD(EnumSet.of(
            EnumConsentV3Permission.CONTRACT_LIFE_PENSION_LEAD_CREATE,
            EnumConsentV3Permission.CONTRACT_LIFE_PENSION_LEAD_UPDATE
    )),
    PENSION_WITHDRAWAL(EnumSet.of(
            EnumConsentV3Permission.PENSION_WITHDRAWAL_CREATE
    )),
    CAPITALIZATION_TITLE_WITHDRAWAL(EnumSet.of(
            EnumConsentV3Permission.CAPITALIZATION_TITLE_WITHDRAWAL_CREATE
    )),
    QUOTE_CAPITALIZATION_TITLE(EnumSet.of(
            EnumConsentV3Permission.QUOTE_CAPITALIZATION_TITLE_LEAD_CREATE,
            EnumConsentV3Permission.QUOTE_CAPITALIZATION_TITLE_LEAD_UPDATE,
            EnumConsentV3Permission.QUOTE_CAPITALIZATION_TITLE_READ,
            EnumConsentV3Permission.QUOTE_CAPITALIZATION_TITLE_CREATE,
            EnumConsentV3Permission.QUOTE_CAPITALIZATION_TITLE_UPDATE
    )),
    QUOTE_CAPITALIZATION_TITLE_RAFFLE(EnumSet.of(
            EnumConsentV3Permission.CAPITALIZATION_TITLE_RAFFLE_CREATE
    ));

    private final Set<EnumConsentV3Permission> permissions;

    private final boolean isAllowed;

    PermissionV3Group(Set<EnumConsentV3Permission> permissions, boolean isAllowed) {
        this.isAllowed = isAllowed;
        this.permissions = permissions;
    }

    PermissionV3Group(Set<EnumConsentV3Permission> permissions) {
        this.isAllowed = true;
        this.permissions = permissions;
    }

    public boolean containsAny(List<EnumConsentV3Permission> permissions) {
        return permissions.stream().anyMatch(this.permissions::contains);
    }
}
