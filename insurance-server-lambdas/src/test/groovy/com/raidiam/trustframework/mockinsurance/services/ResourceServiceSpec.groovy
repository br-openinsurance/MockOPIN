package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.*
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentPermission
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseResourceList
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class ResourceServiceSpec extends CleanupSpecification {
    @Inject
    ResourcesService resourcesService

    @Inject
    ConsentService consentService

    @Inject
    TestEntityDataFactory testEntityDataFactory

    @Shared
    ConsentEntity testConsent

    @Shared
    CapitalizationTitlePlanEntity testCapitalizationTitlePlan

    @Shared
    ConsentCapitalizationTitlePlanEntity testConsentCapitalizationTitlePlan

    @Shared
    FinancialRiskPolicyEntity testFinancialRiskPolicy

    @Shared
    ConsentFinancialRiskPolicyEntity testConsentFinancialRiskPolicy

    @Shared
    HousingPolicyEntity testHousingPolicy

    @Shared
    ConsentHousingPolicyEntity testConsentHousingPolicy

    @Shared
    ResponsibilityPolicyEntity testResponsibilityPolicy

    @Shared
    ConsentResponsibilityPolicyEntity testConsentResponsibilityPolicy

    @Shared
    PersonPolicyEntity testPersonPolicy

    @Shared
    ConsentPersonPolicyEntity testConsentPersonPolicy

    @Shared
    LifePensionContractEntity testLifePensionContract

    @Shared
    FinancialAssistanceContractEntity testFinancialAssistanceContract

    @Shared
    ConsentLifePensionContractEntity testConsentLifePensionContract

    @Shared
    PensionPlanContractEntity testPensionPlanContract

    @Shared
    ConsentPensionPlanContractEntity testConsentPensionPlanContract

    @Shared
    AcceptanceAndBranchesAbroadPolicyEntity testAcceptanceAndBranchesAbroadPolicy

    @Shared
    ConsentAcceptanceAndBranchesAbroadPolicyEntity testConsentAcceptanceAndBranchesAbroadPolicy

    @Shared
    PatrimonialPolicyEntity testPatrimonialPolicy

    @Shared
    ConsentPatrimonialPolicyEntity testConsentPatrimonialPolicy

    @Shared
    RuralPolicyEntity testRuralPolicy

    @Shared
    ConsentRuralPolicyEntity testConsentRuralPolicy

    @Shared
    AutoPolicyEntity testAutoPolicy

    @Shared
    ConsentAutoPolicyEntity testConsentAutoPolicy

    @Shared
    TransportPolicyEntity testTransportPolicy

    @Shared
    ConsentTransportPolicyEntity testConsentTransportPolicy


    @Shared
    AccountHolderEntity testAccountHolder

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId())
            testConsent.setPermissions(List.of(
                    EnumConsentPermission.RESOURCES_READ.toString()
            ))
            testConsent = consentRepository.save(testConsent)

            testCapitalizationTitlePlan = capitalizationTitlePlanRepository.save(TestEntityDataFactory.aCapitalizationTitlePlan(testAccountHolder.getAccountHolderId()))
            consentCapitalizationTitlePlanRepository.save(new ConsentCapitalizationTitlePlanEntity(testConsent, testCapitalizationTitlePlan))

            testFinancialRiskPolicy = financialRiskPolicyRepository.save(TestEntityDataFactory.aFinancialRiskPolicy(testAccountHolder.getAccountHolderId(), null, null, null, null, null))
            consentFinancialRiskPolicyRepository.save(new ConsentFinancialRiskPolicyEntity(testConsent, testFinancialRiskPolicy))

            testHousingPolicy = housingPolicyRepository.save(TestEntityDataFactory.aHousingPolicy(testAccountHolder.getAccountHolderId()))
            consentHousingPolicyRepository.save(new ConsentHousingPolicyEntity(testConsent, testHousingPolicy))

            testResponsibilityPolicy = responsibilityPolicyRepository.save(TestEntityDataFactory.aResponsibilityPolicy(testAccountHolder.getAccountHolderId()))
            consentResponsibilityPolicyRepository.save(new ConsentResponsibilityPolicyEntity(testConsent, testResponsibilityPolicy))

            testPersonPolicy = personPolicyRepository.save(TestEntityDataFactory.aPersonPolicy(testAccountHolder.getAccountHolderId()))
            consentPersonPolicyRepository.save(new ConsentPersonPolicyEntity(testConsent, testPersonPolicy))

            testLifePensionContract = lifePensionContractRepository.save(TestEntityDataFactory.aLifePensionContract(testAccountHolder.getAccountHolderId()))
            consentLifePensionContractRepository.save(new ConsentLifePensionContractEntity(testConsent, testLifePensionContract))

            testPensionPlanContract = pensionPlanContractRepository.save(TestEntityDataFactory.aPensionPlanContract("pension-plan-1", testAccountHolder.getAccountHolderId()))
            consentPensionPlanContractRepository.save(new ConsentPensionPlanContractEntity(testConsent, testPensionPlanContract))

            testFinancialAssistanceContract = financialAssistanceContractRepository.save(TestEntityDataFactory.aFinancialAssistanceContract(testAccountHolder.getAccountHolderId()))
            consentFinancialAssistanceContractRepository.save(new ConsentFinancialAssistanceContractEntity(testConsent, testFinancialAssistanceContract))

            testAcceptanceAndBranchesAbroadPolicy = acceptanceAndBranchesAbroadPolicyRepository.save(TestEntityDataFactory.anAcceptanceAndBranchesAbroadPolicy(testAccountHolder.getAccountHolderId()))
            consentAcceptanceAndBranchesAbroadPolicyRepository.save(new ConsentAcceptanceAndBranchesAbroadPolicyEntity(testConsent, testAcceptanceAndBranchesAbroadPolicy))

            testPatrimonialPolicy = patrimonialPolicyRepository.save(TestEntityDataFactory.aPatrimonialPolicy(testAccountHolder.getAccountHolderId(), "0111"))
            consentPatrimonialPolicyRepository.save(new ConsentPatrimonialPolicyEntity(testConsent, testPatrimonialPolicy))

            testRuralPolicy = ruralPolicyRepository.save(TestEntityDataFactory.aRuralPolicy(testAccountHolder.getAccountHolderId()))
            consentRuralPolicyRepository.save(new ConsentRuralPolicyEntity(testConsent, testRuralPolicy))

            testAutoPolicy = autoPolicyRepository.save(TestEntityDataFactory.anAutoPolicy(testAccountHolder.getAccountHolderId()))
            consentAutoPolicyRepository.save(new ConsentAutoPolicyEntity(testConsent, testAutoPolicy))

            testTransportPolicy = transportPolicyRepository.save(TestEntityDataFactory.aTransportPolicy(testAccountHolder.getAccountHolderId()))
            consentTransportPolicyRepository.save(new ConsentTransportPolicyEntity(testConsent, testTransportPolicy))

            runSetup = false
        }
    }

    def "we can not get resources without permissions"() {
        when:
        resourcesService.getResourceList(Pageable.unpaged(), testConsent.consentId)

        then:
        HttpStatusException e = thrown()
        e.status == HttpStatus.NOT_FOUND
        e.getMessage() == "Resource not found, no appropriate permissions attached to consent"
    }

    def "we can get pages"() {
        given:
        testConsent.setPermissions(List.of(
                EnumConsentPermission.RESOURCES_READ.toString(),
                EnumConsentPermission.CAPITALIZATION_TITLE_READ.toString(),
                EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_READ.toString(),
                EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_READ.toString(),
                EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_READ.toString(),
                EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_READ.toString(),
                EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_READ.toString(),
                EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_READ.toString(),
                EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_READ.toString(),
                EnumConsentPermission.LIFE_PENSION_READ.toString(),
                EnumConsentPermission.PENSION_PLAN_READ.toString(),
                EnumConsentPermission.FINANCIAL_ASSISTANCE_READ.toString(),
                EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_READ.toString(),
                EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_READ.toString(),
        ))
        consentRepository.update(testConsent)

        when:
        def page1 = resourcesService.getResourceList(Pageable.from(0, 10), testConsent.getConsentId())
        def page2 = resourcesService.getResourceList(Pageable.from(1, 10), testConsent.getConsentId())

        then:

        // we can see the number of pages on each page
        page1.getMeta().totalPages == 2

        // first page has 2 resources (for now)
        page1.getData().size() == 10

        // second page has 2 resources (for now)
        page2.getData().size() == 3
    }

    def "we get an 403 on consent not found"() {
        given:
        def consentId = "notfoundconsent"

        when:
        resourcesService.getResourceList(Pageable.unpaged(), consentId)

        then:
        HttpStatusException e = thrown()
        e.status == HttpStatus.NOT_FOUND
        e.getLocalizedMessage() == "Consent Id " + consentId + " not found"
    }

    def "we get an empty list when only customers permissions are granted"() {
        when:
        testConsent.setPermissions(List.of(
                EnumConsentPermission.RESOURCES_READ.toString(),
                EnumConsentPermission.CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ.toString(),
                EnumConsentPermission.CUSTOMERS_PERSONAL_QUALIFICATION_READ.toString(),
                EnumConsentPermission.CUSTOMERS_PERSONAL_ADDITIONALINFO_READ.toString()
        ))
        consentRepository.update(testConsent)
        ResponseResourceList response1 = resourcesService.getResourceList(Pageable.unpaged(), testConsent.getConsentId())

        then:
        response1.getData().isEmpty()

        when:
        testConsent.setPermissions(List.of(
                EnumConsentPermission.RESOURCES_READ.toString(),
                EnumConsentPermission.CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ.toString(),
                EnumConsentPermission.CUSTOMERS_BUSINESS_QUALIFICATION_READ.toString(),
                EnumConsentPermission.CUSTOMERS_BUSINESS_ADDITIONALINFO_READ.toString()
        ))
        consentRepository.update(testConsent)
        ResponseResourceList response2 = resourcesService.getResourceList(Pageable.unpaged(), testConsent.getConsentId())

        then:
        response2.getData().isEmpty()
    }

    def "we get an 403 on wrong permissions"() {
        when:
        testConsent.setPermissions(List.of())
        consentRepository.update(testConsent)
        resourcesService.getResourceList(Pageable.unpaged(), testConsent.consentId)

        then:
        HttpStatusException e = thrown()
        e.getStatus() == HttpStatus.FORBIDDEN
        e.getLocalizedMessage() == "You do not have the correct permission"
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }

}
