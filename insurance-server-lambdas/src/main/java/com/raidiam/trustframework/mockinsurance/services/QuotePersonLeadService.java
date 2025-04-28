package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuotePersonLeadEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


@Singleton
@Transactional
public class QuotePersonLeadService extends QuoteLeadService<QuotePersonLeadEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuotePersonLeadService.class);

    @Override
    protected QuotePersonLeadEntity saveQuote(QuotePersonLeadEntity quote) {
        return quotePersonLeadRepository.save(quote);
    }

    public Optional<QuotePersonLeadEntity> getQuoteByConsentId(String consentId) {
        return quotePersonLeadRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}