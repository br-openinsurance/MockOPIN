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
    FinancialRiskPolicyInsuredObjectEntity testFinancialInsuredObject

    @Shared
    DeductibleEntity testFinancialCoverageDeductible

    @Shared
    PersonalInfoEntity testFinancialRiskPolicyInsured

    @Shared
    BeneficiaryInfoEntity testFinancialRiskPolicyBeneficiary

    @Shared
    PrincipalInfoEntity testFinancialRiskPolicyPrincipal

    @Shared
    IntermediaryEntity testFinancialRiskPolicyIntermediary

    @Shared
    CoinsurerEntity testFinancialRiskPolicyCoinsurer

    @Shared
    testFinancialRiskPolicyPremiumPayment

    @Shared
    POSEntity testFinancialCoveragePos

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
            testFinancialRiskPolicyInsured = personalInfoRepository.save(TestEntityDataFactory.aFinancialRiskPolicyInsured())
            testFinancialRiskPolicyBeneficiary = beneficiaryInfoRepository.save(TestEntityDataFactory.aFinancialRiskPolicyBeneficiary())
            testFinancialRiskPolicyPrincipal = principalInfoRepository.save(TestEntityDataFactory.aFinancialRiskPolicyPrincipal())
            testFinancialRiskPolicyIntermediary = intermediaryRepository.save(TestEntityDataFactory.aFinancialRiskPolicyIntermediary())
            testFinancialRiskPolicyCoinsurer = coinsurerRepository.save(TestEntityDataFactory.aFinancialRiskPolicyCoinsurer())
            testFinancialRiskPolicy = financialRiskPolicyRepository.save(TestEntityDataFactory.aFinancialRiskPolicy(
                    testAccountHolder.getAccountHolderId(),
                    List.of(testFinancialRiskPolicyInsured.getReferenceId()),
                    List.of(testFinancialRiskPolicyBeneficiary.getReferenceId()),
                    List.of(testFinancialRiskPolicyPrincipal.getReferenceId()),
                    List.of(testFinancialRiskPolicyIntermediary.getReferenceId()),
                    List.of(testFinancialRiskPolicyCoinsurer.getCoinsurerId())))
            testFinancialInsuredObject = financialRiskPolicyInsuredObjectRepository.save(TestEntityDataFactory.aFinancialRiskPolicyInsuredObject(testFinancialRiskPolicy.getFinancialRiskPolicyId()))
            financialRiskPolicyInsuredObjectCoverageRepository.save(TestEntityDataFactory.aFinancialRiskPolicyInsuredObjectCoverage(testFinancialInsuredObject.getInsuredObjectId()))
            testFinancialCoverageDeductible = deductibleRepository.save(TestEntityDataFactory.aFinancialRiskPolicyCoverageDeductible())
            testFinancialCoveragePos = posRepository.save(TestEntityDataFactory.aFinancialRiskPolicyCoveragePos())
            financialRiskPolicyCoverageRepository.save(TestEntityDataFactory.aFinancialRiskPolicyCoverage(testFinancialRiskPolicy.getFinancialRiskPolicyId(), testFinancialCoverageDeductible.getReferenceId(), testFinancialCoveragePos.getPosId()))
            consentFinancialRiskPolicyRepository.save(new ConsentFinancialRiskPolicyEntity(testConsent, testFinancialRiskPolicy))
            testFinancialRiskPolicyClaim = financialRiskPolicyClaimRepository.save(TestEntityDataFactory.aFinancialRiskPolicyClaim(testFinancialRiskPolicy.getFinancialRiskPolicyId()))
            financialRiskPolicyClaimCoverageRepository.save(TestEntityDataFactory.aFinancialRiskPolicyClaimCoverage(testFinancialRiskPolicyClaim.getClaimId()))
            testFinancialRiskPolicyPremiumPayment = paymentRepository.save(TestEntityDataFactory.aFinancialRiskPolicyPremiumPayment())
            testFinancialRiskPolicyPremium = financialRiskPolicyPremiumRepository.save(TestEntityDataFactory.aFinancialRiskPolicyPremium(testFinancialRiskPolicy.getFinancialRiskPolicyId(), List.of(testFinancialRiskPolicyPremiumPayment.getPaymentId())))
            financialRiskPolicyPremiumCoverageRepository.save(TestEntityDataFactory.aFinancialRiskPolicyPremiumCoverage(testFinancialRiskPolicyPremium.getPremiumId()))
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
        response.getData().first().getBrand() == "Mock"
        response.getData().first().getCompanies().first().getCompanyName() == "Mock Insurer"
        response.getData().first().getCompanies().first().getPolicies().first().getProductName() == testFinancialRiskPolicy.getProductName()
    }

    def "we can get a policy info" () {
        when:
        def response = financialRiskService.getPolicyInfo(testFinancialRiskPolicy.getFinancialRiskPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getPolicyId() == testFinancialRiskPolicy.getFinancialRiskPolicyId().toString()
        response.getData().getInsureds().first().getName() == "Nome Sobrenome"
        response.getData().getBeneficiaries().first().getName() == "Nome Sobrenome"
        response.getData().getBranchInfo().getIdentification() == testFinancialRiskPolicy.getBranchInfoIdentification()
    }

    def "we can get a policy's claims" () {
        when:
        def response = financialRiskService.getPolicyClaims(testFinancialRiskPolicy.getFinancialRiskPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        response.getData().first().getIdentification() == testFinancialRiskPolicyClaim.getIdentification()
        response.getData().first().getCoverages().first().getInsuredObjectId() == "string"
        response.getData().first().getAmount().getAmount() == testFinancialRiskPolicyClaim.getAmount()
    }

    def "we can get a policy's premium" () {
        when:
        def response = financialRiskService.getPolicyPremium(testFinancialRiskPolicy.getFinancialRiskPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getAmount().getAmount() == testFinancialRiskPolicyPremium.getAmount()
        response.getData().getCoverages().first().getBranch() == "0111"
        response.getData().getPayments().first().getPaymentType().toString() == "BOLETO"
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
