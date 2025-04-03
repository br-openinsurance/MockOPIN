package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.AccountHolderEntity;
import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
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

import java.util.List;
import java.util.Optional;

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

    public ConsentEntity updateConsent(String consentId, UpdateConsent req) {
        var entity = this.getFullConsent(consentId);
        return this.consentRepository.update(entity.updateFromRequest(req));
    }

    public ConsentEntity getFullConsent(String consentId) {
        return getConsent(consentId, OP_CLIENT_ID);
    }

    public ConsentEntity getConsent(String consentId, String clientId) {
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
        ConsentEntity entity = this.getConsent(consentId, clientId);
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
