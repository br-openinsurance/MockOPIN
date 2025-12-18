package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupRuralSpecification
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
class RuralServiceSpec extends CleanupRuralSpecification {

    @Inject
    RuralService ruralService

    @Shared
    RuralPolicyEntity testRuralPolicy

    @Shared
    PersonalInfoEntity testRuralPolicyInsured

    @Shared
    BeneficiaryInfoEntity testRuralPolicyBeneficiary

    @Shared
    PrincipalInfoEntity testRuralPolicyPrincipal

    @Shared
    IntermediaryEntity testRuralPolicyIntermediary

    @Shared
    CoinsurerEntity testRuralPolicyCoinsurer

    @Shared
    RuralPolicyClaimEntity testRuralPolicyClaim

    @Shared
    RuralPolicyPremiumEntity testRuralPolicyPremium

    @Shared
    RuralPolicyInsuredObjectEntity testRuralPolicyInsuredObject

    @Shared
    DeductibleEntity testRuralPolicyCoverageDeductible

    @Shared
    PaymentEntity testRuralPolicyPremiumPayment

    @Shared
    POSEntity testRuralPolicyCoveragePos

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_POLICYINFO_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_PREMIUM_READ,
                    EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_CLAIM_READ)
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            testRuralPolicyInsured = personalInfoRepository.save(TestEntityDataFactory.aPolicyInsured())
            testRuralPolicyBeneficiary = beneficiaryInfoRepository.save(TestEntityDataFactory.aPolicyBeneficiary())
            testRuralPolicyPrincipal = principalInfoRepository.save(TestEntityDataFactory.aPolicyPrincipal())
            testRuralPolicyIntermediary = intermediaryRepository.save(TestEntityDataFactory.aPolicyIntermediary())
            testRuralPolicyCoinsurer = coinsurerRepository.save(TestEntityDataFactory.aPolicyCoinsurer())
            testRuralPolicy = ruralPolicyRepository.save(TestEntityDataFactory.aRuralPolicy(
                    testAccountHolder.getAccountHolderId(),
                    List.of(testRuralPolicyInsured.getReferenceId()),
                    List.of(testRuralPolicyBeneficiary.getReferenceId()),
                    List.of(testRuralPolicyPrincipal.getReferenceId()),
                    List.of(testRuralPolicyIntermediary.getReferenceId()),
                    List.of(testRuralPolicyCoinsurer.getCoinsurerId())))
            testRuralPolicyInsuredObject = ruralPolicyInsuredObjectRepository.save(TestEntityDataFactory.aRuralPolicyInsuredObject(testRuralPolicy.getRuralPolicyId()))
            ruralPolicyInsuredObjectCoverageRepository.save(TestEntityDataFactory.aRuralPolicyInsuredObjectCoverage(testRuralPolicyInsuredObject.getRuralPolicyInsuredObjectId()))
            testRuralPolicyCoverageDeductible = deductibleRepository.save(TestEntityDataFactory.aPolicyCoverageDeductible())
            testRuralPolicyCoveragePos = posRepository.save(TestEntityDataFactory.aPolicyCoveragePos())
            ruralPolicyCoverageRepository.save(TestEntityDataFactory.aRuralPolicyCoverage(
                    testRuralPolicy.getRuralPolicyId(),
                    testRuralPolicyCoverageDeductible.getReferenceId(),
                    testRuralPolicyCoveragePos.getPosId()))
            ruralPolicyBranchInsuredObjectRepository.save(TestEntityDataFactory.aRuralPolicyBranchInsuredObject(testRuralPolicy.getRuralPolicyId()))
            testRuralPolicyClaim = ruralPolicyClaimRepository.save(TestEntityDataFactory.aRuralPolicyClaim(testRuralPolicy.getRuralPolicyId()))
            ruralPolicyClaimCoverageRepository.save(TestEntityDataFactory.aRuralPolicyClaimCoverage(testRuralPolicyClaim.getRuralPolicyClaimId()))
            testRuralPolicyPremiumPayment = paymentRepository.save(TestEntityDataFactory.aPolicyPremiumPayment())
            testRuralPolicyPremium = ruralPolicyPremiumRepository.save(TestEntityDataFactory.aRuralPolicyPremium(
                    testRuralPolicy.getRuralPolicyId(),
                    List.of(testRuralPolicyPremiumPayment.getPaymentId())))
            ruralPolicyPremiumCoverageRepository.save(TestEntityDataFactory.aRuralPolicyPremiumCoverage(testRuralPolicyPremium.getRuralPolicyPremiumId()))
            consentRuralPolicyRepository.save(new ConsentRuralPolicyEntity(testConsent, testRuralPolicy))
            runSetup = false
        }
    }

    def "we can get policies" () {
        when:
        def response = ruralService.getPolicies(testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
        response.getData().first().getBrand() == "Mock"
        response.getData().first().getCompanies().first().getCompanyName() == "Mock Insurer"
        response.getData().first().getCompanies().first().getPolicies().first().getProductName() == testRuralPolicy.getProductName()
    }

    def "we can get policies V2" () {
        when:
        def response = ruralService.getPoliciesV2(testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
        response.getData().first().getBrand() == "Mock"
        response.getData().first().getCompanies().first().getCompanyName() == "Mock Insurer"
        response.getData().first().getCompanies().first().getPolicies().first().getProductName() == testRuralPolicy.getProductName()
    }

    def "we can get a policy info" () {
        when:
        def response = ruralService.getPolicyInfo(testRuralPolicy.getRuralPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getPolicyId() == testRuralPolicy.getPolicyId()
        response.getData().getInsureds().first().getName() == testRuralPolicyInsured.getName()
        response.getData().getBeneficiaries().first().getName() == testRuralPolicyBeneficiary.getName()
        response.getData().getIntermediaries().first().getName() == testRuralPolicyIntermediary.getName()
        response.getData().getPrincipals().first().getName() == testRuralPolicyPrincipal.getName()
        response.getData().getCoinsurers().first().getIdentification() == testRuralPolicyCoinsurer.getIdentification()
    }

    def "we can get a policy info V2" () {
        when:
        def response = ruralService.getPolicyInfoV2(testRuralPolicy.getRuralPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getPolicyId() == testRuralPolicy.getPolicyId()
        response.getData().getInsureds().first().getName() == testRuralPolicyInsured.getName()
        response.getData().getBeneficiaries().first().getName() == testRuralPolicyBeneficiary.getName()
        response.getData().getIntermediaries().first().getName() == testRuralPolicyIntermediary.getName()
        response.getData().getPrincipals().first().getName() == testRuralPolicyPrincipal.getName()
        response.getData().getCoinsurers().first().getIdentification() == testRuralPolicyCoinsurer.getIdentification()
    }

    def "we can get a policy's premium" () {
        when:
        def response = ruralService.getPremium(testRuralPolicy.getRuralPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getAmount().getAmount() == testRuralPolicyPremium.getAmount()
        response.getData().getCoverages().first().getBranch() == "0111"
        response.getData().getPayments().first().getPaymentType().toString() == "BOLETO"
    }

    def "we can get a policy's claims" () {
        when:
        def response = ruralService.getClaims(testRuralPolicy.getRuralPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        response.getData().first().getIdentification() == testRuralPolicyClaim.getIdentification()
        response.getData().first().getCoverages().first().getInsuredObjectId() == "string"
        response.getData().first().getAmount().getAmount() == testRuralPolicyClaim.getAmount()
    }

    def "we can get a policy's claims V2" () {
        when:
        def response = ruralService.getClaimsV2(testRuralPolicy.getRuralPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        response.getData().first().getIdentification() == testRuralPolicyClaim.getIdentification()
        response.getData().first().getCoverages().first().getInsuredObjectId() == "string"
        response.getData().first().getAmount().getAmount() == testRuralPolicyClaim.getAmount()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
