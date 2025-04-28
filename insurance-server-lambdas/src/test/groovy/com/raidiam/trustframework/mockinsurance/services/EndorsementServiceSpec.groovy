package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.TestRequestDataFactory
import com.raidiam.trustframework.mockinsurance.domain.AccountHolderEntity
import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.utils.PermissionGroup
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Stepwise

import java.time.LocalDate

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class EndorsementServiceSpec extends CleanupSpecification {
    @Inject
    EndorsementService endorsementService

    @Shared
    AccountHolderEntity testAccountHolder

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            runSetup = false
        }
    }

    def "We can create an endorsement"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        noExceptionThrown()
        entity.endorsementId != null
        entity.getData() != null
        entity.getData().getProposalId() != null
        entity.getData().getRequestDate() != null
        entity.getData().getEndorsementType().toString() == consent.getEndorsementInformation().getEndorsementType().toString()
    }

    def "We can't create an endorsement without a consent id"() {
        given:
        def endorsement = TestEntityDataFactory.anEndorsement(null)

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: consent id was not informed"
    }

    def "We can't create an endorsement with a consent id that doesn't exist"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def endorsement = TestEntityDataFactory.anEndorsement(consentId)

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.NOT_FOUND
        e.getMessage() == "Consent Id " + consentId + " not found"
    }

    def "We can't create an endorsement with differente client id from the consent"() {
        given:
        def clientId = "other_random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.FORBIDDEN
        e.getMessage() == "NAO_INFORMADO: Requested a consent created with a different oauth client"
    }

    def "We can't create an endorsement with a consent without endorsement information"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: consent does not have endorsement information"
    }

    def "We can't create an endorsement without a request date"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())
        endorsement.setData(new CreateEndorsementData()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
            .customData(new CreateEndorsementDataCustomData())
        )

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: request date was not informed"
    }

    def "We can't create an endorsement with a future request date"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())
        endorsement.setData(new CreateEndorsementData()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
            .requestDate(LocalDate.now().plusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        )

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: request date is invalid"
    }

    def "We can't create an endorsement without an endorsement type"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())
        endorsement.setData(new CreateEndorsementData()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .requestDescription("description")
            .requestDate(LocalDate.now().minusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        )

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: endorsement type was not informed"
    }

    def "We can't create an endorsement with an endorsement type that doesn't match the consent"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())
        endorsement.setData(new CreateEndorsementData()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.EXCLUSAO)
            .requestDescription("description")
            .requestDate(LocalDate.now().minusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        )

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: endorsement type does not match"
    }

    def "We can't create an endorsement without an insured object id"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())
        endorsement.setData(new CreateEndorsementData()
            .policyId("random_policy_id")
            .proposalId("random_proposal_id")
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
            .requestDate(LocalDate.now().minusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        )

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: insured object id was not informed"
    }

    def "We can't create an endorsement with an insured object id that doesn't match the consent"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())
        endorsement.setData(new CreateEndorsementData()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("654321")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
            .requestDate(LocalDate.now().minusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        )

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: insured object id does not match"
    }

    def "We can't create an endorsement without a policy id"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())
        endorsement.setData(new CreateEndorsementData()
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
            .requestDate(LocalDate.now().minusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        )

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: policy id was not informed"
    }

    def "We can't create an endorsement with a policy id that doesn't match the consent"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())
        endorsement.setData(new CreateEndorsementData()
            .policyId("other_random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
            .requestDate(LocalDate.now().minusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        )

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: policy id does not match"
    }

    def "We can't create an endorsement without a proposal id"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())
        endorsement.setData(new CreateEndorsementData()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
            .requestDate(LocalDate.now().minusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        )

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: proposal id was not informed"
    }

    def "We can't create an endorsement with a proposal id that doesn't match the consent"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())
        endorsement.setData(new CreateEndorsementData()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("other_random_proposal_id")
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
            .requestDate(LocalDate.now().minusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        )

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: proposal id does not match"
    }

    def "We can't create an endorsement without a request description"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())
        endorsement.setData(new CreateEndorsementData()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.ALTERACAO)
            .requestDate(LocalDate.now().minusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        )

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: request description was not informed"
    }

    def "We can't create an endorsement with a request description that doesn't match the consent"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())
        endorsement.setData(new CreateEndorsementData()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("other_description")
            .requestDate(LocalDate.now().minusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        )

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: request description does not match"
    }

    def "We can't create an endorsement with a rejected consent"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.REJECTED.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())
        endorsement.setData(new CreateEndorsementData()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("other_description")
            .requestDate(LocalDate.now().minusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        )

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.FORBIDDEN
        e.getMessage() == "NAO_INFORMADO: consent is not authorised"
    }

    def "We can't create an endorsement with a consent that's awaiting authorisation"() {
        given:
        
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        consent.setStatus(EnumConsentStatus.AWAITING_AUTHORISATION.toString())
        consent.setEndorsementInformation(new CreateConsentDataEndorsementInformation()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateConsentDataEndorsementInformation.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
        )
        consent = consentRepository.save(consent)

        def endorsement = TestEntityDataFactory.anEndorsement(consent.getConsentId())
        endorsement.setData(new CreateEndorsementData()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("other_description")
            .requestDate(LocalDate.now().minusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        )

        when:
        def entity = endorsementService.createEndorsement(endorsement)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.FORBIDDEN
        e.getMessage() == "NAO_INFORMADO: consent is not authorised"
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
