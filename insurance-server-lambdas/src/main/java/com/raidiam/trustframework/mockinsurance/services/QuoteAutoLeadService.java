package com.raidiam.trustframework.mockinsurance.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raidiam.trustframework.mockinsurance.domain.QuoteAutoLeadEntity;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
@Transactional
public class QuoteAutoLeadService extends QuoteLeadService<QuoteAutoLeadEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteAutoLeadService.class);

    @Override
    protected Optional<QuoteAutoLeadEntity> getQuoteByConsentId(String consentId) {
        return quoteAutoLeadRepository.findByConsentId(consentId);
    }

    @Override
    protected QuoteAutoLeadEntity saveQuote(QuoteAutoLeadEntity quote) {
        return quoteAutoLeadRepository.save(quote);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

}
