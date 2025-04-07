package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.repository.*;
import jakarta.inject.Inject;

abstract class BaseInsuranceService {

    @Inject
    AccountHolderRepository accountHolderRepository;

    @Inject
    ConsentRepository consentRepository;

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
    WebhookRepository webhookRepository;

}
