package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.AccountHolderEntity
import com.raidiam.trustframework.mockinsurance.models.generated.ClaimNotificationData
import com.raidiam.trustframework.mockinsurance.models.generated.ClaimNotificationInformation
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentStatus
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Stepwise

import java.time.LocalDate

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class ClaimNotificationPersonServiceSpec extends CleanupSpecification {

    @Inject
    ClaimNotificationPersonService claimNotificationPersonService

    @Shared
    AccountHolderEntity testAccountHolder

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            runSetup = false
        }
    }

    def "We can create a claim notification"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def claim = TestEntityDataFactory.aClaimNotificationPerson(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setClaimNotificationInformation(new ClaimNotificationInformation()
            .documentType(ClaimNotificationInformation.DocumentTypeEnum.fromValue(claim.getData().getDocumentType().toString()))
            .policyId(claim.getData().getPolicyId())
            .groupCertificateId(claim.getData().getGroupCertificateId())
            .insuredObjectId(claim.getData().getInsuredObjectId())
            .occurrenceDate(claim.getData().getOccurrenceDate())
        )
        consent = consentRepository.save(consent)

        when:
        def newClaim = claimNotificationPersonService.createClaimNotification(claim)

        then:
        noExceptionThrown()
        newClaim.claimId != null
        newClaim.consentId == consent.getConsentId()
    }

    def "We can't create a claim notification with wrong documentType" () {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def claim = TestEntityDataFactory.aClaimNotificationPerson(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setClaimNotificationInformation(new ClaimNotificationInformation()
                .documentType(ClaimNotificationInformation.DocumentTypeEnum.fromValue(claim.getData().getDocumentType().toString()))
                .policyId(claim.getData().getPolicyId())
                .groupCertificateId(claim.getData().getGroupCertificateId())
                .insuredObjectId(claim.getData().getInsuredObjectId())
                .occurrenceDate(claim.getData().getOccurrenceDate())
        )
        claim.data.setDocumentType(ClaimNotificationData.DocumentTypeEnum.CERTIFICADO_AUTOMOVEL)
        consent = consentRepository.save(consent)

        when:
        def newClaim = claimNotificationPersonService.createClaimNotification(claim)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def "We can't create a claim notification with null policyId if documentType is APOLICE_INDIVIDUAL" () {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def claim = TestEntityDataFactory.aClaimNotificationPerson(clientId, consent.getConsentId())
        claim.data.setDocumentType(ClaimNotificationData.DocumentTypeEnum.APOLICE_INDIVIDUAL)
        claim.data.setPolicyId(null)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setClaimNotificationInformation(new ClaimNotificationInformation()
                .documentType(ClaimNotificationInformation.DocumentTypeEnum.fromValue(claim.getData().getDocumentType().toString()))
                .policyId(null)
                .groupCertificateId(claim.getData().getGroupCertificateId())
                .insuredObjectId(claim.getData().getInsuredObjectId())
                .occurrenceDate(claim.getData().getOccurrenceDate())
        )
        consent = consentRepository.save(consent)

        when:
        def newClaim = claimNotificationPersonService.createClaimNotification(claim)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def "We can't create a claim notification with invalid policyId"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def claim = TestEntityDataFactory.aClaimNotificationPerson(clientId, consent.getConsentId())
        claim.data.setDocumentType(ClaimNotificationData.DocumentTypeEnum.APOLICE_INDIVIDUAL)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setClaimNotificationInformation(new ClaimNotificationInformation()
                .documentType(ClaimNotificationInformation.DocumentTypeEnum.fromValue(claim.getData().getDocumentType().toString()))
                .policyId("other_policy_id")
                .groupCertificateId(claim.getData().getGroupCertificateId())
                .insuredObjectId(claim.getData().getInsuredObjectId())
                .occurrenceDate(claim.getData().getOccurrenceDate())
        )
        consent = consentRepository.save(consent)

        when:
        def newClaim = claimNotificationPersonService.createClaimNotification(claim)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def "We can't create a claim notification with null groupCertificateId if documentType is CERTIFICADO" () {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def claim = TestEntityDataFactory.aClaimNotificationPerson(clientId, consent.getConsentId())
        claim.data.setDocumentType(ClaimNotificationData.DocumentTypeEnum.CERTIFICADO)
        claim.data.setGroupCertificateId(null)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setClaimNotificationInformation(new ClaimNotificationInformation()
                .documentType(ClaimNotificationInformation.DocumentTypeEnum.fromValue(claim.getData().getDocumentType().toString()))
                .policyId(claim.getData().getPolicyId())
                .groupCertificateId(null)
                .insuredObjectId(claim.getData().getInsuredObjectId())
                .occurrenceDate(claim.getData().getOccurrenceDate())
        )
        consent = consentRepository.save(consent)

        when:
        def newClaim = claimNotificationPersonService.createClaimNotification(claim)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def "We can't create a claim notification with invalid groupCertificateId"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def claim = TestEntityDataFactory.aClaimNotificationPerson(clientId, consent.getConsentId())
        claim.data.setDocumentType(ClaimNotificationData.DocumentTypeEnum.CERTIFICADO)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setClaimNotificationInformation(new ClaimNotificationInformation()
                .documentType(ClaimNotificationInformation.DocumentTypeEnum.fromValue(claim.getData().getDocumentType().toString()))
                .policyId(claim.getData().getPolicyId())
                .groupCertificateId("other_group_certificate_id")
                .insuredObjectId(claim.getData().getInsuredObjectId())
                .occurrenceDate(claim.getData().getOccurrenceDate())
        )
        consent = consentRepository.save(consent)

        when:
        def newClaim = claimNotificationPersonService.createClaimNotification(claim)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def "We can't create a claim notification with invalid insuredObjectId"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def claim = TestEntityDataFactory.aClaimNotificationPerson(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setClaimNotificationInformation(new ClaimNotificationInformation()
                .documentType(ClaimNotificationInformation.DocumentTypeEnum.fromValue(claim.getData().getDocumentType().toString()))
                .policyId(claim.getData().getPolicyId())
                .groupCertificateId(claim.getData().getGroupCertificateId())
                .insuredObjectId(List.of("other_insured_object_id"))
                .occurrenceDate(claim.getData().getOccurrenceDate())
        )
        consent = consentRepository.save(consent)

        when:
        def newClaim = claimNotificationPersonService.createClaimNotification(claim)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def "We can't create a claim notification with invalid occurrenceDate"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def claim = TestEntityDataFactory.aClaimNotificationPerson(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setClaimNotificationInformation(new ClaimNotificationInformation()
                .documentType(ClaimNotificationInformation.DocumentTypeEnum.fromValue(claim.getData().getDocumentType().toString()))
                .policyId(claim.getData().getPolicyId())
                .groupCertificateId(claim.getData().getGroupCertificateId())
                .insuredObjectId(claim.getData().getInsuredObjectId())
                .occurrenceDate(LocalDate.of(2000, 1, 1))
        )
        consent = consentRepository.save(consent)

        when:
        def newClaim = claimNotificationPersonService.createClaimNotification(claim)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def "We can't create a claim notification without a consentId"() {
        given:
        def clientId = "random_client_id"
        def claim = TestEntityDataFactory.aClaimNotificationPerson(clientId, null)

        when:
        claimNotificationPersonService.createClaimNotification(claim)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def "We can't create a claim notification with a rejected consent"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def claim = TestEntityDataFactory.aClaimNotificationPerson(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.REJECTED.toString())
        consent.setClaimNotificationInformation(new ClaimNotificationInformation()
                .documentType(ClaimNotificationInformation.DocumentTypeEnum.fromValue(claim.getData().getDocumentType().toString()))
                .policyId(claim.getData().getPolicyId())
                .groupCertificateId(claim.getData().getGroupCertificateId())
                .insuredObjectId(claim.getData().getInsuredObjectId())
                .occurrenceDate(claim.getData().getOccurrenceDate())
        )
        consent = consentRepository.save(consent)

        when:
        claimNotificationPersonService.createClaimNotification(claim)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.FORBIDDEN
    }

    def "We can't create a claim notification with a consent that's awaiting authorisation"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def claim = TestEntityDataFactory.aClaimNotificationPerson(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AWAITING_AUTHORISATION.toString())
        consent.setClaimNotificationInformation(new ClaimNotificationInformation()
                .documentType(ClaimNotificationInformation.DocumentTypeEnum.fromValue(claim.getData().getDocumentType().toString()))
                .policyId(claim.getData().getPolicyId())
                .groupCertificateId(claim.getData().getGroupCertificateId())
                .insuredObjectId(claim.getData().getInsuredObjectId())
                .occurrenceDate(claim.getData().getOccurrenceDate())
        )
        consent = consentRepository.save(consent)

        when:
        claimNotificationPersonService.createClaimNotification(claim)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.FORBIDDEN
    }

    def "We can't create a claim notification with a wrong clientId" () {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def claim = TestEntityDataFactory.aClaimNotificationPerson("other_client_id", consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setClaimNotificationInformation(new ClaimNotificationInformation()
                .documentType(ClaimNotificationInformation.DocumentTypeEnum.fromValue(claim.getData().getDocumentType().toString()))
                .policyId(claim.getData().getPolicyId())
                .groupCertificateId(claim.getData().getGroupCertificateId())
                .insuredObjectId(claim.getData().getInsuredObjectId())
                .occurrenceDate(claim.getData().getOccurrenceDate())
        )
        consent = consentRepository.save(consent)

        when:
        claimNotificationPersonService.createClaimNotification(claim)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.FORBIDDEN
    }

    def "We can't create a claim notification with a consent that does not have claimNotificationInformation" () {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def claim = TestEntityDataFactory.aClaimNotificationPerson(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent = consentRepository.save(consent)

        when:
        claimNotificationPersonService.createClaimNotification(claim)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def "enable cleanup"() {
        when:
        runCleanup = true

        then:
        runCleanup
    }

}
