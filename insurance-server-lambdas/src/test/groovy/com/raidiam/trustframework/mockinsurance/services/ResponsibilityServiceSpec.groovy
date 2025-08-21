package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.*
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentPermission
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentStatus
import io.micronaut.data.model.Pageable
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class ResponsibilityServiceSpec extends CleanupSpecification {

    @Inject
    ResponsibilityService responsibilityService

    @Shared
    ResponsibilityPolicyEntity testResponsibilityPolicy

    @Shared
    ResponsibilityPolicyClaimEntity testResponsibilityPolicyClaim

    @Shared
    ResponsibilityPolicyPremiumEntity testResponsibilityPolicyPremium

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    @Shared
    ConsentResponsibilityPolicyEntity testConsentResponsibilityPolicy

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_POLICYINFO_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_CLAIM_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_PREMIUM_READ,
            )
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            testResponsibilityPolicy = responsibilityPolicyRepository.save(TestEntityDataFactory.aResponsibilityPolicy(testAccountHolder.getAccountHolderId()))
            consentResponsibilityPolicyRepository.save(new ConsentResponsibilityPolicyEntity(testConsent, testResponsibilityPolicy))
            testResponsibilityPolicyClaim = responsibilityPolicyClaimRepository.save(TestEntityDataFactory.aResponsibilityPolicyClaim(testResponsibilityPolicy.getResponsibilityPolicyId()))
            testResponsibilityPolicyPremium = responsibilityPolicyPremiumRepository.save(TestEntityDataFactory.aResponsibilityPolicyPremium(testResponsibilityPolicy.getResponsibilityPolicyId()))
            runSetup = false
        }
    }

    def "we can get policies" () {
        when:
        def response = responsibilityService.getPolicies(Pageable.from(0, 1), testConsent.getConsentId().toString())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get a policy info" () {
        when:
        def response = responsibilityService.getPolicyInfo(testResponsibilityPolicy.getResponsibilityPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy's claims" () {
        when:
        def response = responsibilityService.getPolicyClaims(testResponsibilityPolicy.getResponsibilityPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a policy's premium" () {
        when:
        def response = responsibilityService.getPolicyPremium(testResponsibilityPolicy.getResponsibilityPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
