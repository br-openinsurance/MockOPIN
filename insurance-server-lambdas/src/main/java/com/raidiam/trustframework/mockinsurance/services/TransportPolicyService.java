package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyClaimEntity;
import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyEntity;
import com.raidiam.trustframework.mockinsurance.domain.ConsentTransportPolicyEntity;
import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseBrandAndCompanyDataV2;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseBrandAndCompanyDataV2Companies;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseInsuranceResponseV2;
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentPermission;
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentV3Permission;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceTransportClaimsV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceTransportPolicyInfoV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceTransportPremium;
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
import java.util.UUID;

@Singleton
@Transactional
public class TransportPolicyService extends BaseInsuranceService {

    @Inject
    ResourcesService resourcesService;

    private static final Logger LOG = LoggerFactory.getLogger(TransportPolicyService.class);

    public BaseInsuranceResponseV2 getPoliciesV2(String consentId, Pageable pageable) {
        LOG.info("Getting transport policies response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_TRANSPORT_READ);

        var consentPolicies = consentTransportPolicyRepository.findByConsentConsentIdOrderByCreatedAtAsc(consentId, pageable);
        this.checkConsentOwnerIsPolicyOwner(consentPolicies, consentEntity);

        var response = new BaseInsuranceResponseV2()
                .data(List.of(new BaseBrandAndCompanyDataV2()
                        .brand("Mock")
                        .companies(List.of(new BaseBrandAndCompanyDataV2Companies()
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

    public ResponseInsuranceTransportPolicyInfoV2 getPolicyInfoV2(String policyId, String consentId) {
        LOG.info("Getting transport policy info response for consent id {}", consentId);
        var policy = getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_POLICYINFO_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_TRANSPORT_POLICYINFO_READ);
        var response = policy.mapInfoDtoV2();

        policy.getInsuredIds().forEach(insuredId -> personalInfoRepository.findById(insuredId)
                .ifPresentOrElse(insured -> response.getData().addInsuredsItem(insured.mapDTOV2()),
                        () -> logUnresolved("Personal info", insuredId, policyId)));

        policy.getBeneficiaryIds().forEach(beneficiaryId -> beneficiaryInfoRepository.findById(beneficiaryId)
                .ifPresentOrElse(beneficiary -> response.getData().addBeneficiariesItem(beneficiary.mapDTO()),
                        () -> logUnresolved("Beneficiary", beneficiaryId, policyId)));

        policy.getPrincipalIds().forEach(principalId -> principalInfoRepository.findById(principalId)
                .ifPresentOrElse(principal -> response.getData().addPrincipalsItem(principal.mapDTOV2()),
                        () -> logUnresolved("Principal", principalId, policyId)));

        policy.getIntermediaryIds().forEach(intermediaryId -> intermediaryRepository.findById(intermediaryId)
                .ifPresentOrElse(intermediary -> response.getData().addIntermediariesItem(intermediary.mapDTOV2()),
                        () -> logUnresolved("Intermediary", intermediaryId, policyId)));

        policy.getCoinsurerIds().forEach(coinsurerId -> coinsurerRepository.findById(coinsurerId)
                .ifPresentOrElse(coinsurer -> response.getData().addCoinsurersItem(coinsurer.mapDTO()),
                        () -> logUnresolved("Coinsurer", coinsurerId, policyId)));

        return response;
    }

    public ResponseInsuranceTransportPremium getPolicyPremium(String policyId, String consentId) {
        LOG.info("Getting transport policy premium response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_PREMIUM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_TRANSPORT_PREMIUM_READ);

        var premium = transportPolicyPremiumRepository.findByTransportPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));
        var response = new ResponseInsuranceTransportPremium().data(premium.mapDto());

        premium.getPaymentIds().forEach(paymentId -> paymentRepository.findById(paymentId)
                .ifPresentOrElse(payment -> response.getData().addPaymentsItem(payment.mapDTO()),
                        () -> logUnresolved("Payment", paymentId, policyId)));
        return response;
    }

    public ResponseInsuranceTransportClaimsV2 getPolicyClaimsV2(String policyId, String consentId, Pageable pageable) {
        LOG.info("Getting transport policy claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_CLAIM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_TRANSPORT_CLAIM_READ);

        var claims = transportPolicyClaimRepository.findByTransportPolicyId(policyId, pageable);
        var resp = new ResponseInsuranceTransportClaimsV2()
                .data(claims.getContent().stream().map(TransportPolicyClaimEntity::mapDtoV2).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

    private void logUnresolved(String type, UUID referencedId, String policyId) {
        LOG.warn("{} {} referenced by transport policy {} was not found - omitting it from the response",
                type, referencedId, policyId);
    }

    private TransportPolicyEntity getPolicy(String policyId, String consentId, EnumConsentPermission permission, EnumConsentV3Permission permissionV3) {
        LOG.info("Getting transport policy for policy id {} and consent id {}", policyId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var policy = transportPolicyRepository.findByTransportPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission, permissionV3);
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
