package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuoteTransportLeadEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


@Singleton
@Transactional
public class QuoteTransportLeadService extends QuoteLeadService<QuoteTransportLeadEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteTransportLeadService.class);

    @Override
    protected QuoteTransportLeadEntity saveQuote(QuoteTransportLeadEntity quote) {
        return quoteTransportLeadRepository.save(quote);
    }

    public Optional<QuoteTransportLeadEntity> getQuoteByConsentId(String consentId) {
        return quoteTransportLeadRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}