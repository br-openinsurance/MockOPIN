package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.AutoPolicyClaimEntity;
import com.raidiam.trustframework.mockinsurance.domain.AutoPolicyEntity;
import com.raidiam.trustframework.mockinsurance.domain.ConsentAutoPolicyEntity;
import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Singleton
@Transactional
public class AutoPolicyService extends BaseInsuranceService {

    @Inject
    ResourcesService resourcesService;

    private static final Logger LOG = LoggerFactory.getLogger(AutoPolicyService.class);

    public BaseInsuranceResponse getPolicies(String consentId, Pageable pageable) {
        LOG.info("Getting auto policies response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_READ);

        var consentPolicies = consentAutoPolicyRepository.findByConsentConsentIdOrderByCreatedAtAsc(consentId, pageable);
        this.checkConsentOwnerIsPolicyOwner(consentPolicies, consentEntity);

        var response = new BaseInsuranceResponse()
                .data(List.of(new BaseBrandAndCompanyData()
                        .brand("Mock")
                        .companies(List.of(new BaseBrandAndCompanyDataCompanies()
                                .companyName("Mock Insurer")
                                .cnpjNumber("12345678901234")
                                .policies(consentPolicies.getContent()
                                        .stream()
                                        .map(consentAccountEntity -> {
                                            resourcesService.checkStatusAvailable(consentAccountEntity.getAutoPolicy(), consentEntity);
                                            return consentAccountEntity.getAutoPolicy();
                                        })
                                        .map(AutoPolicyEntity::mapPolicyDto)
                                        .toList())))));
        response.setMeta(InsuranceLambdaUtils.getMeta(consentPolicies, false));
        return response;
    }

    public ResponseInsuranceAutoPolicyInfo getPolicyInfo(String policyId, String consentId) {
        LOG.info("Getting auto policy info response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_POLICYINFO_READ).mapInfoDto();
    }

    public ResponseInsuranceAutoPremium getPolicyPremium(String policyId, String consentId) {
        LOG.info("Getting auto policy premium response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_PREMIUM_READ).mapPremiumDto();
    }

    public ResponseInsuranceAutoClaims getPolicyClaims(String policyId, String consentId, Pageable pageable) {
        LOG.info("Getting auto policy claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_CLAIM_READ);

        var claims = autoPolicyClaimRepository.findByAutoPolicyId(policyId, pageable);
        var resp = new ResponseInsuranceAutoClaims()
                .data(claims.getContent().stream().map(AutoPolicyClaimEntity::mapDto).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

    private AutoPolicyEntity getPolicy(String policyId, String consentId, EnumConsentPermission permission) {
        LOG.info("Getting auto policy for policy id {} and consent id {}", policyId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var policy = autoPolicyRepository.findByAutoPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission);
        this.checkConsentCoversPolicy(consentEntity, policy);
        this.checkConsentOwnerIsPolicyOwner(consentEntity, policy);

        return policy;
    }

    public void checkConsentCoversPolicy(ConsentEntity consentEntity, AutoPolicyEntity policy) {
        var policyFromConsent = consentEntity.getAutoPolicies()
                .stream()
                .filter(p -> policy.getAutoPolicyId().equals(p.getAutoPolicyId()))
                .findFirst();
        if (policyFromConsent.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad request, consent does not cover this auto policy!");
        }
    }

    public void checkConsentOwnerIsPolicyOwner(ConsentEntity consentEntity, AutoPolicyEntity policy) {
        if (!consentEntity.getAccountHolderId().equals(policy.getAccountHolderId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match account owner!");
        }
    }

    public void checkConsentOwnerIsPolicyOwner(Page<ConsentAutoPolicyEntity> consentPolicy, ConsentEntity consentEntity) {
        if(consentPolicy.getContent()
                .stream()
                .map(ConsentAutoPolicyEntity::getAutoPolicy)
                .map(AutoPolicyEntity::getAccountHolderId)
                .anyMatch(accountHolderId -> !accountHolderId.equals(consentEntity.getAccountHolderId()))) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match policy owner!");
        }
    }
}
