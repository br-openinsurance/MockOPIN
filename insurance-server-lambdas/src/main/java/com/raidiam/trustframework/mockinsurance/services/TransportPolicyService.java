package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyClaimEntity;
import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyEntity;
import com.raidiam.trustframework.mockinsurance.domain.ConsentTransportPolicyEntity;
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
public class TransportPolicyService extends BaseInsuranceService {

    @Inject
    ResourcesService resourcesService;

    private static final Logger LOG = LoggerFactory.getLogger(TransportPolicyService.class);

    public BaseInsuranceResponse getPolicies(String consentId, Pageable pageable) {
        LOG.info("Getting transport policies response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_READ);

        var consentPolicies = consentTransportPolicyRepository.findByConsentConsentIdOrderByCreatedAtAsc(consentId, pageable);
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
                                            resourcesService.checkStatusAvailable(consentAccountEntity.getTransportPolicy(), consentEntity);
                                            return consentAccountEntity.getTransportPolicy();
                                        })
                                        .map(TransportPolicyEntity::mapPolicyDto)
                                        .toList())))));
        response.setMeta(InsuranceLambdaUtils.getMeta(consentPolicies, false));
        return response;
    }

    public ResponseInsuranceTransportPolicyInfo getPolicyInfo(String policyId, String consentId) {
        LOG.info("Getting transport policy info response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_POLICYINFO_READ).mapInfoDto();
    }

    public ResponseInsuranceTransportPremium getPolicyPremium(String policyId, String consentId) {
        LOG.info("Getting transport policy premium response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_PREMIUM_READ).mapPremiumDto();
    }

    public ResponseInsuranceTransportClaims getPolicyClaims(String policyId, String consentId, Pageable pageable) {
        LOG.info("Getting transport policy claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_CLAIM_READ);

        var claims = transportPolicyClaimRepository.findByTransportPolicyId(policyId, pageable);
        var resp = new ResponseInsuranceTransportClaims()
                .data(claims.getContent().stream().map(TransportPolicyClaimEntity::mapDto).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

    private TransportPolicyEntity getPolicy(String policyId, String consentId, EnumConsentPermission permission) {
        LOG.info("Getting transport policy for policy id {} and consent id {}", policyId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var policy = transportPolicyRepository.findByTransportPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission);
        this.checkConsentCoversPolicy(consentEntity, policy);
        this.checkConsentOwnerIsPolicyOwner(consentEntity, policy);

        return policy;
    }

    public void checkConsentCoversPolicy(ConsentEntity consentEntity, TransportPolicyEntity policy) {
        var policyFromConsent = consentEntity.getTransportPolicies()
                .stream()
                .filter(p -> policy.getTransportPolicyId().equals(p.getTransportPolicyId()))
                .findFirst();
        if (policyFromConsent.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad request, consent does not cover this transport policy!");
        }
    }

    public void checkConsentOwnerIsPolicyOwner(ConsentEntity consentEntity, TransportPolicyEntity policy) {
        if (!consentEntity.getAccountHolderId().equals(policy.getAccountHolderId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match account owner!");
        }
    }

    public void checkConsentOwnerIsPolicyOwner(Page<ConsentTransportPolicyEntity> consentPolicy, ConsentEntity consentEntity) {
        if(consentPolicy.getContent()
                .stream()
                .map(ConsentTransportPolicyEntity::getTransportPolicy)
                .map(TransportPolicyEntity::getAccountHolderId)
                .anyMatch(accountHolderId -> !accountHolderId.equals(consentEntity.getAccountHolderId()))) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match policy owner!");
        }
    }
}
