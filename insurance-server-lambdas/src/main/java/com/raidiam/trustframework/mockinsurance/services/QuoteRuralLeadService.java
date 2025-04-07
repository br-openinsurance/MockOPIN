package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuoteRuralLeadEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
@Transactional
public class QuoteRuralLeadService extends QuoteLeadService<QuoteRuralLeadEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteRuralLeadService.class);

    @Override
    protected QuoteRuralLeadEntity saveQuote(QuoteRuralLeadEntity quote) {
        return quoteRuralLeadRepository.save(quote);
    }

    public Optional<QuoteRuralLeadEntity> getQuoteByConsentId(String consentId) {
        return quoteRuralLeadRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}