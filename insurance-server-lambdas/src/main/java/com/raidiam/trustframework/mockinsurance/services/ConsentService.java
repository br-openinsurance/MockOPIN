package com.raidiam.trustframework.mockinsurance.services;


import com.raidiam.trustframework.mockinsurance.domain.*;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import com.raidiam.trustframework.mockinsurance.utils.PermissionGroup;
import com.raidiam.trustframework.mockinsurance.utils.PermissionPhase;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Singleton
@Transactional
public class ConsentService extends BaseInsuranceService {
    private static final Logger LOG = LoggerFactory.getLogger(ConsentService.class);

    private static final String OP_CLIENT_ID = "op";

    public ConsentEntity createConsent(CreateConsent req, String clientId) {
        this.validateRequest(req);

        var accountHolderId = this.accountHolderRepository.findByDocumentIdentificationAndDocumentRel(
                req.getData().getLoggedUser().getDocument().getIdentification(),
                req.getData().getLoggedUser().getDocument().getRel()
        ).map(AccountHolderEntity::getAccountHolderId).orElse(null);
        var entity = ConsentEntity.fromRequest(req, accountHolderId, clientId);

        return this.consentRepository.save(entity);
    }

    public ResponseConsent updateConsent(String consentId, UpdateConsent req) {
        var consent = this.getConsentEntity(consentId);
        if (req.getData().getStatus() != null) {
            consent.setStatus(req.getData().getStatus().toString());
            if (EnumConsentStatus.REJECTED.equals(req.getData().getStatus())) {
                consent.setRejectionCode(EnumReasonCode.CUSTOMER_MANUALLY_REJECTED.name());
                consent.setRejectedBy(EnumRejectedBy.USER.name());
            }
        }

        // add resources to consent here as they are implemented
        if (req.getData().getLinkedCapitalizationTilePlanIds() != null) {
            addCapitalizationTitlePlansToConsent(consent, req.getData().getLinkedCapitalizationTilePlanIds());
        }

        if (req.getData().getLinkedFinancialRiskPolicyIds() != null) {
            addFinancialRiskPoliciesToConsent(consent, req.getData().getLinkedFinancialRiskPolicyIds());
        }

        if (req.getData().getLinkedHousingPolicyIds() != null) {
            addHousingPoliciesToConsent(consent, req.getData().getLinkedHousingPolicyIds());
        }

        if (req.getData().getLinkedResponsibilityPolicyIds() != null) {
            addResponsibilityPoliciesToConsent(consent, req.getData().getLinkedResponsibilityPolicyIds());
        }

        if (req.getData().getLinkedPersonPolicyIds() != null) {
            addPersonPoliciesToConsent(consent, req.getData().getLinkedPersonPolicyIds());
        }

        if (req.getData().getLinkedLifePensionContractIds() != null) {
            addLifePensionContractsToConsent(consent, req.getData().getLinkedLifePensionContractIds());
        }

        if (req.getData().getLinkedPensionPlanContractIds() != null) {
            addPensionPlanContractsToConsent(consent, req.getData().getLinkedPensionPlanContractIds());
        }

        if (req.getData().getLinkedAcceptanceAndBranchesAbroadPolicyIds() != null) {
            addAcceptanceAndBranchesAbroadPoliciesToConsent(consent, req.getData().getLinkedAcceptanceAndBranchesAbroadPolicyIds());
        }
        
        if (req.getData().getLinkedPatrimonialPolicyIds() != null) {
            addPatrimonialPoliciesToConsent(consent, req.getData().getLinkedPatrimonialPolicyIds());
        }
        
        if (req.getData().getLinkedRuralPolicyIds() != null) {
            addRuralPoliciesToConsent(consent, req.getData().getLinkedRuralPolicyIds());
        }

        if (req.getData().getLinkedFinancialAssistanceContractIds() != null) {
            addFinancialAssistanceContractsToConsent(consent, req.getData().getLinkedFinancialAssistanceContractIds());
        }

        if (req.getData().getLinkedAutoPolicyIds() != null) {
            addAutoPoliciesToConsent(consent, req.getData().getLinkedAutoPolicyIds());
        }

        if (req.getData().getLinkedTransportPolicyIds() != null) {
            addTransportPoliciesToConsent(consent, req.getData().getLinkedTransportPolicyIds());
        }

        consent.setStatusUpdateDateTime(new Date());
        consentRepository.update(consent);

        return consentRepository.findById(consent.getReferenceId())
                .orElseThrow(() -> new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not look up consent that has just been updated")).toFullResponse();
    }

    private void addCapitalizationTitlePlansToConsent(ConsentEntity consent, List<String> requestedPlans) {
        addObjectToConsent(consent, requestedPlans, "Capitalization Title Plan",
                capitalizationTitleRepository::findByCapitalizationTitlePlanId,
                CapitalizationTitlePlanEntity::getAccountHolder,
                c -> consentCapitalizationTitlePlanRepository.save(new ConsentCapitalizationTitlePlanEntity(consent, c)));
    }

    private void addFinancialRiskPoliciesToConsent(ConsentEntity consent, List<String> requestedPolicies) {
        addObjectToConsent(consent, requestedPolicies, "Financial Risk Policy",
                financialRiskPolicyRepository::findByFinancialRiskPolicyId,
                FinancialRiskPolicyEntity::getAccountHolder,
                c -> consentFinancialRiskPolicyRepository.save(new ConsentFinancialRiskPolicyEntity(consent, c)));
    }

    private void addHousingPoliciesToConsent(ConsentEntity consent, List<String> requestedPolicies) {
        addObjectToConsent(consent, requestedPolicies, "Housing Policy",
                housingPolicyRepository::findByHousingPolicyId,
                HousingPolicyEntity::getAccountHolder,
                c -> consentHousingPolicyRepository.save(new ConsentHousingPolicyEntity(consent, c)));
    }

    private void addResponsibilityPoliciesToConsent(ConsentEntity consent, List<String> requestedPolicies) {
        addObjectToConsent(consent, requestedPolicies, "Responsibility Policy",
                responsibilityPolicyRepository::findByResponsibilityPolicyId,
                ResponsibilityPolicyEntity::getAccountHolder,
                c -> consentResponsibilityPolicyRepository.save(new ConsentResponsibilityPolicyEntity(consent, c)));
    }

    private void addPersonPoliciesToConsent(ConsentEntity consent, List<String> requestedPolicies) {
        addObjectToConsent(consent, requestedPolicies, "Person Policy",
                personPolicyRepository::findByPersonPolicyId,
                PersonPolicyEntity::getAccountHolder,
                c -> consentPersonPolicyRepository.save(new ConsentPersonPolicyEntity(consent, c)));
    }

    private void addLifePensionContractsToConsent(ConsentEntity consent, List<String> requestedPlans) {
        addObjectToConsent(consent, requestedPlans, "Life Pension Contract",
                lifePensionContractRepository::findByLifePensionContractId,
                LifePensionContractEntity::getAccountHolder,
                c -> consentLifePensionContractRepository.save(new ConsentLifePensionContractEntity(consent, c)));
    }

    private void addPensionPlanContractsToConsent(ConsentEntity consent, List<String> requestedPlans) {
        addObjectToConsentIdAsString(consent, requestedPlans, "Pension Pension Contract",
                pensionPlanContractRepository::findByPensionPlanContractId,
                PensionPlanContractEntity::getAccountHolder,
                c -> consentPensionPlanContractRepository.save(new ConsentPensionPlanContractEntity(consent, c)));
    }
  
    private void addAcceptanceAndBranchesAbroadPoliciesToConsent(ConsentEntity consent, List<String> requestedPolicies) {
        addObjectToConsent(consent, requestedPolicies, "Acceptance And Branches Abroad Policy",
                acceptanceAndBranchesAbroadPolicyRepository::findByPolicyId,
                AcceptanceAndBranchesAbroadPolicyEntity::getAccountHolder,
                c -> consentAcceptanceAndBranchesAbroadPolicyRepository.save(new ConsentAcceptanceAndBranchesAbroadPolicyEntity(consent, c)));
    }
  
    private void addPatrimonialPoliciesToConsent(ConsentEntity consent, List<String> requestedPolicies) {
        addObjectToConsent(consent, requestedPolicies, "Patrimonial Policy",
                patrimonialPolicyRepository::findByPolicyId,
                PatrimonialPolicyEntity::getAccountHolder,
                c -> consentPatrimonialPolicyRepository.save(new ConsentPatrimonialPolicyEntity(consent, c)));
    }
  
    private void addRuralPoliciesToConsent(ConsentEntity consent, List<String> requestedPolicies) {
        addObjectToConsent(consent, requestedPolicies, "Rural Policy",
                ruralPolicyRepository::findByPolicyId,
                RuralPolicyEntity::getAccountHolder,
                c -> consentRuralPolicyRepository.save(new ConsentRuralPolicyEntity(consent, c)));
    }

    private void addFinancialAssistanceContractsToConsent(ConsentEntity consent, List<String> requestedContracts) {
        addObjectToConsentIdAsString(consent, requestedContracts, "Financial Assistance Contract",
                financialAssistanceContractRepository::findByFinancialAssistanceContractId,
                FinancialAssistanceContractEntity::getAccountHolder,
                c -> consentFinancialAssistanceContractRepository.save(new ConsentFinancialAssistanceContractEntity(consent, c)));
    }

    private void addAutoPoliciesToConsent(ConsentEntity consent, List<String> requestedContracts) {
        addObjectToConsentIdAsString(consent, requestedContracts, "Auto Policy",
                autoPolicyRepository::findByAutoPolicyId,
                AutoPolicyEntity::getAccountHolder,
                c -> consentAutoPolicyRepository.save(new ConsentAutoPolicyEntity(consent, c)));
    }

    private void addTransportPoliciesToConsent(ConsentEntity consent, List<String> requestedContracts) {
        addObjectToConsentIdAsString(consent, requestedContracts, "Transport Policy",
                transportPolicyRepository::findByTransportPolicyId,
                TransportPolicyEntity::getAccountHolder,
                c -> consentTransportPolicyRepository.save(new ConsentTransportPolicyEntity(consent, c)));
    }

    private <T> void addObjectToConsent(ConsentEntity consent, List<String> requestAccounts, String logType,
                                        Function<UUID, Optional<T>> entityFinder,
                                        Function<T, AccountHolderEntity> getAccountHolder,
                                        Consumer<T> consentBinder) {
        for (var accountId : requestAccounts) {
            var entity = entityFinder.apply(UUID.fromString(accountId))
                    .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, String.format("%s could not be found, cannot update consent", logType)));
            if (!getAccountHolder.apply(entity).equals(consent.getAccountHolder())) {
                throw new HttpStatusException(HttpStatus.FORBIDDEN, String.format("%s does not belong to this accountHolder", logType));
            }
            consentBinder.accept(entity);
        }
    }

    private <T> void addObjectToConsentIdAsString(ConsentEntity consent, List<String> requestAccounts, String logType,
                                        Function<String, Optional<T>> entityFinder,
                                        Function<T, AccountHolderEntity> getAccountHolder,
                                        Consumer<T> consentBinder) {
        for (var accountId : requestAccounts) {
            var entity = entityFinder.apply(accountId)
                    .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, String.format("%s could not be found, cannot update consent", logType)));
            if (!getAccountHolder.apply(entity).equals(consent.getAccountHolder())) {
                throw new HttpStatusException(HttpStatus.FORBIDDEN, String.format("%s does not belong to this accountHolder", logType));
            }
            consentBinder.accept(entity);
        }
    }

    public ResponseConsent getFullConsent(String consentId) {
        return getConsentEntity(consentId, OP_CLIENT_ID).toFullResponse();
    }

    public ResponseConsent getConsent(String consentId, String clientId) {
        return getConsentEntity(consentId, clientId).toResponse();
    }

    private ConsentEntity getConsentEntity(String consentId) {
        return getConsentEntity(consentId, OP_CLIENT_ID);
    }

    public ConsentEntity getConsentEntity(String consentId, String clientId) {
        ConsentEntity entity = this.consentRepository.findByConsentId(consentId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Consent Id " + consentId + " not found"));

        if (!OP_CLIENT_ID.equals(clientId) && !entity.getClientId().equals(clientId)) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "NAO_INFORMADO: Requested a consent created with a different oauth client");
        }

        var now = InsuranceLambdaUtils.getOffsetDateTimeUTC();
        var createdAt = InsuranceLambdaUtils.dateToOffsetDate(entity.getCreationDateTime());
        if (EnumConsentStatus.AWAITING_AUTHORISATION.toString().equals(entity.getStatus()) && now.isAfter(createdAt.plusHours(1))) {
            LOG.info("Consent awaiting authorization for too long, moving to rejected");
            entity.setStatus(EnumConsentStatus.REJECTED.name());
            entity.setRejectionCode(EnumReasonCode.CONSENT_EXPIRED.name());
            entity.setRejectedBy(EnumRejectedBy.USER.name());
            return this.consentRepository.save(entity);
        }

        var expiresAt = InsuranceLambdaUtils.dateToOffsetDate(entity.getExpirationDateTime());
        if (EnumConsentStatus.AUTHORISED.toString().equals(entity.getStatus()) && now.isAfter(expiresAt)) {
            LOG.info("Consent reached expiration, moving to rejected");
            entity.setStatus(EnumConsentStatus.REJECTED.name());
            entity.setRejectionCode(EnumReasonCode.CONSENT_MAX_DATE_REACHED.name());
            entity.setRejectedBy(EnumRejectedBy.ASPSP.name());
            return this.consentRepository.save(entity);
        }

        return entity;
    }

    public void deleteConsent(String consentId, String clientId) {
        ConsentEntity entity = this.getConsentEntity(consentId, clientId);
        if (EnumConsentStatus.REJECTED.name().equals(entity.getStatus())) {
            return;
        }

        entity.setRejectionCode(entity.getStatus().equals(EnumConsentStatus.AWAITING_AUTHORISATION.name()) ?
                EnumReasonCode.CUSTOMER_MANUALLY_REJECTED.name() : EnumReasonCode.CUSTOMER_MANUALLY_REVOKED.name());
        entity.setStatus(EnumConsentStatus.REJECTED.name());
        entity.setRejectedBy(EnumRejectedBy.USER.name());
        consentRepository.save(entity);
    }

    private void validateRequest(CreateConsent req) {
        this.validatePermissions(req);
        this.validateExpiration(req);
    }

    private void validateExpiration(CreateConsent req) {
        var expirationDateTime = req.getData().getExpirationDateTime();
        if (expirationDateTime == null) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: expirationDateTime is required");
        }

        var now = InsuranceLambdaUtils.getOffsetDateTimeUTC();
        if (expirationDateTime.isBefore(now)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NAO_INFORMADO: The expiration time cannot be in the past");
        }
        if (expirationDateTime.isAfter(now.plusYears(1))) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NAO_INFORMADO: The expiration time cannot be greater than one year");
        }
    }

    private void validatePermissions(CreateConsent req) {
        var permissions = req.getData().getPermissions();
        permissions.forEach(p -> Optional.ofNullable(p)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.BAD_REQUEST, "BAD_PERMISSION: Invalid permission")));

        boolean isPhase2 = PermissionPhase.PHASE2.containsAny(permissions);
        boolean isPhase3 = PermissionPhase.PHASE3.containsAny(permissions);
        if (isPhase2 && isPhase3) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: Cannot request permissions from phase 2 and 3 at the same time");
        }

        if (isPhase2) {
            validatePhase2Permissions(permissions);
        }

        if (isPhase3) {
            validatePhase3Permissions(permissions);
        }
    }

    private void validatePhase2Permissions(List<EnumConsentPermission> permissions) {
        if (!permissions.contains(EnumConsentPermission.RESOURCES_READ)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NAO_INFORMADO: The permission RESOURCES_READ is required for phase 2");
        }

        if (permissions.size() == 1) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NAO_INFORMADO: The permission RESOURCES_READ cannot be requested alone");
        }
    }

    private void validatePhase3Permissions(List<EnumConsentPermission> permissions) {
        PermissionGroup group = null;
        int numberOfGroups = 0;
        for (PermissionGroup g : PermissionGroup.values()) {
            // Check if any of the requested permissions belongs to the current group.
            if (!g.containsAny(permissions)) {
                continue;
            }

            if (!g.isAllowed()) {
                throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: Permission not allowed");
            }

            numberOfGroups++;
            group = g;
        }

        if (numberOfGroups != 1) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: The permissions of different phase 3 categories were requested");
        }

        for (EnumConsentPermission p : group.getPermissions()) {
            if (!permissions.contains(p)) {
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NAO_INFORMADO: All the permission from the group must be requested");
            }
        }
    }
}
