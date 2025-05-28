package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.domain.ResponsibilityPolicyClaimEntity;
import com.raidiam.trustframework.mockinsurance.domain.ResponsibilityPolicyEntity;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@Singleton
@Transactional
public class ResponsibilityService extends BaseInsuranceService {

    private static final Logger LOG = LoggerFactory.getLogger(ResponsibilityService.class);

    public BaseInsuranceResponse getPolicies(Pageable pageable, String consentId) {
        LOG.info("Getting Personal Customers Identification response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_READ);

        var policies = responsibilityPolicyRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId(), pageable).getContent();
        return new BaseInsuranceResponse()
                .data(List.of(new BaseBrandAndCompanyData()
                        .brand("Mock")
                        .companies(List.of(new BaseBrandAndCompanyDataCompanies()
                                        .companyName("Mock Insurer")
                                        .cnpjNumber("12345678901234")
                                        .policies(policies.stream().map(ResponsibilityPolicyEntity::mapPolicyDTO).toList())))));
    }

    private ResponsibilityPolicyEntity getPolicy(UUID policyId, String consentId, EnumConsentPermission permission) {
        LOG.info("Getting responsibility policy for policy id {} and consent id {}", policyId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var policy = responsibilityPolicyRepository.findByResponsibilityPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission);
        this.checkConsentCoversPolicy(consentEntity, policy);
        this.checkConsentOwnerIsPolicyOwner(consentEntity, policy);

        return policy;
    }

    public ResponseInsuranceResponsibilityPolicyInfo getPolicyInfo(UUID policyId, String consentId) {
        LOG.info("Getting responsibility policy info response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_POLICYINFO_READ).mapPolicyInfoDTO();
    }

    public ResponseInsuranceResponsibilityClaims getPolicyClaims(UUID policyId, String consentId, Pageable pageable) {
        LOG.info("Getting responsibility policy claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_CLAIM_READ);

        var claims = responsibilityPolicyClaimRepository.findByResponsibilityPolicyId(policyId, pageable);
        var resp = new ResponseInsuranceResponsibilityClaims()
                .data(claims.getContent().stream().map(ResponsibilityPolicyClaimEntity::mapDTO).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

    public ResponseInsuranceResponsibilityPremium getPolicyPremium(UUID policyId, String consentId) {
        LOG.info("Getting responsibility policy premium response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_PREMIUM_READ);

        var premium = responsibilityPolicyPremiumRepository.findByResponsibilityPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));
        return new ResponseInsuranceResponsibilityPremium().data(premium.mapDTO());
    }

    public void checkConsentCoversPolicy(ConsentEntity consentEntity, ResponsibilityPolicyEntity policy) {
        var policyFromConsent = consentEntity.getResponsibilityPolicies()
                .stream()
                .filter(p -> policy.getResponsibilityId().equals(p.getResponsibilityId()))
                .findFirst();
        if (policyFromConsent.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad request, consent does not cover this responsibility!");
        }
    }

    public void checkConsentOwnerIsPolicyOwner(ConsentEntity consentEntity, ResponsibilityPolicyEntity policy) {
        if (!consentEntity.getAccountHolderId().equals(policy.getAccountHolderId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match account owner!");
        }
    }
}
