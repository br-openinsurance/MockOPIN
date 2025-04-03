package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuoteFinancialRiskLeadEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


@Singleton
@Transactional
public class QuoteFinancialRiskLeadService extends QuoteLeadService<QuoteFinancialRiskLeadEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteFinancialRiskLeadService.class);

    @Override
    protected QuoteFinancialRiskLeadEntity saveQuote(QuoteFinancialRiskLeadEntity quote) {
        return quoteFinancialRiskLeadRepository.save(quote);
    }

    public Optional<QuoteFinancialRiskLeadEntity> getQuoteByConsentId(String consentId) {
        return quoteFinancialRiskLeadRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}