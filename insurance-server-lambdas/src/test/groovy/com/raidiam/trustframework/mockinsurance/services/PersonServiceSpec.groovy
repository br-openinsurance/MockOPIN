package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupPersonSpecification
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
class PersonServiceSpec extends CleanupPersonSpecification {

    @Inject
    PersonService personService

    @Shared
    PersonPolicyEntity testPersonPolicy

    @Shared
    PersonPolicyClaimEntity testPersonPolicyClaim

    @Shared
    PersonPolicyPremiumEntity testPersonPolicyPremium

    @Shared
    PersonPolicyInsuredObjectEntity testPersonInsuredObject

    @Shared
    PersonalInfoEntity testPersonInsured

    @Shared
    BeneficiaryInfoEntity testPersonBeneficiary

    @Shared
    IntermediaryEntity testPersonIntermediary

    @Shared
    PaymentEntity testPersonPremiumPayment

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

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

            testPersonInsured = personalInfoRepository.save(TestEntityDataFactory.aPolicyInsured())
            testPersonBeneficiary = beneficiaryInfoRepository.save(TestEntityDataFactory.aPolicyBeneficiary())
            testPersonIntermediary = intermediaryRepository.save(TestEntityDataFactory.aPolicyIntermediary())

            testPersonPolicy = personPolicyRepository.save(TestEntityDataFactory.aPersonPolicy(
                    testAccountHolder.getAccountHolderId(),
                    List.of(testPersonInsured.getReferenceId()),
                    List.of(testPersonBeneficiary.getReferenceId()),
                    List.of(testPersonIntermediary.getReferenceId())))
            consentPersonPolicyRepository.save(new ConsentPersonPolicyEntity(testConsent, testPersonPolicy))

            testPersonInsuredObject = personPolicyInsuredObjectRepository.save(TestEntityDataFactory.aPersonPolicyInsuredObject(testPersonPolicy.getPersonPolicyId()))
            personPolicyInsuredObjectCoverageRepository.save(TestEntityDataFactory.aPersonPolicyInsuredObjectCoverage(testPersonInsuredObject.getPersonInsuredObjectId()))

            testPersonPolicyClaim = personPolicyClaimRepository.save(TestEntityDataFactory.aPersonPolicyClaim(testPersonPolicy.getPersonPolicyId()))
            personPolicyClaimCoverageRepository.save(TestEntityDataFactory.aPersonPolicyClaimCoverage(testPersonPolicyClaim.getClaimId()))

            testPersonPremiumPayment = paymentRepository.save(TestEntityDataFactory.aPolicyPremiumPayment())
            testPersonPolicyPremium = personPolicyPremiumRepository.save(TestEntityDataFactory.aPersonPolicyPremium(testPersonPolicy.getPersonPolicyId(), List.of(testPersonPremiumPayment.getPaymentId())))
            personPolicyPremiumCoverageRepository.save(TestEntityDataFactory.aPersonPolicyPremiumCoverage(testPersonPolicyPremium.getPremiumId()))
            runSetup = false
        }
    }

    def "we can get policies" () {
        when:
        def response = personService.getPolicies(Pageable.from(0, 1), testConsent.getConsentId().toString())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first().getBrand().getCompanies().first().getPolicies().first().getProductName() == "Mock Insurer Person Policy"
    }

    def "we can get policies V2" () {
        when:
        def response = personService.getPoliciesV2(Pageable.from(0, 1), testConsent.getConsentId().toString())

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
        response.getData().getDocumentType().toString() == "APOLICE_INDIVIDUAL"
        response.getData().getInsureds().size() == 1
        response.getData().getBeneficiaries().size() == 1
        response.getData().getIntermediaries().size() == 1
        response.getData().getInsuredObjects().size() == 1
        response.getData().getInsuredObjects().first().getCoverages().size() == 1
        response.getData().getPmBaC() != null
    }

    def "we can get a policy info V2" () {
        when:
        def response = personService.getPolicyInfoV2(testPersonPolicy.getPersonPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getInsureds().size() == 1
        response.getData().getBeneficiaries().size() == 1
        response.getData().getIntermediaries().size() == 1
        response.getData().getInsuredObjects().size() == 1
        response.getData().getInsuredObjects().first().getCoverages().size() == 1
    }

    def "we can get a policy's claims" () {
        when:
        def response = personService.getPolicyClaims(testPersonPolicy.getPersonPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        response.getData().size() == 1
        response.getData().first().getCoverages().size() == 1
    }

    def "we can get a policy's claims V2" () {
        when:
        def response = personService.getPolicyClaimsV2(testPersonPolicy.getPersonPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        response.getData().size() == 1
        response.getData().first().getCoverages().size() == 1
    }

    def "we can get a policy's premium" () {
        when:
        def response = personService.getPolicyPremium(testPersonPolicy.getPersonPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getPaymentsQuantity() == 4
        response.getData().getCoverages().size() == 1
        response.getData().getPayments().size() == 1
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
