package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.*;
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
public class FinancialRiskService extends BaseInsuranceService {

    private static final Logger LOG = LoggerFactory.getLogger(FinancialRiskService.class);

    public BaseInsuranceResponse getPolicies(Pageable pageable, String consentId) {
        LOG.info("Getting Personal Customers Identification response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_READ);

        var policies = financialRiskPolicyRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId(), pageable).getContent();
        return new BaseInsuranceResponse()
                .data(List.of(new BaseBrandAndCompanyData()
                        .brand("Mock")
                        .companies(List.of(new BaseBrandAndCompanyDataCompanies()
                                        .companyName("Mock Insurer")
                                        .cnpjNumber("12345678901234")
                                        .policies(policies.stream().map(FinancialRiskPolicyEntity::mapPolicyDTO).toList())))));
    }

    private FinancialRiskPolicyEntity getPolicy(UUID policyId, String consentId, EnumConsentPermission permission, EnumConsentV3Permission permissionV3) {
        LOG.info("Getting financial risk policy for policy id {} and consent id {}", policyId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var policy = financialRiskPolicyRepository.findByFinancialRiskPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission, permissionV3);
        this.checkConsentCoversPolicy(consentEntity, policy);
        this.checkConsentOwnerIsPolicyOwner(consentEntity, policy);

        return policy;
    }

    public ResponseInsuranceFinancialRiskPolicyInfo getPolicyInfo(UUID policyId, String consentId) {
        LOG.info("Getting financial risk policy info response for consent id {}", consentId);
        var policy = getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_POLICYINFO_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_POLICYINFO_READ);
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

        policy.getPrincipalIds().forEach(principalId -> response.getData().addPrincipalsItem(principalInfoRepository.findById(principalId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Principal not found for UUID %s", principalId)))
                .mapDTO()));

        policy.getCoinsurerIds().forEach(coinsurerId -> response.getData().addCoinsurersItem(coinsurerRepository.findById(coinsurerId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Coinsurer not found for UUID %s", coinsurerId)))
                .mapDTO()));

        return response;
    }

    public ResponseInsuranceFinancialRiskClaims getPolicyClaims(UUID policyId, String consentId, Pageable pageable) {
        LOG.info("Getting financial risk policy claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_CLAIM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_CLAIM_READ);

        var claims = financialRiskPolicyClaimRepository.findByFinancialRiskPolicyId(policyId, pageable);
        var resp = new ResponseInsuranceFinancialRiskClaims()
                .data(claims.getContent().stream().map(FinancialRiskPolicyClaimEntity::mapDTO).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

    public ResponseInsuranceFinancialRiskPremium getPolicyPremium(UUID policyId, String consentId) {
        LOG.info("Getting financial risk policy premium response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_PREMIUM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_PREMIUM_READ);

        var premium = financialRiskPolicyPremiumRepository.findByFinancialRiskPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));

        var response = new ResponseInsuranceFinancialRiskPremium().data(premium.mapDTO());

        premium.getPaymentIds().forEach(paymentId -> response.getData().addPaymentsItem(paymentRepository.findById(paymentId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Payment not found for UUID %s", paymentId)))
                .mapDTO()));
        return response;
    }

    public void checkConsentCoversPolicy(ConsentEntity consentEntity, FinancialRiskPolicyEntity policy) {
        var policyFromConsent = consentEntity.getFinancialRiskPolicies()
                .stream()
                .filter(p -> policy.getFinancialRiskId().equals(p.getFinancialRiskId()))
                .findFirst();
        if (policyFromConsent.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad request, consent does not cover this financial risk!");
        }
    }

    public void checkConsentOwnerIsPolicyOwner(ConsentEntity consentEntity, FinancialRiskPolicyEntity policy) {
        if (!consentEntity.getAccountHolderId().equals(policy.getAccountHolderId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match account owner!");
        }
    }
}
