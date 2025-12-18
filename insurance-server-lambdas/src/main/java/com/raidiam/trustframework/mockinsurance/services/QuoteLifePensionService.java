package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuoteLifePensionEntity;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
@Transactional
public class QuoteLifePensionService extends QuoteService<QuoteLifePensionEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteLifePensionService.class);

    protected QuoteLifePensionEntity saveQuote(QuoteLifePensionEntity quote) {
        return quoteLifePensionRepository.save(quote);
    }

    public Optional<QuoteLifePensionEntity> getQuoteByConsentId(String consentId) {
        return quoteLifePensionRepository.findByConsentId(consentId);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
    

    @Override
    protected void validateQuote(QuoteLifePensionEntity quote) {
        super.validateQuote(quote);

        var initialContribution = quote.getData().getInitialContribution();

        if (initialContribution.getUnitType() == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NAO_INFORMADO: Unit type missing from amount details");
        }
        
        if (StringUtils.isBlank(initialContribution.getAmount())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NAO_INFORMADO: Amount missing from amount details");
        }
    }
}