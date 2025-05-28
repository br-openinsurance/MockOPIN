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
class CapitalizationTitleServiceSpec extends CleanupSpecification {

    @Inject
    CapitalizationTitleService capitalizationTitleService

    @Shared
    CapitalizationTitlePlanEntity testCapitalizationTitlePlan

    @Shared
    CapitalizationTitlePlanEventEntity testCapitalizationTitlePlanEvent

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    @Shared
    ConsentCapitalizationTitlePlanEntity testConsentCapitalizationTitlePlan

    @Shared
    CapitalizationTitlePlanSettlementEntity testCapitalizationTitlePlanSettlement

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.CAPITALIZATION_TITLE_READ,
                    EnumConsentPermission.CAPITALIZATION_TITLE_EVENTS_READ,
                    EnumConsentPermission.CAPITALIZATION_TITLE_SETTLEMENTS_READ,
                    EnumConsentPermission.CAPITALIZATION_TITLE_PLANINFO_READ)
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            testCapitalizationTitlePlan = capitalizationTitlePlanRepository.save(TestEntityDataFactory.aCapitalizationTitlePlan(testAccountHolder.getAccountHolderId()))
            consentCapitalizationTitlePlanRepository.save(new ConsentCapitalizationTitlePlanEntity(testConsent, testCapitalizationTitlePlan))
            testCapitalizationTitlePlanEvent = capitalizationTitlePlanEventRepository.save(TestEntityDataFactory.aCapitalizationTitlePlanEvent(testCapitalizationTitlePlan.getCapitalizationTitlePlanId()))
            testCapitalizationTitlePlanSettlement = capitalizationTitlePlanSettlementRepository.save(TestEntityDataFactory.aCapitalizationTitlePlanSettlement(testCapitalizationTitlePlan.getCapitalizationTitlePlanId()))
            runSetup = false
        }
    }

    def "we can get plans" () {
        when:
        def response = capitalizationTitleService.getPlans(testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get a plan info" () {
        when:
        def response = capitalizationTitleService.getPlanInfo(testCapitalizationTitlePlan.getCapitalizationTitlePlanId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a plan's events" () {
        when:
        def response = capitalizationTitleService.getPlanEvents(testCapitalizationTitlePlan.getCapitalizationTitlePlanId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a plan's settlements" () {
        when:
        def response = capitalizationTitleService.getPlanSettlements(testCapitalizationTitlePlan.getCapitalizationTitlePlanId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
