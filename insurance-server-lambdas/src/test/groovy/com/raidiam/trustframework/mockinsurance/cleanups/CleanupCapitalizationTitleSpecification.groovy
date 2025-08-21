package com.raidiam.trustframework.mockinsurance.cleanups

import com.raidiam.trustframework.mockinsurance.repository.AccountHolderRepository
import com.raidiam.trustframework.mockinsurance.repository.CapitalizationTitleBrokerRepository
import com.raidiam.trustframework.mockinsurance.repository.CapitalizationTitleHolderRepository
import com.raidiam.trustframework.mockinsurance.repository.CapitalizationTitlePlanEventRepository
import com.raidiam.trustframework.mockinsurance.repository.CapitalizationTitlePlanPlanSeriesRepository
import com.raidiam.trustframework.mockinsurance.repository.CapitalizationTitlePlanQuotaRepository
import com.raidiam.trustframework.mockinsurance.repository.CapitalizationTitlePlanRepository
import com.raidiam.trustframework.mockinsurance.repository.CapitalizationTitlePlanSettlementRepository
import com.raidiam.trustframework.mockinsurance.repository.CapitalizationTitlePlanSubscriberRepository
import com.raidiam.trustframework.mockinsurance.repository.CapitalizationTitlePlanTechnicalProvisionsRepository
import com.raidiam.trustframework.mockinsurance.repository.CapitalizationTitlePlanTitleRepository
import com.raidiam.trustframework.mockinsurance.repository.ConsentCapitalizationTitlePlanRepository
import com.raidiam.trustframework.mockinsurance.repository.ConsentRepository
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class CleanupCapitalizationTitleSpecification extends Specification {
    Logger LOG = LoggerFactory.getLogger(CleanupCapitalizationTitleSpecification.class)

    @Inject
    AccountHolderRepository accountHolderRepository

    @Inject
    ConsentRepository consentRepository

    @Inject
    CapitalizationTitlePlanRepository capitalizationTitlePlanRepository

    @Inject
    ConsentCapitalizationTitlePlanRepository consentCapitalizationTitlePlanRepository

    @Inject
    CapitalizationTitlePlanEventRepository capitalizationTitlePlanEventRepository

    @Inject
    CapitalizationTitlePlanSettlementRepository capitalizationTitlePlanSettlementRepository

    @Inject
    CapitalizationTitlePlanPlanSeriesRepository capitalizationTitlePlanSeriesRepository

    @Inject
    CapitalizationTitlePlanQuotaRepository capitalizationTitlePlanQuotaRepository

    @Inject
    CapitalizationTitleBrokerRepository capitalizationTitlePlanBrokerRepository

    @Inject
    CapitalizationTitlePlanTitleRepository capitalizationTitlePlanTitleRepository

    @Inject
    CapitalizationTitlePlanSubscriberRepository capitalizationTitlePlanSubscriberRepository

    @Inject
    CapitalizationTitlePlanTechnicalProvisionsRepository capitalizationTitlePlanTechnicalProvisionsRepository

    @Inject
    CapitalizationTitleHolderRepository capitalizationTitlePlanHolderRepository

    @Shared
    boolean runSetup = true

    @Shared
    boolean runCleanup = false

    def cleanup() {
        if (runCleanup) {
            LOG.info("Running Cleanup")
            accountHolderRepository.deleteAll()
            consentRepository.deleteAll()
            capitalizationTitlePlanRepository.deleteAll()
            consentCapitalizationTitlePlanRepository.deleteAll()
            capitalizationTitlePlanEventRepository.deleteAll()
            capitalizationTitlePlanSeriesRepository.deleteAll()
            capitalizationTitlePlanQuotaRepository.deleteAll()
            capitalizationTitlePlanBrokerRepository.deleteAll()
            capitalizationTitlePlanTitleRepository.deleteAll()
            capitalizationTitlePlanSubscriberRepository.deleteAll()
            capitalizationTitlePlanTechnicalProvisionsRepository.deleteAll()
            capitalizationTitlePlanHolderRepository.deleteAll()
            capitalizationTitlePlanSettlementRepository.deleteAll()
            runCleanup = false
            runSetup = true
        }
    }
}
