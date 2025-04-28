package com.raidiam.trustframework.mockinsurance

import com.raidiam.trustframework.mockinsurance.repository.*
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class CleanupSpecification extends Specification {
    Logger LOG = LoggerFactory.getLogger(CleanupSpecification.class)

    @Inject
    AccountHolderRepository accountHolderRepository

    @Inject
    ConsentRepository consentRepository

    @Inject
    EndorsementRepository endorsementRepository

    @Inject
    ClaimNotificationDamageRepository claimNotificationDamageRepository

    @Inject
    ClaimNotificationPersonRepository claimNotificationPersonRepository

    @Inject
    QuotePatrimonialLeadRepository quotePatrimonialLeadRepository

    @Inject
    QuotePatrimonialBusinessRepository quotePatrimonialBusinessRepository

    @Inject
    QuotePatrimonialHomeRepository quotePatrimonialHomeRepository

    @Inject
    QuotePatrimonialCondominiumRepository quotePatrimonialCondominiumRepository

    @Inject
    QuotePatrimonialDiverseRisksRepository quotePatrimonialDiverseRisksRepository

    @Inject
    QuoteFinancialRiskLeadRepository quoteFinancialRiskLeadRepository

    @Inject
    QuoteHousingLeadRepository quoteHousingLeadRepository

    @Inject
    QuoteResponsibilityLeadRepository quoteResponsibilityLeadRepository

    @Inject
    QuoteRuralLeadRepository quoteRuralLeadRepository

    @Inject
    QuoteAutoRepository quoteAutoRepository

    @Inject
    QuoteAutoLeadRepository quoteAutoLeadRepository

    @Inject
    QuoteLifePensionRepository quoteLifePensionRepository

    @Inject
    QuoteLifePensionLeadRepository quoteLifePensionLeadRepository

    @Inject
    QuotePersonLifeRepository quotePersonLifeRepository

    @Inject
    QuotePersonLifeRepository quotePersonTravelRepository

    @Inject
    QuotePersonLeadRepository quotePersonLeadRepository

    @Inject
    QuoteCapitalizationTitleRepository quoteCapitalizationTitleRepository

    @Inject
    QuoteCapitalizationTitleLeadRepository quoteCapitalizationTitleLeadRepository

    @Inject
    CapitalizationTitleRaffleRepository capitalizationTitleRaffleRepository

    @Inject
    WebhookRepository webhookRepository

    @Shared
    boolean runSetup = true

    @Shared
    boolean runCleanup = false

    def cleanup() {
        if (runCleanup) {
            LOG.info("Running Cleanup")
            accountHolderRepository.deleteAll()
            consentRepository.deleteAll()
            endorsementRepository.deleteAll()
            claimNotificationDamageRepository.deleteAll()
            quotePatrimonialLeadRepository.deleteAll()
            quotePatrimonialBusinessRepository.deleteAll()
            quotePatrimonialHomeRepository.deleteAll()
            quotePatrimonialCondominiumRepository.deleteAll()
            quotePatrimonialDiverseRisksRepository.deleteAll()
            quoteFinancialRiskLeadRepository.deleteAll()
            quoteHousingLeadRepository.deleteAll()
            quoteResponsibilityLeadRepository.deleteAll()
            quoteRuralLeadRepository.deleteAll()
            quoteAutoRepository.deleteAll()
            quoteAutoLeadRepository.deleteAll()
            quoteLifePensionRepository.deleteAll()
            quoteLifePensionLeadRepository.deleteAll()
            quotePersonLifeRepository.deleteAll()
            quotePersonTravelRepository.deleteAll()
            quoteCapitalizationTitleRepository.deleteAll()
            quoteCapitalizationTitleLeadRepository.deleteAll()
            capitalizationTitleRaffleRepository.deleteAll()
            webhookRepository.deleteAll()
            runCleanup = false
            runSetup = true
        }
    }
}
