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
class AutoPolicyServiceSpec extends CleanupSpecification {

    @Inject
    AutoPolicyService autoPolicyService

    @Shared
    AutoPolicyEntity testAutoPolicy

    @Shared
    AutoPolicyClaimEntity testAutoPolicyClaim

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    @Shared
    ConsentAutoPolicyEntity testConsentAutoPolicy


    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_POLICYINFO_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_CLAIM_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_PREMIUM_READ)
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            testAutoPolicy = autoPolicyRepository.save(TestEntityDataFactory.anAutoPolicy(testAccountHolder.getAccountHolderId()))
            consentAutoPolicyRepository.save(new ConsentAutoPolicyEntity(testConsent, testAutoPolicy))
            testAutoPolicyClaim = autoPolicyClaimRepository.save(TestEntityDataFactory.anAutoPolicyClaim(UUID.randomUUID().toString(), testAutoPolicy.getAutoPolicyId()))
            runSetup = false
        }
    }

    def "we can get policies" () {
        when:
        def response = autoPolicyService.getPolicies(testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get policies V2" () {
        when:
        def response = autoPolicyService.getPoliciesV2(testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get a policy info" () {
        when:
        def response = autoPolicyService.getPolicyInfo(testAutoPolicy.getAutoPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy info V2" () {
        when:
        def response = autoPolicyService.getPolicyInfoV2(testAutoPolicy.getAutoPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy's premium" () {
        when:
        def response = autoPolicyService.getPolicyPremium(testAutoPolicy.getAutoPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy's premium V2" () {
        when:
        def response = autoPolicyService.getPolicyPremiumV2(testAutoPolicy.getAutoPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy's claims" () {
        when:
        def response = autoPolicyService.getPolicyClaims(testAutoPolicy.getAutoPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a policy's claims V2" () {
        when:
        def response = autoPolicyService.getPolicyClaimsV2(testAutoPolicy.getAutoPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

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