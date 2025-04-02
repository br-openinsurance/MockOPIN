package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuoteLifePensionLeadEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


@Singleton
@Transactional
public class QuoteLifePensionLeadService extends QuoteLeadService<QuoteLifePensionLeadEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteLifePensionLeadService.class);

    @Override
    protected QuoteLifePensionLeadEntity saveQuote(QuoteLifePensionLeadEntity quote) {
        return quoteLifePensionLeadRepository.save(quote);
    }

    public Optional<QuoteLifePensionLeadEntity> getQuoteByConsentId(String consentId) {
        return quoteLifePensionLeadRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}