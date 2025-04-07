package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialLeadEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


@Singleton
@Transactional
public class QuotePatrimonialLeadService extends QuoteLeadService<QuotePatrimonialLeadEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuotePatrimonialLeadService.class);

    @Override
    protected QuotePatrimonialLeadEntity saveQuote(QuotePatrimonialLeadEntity quote) {
        return quotePatrimonialLeadRepository.save(quote);
    }

    public Optional<QuotePatrimonialLeadEntity> getQuoteByConsentId(String consentId) {
        return quotePatrimonialLeadRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}