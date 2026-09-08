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

    private List<PatrimonialPolicyEntity> getPatrimonialPolicyEntities(Pageable pageable, String consentId) {
        LOG.info("Getting patrimonial policies response for consent id {}", consentId);
    
        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
    
        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PATRIMONIAL_READ);
    
        return patrimonialPolicyRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId(), pageable).getContent();
    }

    public BaseInsuranceResponse getPolicies(String consentId, Pageable pageable) {
        List<PatrimonialPolicyEntity> policies = getPatrimonialPolicyEntities(pageable, consentId);

        return new BaseInsuranceResponse()
                .data(List.of(new BaseBrandAndCompanyData()
                    .brand("Mock")
                    .companies(List.of(new BaseBrandAndCompanyDataCompanies()
                            .companyName("Mock Insurer")
                            .cnpjNumber("12345678901234")
                            .policies(policies.stream().map(PatrimonialPolicyEntity::mapPolicyDto).toList()))))); 
    }

    public BaseInsuranceResponseV2 getPoliciesV2(String consentId, Pageable pageable) {
        List<PatrimonialPolicyEntity> policies = getPatrimonialPolicyEntities(pageable, consentId);

        return new BaseInsuranceResponseV2()
                .data(List.of(new BaseBrandAndCompanyDataV2()
                    .brand("Mock")
                    .companies(List.of(new BaseBrandAndCompanyDataV2Companies()
                            .companyName("Mock Insurer")
                            .cnpjNumber("12345678901234")
                            .policies(policies.stream().map(PatrimonialPolicyEntity::mapPolicyDto).toList()))))); 
    }

    private PatrimonialPolicyEntity getPolicy(UUID policyId, String consentId, EnumConsentPermission permission, EnumConsentV3Permission permissionV3) {
        LOG.info("Getting patrimonial policy for policy id {} and consent id {}", policyId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var policy = patrimonialPolicyRepository.findByPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission, permissionV3);
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
        var policy = getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_POLICYINFO_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PATRIMONIAL_POLICYINFO_READ);
        var response = policy.mapPolicyInfoDto();

        policy.getInsuredIds().forEach(insuredId -> response.getData().addInsuredsItem(personalInfoRepository.findById(insuredId)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Personal info not found for UUID %s", insuredId)))
            .mapDTO()));

        return response;
    }

    public ResponseInsurancePatrimonialPolicyInfoV2 getPolicyInfoV2(UUID policyId, String consentId) {
        LOG.info("Getting patrimonial policy info response for consent id {}", consentId);
        var policy = getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_POLICYINFO_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PATRIMONIAL_POLICYINFO_READ);
        var response = policy.mapPolicyInfoDtoV2();

        policy.getInsuredIds().forEach(insuredId -> response.getData().addInsuredsItem(personalInfoRepository.findById(insuredId)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Personal info not found for UUID %s", insuredId)))
            .mapDTOV2()));

        return response;
    }

    public ResponseInsurancePatrimonialPremium getPremium(UUID policyId, String consentId) {
        LOG.info("Getting patrimonial premium response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_PREMIUM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PATRIMONIAL_PREMIUM_READ);

        var premium = patrimonialPremiumRepository.findByPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));
        var response = new ResponseInsurancePatrimonialPremium().data(premium.mapDto());

        premium.getPaymentIds().forEach(paymentId -> {
            Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Payment not found for UUID %s", paymentId)))
                .mapDTO();
            payment.setAmount(new AmountDetails()
                .amount(payment.getAmount().getAmount())
                .currency(AmountDetails.CurrencyEnum.BRL));
            response.getData().addPaymentsItem(payment);
        });
        return response;
    }

    public ResponseInsurancePatrimonialClaims getClaims(UUID policyId, String consentId, Pageable pageable) {
        LOG.info("Getting patrimonial claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_CLAIM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PATRIMONIAL_CLAIM_READ);

        var claims = patrimonialClaimRepository.findByPolicyId(policyId, pageable);
        var resp = new ResponseInsurancePatrimonialClaims()
                .data(claims.getContent().stream().map(PatrimonialClaimEntity::toResponse).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

    public ResponseInsurancePatrimonialClaimsV2 getClaimsV2(UUID policyId, String consentId, Pageable pageable) {
        LOG.info("Getting patrimonial claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_CLAIM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PATRIMONIAL_CLAIM_READ);

        var claims = patrimonialClaimRepository.findByPolicyId(policyId, pageable);
        var resp = new ResponseInsurancePatrimonialClaimsV2()
                .data(claims.getContent().stream().map(PatrimonialClaimEntity::toResponseV2).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

}
