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
class FinancialRiskServiceSpec extends CleanupSpecification {

    @Inject
    FinancialRiskService financialRiskService

    @Shared
    FinancialRiskPolicyEntity testFinancialRiskPolicy

    @Shared
    FinancialRiskPolicyClaimEntity testFinancialRiskPolicyClaim

    @Shared
    FinancialRiskPolicyPremiumEntity testFinancialRiskPolicyPremium

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    @Shared
    ConsentFinancialRiskPolicyEntity testConsentFinancialRiskPolicy

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_POLICYINFO_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_CLAIM_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_PREMIUM_READ,
            )
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            testFinancialRiskPolicy = financialRiskPolicyRepository.save(TestEntityDataFactory.aFinancialRiskPolicy(testAccountHolder.getAccountHolderId()))
            consentFinancialRiskPolicyRepository.save(new ConsentFinancialRiskPolicyEntity(testConsent, testFinancialRiskPolicy))
            testFinancialRiskPolicyClaim = financialRiskPolicyClaimRepository.save(TestEntityDataFactory.aFinancialRiskPolicyClaim(testFinancialRiskPolicy.getFinancialRiskPolicyId()))
            testFinancialRiskPolicyPremium = financialRiskPolicyPremiumRepository.save(TestEntityDataFactory.aFinancialRiskPolicyPremium(testFinancialRiskPolicy.getFinancialRiskPolicyId()))
            runSetup = false
        }
    }

    def "we can get policies" () {
        when:
        def response = financialRiskService.getPolicies(Pageable.from(0, 1), testConsent.getConsentId().toString())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get a policy info" () {
        when:
        def response = financialRiskService.getPolicyInfo(testFinancialRiskPolicy.getFinancialRiskPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a policy's claims" () {
        when:
        def response = financialRiskService.getPolicyClaims(testFinancialRiskPolicy.getFinancialRiskPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a policy's premium" () {
        when:
        def response = financialRiskService.getPolicyPremium(testFinancialRiskPolicy.getFinancialRiskPolicyId(), testConsent.getConsentId().toString())

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
