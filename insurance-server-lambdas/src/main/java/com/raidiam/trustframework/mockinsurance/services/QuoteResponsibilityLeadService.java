package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuoteResponsibilityLeadEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


@Singleton
@Transactional
public class QuoteResponsibilityLeadService extends QuoteLeadService<QuoteResponsibilityLeadEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteResponsibilityLeadService.class);

    @Override
    protected QuoteResponsibilityLeadEntity saveQuote(QuoteResponsibilityLeadEntity quote) {
        return quoteResponsibilityLeadRepository.save(quote);
    }

    public Optional<QuoteResponsibilityLeadEntity> getQuoteByConsentId(String consentId) {
        return quoteResponsibilityLeadRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}