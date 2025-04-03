package com.raidiam.trustframework.mockinsurance.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raidiam.trustframework.mockinsurance.domain.QuoteAutoEntity;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
@Transactional
public class QuoteAutoService extends QuoteService<QuoteAutoEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteAutoService.class);

    @Override
    protected Optional<QuoteAutoEntity> getQuoteByConsentId(String consentId) {
        return quoteAutoRepository.findByConsentId(consentId);
    }

    @Override
    protected QuoteAutoEntity saveQuote(QuoteAutoEntity quote) {
        return quoteAutoRepository.save(quote);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

}
