package com.raidiam.trustframework.mockinsurance.services;


import java.util.List;
import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.domain.AcceptanceAndBranchesAbroadClaimEntity;
import com.raidiam.trustframework.mockinsurance.domain.AcceptanceAndBranchesAbroadPolicyEntity;
import com.raidiam.trustframework.mockinsurance.domain.ConsentAcceptanceAndBranchesAbroadPolicyEntity;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
@Transactional
public class AcceptanceAndBranchesAbroadService extends BaseInsuranceService {
    private static final Logger LOG = LoggerFactory.getLogger(AcceptanceAndBranchesAbroadService.class);

    private List<AcceptanceAndBranchesAbroadPolicyEntity> getPolicyEntities(String consentId, Pageable pageable) {
        LOG.info("Getting acceptance and branches abroad policies response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_READ);

        return acceptanceAndBranchesAbroadPolicyRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId(), pageable).getContent();
    }

    public BaseInsuranceResponse getPolicies(String consentId, Pageable pageable) {
        List<AcceptanceAndBranchesAbroadPolicyEntity> policies = getPolicyEntities(consentId, pageable);
        
        return new BaseInsuranceResponse()
            .data(List.of(new BaseBrandAndCompanyData()
                .brand("Mock")
                .companies(List.of(new BaseBrandAndCompanyDataCompanies()
                    .companyName("Mock Insurer")
                    .cnpjNumber("12345678901234")
                    .policies(policies.stream().map(AcceptanceAndBranchesAbroadPolicyEntity::mapPolicyDto).toList())))));
    }
    
    public BaseInsuranceResponseV2 getPoliciesV2(String consentId, Pageable pageable) {
        List<AcceptanceAndBranchesAbroadPolicyEntity> policies = getPolicyEntities(consentId, pageable);

        return new BaseInsuranceResponseV2()
                .data(List.of(new BaseBrandAndCompanyDataV2()
                    .brand("Mock")
                    .companies(List.of(new BaseBrandAndCompanyDataV2Companies()
                            .companyName("Mock Insurer")
                            .cnpjNumber("12345678901234")
                            .policies(policies.stream().map(AcceptanceAndBranchesAbroadPolicyEntity::mapPolicyDto).toList())))));
    }

    private AcceptanceAndBranchesAbroadPolicyEntity getPolicy(UUID policyId, String consentId, EnumConsentPermission permission, EnumConsentV3Permission permissionV3) {
        LOG.info("Getting acceptance and branches abroad policy for policy id {} and consent id {}", policyId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var policy = acceptanceAndBranchesAbroadPolicyRepository.findByPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission, permissionV3);
        this.checkConsentCoversPolicy(consentEntity, policy);
        this.checkConsentOwnerIsPolicyOwner(consentEntity, policy);

        return policy;
    }

    public void checkConsentCoversPolicy(ConsentEntity consentEntity, AcceptanceAndBranchesAbroadPolicyEntity policy) {
        var policyFromConsent = consentEntity.getAcceptanceAndBranchesAbroadPolicies()
            .stream()
            .filter(p -> policy.getInsuranceId().equals(p.getInsuranceId()))
            .findFirst();
        if (policyFromConsent.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad request: consent does not cover this acceptance and branches abroad policy!");
        }
    }

    public void checkConsentOwnerIsPolicyOwner(ConsentEntity consentEntity, AcceptanceAndBranchesAbroadPolicyEntity policy) {
        if (!consentEntity.getAccountHolderId().equals(policy.getAccountHolderId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden: consent owner does not match account owner!");
        }
    }

    public void checkConsentOwnerIsPolicyOwner(Page<ConsentAcceptanceAndBranchesAbroadPolicyEntity> consentPolicy, ConsentEntity consentEntity) {
        if(consentPolicy.getContent()
                .stream()
                .map(ConsentAcceptanceAndBranchesAbroadPolicyEntity::getPolicy)
                .map(AcceptanceAndBranchesAbroadPolicyEntity::getAccountHolderId)
                .anyMatch(accountHolderId -> !accountHolderId.equals(consentEntity.getAccountHolderId()))) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match policy owner!");
        }
    }
    
    public ResponseInsuranceAcceptanceAndBranchesAbroadPolicyInfo getPolicyInfo(UUID policyId, String consentId) {
        LOG.info("Getting acceptance and branches abroad policy info response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_POLICYINFO_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_POLICYINFO_READ).mapPolicyInfoDto();
    }
    
    public ResponseInsuranceAcceptanceAndBranchesAbroadPolicyInfoV2 getPolicyInfoV2(UUID policyId, String consentId) {
        LOG.info("Getting acceptance and branches abroad policy info response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_POLICYINFO_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_POLICYINFO_READ).mapPolicyInfoDtoV2();
    }

    public ResponseInsuranceAcceptanceAndBranchesAbroadPremium getPremium(UUID policyId, String consentId) {
        LOG.info("Getting acceptance and branches abroad premium response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_PREMIUM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_PREMIUM_READ).mapPremiumDto();
    }

    public ResponseInsuranceAcceptanceAndBranchesAbroadPremiumV2 getPremiumV2(UUID policyId, String consentId) {
        LOG.info("Getting acceptance and branches abroad premium response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_PREMIUM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_PREMIUM_READ).mapPremiumDtoV2();
    }

    public ResponseInsuranceAcceptanceAndBranchesAbroadClaims getClaims(UUID policyId, String consentId, Pageable pageable) {
        LOG.info("Getting acceptance and branches abroad claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_CLAIM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_CLAIM_READ);

        var claims = acceptanceAndBranchesAbroadClaimRepository.findByPolicyId(policyId, pageable);
        var resp = new ResponseInsuranceAcceptanceAndBranchesAbroadClaims()
                .data(claims.getContent().stream().map(AcceptanceAndBranchesAbroadClaimEntity::toResponse).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

    public ResponseInsuranceAcceptanceAndBranchesAbroadClaimsV2 getClaimsV2(UUID policyId, String consentId, Pageable pageable) {
        LOG.info("Getting acceptance and branches abroad claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_CLAIM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_CLAIM_READ);

        var claims = acceptanceAndBranchesAbroadClaimRepository.findByPolicyId(policyId, pageable);
        var resp = new ResponseInsuranceAcceptanceAndBranchesAbroadClaimsV2()
                .data(claims.getContent().stream().map(AcceptanceAndBranchesAbroadClaimEntity::toResponseV2).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

}
