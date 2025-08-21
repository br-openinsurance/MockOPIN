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
class TransportPolicyServiceSpec extends CleanupSpecification {

    @Inject
    TransportPolicyService transportPolicyService

    @Shared
    TransportPolicyEntity testTransportPolicy

    @Shared
    TransportPolicyClaimEntity testTransportPolicyClaim

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    @Shared
    ConsentTransportPolicyEntity testConsentTransportPolicy


    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_POLICYINFO_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_CLAIM_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_PREMIUM_READ)
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            testTransportPolicy = transportPolicyRepository.save(TestEntityDataFactory.aTransportPolicy(testAccountHolder.getAccountHolderId()))
            consentTransportPolicyRepository.save(new ConsentTransportPolicyEntity(testConsent, testTransportPolicy))
            testTransportPolicyClaim = transportPolicyClaimRepository.save(TestEntityDataFactory.aTransportPolicyClaim(testTransportPolicy.getTransportPolicyId()))
            runSetup = false
        }
    }

    def "we can get policies" () {
        when:
        def response = transportPolicyService.getPolicies(testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get a policy info" () {
        when:
        def response = transportPolicyService.getPolicyInfo(testTransportPolicy.getTransportPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy's premium" () {
        when:
        def response = transportPolicyService.getPolicyPremium(testTransportPolicy.getTransportPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy's claims" () {
        when:
        def response = transportPolicyService.getPolicyClaims(testTransportPolicy.getTransportPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

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