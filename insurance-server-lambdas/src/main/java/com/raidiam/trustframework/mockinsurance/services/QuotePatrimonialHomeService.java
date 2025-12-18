package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialHomeEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
@Transactional
public class QuotePatrimonialHomeService extends QuoteService<QuotePatrimonialHomeEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuotePatrimonialHomeService.class);

    protected QuotePatrimonialHomeEntity saveQuote(QuotePatrimonialHomeEntity quote) {
        return quotePatrimonialHomeRepository.save(quote);
    }

    public Optional<QuotePatrimonialHomeEntity> getQuoteByConsentId(String consentId) {
        return quotePatrimonialHomeRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected void validateQuote(QuotePatrimonialHomeEntity quote) {
        super.validateQuote(quote);

        if (quote.getData().getMaxLMG() != null) {
            validateAmountDetails(quote.getData().getMaxLMG());
        }
    }
}