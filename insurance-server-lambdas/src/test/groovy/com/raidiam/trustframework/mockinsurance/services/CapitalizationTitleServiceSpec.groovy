package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupCapitalizationTitleSpecification
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
class CapitalizationTitleServiceSpec extends CleanupCapitalizationTitleSpecification {

    @Inject
    CapitalizationTitleService capitalizationTitleService

    @Shared
    CapitalizationTitlePlanEntity testCapitalizationTitlePlan

    @Shared
    CapitalizationTitlePlanEventEntity testCapitalizationTitlePlanEvent

    @Shared
    CapitalizationTitlePlanSeriesEntity testCapitalizationTitlePlanSeries

    @Shared
    CapitalizationTitlePlanTitleEntity testCapitalizationTitlePlanTitle

    @Shared
    CapitalizationTitlePlanSubscriberEntity testCapitalizationTitlePlanSubscriber

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
            testCapitalizationTitlePlanSeries = capitalizationTitlePlanSeriesRepository.save(TestEntityDataFactory.aCapitalizationPlanSeries(testCapitalizationTitlePlan.getCapitalizationTitlePlanId()))
            capitalizationTitlePlanQuotaRepository.save(TestEntityDataFactory.aCapitalizationTitlePlanQuota(testCapitalizationTitlePlanSeries.getSeriesId()))
            capitalizationTitlePlanBrokerRepository.save(TestEntityDataFactory.aCapitalizationPlanBroker(testCapitalizationTitlePlanSeries.getSeriesId()))
            testCapitalizationTitlePlanTitle = capitalizationTitlePlanTitleRepository.save(TestEntityDataFactory.aCapitalizationTitlePlanTitle(testCapitalizationTitlePlanSeries.getSeriesId()))
            testCapitalizationTitlePlanSubscriber = capitalizationTitlePlanSubscriberRepository.save(TestEntityDataFactory.aCapitalizationTitlePlanSubscriber(testCapitalizationTitlePlanTitle.getTitleId()))
            capitalizationTitlePlanHolderRepository.save(TestEntityDataFactory.aCapitalizationTitlePlanHolder(testCapitalizationTitlePlanSubscriber.getSubscriberId()))
            capitalizationTitlePlanTechnicalProvisionsRepository.save(TestEntityDataFactory.aCapitalizationTitlePlanTechnicalProvisions(testCapitalizationTitlePlanTitle.getTitleId()))
            runSetup = false
        }
    }

    def "we can get plans" () {
        when:
        def response = capitalizationTitleService.getPlans(testConsent.getConsentId().toString(), Pageable.from(0, 1))
        def plan = response.getData().first().getBrand().getCompanies().first().getProducts().first()

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first().getBrand().getName() == "Mock"
        response.getData().first().getBrand().getCompanies().first().getCompanyName() == "Mock Insurer"
        plan.getPlanId() == testCapitalizationTitlePlan.getCapitalizationTitlePlanId().toString()
    }

    def "we can get a plan info" () {
        when:
        def response = capitalizationTitleService.getPlanInfo(testCapitalizationTitlePlan.getCapitalizationTitlePlanId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getSeries().first().getSeriesId() == testCapitalizationTitlePlanSeries.getSeriesId().toString()
        response.getData().getSeries().first().getQuotas().first().getQuota() == 10
        response.getData().getSeries().first().getTitles().first().getContributionAmount().getAmount() == "370"
        response.getData().getSeries().first().getTitles().first().getTechnicalProvisions().first().getPdbAmount().getAmount() == "100.00"
        response.getData().getSeries().first().getTitles().first().getSubscriber().first().getHolder().first().getHolderName() == "Nome do Titular"
        response.getData().getSeries().first().getTitles().first().getSubscriber().first().getSubscriberPhones().first().getNumber() == "29875132"
        response.getData().getSeries().first().getBroker().first().getSusepBrokerCode() == "123123123"
    }

    def "we can get a plan's events" () {
        when:
        def response = capitalizationTitleService.getPlanEvents(testCapitalizationTitlePlan.getCapitalizationTitlePlanId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        response.getData().first().getEventType().toString() == testCapitalizationTitlePlanEvent.getEventType()
        response.getData().first().getEvent().getRaffle().getRaffleDate() == testCapitalizationTitlePlanEvent.getRaffleDate()
        response.getData().first().getEvent().getRaffle().getRaffleAmount().getAmount() == testCapitalizationTitlePlanEvent.getRaffleAmount()
    }

    def "we can get a plan's settlements" () {
        when:
        def response = capitalizationTitleService.getPlanSettlements(testCapitalizationTitlePlan.getCapitalizationTitlePlanId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        response.getData().first().getSettlementId() == testCapitalizationTitlePlanSettlement.getSettlementId().toString()
        response.getData().first().getSettlementFinancialAmount().getAmount() == testCapitalizationTitlePlanSettlement.getSettlementFinancialAmount()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
