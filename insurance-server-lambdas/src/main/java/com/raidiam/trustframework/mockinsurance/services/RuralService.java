package com.raidiam.trustframework.mockinsurance.services;


import java.util.List;
import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.domain.RuralClaimEntity;
import com.raidiam.trustframework.mockinsurance.domain.RuralPolicyEntity;
import com.raidiam.trustframework.mockinsurance.domain.ConsentRuralPolicyEntity;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
@Transactional
public class RuralService extends BaseInsuranceService {
    private static final Logger LOG = LoggerFactory.getLogger(RuralService.class);

    public BaseInsuranceResponse getPolicies(String consentId, Pageable pageable) {
        LOG.info("Getting rural policies response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_READ);

        List<RuralPolicyEntity> policies = ruralPolicyRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId(), pageable).getContent();

        var response = new BaseInsuranceResponse()
                .data(List.of(new BaseBrandAndCompanyData()
                    .brand("Mock")
                    .companies(List.of(new BaseBrandAndCompanyDataCompanies()
                            .companyName("Mock Insurer")
                            .cnpjNumber("12345678901234")
                            .policies(policies.stream().map(RuralPolicyEntity::mapPolicyDto).toList()))))); 
        return response;
    }

    private RuralPolicyEntity getPolicy(UUID policyId, String consentId, EnumConsentPermission permission) {
        LOG.info("Getting rural policy for policy id {} and consent id {}", policyId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var policy = ruralPolicyRepository.findByPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission);
        this.checkConsentCoversPolicy(consentEntity, policy);
        this.checkConsentOwnerIsPolicyOwner(consentEntity, policy);

        return policy;
    }

    public void checkConsentCoversPolicy(ConsentEntity consentEntity, RuralPolicyEntity policy) {
        var policyFromConsent = consentEntity.getRuralPolicies();
        if (policyFromConsent.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "TA VAZIO DE NOVO!");
        }
        policyFromConsent
            .stream()
            .filter(p -> policy.getInsuranceId().equals(p.getInsuranceId()))
            .findFirst();
        if (policyFromConsent.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad request: consent does not cover this rural policy!");
        }
    }

    public void checkConsentOwnerIsPolicyOwner(ConsentEntity consentEntity, RuralPolicyEntity policy) {
        if (!consentEntity.getAccountHolderId().equals(policy.getAccountHolderId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden: consent owner does not match account owner!");
        }
    }

    public void checkConsentOwnerIsPolicyOwner(Page<ConsentRuralPolicyEntity> consentPolicy, ConsentEntity consentEntity) {
        if(consentPolicy.getContent()
                .stream()
                .map(ConsentRuralPolicyEntity::getPolicy)
                .map(RuralPolicyEntity::getAccountHolderId)
                .anyMatch(accountHolderId -> !accountHolderId.equals(consentEntity.getAccountHolderId()))) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match policy owner!");
        }
    }
    
    public ResponseInsuranceRuralPolicyInfo getPolicyInfo(UUID policyId, String consentId) {
        LOG.info("Getting rural policy info response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_POLICYINFO_READ).mapPolicyInfoDto();
    }

    public ResponseInsuranceRuralPremium getPremium(UUID policyId, String consentId) {
        LOG.info("Getting rural premium response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_PREMIUM_READ).mapPremiumDto();
    }

    public ResponseInsuranceRuralClaims getClaims(UUID policyId, String consentId, Pageable pageable) {
        LOG.info("Getting rural claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_CLAIM_READ);

        var claims = ruralClaimRepository.findByPolicyId(policyId, pageable);
        var resp = new ResponseInsuranceRuralClaims()
                .data(claims.getContent().stream().map(RuralClaimEntity::toResponse).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

}
