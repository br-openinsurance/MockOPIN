package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuotePersonLifeEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
@Transactional
public class QuotePersonLifeService extends QuoteService<QuotePersonLifeEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuotePersonLifeService.class);

    protected QuotePersonLifeEntity saveQuote(QuotePersonLifeEntity quote) {
        return quotePersonLifeRepository.save(quote);
    }

    public Optional<QuotePersonLifeEntity> getQuoteByConsentId(String consentId) {
        return quotePersonLifeRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}