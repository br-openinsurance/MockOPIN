package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.ClaimNotificationEntity;
import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.models.generated.ClaimNotificationData;
import com.raidiam.trustframework.mockinsurance.models.generated.ClaimNotificationInformation;
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentStatus;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;



public abstract class ClaimNotificationService< E extends ClaimNotificationEntity > extends BaseInsuranceService {
    protected abstract Logger getLogger();
    protected abstract E saveClaimNotification(E claim);

    public E createClaimNotification(E claim) {
        this.getLogger().info("Validating claim notification");
        this.validateClaimNotification(claim);

        this.getLogger().info("Creating claim notification for consent id {}", claim.getConsentId());
        return this.saveClaimNotification(claim);
    }

    public void validateClaimNotification(E claim) {
        if (StringUtils.isBlank(claim.getConsentId())) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: consent id was not informed");
        }

        ConsentEntity consent = InsuranceLambdaUtils.getConsent(claim.getConsentId(), consentRepository);

        if (!claim.getClientId().equals(consent.getClientId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "NAO_INFORMADO: Requested a consent created with a different oauth client");
        }

        if (!consent.getStatus().equals(EnumConsentStatus.AUTHORISED.toString())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "NAO_INFORMADO: consent is not authorised");
        }

        var claimNotificationInformation = consent.getClaimNotificationInformation();

        if (claimNotificationInformation == null) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: consent does not have claim notification information");
        }

        validateClaimData(claim.getClaimData(), claimNotificationInformation);
    }

    public void validateClaimData(ClaimNotificationData claimData, ClaimNotificationInformation info) {
        // documentType
        if (claimData.getDocumentType() == null) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: document type was not informed");
        }
        if (!claimData.getDocumentType().toString().equals(info.getDocumentType().toString())) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: document type does not match");
        }

        // policyId (only when documentType is APOLICE_INDIVIDUAL or BILHETE)
        if (claimData.getDocumentType().toString().equals("APOLICE_INDIVIDUAL") || claimData.getDocumentType().toString().equals("BILHETE")) {
            if (claimData.getPolicyId() == null) {
                throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: policy id was not informed");
            }
            if (!claimData.getPolicyId().equals(info.getPolicyId())) {
                throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: policy id does not match");
            }
        }

        // groupCertificateId (only when documentType is CERTIFICADO)
        if (claimData.getDocumentType().toString().equals("CERTIFICADO")) {
            if (claimData.getGroupCertificateId() == null) {
                throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: group certificate id was not informed");
            }
            if (!claimData.getGroupCertificateId().equals(info.getGroupCertificateId())) {
                throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: group certificate id does not match");
            }
        }

        // insuredObjectId
        if (!claimData.getInsuredObjectId().equals(info.getInsuredObjectId())) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: insured object id does not match");
        }

        // occurrenceDate
        if (!claimData.getOccurrenceDate().equals(info.getOccurrenceDate())) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: occurrence date does not match");
        }
    }
}