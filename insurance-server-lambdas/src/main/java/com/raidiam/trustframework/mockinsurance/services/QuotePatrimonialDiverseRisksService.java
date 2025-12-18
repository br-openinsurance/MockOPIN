package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialDiverseRisksEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
@Transactional
public class QuotePatrimonialDiverseRisksService extends QuoteService<QuotePatrimonialDiverseRisksEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuotePatrimonialDiverseRisksService.class);

    protected QuotePatrimonialDiverseRisksEntity saveQuote(QuotePatrimonialDiverseRisksEntity quote) {
        return quotePatrimonialDiverseRisksRepository.save(quote);
    }

    public Optional<QuotePatrimonialDiverseRisksEntity> getQuoteByConsentId(String consentId) {
        return quotePatrimonialDiverseRisksRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected void validateQuote(QuotePatrimonialDiverseRisksEntity quote) {
        super.validateQuote(quote);

        if (quote.getData().getMaxLMG() != null) {
            validateAmountDetails(quote.getData().getMaxLMG());
        }
    }
}