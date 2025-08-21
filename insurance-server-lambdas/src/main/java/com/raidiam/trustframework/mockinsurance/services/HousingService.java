package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.domain.HousingPolicyClaimEntity;
import com.raidiam.trustframework.mockinsurance.domain.HousingPolicyEntity;
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
public class HousingService extends BaseInsuranceService {

    private static final Logger LOG = LoggerFactory.getLogger(HousingService.class);

    public BaseInsuranceResponse getPolicies(Pageable pageable, String consentId) {
        LOG.info("Getting Personal Customers Identification response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_READ);

        var policies = housingPolicyRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId(), pageable).getContent();
        return new BaseInsuranceResponse()
                .data(List.of(new BaseBrandAndCompanyData()
                        .brand("Mock")
                        .companies(List.of(new BaseBrandAndCompanyDataCompanies()
                                        .companyName("Mock Insurer")
                                        .cnpjNumber("12345678901234")
                                        .policies(policies.stream().map(HousingPolicyEntity::mapPolicyDTO).toList())))));
    }

    private HousingPolicyEntity getPolicy(UUID policyId, String consentId, EnumConsentPermission permission) {
        LOG.info("Getting housing policy for policy id {} and consent id {}", policyId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var policy = housingPolicyRepository.findByHousingPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission);
        this.checkConsentCoversPolicy(consentEntity, policy);
        this.checkConsentOwnerIsPolicyOwner(consentEntity, policy);

        return policy;
    }

    public ResponseInsuranceHousingPolicyInfo getPolicyInfo(UUID policyId, String consentId) {
        LOG.info("Getting housing policy info response for consent id {}", consentId);
        var policy =  getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_POLICYINFO_READ);
        var response = policy.mapPolicyInfoDTO();

        policy.getBeneficiaryIds().forEach(beneficiaryId -> response.getData().addBeneficiariesItem(beneficiaryInfoRepository.findById(beneficiaryId)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Beneficiary not found for UUID %s", beneficiaryId)))
            .mapDTO()));

        policy.getInsuredIds().forEach(insuredIds -> response.getData().addInsuredsItem(personalInfoRepository.findById(insuredIds)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Personal info not found for UUID %s", insuredIds)))
            .mapDTO()));

        policy.getIntermediaryIds().forEach(intermediaryId -> response.getData().addIntermediariesItem(intermediaryRepository.findById(intermediaryId)
            .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Intermediary not found for UUID %s", intermediaryId)))
            .mapDTO()));

        return response;
    }

    public ResponseInsuranceHousingClaims getPolicyClaims(UUID policyId, String consentId, Pageable pageable) {
        LOG.info("Getting housing policy claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_CLAIM_READ);

        var claims = housingPolicyClaimRepository.findByHousingPolicyId(policyId, pageable);
        var resp = new ResponseInsuranceHousingClaims()
                .data(claims.getContent().stream().map(HousingPolicyClaimEntity::mapDTO).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

    public ResponseInsuranceHousingPremium getPolicyPremium(UUID policyId, String consentId) {
        LOG.info("Getting housing policy premium response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_PREMIUM_READ);

        var premium = housingPolicyPremiumRepository.findByHousingPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));
        var response = new ResponseInsuranceHousingPremium().data(premium.mapDTO());

        premium.getPaymentIds().forEach(paymentId -> response.getData().addPaymentsItem(paymentRepository.findById(paymentId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Payment not found for UUID %s", paymentId)))
                .mapDTO()));
        return response;
    }

    public void checkConsentCoversPolicy(ConsentEntity consentEntity, HousingPolicyEntity policy) {
        var policyFromConsent = consentEntity.getHousingPolicies()
                .stream()
                .filter(p -> policy.getHousingId().equals(p.getHousingId()))
                .findFirst();
        if (policyFromConsent.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad request, consent does not cover this housing!");
        }
    }

    public void checkConsentOwnerIsPolicyOwner(ConsentEntity consentEntity, HousingPolicyEntity policy) {
        if (!consentEntity.getAccountHolderId().equals(policy.getAccountHolderId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match account owner!");
        }
    }
}
