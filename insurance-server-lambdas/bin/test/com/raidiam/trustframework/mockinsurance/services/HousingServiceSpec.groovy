package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupHousingSpecification
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
class HousingServiceSpec extends CleanupHousingSpecification {

    @Inject
    HousingService housingService

    @Shared
    HousingPolicyEntity testHousingPolicy

    @Shared
    PersonalInfoEntity testHousingPolicyInsured

    @Shared
    BeneficiaryInfoEntity testHousingPolicyBeneficiary

    @Shared
    IntermediaryEntity testHousingPolicyIntermediary

    @Shared
    HousingPolicyInsuredObjectEntity testHousingPolicyInsuredObject

    @Shared
    HousingPolicyInsuredObjectCoverageEntity testHousingPolicyInsuredObjectCoverage

    @Shared
    HousingPolicyBranchInsuredObjectEntity testHousingPolicyBranchInsuredObject

    @Shared
    HousingPolicyBranchInsuredObjectLenderEntity testHousingPolicyBranchInsuredObjectLender

    @Shared
    HousingPolicyBranchInsuredEntity testHousingPolicyBranchInsured

    @Shared
    HousingPolicyClaimEntity testHousingPolicyClaim

    @Shared
    HousingPolicyClaimCoverageEntity testHousingPolicyClaimCoverage

    @Shared
    HousingPolicyPremiumEntity testHousingPolicyPremium

    @Shared
    HousingPolicyPremiumCoverageEntity testHousingPolicyPremiumCoverage

    @Shared
    PaymentEntity testHousingPolicyPremiumPayment

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
            testHousingPolicyInsured = personalInfoRepository.save(TestEntityDataFactory.aPolicyInsured())
            testHousingPolicyBeneficiary = beneficiaryInfoRepository.save(TestEntityDataFactory.aPolicyBeneficiary())
            testHousingPolicyIntermediary = intermediaryRepository.save(TestEntityDataFactory.aPolicyIntermediary())
            testHousingPolicy = housingPolicyRepository.save(TestEntityDataFactory.aHousingPolicy(
                    testAccountHolder.getAccountHolderId(),
                    List.of(testHousingPolicyInsured.getReferenceId()),
                    List.of(testHousingPolicyBeneficiary.getReferenceId()),
                    List.of(testHousingPolicyIntermediary.getReferenceId())))
            consentHousingPolicyRepository.save(new ConsentHousingPolicyEntity(testConsent, testHousingPolicy))
            testHousingPolicyInsuredObject = housingPolicyInsuredObjectRepository.save(TestEntityDataFactory.aHousingPolicyInsuredObject(testHousingPolicy.getHousingPolicyId()))
            testHousingPolicyInsuredObjectCoverage = housingPolicyInsuredObjectCoverageRepository.save(TestEntityDataFactory.aHousingPolicyInsuredObjectCoverage(testHousingPolicyInsuredObject.getHousingPolicyInsuredObjectId()))
            testHousingPolicyBranchInsuredObject = housingPolicyBranchInsuredObjectRepository.save(TestEntityDataFactory.aHousingPolicyBranchInsuredObject(testHousingPolicy.getHousingPolicyId()))
            testHousingPolicyBranchInsuredObjectLender = housingPolicyBranchInsuredObjectLenderRepository.save(TestEntityDataFactory.aHousingPolicyBranchInsuredObjectLender(testHousingPolicyBranchInsuredObject.getHousingPolicyBranchInsuredObjectId()))
            testHousingPolicyBranchInsured = housingPolicyBranchInsuredRepository.save(TestEntityDataFactory.aHousingPolicyBranchInsured(testHousingPolicy.getHousingPolicyId()))
            testHousingPolicyClaim = housingPolicyClaimRepository.save(TestEntityDataFactory.aHousingPolicyClaim(testHousingPolicy.getHousingPolicyId()))
            testHousingPolicyClaimCoverage = housingPolicyClaimCoverageRepository.save(TestEntityDataFactory.aHousingPolicyClaimCoverage(testHousingPolicyClaim.getClaimId()))
            testHousingPolicyPremiumPayment = paymentRepository.save(TestEntityDataFactory.aPolicyPremiumPayment())
            testHousingPolicyPremium = housingPolicyPremiumRepository.save(TestEntityDataFactory.aHousingPolicyPremium(
                    testHousingPolicy.getHousingPolicyId(),
                    List.of(testHousingPolicyPremiumPayment.getPaymentId())))
            testHousingPolicyPremiumCoverage = housingPolicyPremiumCoverageRepository.save(TestEntityDataFactory.aHousingPolicyPremiumCoverage(testHousingPolicyPremium.getPremiumId()))
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
        response.getData().first().getBrand() == "Mock"
        response.getData().first().getCompanies().first().getCnpjNumber() == "12345678901234"
        response.getData().first().getCompanies().first().getPolicies().first().getPolicyId() == testHousingPolicy.getHousingPolicyId().toString()
    }

    def "we can get policies V2" () {
        when:
        def response = housingService.getPoliciesV2(Pageable.from(0, 1), testConsent.getConsentId().toString())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
        response.getData().first().getBrand() == "Mock"
        response.getData().first().getCompanies().first().getCnpjNumber() == "12345678901234"
        response.getData().first().getCompanies().first().getPolicies().first().getPolicyId() == testHousingPolicy.getHousingPolicyId().toString()
    }

    def "we can get a policy info" () {
        when:
        def response = housingService.getPolicyInfo(testHousingPolicy.getHousingPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getPolicyId() == testHousingPolicy.getPolicyId()
        response.getData().getInsureds().first().getIdentification() == testHousingPolicyInsured.getIdentification()
        response.getData().getBeneficiaries().first().getIdentification() == testHousingPolicyBeneficiary.getIdentification()
        response.getData().getIntermediaries().first().getIdentification() == testHousingPolicyIntermediary.getIdentification()
        response.getData().getInsuredObjects().first().getIdentification() == testHousingPolicyInsuredObject.getIdentification()
        response.getData().getInsuredObjects().first().getCoverages().first().getBranch() == testHousingPolicyInsuredObjectCoverage.getBranch()
        response.getData().getBranchInfo().getInsuredObjects().first().getIdentification() == testHousingPolicyBranchInsuredObject.getIdentification()
        response.getData().getBranchInfo().getInsuredObjects().first().getLenders().first().getCnpjNumber() == testHousingPolicyBranchInsuredObjectLender.getCnpjNumber()
        response.getData().getBranchInfo().getInsureds().first().getIdentification() == testHousingPolicyBranchInsured.getIdentification()
    }

    def "we can get a policy info V2" () {
        when:
        def response = housingService.getPolicyInfoV2(testHousingPolicy.getHousingPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getPolicyId() == testHousingPolicy.getPolicyId()
        response.getData().getInsureds().first().getIdentification() == testHousingPolicyInsured.getIdentification()
        response.getData().getBeneficiaries().first().getIdentification() == testHousingPolicyBeneficiary.getIdentification()
        response.getData().getIntermediaries().first().getIdentification() == testHousingPolicyIntermediary.getIdentification()
        response.getData().getInsuredObjects().first().getIdentification() == testHousingPolicyInsuredObject.getIdentification()
        response.getData().getInsuredObjects().first().getCoverages().first().getBranch() == testHousingPolicyInsuredObjectCoverage.getBranch()
        response.getData().getBranchInfo().getInsuredObjects().first().getIdentification() == testHousingPolicyBranchInsuredObject.getIdentification()
        response.getData().getBranchInfo().getInsuredObjects().first().getLenders().first().getCnpjNumber() == testHousingPolicyBranchInsuredObjectLender.getCnpjNumber()
        response.getData().getBranchInfo().getInsureds().first().getIdentification() == testHousingPolicyBranchInsured.getIdentification()
    }

    def "we can get a policy's claims" () {
        when:
        def response = housingService.getPolicyClaims(testHousingPolicy.getHousingPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        response.getData().first().getIdentification() == testHousingPolicyClaim.getIdentification()
        response.getData().first().getCoverages().first().getBranch() == testHousingPolicyClaimCoverage.getBranch()
    }

    def "we can get a policy's claims V2" () {
        when:
        def response = housingService.getPolicyClaimsV2(testHousingPolicy.getHousingPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        response.getData().first().getIdentification() == testHousingPolicyClaim.getIdentification()
        response.getData().first().getCoverages().first().getBranch() == testHousingPolicyClaimCoverage.getBranch()
    }

    def "we can get a policy's premium" () {
        when:
        def response = housingService.getPolicyPremium(testHousingPolicy.getHousingPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getPaymentsQuantity() == testHousingPolicyPremium.getPaymentsQuantity()
        response.getData().getCoverages().first().getBranch() == testHousingPolicyPremiumCoverage.getBranch()
        response.getData().getPayments().first().getTellerId() == testHousingPolicyPremiumPayment.getTellerId()
        response.getData().getPaymentsQuantity() == testHousingPolicyPremium.getPaymentsQuantity()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
