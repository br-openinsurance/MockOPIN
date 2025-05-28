package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
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
class HousingServiceSpec extends CleanupSpecification {

    @Inject
    HousingService housingService

    @Shared
    HousingPolicyEntity testHousingPolicy

    @Shared
    HousingPolicyClaimEntity testHousingPolicyClaim

    @Shared
    HousingPolicyPremiumEntity testHousingPolicyPremium

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    @Shared
    ConsentHousingPolicyEntity testConsentHousingPolicy

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_POLICYINFO_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_CLAIM_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_PREMIUM_READ,
            )
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            testHousingPolicy = housingPolicyRepository.save(TestEntityDataFactory.aHousingPolicy(testAccountHolder.getAccountHolderId()))
            consentHousingPolicyRepository.save(new ConsentHousingPolicyEntity(testConsent, testHousingPolicy))
            testHousingPolicyClaim = housingPolicyClaimRepository.save(TestEntityDataFactory.aHousingPolicyClaim(testHousingPolicy.getHousingPolicyId()))
            testHousingPolicyPremium = housingPolicyPremiumRepository.save(TestEntityDataFactory.aHousingPolicyPremium(testHousingPolicy.getHousingPolicyId()))
            runSetup = false
        }
    }

    def "we can get policies" () {
        when:
        def response = housingService.getPolicies(Pageable.from(0, 1), testConsent.getConsentId().toString())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get a policy info" () {
        when:
        def response = housingService.getPolicyInfo(testHousingPolicy.getHousingPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy's claims" () {
        when:
        def response = housingService.getPolicyClaims(testHousingPolicy.getHousingPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a policy's premium" () {
        when:
        def response = housingService.getPolicyPremium(testHousingPolicy.getHousingPolicyId(), testConsent.getConsentId().toString())

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
