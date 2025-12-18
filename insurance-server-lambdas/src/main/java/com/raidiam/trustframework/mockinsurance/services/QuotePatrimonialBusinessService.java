package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialBusinessEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
@Transactional
public class QuotePatrimonialBusinessService extends QuoteService<QuotePatrimonialBusinessEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuotePatrimonialBusinessService.class);

    protected QuotePatrimonialBusinessEntity saveQuote(QuotePatrimonialBusinessEntity quote) {
        return quotePatrimonialBusinessRepository.save(quote);
    }

    public Optional<QuotePatrimonialBusinessEntity> getQuoteByConsentId(String consentId) {
        return quotePatrimonialBusinessRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected void validateQuote(QuotePatrimonialBusinessEntity quote) {
        super.validateQuote(quote);

        if (quote.getData().getMaxLMG() != null) {
            validateAmountDetails(quote.getData().getMaxLMG());
        }
    }
}