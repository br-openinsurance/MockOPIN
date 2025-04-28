package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.repository.*;
import jakarta.inject.Inject;

abstract class BaseInsuranceService {

    @Inject
    AccountHolderRepository accountHolderRepository;

    @Inject
    ConsentRepository consentRepository;

    @Inject
    EndorsementRepository endorsementRepository;

    @Inject
    ClaimNotificationDamageRepository claimNotificationDamageRepository;

    @Inject
    ClaimNotificationPersonRepository claimNotificationPersonRepository;

    @Inject
    QuotePatrimonialBusinessRepository quotePatrimonialBusinessRepository;

    @Inject
    QuotePatrimonialHomeRepository quotePatrimonialHomeRepository;

    @Inject
    QuotePatrimonialCondominiumRepository quotePatrimonialCondominiumRepository;

    @Inject
    QuotePatrimonialDiverseRisksRepository quotePatrimonialDiverseRisksRepository;

    @Inject
    QuotePatrimonialLeadRepository quotePatrimonialLeadRepository;

    @Inject
    QuoteFinancialRiskLeadRepository quoteFinancialRiskLeadRepository;

    @Inject
    QuoteAcceptanceAndBranchesAbroadLeadRepository quoteAcceptanceAndBranchesAbroadLeadRepository;

    @Inject
    QuoteHousingLeadRepository quoteHousingLeadRepository;

    @Inject
    QuoteLifePensionLeadRepository quoteLifePensionLeadRepository;

    @Inject
    QuoteLifePensionRepository quoteLifePensionRepository;

    @Inject
    QuoteResponsibilityLeadRepository quoteResponsibilityLeadRepository;

    @Inject
    QuoteTransportLeadRepository quoteTransportLeadRepository;

    @Inject
    QuoteRuralLeadRepository quoteRuralLeadRepository;

    @Inject
    QuoteAutoLeadRepository quoteAutoLeadRepository;

    @Inject
    QuoteAutoRepository quoteAutoRepository;

    @Inject
    QuoteCapitalizationTitleLeadRepository quoteCapitalizationTitleLeadRepository;

    @Inject
    QuoteCapitalizationTitleRepository quoteCapitalizationTitleRepository;

    @Inject
    CapitalizationTitleRaffleRepository capitalizationTitleRaffleRepository;

    @Inject
    QuotePersonLifeRepository quotePersonLifeRepository;

    @Inject
    QuotePersonLeadRepository quotePersonLeadRepository;

    @Inject
    QuotePersonTravelRepository quotePersonTravelRepository;

    @Inject
    WebhookRepository webhookRepository;

}
