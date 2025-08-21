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
class PatrimonialServiceSpec extends CleanupSpecification {

    @Inject
    PatrimonialService patrimonialService

    @Shared
    PatrimonialPolicyEntity testPatrimonialPolicy

    @Shared
    PatrimonialClaimEntity testPatrimonialClaim

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    @Shared
    ConsentPatrimonialPolicyEntity testConsentPatrimonialPolicy


    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_POLICYINFO_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_PREMIUM_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_CLAIM_READ)
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            testPatrimonialPolicy = patrimonialPolicyRepository.save(TestEntityDataFactory.aPatrimonialPolicy(testAccountHolder.getAccountHolderId(), "0111"))
            consentPatrimonialPolicyRepository.save(new ConsentPatrimonialPolicyEntity(testConsent, testPatrimonialPolicy))
            testPatrimonialClaim = patrimonialClaimRepository.save(TestEntityDataFactory.aPatrimonialClaim(testPatrimonialPolicy.getPolicyId()))
            runSetup = false
        }
    }

    def "we can get policies" () {
        when:
        def response = patrimonialService.getPolicies(testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get a policy info" () {
        when:
        def response = patrimonialService.getPolicyInfo(testPatrimonialPolicy.getPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy's premium" () {
        when:
        def response = patrimonialService.getPremium(testPatrimonialPolicy.getPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy's claims" () {
        when:
        def response = patrimonialService.getClaims(testPatrimonialPolicy.getPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

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
