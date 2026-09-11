package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.AccountHolderEntity
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails
import com.raidiam.trustframework.mockinsurance.models.generated.CreateConsentDataWithdrawalCaptalizationInformation
import com.raidiam.trustframework.mockinsurance.models.generated.CreateConsentDataWithdrawalLifePensionInformation
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentPermission
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentStatus
import com.raidiam.trustframework.mockinsurance.models.generated.WithdrawalInfoPensionWithdrawal
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Unroll

import java.time.LocalDate

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class WithdrawalServiceSpec extends CleanupSpecification {

    @Inject
    WithdrawalService withdrawalService

    @Shared
    AccountHolderEntity testAccountHolder

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            runSetup = false
        }
    }

    def "We can create a pension withdrawal lead"() {
        given:
        def clientId = "random_client_id"
        def lifePensionContract = TestEntityDataFactory.aLifePensionContract(testAccountHolder.getAccountHolderId())
        lifePensionContract = lifePensionContractRepository.save(lifePensionContract)
        def certificateId = lifePensionContract.getLifePensionContractId().toString()
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPensionLead(clientId, consent.getConsentId())
        withdrawal.getData().getV1().getGeneralInfo().setCertificateId(certificateId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_LEAD_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId(certificateId)
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        def result = withdrawalService.createPensionWithdrawalLead(withdrawal)

        then:
        noExceptionThrown()
        result.withdrawalLeadId != null
        result.consentId == consent.getConsentId()
    }

    def "We can create a pension withdrawal"() {
        given:
        def clientId = "random_client_id"
        def lifePensionContract = TestEntityDataFactory.aLifePensionContract(testAccountHolder.getAccountHolderId())
        lifePensionContract = lifePensionContractRepository.save(lifePensionContract)
        def certificateId = lifePensionContract.getLifePensionContractId().toString()
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPension(clientId, consent.getConsentId())
        withdrawal.getData().getV1().getGeneralInfo().setCertificateId(certificateId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId(certificateId)
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        def result = withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        noExceptionThrown()
        result.withdrawalId != null
        result.consentId == consent.getConsentId()
    }

    def "We can create a capitalization title withdrawal"() {
        given:
        def clientId = "random_client_id"
        def capitalizationTitlePlan = TestEntityDataFactory.aCapitalizationTitlePlan(testAccountHolder.getAccountHolderId())
        capitalizationTitlePlan = capitalizationTitlePlanRepository.save(capitalizationTitlePlan)
        def planId = capitalizationTitlePlan.getCapitalizationTitlePlanId().toString()
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalCapitalizationTitle(clientId, consent.getConsentId())
        withdrawal.getData().getV1().getProductInformation().setPlanId(planId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.CAPITALIZATION_TITLE_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalCaptalizationInformation(new CreateConsentDataWithdrawalCaptalizationInformation()
                .capitalizationTitleName("title_name")
                .planId(planId)
                .titleId("title_123")
                .seriesId("series_123")
                .termEndDate(LocalDate.of(2025, 12, 31)))
        consentRepository.save(consent)

        when:
        def result = withdrawalService.createCapitalizationTitleWithdrawal(withdrawal)

        then:
        noExceptionThrown()
        result.withdrawalId != null
        result.consentId == consent.getConsentId()
    }

    def "We can't create a pension withdrawal lead with mismatched certificateId"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPensionLead(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_LEAD_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("other_certificate")
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawalLead(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: certificateId does not match"
    }

    def "We can't create a pension withdrawal lead with mismatched productName"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPensionLead(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_LEAD_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("certificate_123")
                .productName("other_product")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawalLead(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: productName does not match"
    }

    def "We can't create a pension withdrawal lead with mismatched withdrawalType"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPensionLead(clientId, consent.getConsentId())
        withdrawal.getData().getV1().setWithdrawalInfo(new WithdrawalInfoPensionWithdrawal()
                .withdrawalType(WithdrawalInfoPensionWithdrawal.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(WithdrawalInfoPensionWithdrawal.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_LEAD_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("certificate_123")
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._2_PARCIAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawalLead(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: withdrawalType does not match"
    }

    def "We can't create a pension withdrawal lead with mismatched withdrawalReason"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPensionLead(clientId, consent.getConsentId())
        withdrawal.getData().getV1().setWithdrawalInfo(new WithdrawalInfoPensionWithdrawal()
                .withdrawalType(WithdrawalInfoPensionWithdrawal.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(WithdrawalInfoPensionWithdrawal.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_LEAD_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("certificate_123")
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._9_OUTROS))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawalLead(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: withdrawalReason does not match"
    }

    def "We can't create a pension withdrawal without a consentId"() {
        given:
        def withdrawal = TestEntityDataFactory.aWithdrawalPension("random_client_id", null)

        when:
        withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
    }

    def "We can't create a pension withdrawal with a rejected consent"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPension(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.REJECTED.toString())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("certificate_123")
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNAUTHORIZED
    }

    def "We can't create a pension withdrawal with a wrong clientId"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPension("other_client_id", consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("certificate_123")
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.FORBIDDEN
    }

    def "We can't create a pension withdrawal lead without the required permission"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPensionLead(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("certificate_123")
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawalLead(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.FORBIDDEN
    }

    def "We can't create a pension withdrawal without the required permission"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPension(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("certificate_123")
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.FORBIDDEN
    }

    def "We can't create a pension withdrawal without withdrawalLifePensionInformation on consent"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPension(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_CREATE.name())
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: consent does not have withdrawal life pension information"
    }

    def "We can't create a pension withdrawal with mismatched certificateId"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPension(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("other_certificate")
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: certificateId does not match"

        when: "The consent should be consumed"
        withdrawal = TestEntityDataFactory.aWithdrawalPension(clientId, consent.getConsentId())
        withdrawal.getData().getV1().getGeneralInfo().setCertificateId("other_certificate")
        withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        def e2 = thrown(HttpStatusException)
        e2.status == HttpStatus.UNAUTHORIZED
        e2.getMessage() == "NAO_INFORMADO: consent is not authorised"
    }

    def "We can't create a pension withdrawal with mismatched productName"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPension(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("certificate_123")
                .productName("other_product")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: productName does not match"
    }

    def "We can't create a pension withdrawal with mismatched withdrawalType"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPension(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("certificate_123")
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._2_PARCIAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: withdrawalType does not match"
    }

    def "We can't create a pension withdrawal with mismatched withdrawalReason"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPension(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("certificate_123")
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._9_OUTROS))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: withdrawalReason does not match"
    }

    def "We can't create a pension withdrawal with non-existent certificateId and consent becomes CONSUMED"() {
        given:
        def clientId = "random_client_id"
        def nonExistentCertificateId = UUID.randomUUID().toString()
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPension(clientId, consent.getConsentId())
        withdrawal.getData().getV1().getGeneralInfo().setCertificateId(nonExistentCertificateId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId(nonExistentCertificateId)
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: certificateId does not exist in life pension contracts"

        when: "We check the consent status"
        def updatedConsent = consentRepository.findByConsentId(consent.getConsentId())

        then:
        updatedConsent.isPresent()
        updatedConsent.get().getStatus() == EnumConsentStatus.CONSUMED.toString()
    }

    def "We can't create a capitalization title withdrawal without the required permission"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalCapitalizationTitle(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setWithdrawalCaptalizationInformation(new CreateConsentDataWithdrawalCaptalizationInformation()
                .capitalizationTitleName("title_name")
                .planId("plan_123")
                .titleId("title_123")
                .seriesId("series_123")
                .termEndDate(LocalDate.of(2025, 12, 31)))
        consentRepository.save(consent)

        when:
        withdrawalService.createCapitalizationTitleWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.FORBIDDEN
    }

    def "We can't create a capitalization title withdrawal without withdrawalCaptalizationInformation on consent"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalCapitalizationTitle(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.CAPITALIZATION_TITLE_WITHDRAWAL_CREATE.name())
        consentRepository.save(consent)

        when:
        withdrawalService.createCapitalizationTitleWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: consent does not have withdrawal capitalization information"
    }

    def "We can't create a capitalization title withdrawal with mismatched capitalizationTitleName"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalCapitalizationTitle(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.CAPITALIZATION_TITLE_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalCaptalizationInformation(new CreateConsentDataWithdrawalCaptalizationInformation()
                .capitalizationTitleName("other_title")
                .planId("plan_123")
                .titleId("title_123")
                .seriesId("series_123")
                .termEndDate(LocalDate.of(2025, 12, 31)))
        consentRepository.save(consent)

        when:
        withdrawalService.createCapitalizationTitleWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: capitalizationTitleName does not match"
    }

    def "We can't create a capitalization title withdrawal with mismatched planId"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalCapitalizationTitle(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.CAPITALIZATION_TITLE_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalCaptalizationInformation(new CreateConsentDataWithdrawalCaptalizationInformation()
                .capitalizationTitleName("title_name")
                .planId("other_plan")
                .titleId("title_123")
                .seriesId("series_123")
                .termEndDate(LocalDate.of(2025, 12, 31)))
        consentRepository.save(consent)

        when:
        withdrawalService.createCapitalizationTitleWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: planId does not match"
    }

    def "We can't create a capitalization title withdrawal with mismatched titleId"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalCapitalizationTitle(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.CAPITALIZATION_TITLE_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalCaptalizationInformation(new CreateConsentDataWithdrawalCaptalizationInformation()
                .capitalizationTitleName("title_name")
                .planId("plan_123")
                .titleId("other_title")
                .seriesId("series_123")
                .termEndDate(LocalDate.of(2025, 12, 31)))
        consentRepository.save(consent)

        when:
        withdrawalService.createCapitalizationTitleWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: titleId does not match"
    }

    def "We can't create a capitalization title withdrawal with mismatched seriesId"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalCapitalizationTitle(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.CAPITALIZATION_TITLE_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalCaptalizationInformation(new CreateConsentDataWithdrawalCaptalizationInformation()
                .capitalizationTitleName("title_name")
                .planId("plan_123")
                .titleId("title_123")
                .seriesId("other_series")
                .termEndDate(LocalDate.of(2025, 12, 31)))
        consentRepository.save(consent)

        when:
        withdrawalService.createCapitalizationTitleWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: seriesId does not match"
    }

    def "We can't create a capitalization title withdrawal with mismatched termEndDate"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalCapitalizationTitle(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.CAPITALIZATION_TITLE_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalCaptalizationInformation(new CreateConsentDataWithdrawalCaptalizationInformation()
                .capitalizationTitleName("title_name")
                .planId("plan_123")
                .titleId("title_123")
                .seriesId("series_123")
                .termEndDate(LocalDate.of(2030, 1, 1)))
        consentRepository.save(consent)

        when:
        withdrawalService.createCapitalizationTitleWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: termEndDate does not match"
    }

    def "We can't create a capitalization title withdrawal with invalid planId and consent becomes CONSUMED"() {
        given:
        def clientId = "random_client_id"
        def nonExistentPlanId = UUID.randomUUID().toString()
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalCapitalizationTitle(clientId, consent.getConsentId())
        withdrawal.getData().getV1().getProductInformation().setPlanId(nonExistentPlanId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.CAPITALIZATION_TITLE_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalCaptalizationInformation(new CreateConsentDataWithdrawalCaptalizationInformation()
                .capitalizationTitleName("title_name")
                .planId(nonExistentPlanId)
                .titleId("title_123")
                .seriesId("series_123")
                .termEndDate(LocalDate.of(2025, 12, 31)))
        consentRepository.save(consent)

        when:
        withdrawalService.createCapitalizationTitleWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: planId does not exist in capitalization title plans"

        when: "We check the consent status"
        def updatedConsent = consentRepository.findByConsentId(consent.getConsentId())

        then:
        updatedConsent.isPresent()
        updatedConsent.get().getStatus() == EnumConsentStatus.CONSUMED.toString()
    }

    def "We can't create a capitalization title withdrawal with mismatched withdrawalReason and consent becomes CONSUMED"() {
        given:
        def clientId = "random_client_id"
        def capitalizationTitlePlan = TestEntityDataFactory.aCapitalizationTitlePlan(testAccountHolder.getAccountHolderId())
        capitalizationTitlePlan = capitalizationTitlePlanRepository.save(capitalizationTitlePlan)
        def planId = capitalizationTitlePlan.getCapitalizationTitlePlanId().toString()
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalCapitalizationTitle(clientId, consent.getConsentId())
        withdrawal.getData().getV1().getProductInformation().setPlanId(planId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.CAPITALIZATION_TITLE_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalCaptalizationInformation(new CreateConsentDataWithdrawalCaptalizationInformation()
                .capitalizationTitleName("title_name")
                .planId(planId)
                .titleId("title_123")
                .seriesId("series_123")
                .termEndDate(LocalDate.of(2025, 12, 31))
                .withdrawalReason(CreateConsentDataWithdrawalCaptalizationInformation.WithdrawalReasonEnum.PERDA_DE_INTERESSE)
                .withdrawalTotalAmount(new AmountDetails().amount("1000.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO)))
        consentRepository.save(consent)

        when:
        withdrawalService.createCapitalizationTitleWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: withdrawalReason does not match"

        when: "We check the consent status"
        def updatedConsent = consentRepository.findByConsentId(consent.getConsentId())

        then:
        updatedConsent.isPresent()
        updatedConsent.get().getStatus() == EnumConsentStatus.CONSUMED.toString()
    }

    def "We can create a pension withdrawal lead with V2 data"() {
        given:
        def clientId = "random_client_id"
        def lifePensionContract = TestEntityDataFactory.aLifePensionContract(testAccountHolder.getAccountHolderId())
        lifePensionContract = lifePensionContractRepository.save(lifePensionContract)
        def certificateId = lifePensionContract.getLifePensionContractId().toString()
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPensionLeadV2(clientId, consent.getConsentId())
        withdrawal.getData().getV2().getGeneralInfo().setCertificateId(certificateId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_LEAD_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId(certificateId)
                .productName("product_name"))
        consentRepository.save(consent)

        when:
        def result = withdrawalService.createPensionWithdrawalLead(withdrawal)

        then:
        noExceptionThrown()
        result.withdrawalLeadId != null
        result.consentId == consent.getConsentId()
    }

    def "We can create a pension withdrawal with V2 data"() {
        given:
        def clientId = "random_client_id"
        def lifePensionContract = TestEntityDataFactory.aLifePensionContract(testAccountHolder.getAccountHolderId())
        lifePensionContract = lifePensionContractRepository.save(lifePensionContract)
        def certificateId = lifePensionContract.getLifePensionContractId().toString()
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPensionV2(clientId, consent.getConsentId())
        withdrawal.getData().getV2().getGeneralInfo().setCertificateId(certificateId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId(certificateId)
                .productName("product_name"))
        consentRepository.save(consent)

        when:
        def result = withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        noExceptionThrown()
        result.withdrawalId != null
        result.consentId == consent.getConsentId()
    }

    def "We can't create a V2 pension withdrawal with mismatched certificateId"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPensionV2(clientId, consent.getConsentId())
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("other_certificate")
                .productName("product_name"))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: certificateId does not match"
    }

    def "We can create a pension withdrawal lead with V2 data when consent stores the v1/v2 enum form"() {
        given:
        def clientId = "random_client_id"
        def lifePensionContract = TestEntityDataFactory.aLifePensionContract(testAccountHolder.getAccountHolderId())
        lifePensionContract = lifePensionContractRepository.save(lifePensionContract)
        def certificateId = lifePensionContract.getLifePensionContractId().toString()
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPensionLeadV2(clientId, consent.getConsentId())
        withdrawal.getData().getV2().getGeneralInfo().setCertificateId(certificateId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_LEAD_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId(certificateId)
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        def result = withdrawalService.createPensionWithdrawalLead(withdrawal)

        then:
        noExceptionThrown()
        result.withdrawalLeadId != null
        result.consentId == consent.getConsentId()
    }

    def "We can create a pension withdrawal with V2 data when consent stores the v1/v2 enum form"() {
        given:
        def clientId = "random_client_id"
        def lifePensionContract = TestEntityDataFactory.aLifePensionContract(testAccountHolder.getAccountHolderId())
        lifePensionContract = lifePensionContractRepository.save(lifePensionContract)
        def certificateId = lifePensionContract.getLifePensionContractId().toString()
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPensionV2(clientId, consent.getConsentId())
        withdrawal.getData().getV2().getGeneralInfo().setCertificateId(certificateId)
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId(certificateId)
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        def result = withdrawalService.createPensionWithdrawal(withdrawal)

        then:
        noExceptionThrown()
        result.withdrawalId != null
        result.consentId == consent.getConsentId()
    }

    def "We can't create a V2 pension withdrawal lead with mismatched withdrawalType across enum versions"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPensionLeadV2(clientId, consent.getConsentId())
        // V2 factory defaults to TOTAL; consent stores _2_PARCIAL → genuine mismatch.
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_LEAD_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("certificate_123")
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._2_PARCIAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._1_EMERGENCIAS_DE_SAUDE))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawalLead(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: withdrawalType does not match"

        when: "We check the consent status"
        def updatedConsent = consentRepository.findByConsentId(consent.getConsentId())

        then:
        updatedConsent.isPresent()
        updatedConsent.get().getStatus() == EnumConsentStatus.CONSUMED.toString()
    }

    def "We can't create a V2 pension withdrawal lead with mismatched withdrawalReason across enum versions"() {
        given:
        def clientId = "random_client_id"
        def consent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        def withdrawal = TestEntityDataFactory.aWithdrawalPensionLeadV2(clientId, consent.getConsentId())
        // V2 factory defaults reason to EMERGENCIAS_DE_SAUDE; consent stores _9_OUTROS → mismatch.
        consent.setStatus(EnumConsentStatus.AUTHORISED.toString())
        consent.setPermissions(new ArrayList<>(consent.getPermissions()))
        consent.getPermissions().add(EnumConsentPermission.PENSION_WITHDRAWAL_LEAD_CREATE.name())
        consent.setWithdrawalLifePensionInformation(new CreateConsentDataWithdrawalLifePensionInformation()
                .certificateId("certificate_123")
                .productName("product_name")
                .withdrawalType(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalTypeEnum._1_TOTAL)
                .withdrawalReason(CreateConsentDataWithdrawalLifePensionInformation.WithdrawalReasonEnum._9_OUTROS))
        consentRepository.save(consent)

        when:
        withdrawalService.createPensionWithdrawalLead(withdrawal)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "NAO_INFORMADO: withdrawalReason does not match"

        when: "We check the consent status"
        def updatedConsent = consentRepository.findByConsentId(consent.getConsentId())

        then:
        updatedConsent.isPresent()
        updatedConsent.get().getStatus() == EnumConsentStatus.CONSUMED.toString()
    }

    @Unroll
    def "stripVersionedEnumPrefix(#input) returns #expected"() {
        expect:
        WithdrawalService.stripVersionedEnumPrefix(input) == expected

        where:
        input        || expected
        null         || null
        ""           || ""
        "TOTAL"      || "TOTAL"
        "PARCIAL"    || "PARCIAL"
        "1_TOTAL"    || "TOTAL"
        "2_PARCIAL"  || "PARCIAL"
        "9_OUTROS"   || "OUTROS"
        "10_FOO"     || "FOO"
        "_TOTAL"     || "_TOTAL"
        "ABC_DEF"    || "ABC_DEF"
        "1_2_FOO"    || "2_FOO"
    }

    def "enable cleanup"() {
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
