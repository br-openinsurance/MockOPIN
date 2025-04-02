package com.raidiam.trustframework.mockinsurance.utils;

import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentPermission;
import lombok.Getter;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Getter
public enum PermissionGroup {

    PERSONAL_REGISTRATION(EnumSet.of(
            EnumConsentPermission.RESOURCES_READ,
            EnumConsentPermission.CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ,
            EnumConsentPermission.CUSTOMERS_PERSONAL_QUALIFICATION_READ,
            EnumConsentPermission.CUSTOMERS_PERSONAL_ADDITIONALINFO_READ
    )),
    BUSINESS_REGISTRATION(EnumSet.of(
            EnumConsentPermission.RESOURCES_READ,
            EnumConsentPermission.CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ,
            EnumConsentPermission.CUSTOMERS_BUSINESS_QUALIFICATION_READ,
            EnumConsentPermission.CUSTOMERS_BUSINESS_ADDITIONALINFO_READ
    )),
    CAPITALIZATION_TITLE(EnumSet.of(
            EnumConsentPermission.RESOURCES_READ,
            EnumConsentPermission.CAPITALIZATION_TITLE_READ,
            EnumConsentPermission.CAPITALIZATION_TITLE_PLANINFO_READ,
            EnumConsentPermission.CAPITALIZATION_TITLE_EVENTS_READ,
            EnumConsentPermission.CAPITALIZATION_TITLE_SETTLEMENTS_READ
    )),
    PENSION_PLAN(EnumSet.of(
            EnumConsentPermission.RESOURCES_READ,
            EnumConsentPermission.PENSION_PLAN_READ,
            EnumConsentPermission.PENSION_PLAN_CONTRACTINFO_READ,
            EnumConsentPermission.PENSION_PLAN_MOVEMENTS_READ,
            EnumConsentPermission.PENSION_PLAN_PORTABILITIES_READ,
            EnumConsentPermission.PENSION_PLAN_WITHDRAWALS_READ,
            EnumConsentPermission.PENSION_PLAN_CLAIM
    )),
    LIFE_PENSION_PLAN(EnumSet.of(
            EnumConsentPermission.RESOURCES_READ,
            EnumConsentPermission.LIFE_PENSION_READ,
            EnumConsentPermission.LIFE_PENSION_CONTRACTINFO_READ,
            EnumConsentPermission.LIFE_PENSION_MOVEMENTS_READ,
            EnumConsentPermission.LIFE_PENSION_PORTABILITIES_READ,
            EnumConsentPermission.LIFE_PENSION_WITHDRAWALS_READ,
            EnumConsentPermission.LIFE_PENSION_CLAIM
    )),
    FINANCIAL_ASSISTANCE(EnumSet.of(
            EnumConsentPermission.FINANCIAL_ASSISTANCE_READ,
            EnumConsentPermission.FINANCIAL_ASSISTANCE_CONTRACTINFO_READ,
            EnumConsentPermission.FINANCIAL_ASSISTANCE_MOVEMENTS_READ
    )),
    CLAIM_NOTIFICATION_REQUEST_DAMAGE(EnumSet.of(
            EnumConsentPermission.CLAIM_NOTIFICATION_REQUEST_DAMAGE_CREATE
    )),
    CLAIM_NOTIFICATION_REQUEST_PERSON(EnumSet.of(
            EnumConsentPermission.CLAIM_NOTIFICATION_REQUEST_PERSON_CREATE
    )),
    ENDORSEMENT_REQUEST(EnumSet.of(
            EnumConsentPermission.ENDORSEMENT_REQUEST_CREATE
    )),
    QUOTE_PATRIMONIAL_LEAD(EnumSet.of(
            EnumConsentPermission.QUOTE_PATRIMONIAL_LEAD_CREATE,
            EnumConsentPermission.QUOTE_PATRIMONIAL_LEAD_UPDATE
    ), false),
    QUOTE_PATRIMONIAL_HOME(EnumSet.of(
            EnumConsentPermission.QUOTE_PATRIMONIAL_HOME_READ,
            EnumConsentPermission.QUOTE_PATRIMONIAL_HOME_CREATE,
            EnumConsentPermission.QUOTE_PATRIMONIAL_HOME_UPDATE
    ), false),
    QUOTE_PATRIMONIAL_CONDOMINIUM(EnumSet.of(
            EnumConsentPermission.QUOTE_PATRIMONIAL_CONDOMINIUM_READ,
            EnumConsentPermission.QUOTE_PATRIMONIAL_CONDOMINIUM_CREATE,
            EnumConsentPermission.QUOTE_PATRIMONIAL_CONDOMINIUM_UPDATE
    ), false),
    QUOTE_PATRIMONIAL_BUSINESS(EnumSet.of(
            EnumConsentPermission.QUOTE_PATRIMONIAL_BUSINESS_READ,
            EnumConsentPermission.QUOTE_PATRIMONIAL_BUSINESS_CREATE,
            EnumConsentPermission.QUOTE_PATRIMONIAL_BUSINESS_UPDATE
    ), false),
    QUOTE_PERSON_LIFE(EnumSet.of(
            EnumConsentPermission.QUOTE_PERSON_LIFE_READ,
            EnumConsentPermission.QUOTE_PERSON_LIFE_CREATE,
            EnumConsentPermission.QUOTE_PERSON_LIFE_UPDATE
    ), false),
    QUOTE_TRAVEL(EnumSet.of(
            EnumConsentPermission.QUOTE_PERSON_TRAVEL_READ,
            EnumConsentPermission.QUOTE_PERSON_TRAVEL_CREATE,
            EnumConsentPermission.QUOTE_PERSON_TRAVEL_UPDATE
    ), false),
    CONTRACT_PENSION_PLAN_LEAD(EnumSet.of(
            EnumConsentPermission.CONTRACT_PENSION_PLAN_LEAD_CREATE,
            EnumConsentPermission.CONTRACT_PENSION_PLAN_LEAD_UPDATE
    )),
    CONTRACT_LIFE_PENSION_PLAN_LEAD(EnumSet.of(
            EnumConsentPermission.CONTRACT_LIFE_PENSION_LEAD_CREATE,
            EnumConsentPermission.CONTRACT_LIFE_PENSION_LEAD_UPDATE
    )),
    PENSION_WITHDRAWAL(EnumSet.of(
            EnumConsentPermission.PENSION_WITHDRAWAL_CREATE
    )),
    CAPITALIZATION_TITLE_WITHDRAWAL(EnumSet.of(
            EnumConsentPermission.CAPITALIZATION_TITLE_WITHDRAWAL_CREATE
    ));

    private final Set<EnumConsentPermission> permissions;

    private final boolean isAllowed;

    PermissionGroup(Set<EnumConsentPermission> permissions, boolean isAllowed) {
        this.isAllowed = isAllowed;
        this.permissions = permissions;
    }

    PermissionGroup(Set<EnumConsentPermission> permissions) {
        this.isAllowed = true;
        this.permissions = permissions;
    }

    public boolean containsAny(List<EnumConsentPermission> permissions) {
        return permissions.stream().anyMatch(this.permissions::contains);
    }
}
