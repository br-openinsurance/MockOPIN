package com.raidiam.trustframework.mockinsurance.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raidiam.trustframework.mockinsurance.domain.QuoteCapitalizationTitleLeadEntity;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
@Transactional
public class QuoteCapitalizationTitleLeadService extends QuoteLeadService<QuoteCapitalizationTitleLeadEntity> {
    
    private static final Logger LOG = LoggerFactory.getLogger(QuoteCapitalizationTitleLeadService.class);

    @Override
    protected Optional<QuoteCapitalizationTitleLeadEntity> getQuoteByConsentId(String consentId) {
        return quoteCapitalizationTitleLeadRepository.findByConsentId(consentId);
    }

    @Override
    protected QuoteCapitalizationTitleLeadEntity saveQuote(QuoteCapitalizationTitleLeadEntity quote) {
        return quoteCapitalizationTitleLeadRepository.save(quote);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

}
