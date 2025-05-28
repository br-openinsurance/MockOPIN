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
class PersonServiceSpec extends CleanupSpecification {

    @Inject
    PersonService personService

    @Shared
    PersonPolicyEntity testPersonPolicy

    @Shared
    PersonPolicyClaimEntity testPersonPolicyClaim

    @Shared
    PersonPolicyPremiumEntity testPersonPolicyPremium

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    @Shared
    ConsentPersonPolicyEntity testConsentPersonPolicy

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_POLICYINFO_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_CLAIM_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_PREMIUM_READ,
            )
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            testPersonPolicy = personPolicyRepository.save(TestEntityDataFactory.aPersonPolicy(testAccountHolder.getAccountHolderId()))
            consentPersonPolicyRepository.save(new ConsentPersonPolicyEntity(testConsent, testPersonPolicy))
            testPersonPolicyClaim = personPolicyClaimRepository.save(TestEntityDataFactory.aPersonPolicyClaim(testPersonPolicy.getPersonPolicyId()))
            testPersonPolicyPremium = personPolicyPremiumRepository.save(TestEntityDataFactory.aPersonPolicyPremium(testPersonPolicy.getPersonPolicyId()))
            runSetup = false
        }
    }

    def "we can get policies" () {
        when:
        def response = personService.getPolicies(Pageable.from(0, 1), testConsent.getConsentId().toString())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get a policy info" () {
        when:
        def response = personService.getPolicyInfo(testPersonPolicy.getPersonPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy's claims" () {
        when:
        def response = personService.getPolicyClaims(testPersonPolicy.getPersonPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a policy's premium" () {
        when:
        def response = personService.getPolicyPremium(testPersonPolicy.getPersonPolicyId(), testConsent.getConsentId().toString())

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
