package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuotePersonTravelEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
@Transactional
public class QuotePersonTravelService extends QuoteService<QuotePersonTravelEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuotePersonTravelService.class);

    protected QuotePersonTravelEntity saveQuote(QuotePersonTravelEntity quote) {
        return quotePersonTravelRepository.save(quote);
    }

    public Optional<QuotePersonTravelEntity> getQuoteByConsentId(String consentId) {
        return quotePersonTravelRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}