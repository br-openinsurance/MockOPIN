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
class AcceptanceAndBranchesAbroadServiceSpec extends CleanupSpecification {

    @Inject
    AcceptanceAndBranchesAbroadService acceptanceAndBranchesAbroadService

    @Shared
    AcceptanceAndBranchesAbroadPolicyEntity testAcceptanceAndBranchesAbroadPolicy

    @Shared
    AcceptanceAndBranchesAbroadClaimEntity testAcceptanceAndBranchesAbroadClaim

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    @Shared
    ConsentAcceptanceAndBranchesAbroadPolicyEntity testConsentAcceptanceAndBranchesAbroadPolicy


    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_POLICYINFO_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_PREMIUM_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_CLAIM_READ)
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            testAcceptanceAndBranchesAbroadPolicy = acceptanceAndBranchesAbroadPolicyRepository.save(TestEntityDataFactory.anAcceptanceAndBranchesAbroadPolicy(testAccountHolder.getAccountHolderId()))
            consentAcceptanceAndBranchesAbroadPolicyRepository.save(new ConsentAcceptanceAndBranchesAbroadPolicyEntity(testConsent, testAcceptanceAndBranchesAbroadPolicy))
            testAcceptanceAndBranchesAbroadClaim = acceptanceAndBranchesAbroadClaimRepository.save(TestEntityDataFactory.anAcceptanceAndBranchesAbroadClaim(testAcceptanceAndBranchesAbroadPolicy.getPolicyId()))
            runSetup = false
        }
    }

    def "we can get policies" () {
        when:
        def response = acceptanceAndBranchesAbroadService.getPolicies(testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get policies V2" () {
        when:
        def response = acceptanceAndBranchesAbroadService.getPoliciesV2(testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get a policy info" () {
        when:
        def response = acceptanceAndBranchesAbroadService.getPolicyInfo(testAcceptanceAndBranchesAbroadPolicy.getPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy info V2" () {
        when:
        def response = acceptanceAndBranchesAbroadService.getPolicyInfoV2(testAcceptanceAndBranchesAbroadPolicy.getPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy's premium" () {
        when:
        def response = acceptanceAndBranchesAbroadService.getPremium(testAcceptanceAndBranchesAbroadPolicy.getPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy's premium V2" () {
        when:
        def response = acceptanceAndBranchesAbroadService.getPremiumV2(testAcceptanceAndBranchesAbroadPolicy.getPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy's claims" () {
        when:
        def response = acceptanceAndBranchesAbroadService.getClaims(testAcceptanceAndBranchesAbroadPolicy.getPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a policy's claims V2" () {
        when:
        def response = acceptanceAndBranchesAbroadService.getClaimsV2(testAcceptanceAndBranchesAbroadPolicy.getPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

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
