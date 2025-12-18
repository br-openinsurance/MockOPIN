package com.raidiam.trustframework.mockinsurance.services;


import java.util.List;
import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.domain.RuralPolicyClaimEntity;
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

    private List<RuralPolicyEntity> getRuralPolicyEntities(Pageable pageable, String consentId){
        LOG.info("Getting rural policies response for consent id {}", consentId);
    
        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
    
        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_RURAL_READ);
    
        return ruralPolicyRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId(), pageable).getContent();
    }

    public BaseInsuranceResponse getPolicies(String consentId, Pageable pageable) {
        List<RuralPolicyEntity> policies = getRuralPolicyEntities(pageable, consentId);

        return new BaseInsuranceResponse()
                .data(List.of(new BaseBrandAndCompanyData()
                    .brand("Mock")
                    .companies(List.of(new BaseBrandAndCompanyDataCompanies()
                            .companyName("Mock Insurer")
                            .cnpjNumber("12345678901234")
                            .policies(policies.stream().map(RuralPolicyEntity::mapPolicyDto).toList()))))); 
    }

    public BaseInsuranceResponseV2 getPoliciesV2(String consentId, Pageable pageable) {
        List<RuralPolicyEntity> policies = getRuralPolicyEntities(pageable, consentId);

        return new BaseInsuranceResponseV2()
                .data(List.of(new BaseBrandAndCompanyDataV2()
                    .brand("Mock")
                    .companies(List.of(new BaseBrandAndCompanyDataV2Companies()
                            .companyName("Mock Insurer")
                            .cnpjNumber("12345678901234")
                            .policies(policies.stream().map(RuralPolicyEntity::mapPolicyDto).toList()))))); 
    }

    private RuralPolicyEntity getPolicy(UUID policyId, String consentId, EnumConsentPermission permission, EnumConsentV3Permission permissionV3) {
        LOG.info("Getting rural policy for policy id {} and consent id {}", policyId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var policy = ruralPolicyRepository.findByRuralPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission, permissionV3);
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
                .map(ConsentRuralPolicyEntity::getRuralPolicy)
                .map(RuralPolicyEntity::getAccountHolderId)
                .anyMatch(accountHolderId -> !accountHolderId.equals(consentEntity.getAccountHolderId()))) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match policy owner!");
        }
    }
    
    public ResponseInsuranceRuralPolicyInfo getPolicyInfo(UUID policyId, String consentId) {
        LOG.info("Getting rural policy info response for consent id {}", consentId);
        var policy = getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_POLICYINFO_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_RURAL_POLICYINFO_READ);
        var response = policy.mapPolicyInfoDto();

        policy.getBeneficiaryIds().forEach(beneficiaryId -> response.getData().addBeneficiariesItem(beneficiaryInfoRepository.findById(beneficiaryId)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Beneficiary not found for UUID %s", beneficiaryId)))
            .mapDTO()));
        
        policy.getInsuredIds().forEach(insuredIds -> response.getData().addInsuredsItem(personalInfoRepository.findById(insuredIds)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Personal info not found for UUID %s", insuredIds)))
            .mapDTO()));
        
        policy.getIntermediaryIds().forEach(intermediaryId -> response.getData().addIntermediariesItem(intermediaryRepository.findById(intermediaryId)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Intermediary not found for UUID %s", intermediaryId)))
            .mapDTO()));
        
        policy.getPrincipalIds().forEach(principalId -> response.getData().addPrincipalsItem(principalInfoRepository.findById(principalId)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Principal not found for UUID %s", principalId)))
            .mapDTO()));
        
        policy.getCoinsurerIds().forEach(coinsurerId -> response.getData().addCoinsurersItem(coinsurerRepository.findById(coinsurerId)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Coinsurer not found for UUID %s", coinsurerId)))
            .mapDTO()));
         
        return response;
    }
    
    public ResponseInsuranceRuralPolicyInfoV2 getPolicyInfoV2(UUID policyId, String consentId) {
        LOG.info("Getting rural policy info response for consent id {}", consentId);
        var policy = getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_POLICYINFO_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_RURAL_POLICYINFO_READ);
        var response = policy.mapPolicyInfoDtoV2();

        policy.getBeneficiaryIds().forEach(beneficiaryId -> response.getData().addBeneficiariesItem(beneficiaryInfoRepository.findById(beneficiaryId)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Beneficiary not found for UUID %s", beneficiaryId)))
            .mapDTO()));
        
        policy.getInsuredIds().forEach(insuredIds -> response.getData().addInsuredsItem(personalInfoRepository.findById(insuredIds)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Personal info not found for UUID %s", insuredIds)))
            .mapDTOV2()));
        
        policy.getIntermediaryIds().forEach(intermediaryId -> response.getData().addIntermediariesItem(intermediaryRepository.findById(intermediaryId)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Intermediary not found for UUID %s", intermediaryId)))
            .mapDTOV2()));
        
        policy.getPrincipalIds().forEach(principalId -> response.getData().addPrincipalsItem(principalInfoRepository.findById(principalId)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Principal not found for UUID %s", principalId)))
            .mapDTOV2()));
        
        policy.getCoinsurerIds().forEach(coinsurerId -> response.getData().addCoinsurersItem(coinsurerRepository.findById(coinsurerId)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Coinsurer not found for UUID %s", coinsurerId)))
            .mapDTO()));
         
        return response;
    }

    public ResponseInsuranceRuralPremium getPremium(UUID policyId, String consentId) {
        LOG.info("Getting rural premium response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_PREMIUM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_RURAL_PREMIUM_READ);

        var premium = ruralPolicyPremiumRepository.findByRuralPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));
        var response = new ResponseInsuranceRuralPremium().data(premium.mapDto());

        premium.getPaymentIds().forEach(paymentId -> response.getData().addPaymentsItem(paymentRepository.findById(paymentId)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Payment not found for UUID %s", paymentId)))
            .mapDTO()));
        return response;
    }

    public ResponseInsuranceRuralClaims getClaims(UUID policyId, String consentId, Pageable pageable) {
        LOG.info("Getting rural claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_CLAIM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_RURAL_CLAIM_READ);

        var claims = ruralPolicyClaimRepository.findByRuralPolicyId(policyId, pageable);
        var resp = new ResponseInsuranceRuralClaims()
                .data(claims.getContent().stream().map(RuralPolicyClaimEntity::toResponse).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

    public ResponseInsuranceRuralClaimsV2 getClaimsV2(UUID policyId, String consentId, Pageable pageable) {
        LOG.info("Getting rural claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_CLAIM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_RURAL_CLAIM_READ);

        var claims = ruralPolicyClaimRepository.findByRuralPolicyId(policyId, pageable);
        var resp = new ResponseInsuranceRuralClaimsV2()
                .data(claims.getContent().stream().map(RuralPolicyClaimEntity::toResponseV2).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

}
