package com.raidiam.trustframework.mockinsurance.services;


import java.util.List;
import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.domain.PatrimonialClaimEntity;
import com.raidiam.trustframework.mockinsurance.domain.PatrimonialPolicyEntity;
import com.raidiam.trustframework.mockinsurance.domain.ConsentPatrimonialPolicyEntity;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
@Transactional
public class PatrimonialService extends BaseInsuranceService {
    private static final Logger LOG = LoggerFactory.getLogger(PatrimonialService.class);

    public BaseInsuranceResponse getPolicies(String consentId, Pageable pageable) {
        LOG.info("Getting patrimonial policies response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_READ);

        List<PatrimonialPolicyEntity> policies = patrimonialPolicyRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId(), pageable).getContent();

        var response = new BaseInsuranceResponse()
                .data(List.of(new BaseBrandAndCompanyData()
                    .brand("Mock")
                    .companies(List.of(new BaseBrandAndCompanyDataCompanies()
                            .companyName("Mock Insurer")
                            .cnpjNumber("12345678901234")
                            .policies(policies.stream().map(PatrimonialPolicyEntity::mapPolicyDto).toList()))))); 
        return response;
    }

    private PatrimonialPolicyEntity getPolicy(UUID policyId, String consentId, EnumConsentPermission permission) {
        LOG.info("Getting patrimonial policy for policy id {} and consent id {}", policyId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var policy = patrimonialPolicyRepository.findByPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission);
        this.checkConsentCoversPolicy(consentEntity, policy);
        this.checkConsentOwnerIsPolicyOwner(consentEntity, policy);

        return policy;
    }

    public void checkConsentCoversPolicy(ConsentEntity consentEntity, PatrimonialPolicyEntity policy) {
        var policyFromConsent = consentEntity.getPatrimonialPolicies()
            .stream()
            .filter(p -> policy.getInsuranceId().equals(p.getInsuranceId()))
            .findFirst();
        if (policyFromConsent.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad request: consent does not cover this patrimonial policy!");
        }
    }

    public void checkConsentOwnerIsPolicyOwner(ConsentEntity consentEntity, PatrimonialPolicyEntity policy) {
        if (!consentEntity.getAccountHolderId().equals(policy.getAccountHolderId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden: consent owner does not match account owner!");
        }
    }

    public void checkConsentOwnerIsPolicyOwner(Page<ConsentPatrimonialPolicyEntity> consentPolicy, ConsentEntity consentEntity) {
        if(consentPolicy.getContent()
                .stream()
                .map(ConsentPatrimonialPolicyEntity::getPolicy)
                .map(PatrimonialPolicyEntity::getAccountHolderId)
                .anyMatch(accountHolderId -> !accountHolderId.equals(consentEntity.getAccountHolderId()))) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match policy owner!");
        }
    }
    
    public ResponseInsurancePatrimonialPolicyInfo getPolicyInfo(UUID policyId, String consentId) {
        LOG.info("Getting patrimonial policy info response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_POLICYINFO_READ).mapPolicyInfoDto();
    }

    public ResponseInsurancePatrimonialPremium getPremium(UUID policyId, String consentId) {
        LOG.info("Getting patrimonial premium response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_PREMIUM_READ).mapPremiumDto();
    }

    public ResponseInsurancePatrimonialClaims getClaims(UUID policyId, String consentId, Pageable pageable) {
        LOG.info("Getting patrimonial claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_CLAIM_READ);

        var claims = patrimonialClaimRepository.findByPolicyId(policyId, pageable);
        var resp = new ResponseInsurancePatrimonialClaims()
                .data(claims.getContent().stream().map(PatrimonialClaimEntity::toResponse).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

}
