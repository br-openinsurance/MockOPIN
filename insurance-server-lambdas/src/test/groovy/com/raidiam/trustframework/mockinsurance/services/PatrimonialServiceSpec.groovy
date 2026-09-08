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
            def testInsured = personalInfoRepository.save(TestEntityDataFactory.aPolicyInsured())
            def patrimonialPolicy = TestEntityDataFactory.aPatrimonialPolicy(testAccountHolder.getAccountHolderId(), "0111")
            patrimonialPolicy.setInsuredIds([testInsured.getReferenceId()])
            testPatrimonialPolicy = patrimonialPolicyRepository.save(patrimonialPolicy)
            consentPatrimonialPolicyRepository.save(new ConsentPatrimonialPolicyEntity(testConsent, testPatrimonialPolicy))
            testPatrimonialClaim = patrimonialClaimRepository.save(TestEntityDataFactory.aPatrimonialClaim(testPatrimonialPolicy.getPolicyId()))
            def testInsuredObject = patrimonialInsuredObjectRepository.save(TestEntityDataFactory.aPatrimonialInsuredObject(testPatrimonialPolicy.getPolicyId()))
            patrimonialInsuredObjectCoverageRepository.save(TestEntityDataFactory.aPatrimonialInsuredObjectCoverage(testInsuredObject.getPatrimonialInsuredObjectId()))
            def testPayment = paymentRepository.save(TestEntityDataFactory.aPolicyPremiumPayment())
            def testPremium = patrimonialPremiumRepository.save(TestEntityDataFactory.aPatrimonialPremium(testPatrimonialPolicy.getPolicyId(), [testPayment.getPaymentId()]))
            patrimonialPremiumCoverageRepository.save(TestEntityDataFactory.aPatrimonialPremiumCoverage(testPremium.getPatrimonialPremiumId()))
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

    def "we can get policies V2" () {
        when:
        def response = patrimonialService.getPoliciesV2(testConsent.getConsentId().toString(), Pageable.from(0, 1))

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
        response.getData().getDocumentType().toString() == "APOLICE_INDIVIDUAL"
        response.getData().getIssuanceType().toString() == "EMISSAO_PROPRIA"
        response.getData().getIssuanceDate().toString() == "2022-12-31"
        response.getData().getTermStartDate().toString() == "2022-12-31"
        response.getData().getTermEndDate().toString() == "2023-12-31"
        response.getData().getProposalId() == "123456"
        response.getData().getMaxLMG().getAmount() == "2000.00"
        response.getData().getMaxLMG().getCurrency().toString() == "BRL"
        response.getData().getMaxLMG().getUnitType() == null
        response.getData().getInsureds().size() == 1
        response.getData().getInsureds().first().getName() == "Nome Sobrenome"
        response.getData().getInsuredObjects().size() == 1
        response.getData().getInsuredObjects().first().getCoverages().size() == 1
        response.getData().getInsuredObjects().first().getCoverages().first().getBranch() == "0114"
    }

    def "we can get a policy info V2" () {
        when:
        def response = patrimonialService.getPolicyInfoV2(testPatrimonialPolicy.getPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getInsureds().size() == 1
        response.getData().getInsureds().first().getName() == "Nome Sobrenome"
        response.getData().getInsuredObjects().size() == 1
        response.getData().getInsuredObjects().first().getCoverages().size() == 1
    }

    def "we can get a policy's premium" () {
        when:
        def response = patrimonialService.getPremium(testPatrimonialPolicy.getPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getPaymentsQuantity() == 4
        response.getData().getAmount().getAmount() == "2000.00"
        response.getData().getCoverages().size() == 1
        response.getData().getPayments().size() == 1
        response.getData().getPayments().first().getPaymentType().toString() == "BOLETO"
        response.getData().getPayments().first().getAmount().getAmount() == "55595"
        response.getData().getPayments().first().getAmount().getCurrency().toString() == "BRL"
        response.getData().getPayments().first().getAmount().getUnitType() == null
        response.getData().getPayments().first().getAmount().getUnit() == null
    }

    def "we can get a policy's claims" () {
        when:
        def response = patrimonialService.getClaims(testPatrimonialPolicy.getPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a policy's claims V2" () {
        when:
        def response = patrimonialService.getClaimsV2(testPatrimonialPolicy.getPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

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
