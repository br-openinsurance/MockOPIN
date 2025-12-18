package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.TestRequestDataFactory
import com.raidiam.trustframework.mockinsurance.domain.AccountHolderEntity
import com.raidiam.trustframework.mockinsurance.domain.AutoPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.CapitalizationTitlePlanEntity
import com.raidiam.trustframework.mockinsurance.domain.FinancialAssistanceContractEntity
import com.raidiam.trustframework.mockinsurance.domain.FinancialRiskPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.HousingPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.LifePensionContractEntity
import com.raidiam.trustframework.mockinsurance.domain.AcceptanceAndBranchesAbroadPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.PatrimonialPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.PensionPlanContractEntity
import com.raidiam.trustframework.mockinsurance.domain.RuralPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.PersonPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.ResponsibilityPolicyEntity
import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyEntity
import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.utils.PermissionGroup
import com.raidiam.trustframework.mockinsurance.utils.PermissionV3Group
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Stepwise

import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class ConsentServiceSpec extends CleanupSpecification {
    @Inject
    ConsentService consentService

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    CapitalizationTitlePlanEntity testCapitalizationTitlePlan

    @Shared
    FinancialRiskPolicyEntity testFinancialRiskPolicy

    @Shared
    HousingPolicyEntity testHousingPolicy

    @Shared
    ResponsibilityPolicyEntity testResponsibilityPolicy

    @Shared
    PersonPolicyEntity testPersonPolicy

    @Shared
    LifePensionContractEntity testLifePensionContract

    @Shared
    PensionPlanContractEntity testPensionPlanContract

    @Shared
    FinancialAssistanceContractEntity testFinancialAssistanceContract

    @Shared
    AcceptanceAndBranchesAbroadPolicyEntity testAcceptanceAndBranchesAbroadPolicy

    @Shared
    PatrimonialPolicyEntity testPatrimonialPolicy

    @Shared
    RuralPolicyEntity testRuralPolicy

    @Shared
    AutoPolicyEntity testAutoPolicy

    @Shared
    TransportPolicyEntity testTransportPolicy

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testCapitalizationTitlePlan = capitalizationTitlePlanRepository.save(TestEntityDataFactory.aCapitalizationTitlePlan(testAccountHolder.getAccountHolderId()))
            testFinancialRiskPolicy = financialRiskPolicyRepository.save(TestEntityDataFactory.aFinancialRiskPolicy(testAccountHolder.getAccountHolderId()))
            testHousingPolicy = housingPolicyRepository.save(TestEntityDataFactory.aHousingPolicy(testAccountHolder.getAccountHolderId()))
            testResponsibilityPolicy = responsibilityPolicyRepository.save(TestEntityDataFactory.aResponsibilityPolicy(testAccountHolder.getAccountHolderId()))
            testPersonPolicy = personPolicyRepository.save(TestEntityDataFactory.aPersonPolicy(testAccountHolder.getAccountHolderId()))
            testLifePensionContract = lifePensionContractRepository.save(TestEntityDataFactory.aLifePensionContract(testAccountHolder.getAccountHolderId()))
            testPensionPlanContract = pensionPlanContractRepository.save(TestEntityDataFactory.aPensionPlanContract("pension-plan-1", testAccountHolder.getAccountHolderId()))
            testFinancialAssistanceContract = financialAssistanceContractRepository.save(TestEntityDataFactory.aFinancialAssistanceContract(testAccountHolder.getAccountHolderId()))
            testAcceptanceAndBranchesAbroadPolicy = acceptanceAndBranchesAbroadPolicyRepository.save(TestEntityDataFactory.anAcceptanceAndBranchesAbroadPolicy(testAccountHolder.getAccountHolderId()))
            testPatrimonialPolicy = patrimonialPolicyRepository.save(TestEntityDataFactory.aPatrimonialPolicy(testAccountHolder.getAccountHolderId(), "0111"))
            testRuralPolicy = ruralPolicyRepository.save(TestEntityDataFactory.aRuralPolicy(testAccountHolder.getAccountHolderId()))
            testAutoPolicy = autoPolicyRepository.save(TestEntityDataFactory.anAutoPolicy(testAccountHolder.getAccountHolderId()))
            testTransportPolicy = transportPolicyRepository.save(TestEntityDataFactory.aTransportPolicy(testAccountHolder.getAccountHolderId()))
            runSetup = false
        }
    }

    def "We can create a consent"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentRequest(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                PermissionGroup.PERSONAL_REGISTRATION.getPermissions().toList()
        )

        when:
        def entity = consentService.createConsent(req, clientId)

        then:
        noExceptionThrown()
        entity.consentId != null
        entity.status == EnumConsentStatus.AWAITING_AUTHORISATION.name()
    }

    def "We can create a consent V3"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentV3Request(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                PermissionV3Group.PERSONAL_REGISTRATION.getPermissions().toList()
        )

        when:
        def entity = consentService.createConsentV3(req, clientId)

        then:
        noExceptionThrown()
        entity.consentId != null
        entity.status == EnumConsentStatus.AWAITING_AUTHORISATION.name()
    }

    def "We can't create a consent with invalid permissions"() {
        given:
        def clientId = "random_client_id"
        def permissions = PermissionGroup.PERSONAL_REGISTRATION.getPermissions().toList()
        permissions.add(null)
        def req = TestRequestDataFactory.createConsentRequest(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                permissions
        )

        when:
        consentService.createConsent(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.BAD_REQUEST
        e.getMessage() == "BAD_PERMISSION: Invalid permission"
    }

    def "We can't create a consent V3 with invalid permissions"() {
        given:
        def clientId = "random_client_id"
        def permissions = PermissionV3Group.PERSONAL_REGISTRATION.getPermissions().toList()
        permissions.add(null)
        def req = TestRequestDataFactory.createConsentV3Request(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                permissions
        )

        when:
        consentService.createConsentV3(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.BAD_REQUEST
        e.getMessage() == "BAD_PERMISSION: Invalid permission"
    }

    def "We can't create a consent with permissions from phase 2 and 3"() {
        given:
        def clientId = "random_client_id"
        def permissions = new ArrayList<>(PermissionGroup.PERSONAL_REGISTRATION.getPermissions())
        permissions.addAll(PermissionGroup.ENDORSEMENT_REQUEST.getPermissions())
        def req = TestRequestDataFactory.createConsentRequest(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                permissions.toList()
        )

        when:
        consentService.createConsent(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: Cannot request permissions from phase 2 and 3 at the same time"
    }

    def "We can't create a consent V3 with permissions from phase 2 and 3"() {
        given:
        def clientId = "random_client_id"
        def permissions = new ArrayList<>(PermissionV3Group.PERSONAL_REGISTRATION.getPermissions())
        permissions.addAll(PermissionV3Group.ENDORSEMENT_REQUEST.getPermissions())
        def req = TestRequestDataFactory.createConsentV3Request(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                permissions.toList()
        )

        when:
        consentService.createConsentV3(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: Cannot request permissions from phase 2 and 3 at the same time"
    }

    def "We can't create a consent with permissions from phase 2 without RESOURCES_READ"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentRequest(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                List.of(EnumConsentPermission.CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ)
        )

        when:
        consentService.createConsent(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.BAD_REQUEST
        e.getMessage() == "NAO_INFORMADO: The permission RESOURCES_READ is required for phase 2"
    }

    def "We can't create a consent V3 with permissions from phase 2 without RESOURCES_READ"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentV3Request(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                List.of(EnumConsentV3Permission.CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ)
        )

        when:
        consentService.createConsentV3(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.BAD_REQUEST
        e.getMessage() == "NAO_INFORMADO: The permission RESOURCES_READ is required for phase 2"
    }

    def "We can't create a consent with only the permission RESOURCES_READ"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentRequest(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                List.of(EnumConsentPermission.RESOURCES_READ)
        )

        when:
        consentService.createConsent(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.BAD_REQUEST
        e.getMessage() == "NAO_INFORMADO: The permission RESOURCES_READ cannot be requested alone"
    }

    def "We can't create a consent with only the permission RESOURCES_READ"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentV3Request(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                List.of(EnumConsentV3Permission.RESOURCES_READ)
        )

        when:
        consentService.createConsentV3(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.BAD_REQUEST
        e.getMessage() == "NAO_INFORMADO: The permission RESOURCES_READ cannot be requested alone"
    }

    def "We can't create a consent with some permissions from phase 3"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentRequest(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                PermissionGroup.QUOTE_PATRIMONIAL_BUSINESS.getPermissions().toList()
        )

        when:
        consentService.createConsent(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: Permission not allowed"
    }

    def "We can't create a consent V3 with some permissions from phase 3"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentV3Request(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                PermissionV3Group.QUOTE_PATRIMONIAL_BUSINESS.getPermissions().toList()
        )

        when:
        consentService.createConsentV3(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: Permission not allowed"
    }

    def "We can't create a consent with more than one permission group from phase 3"() {
        given:
        def clientId = "random_client_id"
        def permissions = new ArrayList(PermissionGroup.ENDORSEMENT_REQUEST.getPermissions())
        permissions.addAll(PermissionGroup.CLAIM_NOTIFICATION_REQUEST_DAMAGE.getPermissions())
        def req = TestRequestDataFactory.createConsentRequest(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                permissions
        )

        when:
        consentService.createConsent(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: The permissions of different phase 3 categories were requested"
    }

    def "We can't create a consent V3 with more than one permission group from phase 3"() {
        given:
        def clientId = "random_client_id"
        def permissions = new ArrayList(PermissionV3Group.ENDORSEMENT_REQUEST.getPermissions())
        permissions.addAll(PermissionV3Group.CLAIM_NOTIFICATION_REQUEST_DAMAGE.getPermissions())
        def req = TestRequestDataFactory.createConsentV3Request(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                permissions
        )

        when:
        consentService.createConsentV3(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: The permissions of different phase 3 categories were requested"
    }

    def "We can't create a consent for a phase 3 permission group without requesting all permissions from the same group"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentRequest(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                List.of(EnumConsentPermission.CONTRACT_PENSION_PLAN_LEAD_CREATE)
        )

        when:
        consentService.createConsent(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.BAD_REQUEST
        e.getMessage() == "NAO_INFORMADO: All the permission from the group must be requested"
    }

    def "We can't create a consent V3 for a phase 3 permission group without requesting all permissions from the same group"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentV3Request(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                List.of(EnumConsentV3Permission.CONTRACT_PENSION_PLAN_LEAD_CREATE)
        )

        when:
        consentService.createConsentV3(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.BAD_REQUEST
        e.getMessage() == "NAO_INFORMADO: All the permission from the group must be requested"
    }

    def "We can't create a consent without expirationDateTime"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentRequest(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                null,
                PermissionGroup.PERSONAL_REGISTRATION.getPermissions().toList()
        )

        when:
        consentService.createConsent(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: expirationDateTime is required"
    }

    def "We can't create a consent V3 without expirationDateTime"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentV3Request(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                null,
                PermissionV3Group.PERSONAL_REGISTRATION.getPermissions().toList()
        )

        when:
        consentService.createConsentV3(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage() == "NAO_INFORMADO: expirationDateTime is required"
    }

    def "We can't create a consent with expirationDateTime in the past"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentRequest(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().minusDays(1),
                PermissionGroup.PERSONAL_REGISTRATION.getPermissions().toList()
        )

        when:
        consentService.createConsent(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.BAD_REQUEST
        e.getMessage() == "NAO_INFORMADO: The expiration time cannot be in the past"
    }

    def "We can't create a consent V3 with expirationDateTime in the past"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentV3Request(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().minusDays(1),
                PermissionV3Group.PERSONAL_REGISTRATION.getPermissions().toList()
        )

        when:
        consentService.createConsentV3(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.BAD_REQUEST
        e.getMessage() == "NAO_INFORMADO: The expiration time cannot be in the past"
    }

    def "We can't create a consent with expirationDateTime more than one year in the future"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentRequest(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusYears(1).plusDays(1),
                PermissionGroup.PERSONAL_REGISTRATION.getPermissions().toList()
        )

        when:
        consentService.createConsent(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.BAD_REQUEST
        e.getMessage() == "NAO_INFORMADO: The expiration time cannot be greater than one year"
    }

    def "We can't create a consent V3 with expirationDateTime more than one year in the future"() {
        given:
        def clientId = "random_client_id"
        def req = TestRequestDataFactory.createConsentV3Request(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusYears(1).plusDays(1),
                PermissionV3Group.PERSONAL_REGISTRATION.getPermissions().toList()
        )

        when:
        consentService.createConsentV3(req, clientId)

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.BAD_REQUEST
        e.getMessage() == "NAO_INFORMADO: The expiration time cannot be greater than one year"
    }

    def "We can update a consent"() {
        given:
        def entity = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId())
        entity = consentRepository.save(entity)
        def req = new UpdateConsent()
                .data(new UpdateConsentData()
                        .status(EnumConsentStatus.AUTHORISED)
                        .linkedCapitalizationTilePlanIds(List.of(testCapitalizationTitlePlan.getCapitalizationTitlePlanId().toString()))
                        .linkedFinancialRiskPolicyIds(List.of(testFinancialRiskPolicy.getFinancialRiskPolicyId().toString()))
                        .linkedHousingPolicyIds(List.of(testHousingPolicy.getHousingPolicyId().toString()))
                        .linkedLifePensionContractIds(List.of(testLifePensionContract.getLifePensionContractId().toString()))
                        .linkedPensionPlanContractIds(List.of(testPensionPlanContract.getPensionPlanContractId()))
                        .linkedFinancialAssistanceContractIds(List.of(testFinancialAssistanceContract.getFinancialAssistanceContractId()))
                        .linkedAcceptanceAndBranchesAbroadPolicyIds(List.of(testAcceptanceAndBranchesAbroadPolicy.getPolicyId().toString()))
                        .linkedPatrimonialPolicyIds(List.of(testPatrimonialPolicy.getPolicyId().toString()))
                        .linkedRuralPolicyIds(List.of(testRuralPolicy.getRuralPolicyId().toString()))
                        .linkedAutoPolicyIds(List.of(testAutoPolicy.getAutoPolicyId()))
                        .linkedTransportPolicyIds(List.of(testTransportPolicy.getTransportPolicyId()))
                )

        when:
        entity = consentService.updateConsent(entity.getConsentId(), req)

        then:
        noExceptionThrown()
        entity.getData().getConsentId() != null
        entity.getData().getStatus() == EnumConsentStatus.AUTHORISED
    }

    def "We can update a consent V3"() {
        given:
        def entity = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId())
        entity = consentRepository.save(entity)
        def req = new UpdateConsent()
                .data(new UpdateConsentData()
                        .status(EnumConsentStatus.AUTHORISED)
                        .linkedCapitalizationTilePlanIds(List.of(testCapitalizationTitlePlan.getCapitalizationTitlePlanId().toString()))
                        .linkedFinancialRiskPolicyIds(List.of(testFinancialRiskPolicy.getFinancialRiskPolicyId().toString()))
                        .linkedHousingPolicyIds(List.of(testHousingPolicy.getHousingPolicyId().toString()))
                        .linkedLifePensionContractIds(List.of(testLifePensionContract.getLifePensionContractId().toString()))
                        .linkedPensionPlanContractIds(List.of(testPensionPlanContract.getPensionPlanContractId()))
                        .linkedFinancialAssistanceContractIds(List.of(testFinancialAssistanceContract.getFinancialAssistanceContractId()))
                        .linkedAcceptanceAndBranchesAbroadPolicyIds(List.of(testAcceptanceAndBranchesAbroadPolicy.getPolicyId().toString()))
                        .linkedPatrimonialPolicyIds(List.of(testPatrimonialPolicy.getPolicyId().toString()))
                        .linkedRuralPolicyIds(List.of(testRuralPolicy.getRuralPolicyId().toString()))
                        .linkedAutoPolicyIds(List.of(testAutoPolicy.getAutoPolicyId()))
                        .linkedTransportPolicyIds(List.of(testTransportPolicy.getTransportPolicyId()))
                )

        when:
        entity = consentService.updateConsentV3(entity.getConsentId(), req)

        then:
        noExceptionThrown()
        entity.getData().getConsentId() != null
        entity.getData().getStatus() == EnumConsentStatus.AUTHORISED
    }

    def "We can update a consent with a rejected status"() {
        given:
        def entity = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId())
        entity = consentRepository.save(entity)
        def req = new UpdateConsent().data(new UpdateConsentData().status(EnumConsentStatus.REJECTED))

        when:
        entity = consentService.updateConsent(entity.getConsentId(), req)

        then:
        noExceptionThrown()
        entity.getData().getConsentId() != null
        entity.getData().getStatus() == EnumConsentStatus.REJECTED
    }

    def "We can update a consent V3 with a rejected status"() {
        given:
        def entity = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId())
        entity = consentRepository.save(entity)
        def req = new UpdateConsent().data(new UpdateConsentData().status(EnumConsentStatus.REJECTED))

        when:
        entity = consentService.updateConsentV3(entity.getConsentId(), req)

        then:
        noExceptionThrown()
        entity.getData().getConsentId() != null
        entity.getData().getStatus() == EnumConsentStatus.REJECTED
    }

    def "We can fetch a consent"() {
        given:
        def clientId = "random_client_id"
        def entity = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        entity = consentRepository.save(entity)

        when:
        entity = consentService.getConsent(entity.getConsentId(), clientId)

        then:
        noExceptionThrown()
        entity.getData().getConsentId() != null
    }

    def "We can fetch a consent V3"() {
        given:
        def clientId = "random_client_id"
        def entity = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        entity = consentRepository.save(entity)

        when:
        entity = consentService.getConsentV3(entity.getConsentId(), clientId)

        then:
        noExceptionThrown()
        entity.getData().getConsentId() != null
    }

    def "A consent awaiting authorization for more than one hour moves to REJECTED"() {
        given:
        def clientId = "random_client_id"
        def entity = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        entity.status = EnumConsentStatus.AWAITING_AUTHORISATION
        entity.creationDateTime = Date.from(Instant.now() - Duration.ofDays(1))
        entity = consentRepository.save(entity)

        when:
        entity = consentService.getConsent(entity.getConsentId(), clientId)

        then:
        noExceptionThrown()
        entity.getData().getConsentId() != null
        entity.getData().getStatus() == EnumConsentStatus.REJECTED
    }

    def "A consent V3 awaiting authorization for more than one hour moves to REJECTED"() {
        given:
        def clientId = "random_client_id"
        def entity = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        entity.status = EnumConsentStatus.AWAITING_AUTHORISATION
        entity.creationDateTime = Date.from(Instant.now() - Duration.ofDays(1))
        entity = consentRepository.save(entity)

        when:
        entity = consentService.getConsentV3(entity.getConsentId(), clientId)

        then:
        noExceptionThrown()
        entity.getData().getConsentId() != null
        entity.getData().getStatus() == EnumConsentStatus.REJECTED
    }

    def "An authorized consent moves to REJECTED if it's expired"() {
        given:
        def clientId = "random_client_id"
        def entity = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        entity.status = EnumConsentStatus.AUTHORISED
        entity.expirationDateTime = Date.from(Instant.now() - Duration.ofDays(1))
        entity = consentRepository.save(entity)

        when:
        entity = consentService.getConsent(entity.getConsentId(), clientId)

        then:
        noExceptionThrown()
        entity.getData().getConsentId() != null
        entity.getData().getStatus() == EnumConsentStatus.REJECTED
    }

    def "An authorized consent V3 moves to REJECTED if it's expired"() {
        given:
        def clientId = "random_client_id"
        def entity = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        entity.status = EnumConsentStatus.AUTHORISED
        entity.expirationDateTime = Date.from(Instant.now() - Duration.ofDays(1))
        entity = consentRepository.save(entity)

        when:
        entity = consentService.getConsentV3(entity.getConsentId(), clientId)

        then:
        noExceptionThrown()
        entity.getData().getConsentId() != null
        entity.getData().getStatus() == EnumConsentStatus.REJECTED
    }

    def "We can't fetch a consent with an invalid client"() {
        given:
        def clientId = "random_client_id"
        def entity = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        entity = consentRepository.save(entity)

        when:
        consentService.getConsent(entity.getConsentId(), "another_random_client_id")

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.FORBIDDEN
        e.getMessage() == "NAO_INFORMADO: Requested a consent created with a different oauth client"
    }

    def "We can't fetch a consent V3 with an invalid client"() {
        given:
        def clientId = "random_client_id"
        def entity = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        entity = consentRepository.save(entity)

        when:
        consentService.getConsentV3(entity.getConsentId(), "another_random_client_id")

        then:
        def e = thrown(HttpStatusException)
        e.status == HttpStatus.FORBIDDEN
        e.getMessage() == "NAO_INFORMADO: Requested a consent created with a different oauth client"
    }

    def "We can delete a consent already rejected"() {
        given:
        def clientId = "random_client_id"
        def entity = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        entity.status = EnumConsentStatus.REJECTED.name()
        entity = consentRepository.save(entity)

        when:
        consentService.deleteConsent(entity.getConsentId(), clientId)

        then:
        noExceptionThrown()
        def entityOp = consentRepository.findByConsentId(entity.consentId)
        entityOp.isPresent()
        entityOp.get().status == EnumConsentStatus.REJECTED.name()
    }

    def "We can delete an authorized consent"() {
        given:
        def clientId = "random_client_id"
        def entity = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(), clientId)
        entity.status = EnumConsentStatus.AUTHORISED.name()
        entity = consentRepository.save(entity)

        when:
        consentService.deleteConsent(entity.getConsentId(), clientId)

        then:
        noExceptionThrown()
        def entityOp = consentRepository.findByConsentId(entity.consentId)
        entityOp.isPresent()
        entityOp.get().status == EnumConsentStatus.REJECTED.name()
        entityOp.get().rejectedBy == EnumRejectedBy.USER.name()
        entityOp.get().rejectionCode == EnumReasonCode.CUSTOMER_MANUALLY_REVOKED.name()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
