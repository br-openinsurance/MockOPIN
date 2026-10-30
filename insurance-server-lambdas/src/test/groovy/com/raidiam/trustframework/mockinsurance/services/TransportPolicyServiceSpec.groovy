package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupTransportSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.AccountHolderEntity
import com.raidiam.trustframework.mockinsurance.domain.BeneficiaryInfoEntity
import com.raidiam.trustframework.mockinsurance.domain.CoinsurerEntity
import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity
import com.raidiam.trustframework.mockinsurance.domain.ConsentTransportPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.DeductibleEntity
import com.raidiam.trustframework.mockinsurance.domain.IntermediaryEntity
import com.raidiam.trustframework.mockinsurance.domain.POSEntity
import com.raidiam.trustframework.mockinsurance.domain.PaymentEntity
import com.raidiam.trustframework.mockinsurance.domain.PersonalInfoEntity
import com.raidiam.trustframework.mockinsurance.domain.PrincipalInfoEntity
import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyClaimEntity
import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyInsuredObjectEntity
import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyPremiumEntity
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentPermission
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentStatus
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class TransportPolicyServiceSpec extends CleanupTransportSpecification {

    @Inject
    TransportPolicyService transportPolicyService

    @Shared
    TransportPolicyEntity testTransportPolicy

    @Shared
    TransportPolicyClaimEntity testTransportPolicyClaim

    @Shared
    TransportPolicyInsuredObjectEntity testTransportPolicyInsuredObject

    @Shared
    TransportPolicyPremiumEntity testTransportPolicyPremium

    @Shared
    PaymentEntity testTransportPolicyPremiumPayment

    @Shared
    PersonalInfoEntity testTransportPolicyInsured

    @Shared
    BeneficiaryInfoEntity testTransportPolicyBeneficiary

    @Shared
    PrincipalInfoEntity testTransportPolicyPrincipal

    @Shared
    IntermediaryEntity testTransportPolicyIntermediary

    @Shared
    CoinsurerEntity testTransportPolicyCoinsurer

    @Shared
    DeductibleEntity testTransportPolicyCoverageDeductible

    @Shared
    POSEntity testTransportPolicyCoveragePos

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    @Shared
    ConsentEntity testForeignConsent


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

            testTransportPolicyInsured = personalInfoRepository.save(TestEntityDataFactory.aTransportPolicyInsured())
            testTransportPolicyBeneficiary = beneficiaryInfoRepository.save(TestEntityDataFactory.aPolicyBeneficiary())
            testTransportPolicyPrincipal = principalInfoRepository.save(TestEntityDataFactory.aPolicyPrincipal())
            testTransportPolicyIntermediary = intermediaryRepository.save(TestEntityDataFactory.aPolicyIntermediary())
            testTransportPolicyCoinsurer = coinsurerRepository.save(TestEntityDataFactory.aPolicyCoinsurer())

            testTransportPolicy = transportPolicyRepository.save(TestEntityDataFactory.aTransportPolicy(
                    testAccountHolder.getAccountHolderId(),
                    "transport-policy-1",
                    List.of(testTransportPolicyInsured.getReferenceId()),
                    List.of(testTransportPolicyBeneficiary.getReferenceId()),
                    List.of(testTransportPolicyPrincipal.getReferenceId()),
                    List.of(testTransportPolicyIntermediary.getReferenceId()),
                    List.of(testTransportPolicyCoinsurer.getCoinsurerId())))
            consentTransportPolicyRepository.save(new ConsentTransportPolicyEntity(testConsent, testTransportPolicy))

            testTransportPolicyInsuredObject = transportPolicyInsuredObjectRepository.save(TestEntityDataFactory.aTransportPolicyInsuredObject(testTransportPolicy.getTransportPolicyId()))
            transportPolicyInsuredObjectCoverageRepository.save(TestEntityDataFactory.aTransportPolicyInsuredObjectCoverage(testTransportPolicyInsuredObject.getInsuredObjectId()))

            testTransportPolicyCoverageDeductible = deductibleRepository.save(TestEntityDataFactory.aPolicyCoverageDeductible())
            testTransportPolicyCoveragePos = posRepository.save(TestEntityDataFactory.aPolicyCoveragePos())
            transportPolicyCoverageRepository.save(TestEntityDataFactory.aTransportPolicyCoverage(
                    testTransportPolicy.getTransportPolicyId(),
                    testTransportPolicyCoverageDeductible.getReferenceId(),
                    testTransportPolicyCoveragePos.getPosId()))
            transportPolicyEndorsementRepository.save(TestEntityDataFactory.aTransportPolicyEndorsement(testTransportPolicy.getTransportPolicyId()))

            testTransportPolicyPremiumPayment = paymentRepository.save(TestEntityDataFactory.aPolicyPremiumPayment())
            testTransportPolicyPremium = transportPolicyPremiumRepository.save(TestEntityDataFactory.aTransportPolicyPremium(
                    testTransportPolicy.getTransportPolicyId(),
                    List.of(testTransportPolicyPremiumPayment.getPaymentId())))
            transportPolicyPremiumCoverageRepository.save(TestEntityDataFactory.aTransportPolicyPremiumCoverage(testTransportPolicyPremium.getPremiumId()))

            testTransportPolicyClaim = transportPolicyClaimRepository.save(TestEntityDataFactory.aTransportPolicyClaim(
                    "transport-policy-claim-1", testTransportPolicy.getTransportPolicyId()))
            transportPolicyClaimCoverageRepository.save(TestEntityDataFactory.aTransportPolicyClaimCoverage(testTransportPolicyClaim.getTransportPolicyClaimId()))
            runSetup = false
        }
    }

    def "we can get policies V2" () {
        when:
        def response = transportPolicyService.getPoliciesV2(testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first().getCompanies().first().getPolicies().first().getProductName() == "Mock Insurer Transport Policy Plan"
    }

    def "we can get a policy info V2" () {
        when:
        def response = transportPolicyService.getPolicyInfoV2(testTransportPolicy.getTransportPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getInsureds().size() == 1
        response.getData().getInsureds().first().getAddress().getAddress().getType().toString() == "AVENIDA"
        response.getData().getInsureds().first().getAddress().getAddress().getName() == "Naburo Ykesaki"
        response.getData().getInsureds().first().getAddress().getAddress().getNumber() == "1270"
        response.getData().getBeneficiaries().size() == 1
        response.getData().getPrincipals().size() == 1
        response.getData().getIntermediaries().size() == 1
        response.getData().getCoinsurers().size() == 1
        response.getData().getInsuredObjects().size() == 1
        response.getData().getInsuredObjects().first().getCoverages().size() == 1
        response.getData().getCoverages().size() == 1
        response.getData().getBranchInfo().getEndorsements().size() == 1
    }

    def "we can get a policy's premium" () {
        when:
        def response = transportPolicyService.getPolicyPremium(testTransportPolicy.getTransportPolicyId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getPaymentsQuantity() == 3
        response.getData().getAmount().getAmount() == "100.00"
        response.getData().getCoverages().size() == 1
        response.getData().getPayments().size() == 1
    }

    def "we can get a policy's claims V2" () {
        when:
        def response = transportPolicyService.getPolicyClaimsV2(testTransportPolicy.getTransportPolicyId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        response.getData().size() == 1
        response.getData().first().getCoverages().size() == 1
    }

    def "a consent that does not cover the policy is rejected" () {
        given: "an authorised consent with the right permissions, but no link to the policy"
        def unlinkedConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_READ,
                EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_POLICYINFO_READ)
        unlinkedConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        unlinkedConsent = consentRepository.save(unlinkedConsent)

        when:
        transportPolicyService.getPolicyInfoV2(testTransportPolicy.getTransportPolicyId(), unlinkedConsent.getConsentId().toString())

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.BAD_REQUEST
        e.message == "Bad request, consent does not cover this transport policy!"
    }

    def "a consent owned by another account holder is rejected" () {
        given: "a consent belonging to a different account holder - this is the mismatch under test"
        def otherAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
        testForeignConsent = TestEntityDataFactory.aConsent(otherAccountHolder.getAccountHolderId(),
                EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_READ,
                EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_POLICYINFO_READ)
        testForeignConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        testForeignConsent = consentRepository.save(testForeignConsent)

        and: "linked to our policy, so the earlier 'does not cover' guard passes and the owner check is reached"
        consentTransportPolicyRepository.save(new ConsentTransportPolicyEntity(testForeignConsent, testTransportPolicy))

        when:
        transportPolicyService.getPolicyInfoV2(testTransportPolicy.getTransportPolicyId(), testForeignConsent.getConsentId().toString())

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.FORBIDDEN
        e.message == "Forbidden, consent owner does not match account owner!"
    }

    def "listing policies through a consent owned by another account holder is rejected" () {
        when: "the same mismatch, reached through the policy list rather than a single policy"
        transportPolicyService.getPoliciesV2(testForeignConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.FORBIDDEN
        e.message == "Forbidden, consent owner does not match policy owner!"
    }

    def "an unresolvable payment is skipped instead of failing the request" () {
        given: "the referenced payment row is deleted, leaving a dangling id in payment_ids"
        paymentRepository.delete(testTransportPolicyPremiumPayment)

        when:
        def response = transportPolicyService.getPolicyPremium(testTransportPolicy.getTransportPolicyId(), testConsent.getConsentId().toString())

        then: "the request still succeeds, with the payment omitted"
        response.getData() != null
        response.getData().getPaymentsQuantity() == 3
        response.getData().getPayments().isEmpty()
    }

    def "unresolvable linked entities are skipped instead of failing the request" () {
        given: "the referenced rows are deleted, leaving dangling ids in the link tables"
        personalInfoRepository.delete(testTransportPolicyInsured)
        beneficiaryInfoRepository.delete(testTransportPolicyBeneficiary)
        principalInfoRepository.delete(testTransportPolicyPrincipal)
        intermediaryRepository.delete(testTransportPolicyIntermediary)
        coinsurerRepository.delete(testTransportPolicyCoinsurer)

        when:
        def responseV2 = transportPolicyService.getPolicyInfoV2(testTransportPolicy.getTransportPolicyId(), testConsent.getConsentId().toString())

        then: "the request still succeeds and the unresolved entries are simply absent"
        responseV2.getData() != null
        !responseV2.getData().getInsureds()
        !responseV2.getData().getBeneficiaries()
        !responseV2.getData().getPrincipals()
        !responseV2.getData().getIntermediaries()
        !responseV2.getData().getCoinsurers()

        and: "the rest of the payload is unaffected"
        responseV2.getData().getInsuredObjects().size() == 1
        responseV2.getData().getBranchInfo().getEndorsements().size() == 1
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}