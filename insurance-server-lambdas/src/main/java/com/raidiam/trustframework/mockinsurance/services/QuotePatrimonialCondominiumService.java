package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialCondominiumEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
@Transactional
public class QuotePatrimonialCondominiumService extends QuoteService<QuotePatrimonialCondominiumEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuotePatrimonialCondominiumService.class);

    protected QuotePatrimonialCondominiumEntity saveQuote(QuotePatrimonialCondominiumEntity quote) {
        return quotePatrimonialCondominiumRepository.save(quote);
    }

    public Optional<QuotePatrimonialCondominiumEntity> getQuoteByConsentId(String consentId) {
        return quotePatrimonialCondominiumRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected void validateQuote(QuotePatrimonialCondominiumEntity quote) {
        super.validateQuote(quote);

        if (quote.getData().getQuoteData().getMaxLMG() != null) {
            validateAmountDetails(quote.getData().getQuoteData().getMaxLMG());
        }
    }
}