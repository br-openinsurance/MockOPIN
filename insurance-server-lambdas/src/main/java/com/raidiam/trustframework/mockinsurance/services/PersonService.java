package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.domain.PersonPolicyClaimEntity;
import com.raidiam.trustframework.mockinsurance.domain.PersonPolicyEntity;
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
public class PersonService extends BaseInsuranceService {

    private static final Logger LOG = LoggerFactory.getLogger(PersonService.class);

    private List<PersonPolicyEntity> getPersonPolicyEntities(Pageable pageable, String consentId) {
        LOG.info("Getting Personal Customers Identification response for consent id {}", consentId);
    
        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
    
        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PERSON_READ);
    
        return personPolicyRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId(), pageable).getContent();
    }

    public ResponseInsurancePerson getPolicies(Pageable pageable, String consentId) {
        var policies = getPersonPolicyEntities(pageable, consentId);
        return new ResponseInsurancePerson()
                .data(List.of(new ResponseInsurancePersonData()
                        .brand(new ResponseInsurancePersonBrand()
                                .name("Mock")
                                .companies(List.of(new ResponseInsurancePersonBrandCompanies()
                                        .companyName("Mock Insurer")
                                        .cnpjNumber("12345678901234")
                                        .policies(policies.stream().map(PersonPolicyEntity::mapPolicyDTO).toList()))
                                ))
                        )
                );
    }

    public ResponseInsurancePersonV2 getPoliciesV2(Pageable pageable, String consentId) {
        var policies = getPersonPolicyEntities(pageable, consentId);
        return new ResponseInsurancePersonV2()
                .data(List.of(new ResponseInsurancePersonV2Data()
                        .brand(new ResponseInsurancePersonV2Brand()
                                .name("Mock")
                                .companies(List.of(new ResponseInsurancePersonV2BrandCompanies()
                                        .companyName("Mock Insurer")
                                        .cnpjNumber("12345678901234")
                                        .policies(policies.stream().map(PersonPolicyEntity::mapPolicyDTO).toList()))
                                ))
                        )
                );
    }

    private PersonPolicyEntity getPolicy(UUID policyId, String consentId, EnumConsentPermission permission, EnumConsentV3Permission permissionV3) {
        LOG.info("Getting person policy for policy id {} and consent id {}", policyId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var policy = personPolicyRepository.findByPersonPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission, permissionV3);
        this.checkConsentCoversPolicy(consentEntity, policy);
        this.checkConsentOwnerIsPolicyOwner(consentEntity, policy);

        return policy;
    }

    public ResponseInsurancePersonPolicyInfo getPolicyInfo(UUID policyId, String consentId) {
        LOG.info("Getting person policy info response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_POLICYINFO_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PERSON_POLICYINFO_READ).mapPolicyInfoDTO();
    }

    public ResponseInsurancePersonPolicyInfoV2 getPolicyInfoV2(UUID policyId, String consentId) {
        LOG.info("Getting person policy info response for consent id {}", consentId);
        return getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_POLICYINFO_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PERSON_POLICYINFO_READ).mapPolicyInfoDTOV2();
    }

    public ResponseInsurancePersonClaims getPolicyClaims(UUID policyId, String consentId, Pageable pageable) {
        LOG.info("Getting person policy claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_CLAIM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PERSON_CLAIM_READ);

        var claims = personPolicyClaimRepository.findByPersonPolicyId(policyId, pageable);
        var resp = new ResponseInsurancePersonClaims()
                .data(claims.getContent().stream().map(PersonPolicyClaimEntity::mapDTO).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

    public ResponseInsurancePersonClaimsV2 getPolicyClaimsV2(UUID policyId, String consentId, Pageable pageable) {
        LOG.info("Getting person policy claims response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_CLAIM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PERSON_CLAIM_READ);

        var claims = personPolicyClaimRepository.findByPersonPolicyId(policyId, pageable);
        var resp = new ResponseInsurancePersonClaimsV2()
                .data(claims.getContent().stream().map(PersonPolicyClaimEntity::mapDTOV2).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(claims, false));
        return resp;
    }

    public ResponseInsurancePersonPremium getPolicyPremium(UUID policyId, String consentId) {
        LOG.info("Getting person policy premium response for consent id {}", consentId);
        getPolicy(policyId, consentId, EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_PREMIUM_READ, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PERSON_PREMIUM_READ);

        var premium = personPolicyPremiumRepository.findByPersonPolicyId(policyId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Policy id " + policyId + " not found"));
        return new ResponseInsurancePersonPremium().data(premium.mapDTO());
    }

    public void checkConsentCoversPolicy(ConsentEntity consentEntity, PersonPolicyEntity policy) {
        var policyFromConsent = consentEntity.getPersonPolicies()
                .stream()
                .filter(p -> policy.getPersonId().equals(p.getPersonId()))
                .findFirst();
        if (policyFromConsent.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad request, consent does not cover this person!");
        }
    }

    public void checkConsentOwnerIsPolicyOwner(ConsentEntity consentEntity, PersonPolicyEntity policy) {
        if (!consentEntity.getAccountHolderId().equals(policy.getAccountHolderId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match account owner!");
        }
    }
}
