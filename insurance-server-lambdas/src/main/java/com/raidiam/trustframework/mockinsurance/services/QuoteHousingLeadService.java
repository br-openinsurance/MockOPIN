package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuoteHousingLeadEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


@Singleton
@Transactional
public class QuoteHousingLeadService extends QuoteLeadService<QuoteHousingLeadEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteHousingLeadService.class);

    @Override
    protected QuoteHousingLeadEntity saveQuote(QuoteHousingLeadEntity quote) {
        return quoteHousingLeadRepository.save(quote);
    }

    public Optional<QuoteHousingLeadEntity> getQuoteByConsentId(String consentId) {
        return quoteHousingLeadRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}