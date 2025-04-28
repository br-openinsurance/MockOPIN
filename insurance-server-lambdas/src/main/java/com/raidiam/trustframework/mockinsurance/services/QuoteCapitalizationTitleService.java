package com.raidiam.trustframework.mockinsurance.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raidiam.trustframework.mockinsurance.domain.QuoteCapitalizationTitleEntity;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteDataCapitalizationTitle.PaymentTypeEnum;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
@Transactional
public class QuoteCapitalizationTitleService extends QuoteService<QuoteCapitalizationTitleEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteCapitalizationTitleService.class);

    @Override
    protected Optional<QuoteCapitalizationTitleEntity> getQuoteByConsentId(String consentId) {
        return quoteCapitalizationTitleRepository.findByConsentId(consentId);
    }

    @Override
    protected QuoteCapitalizationTitleEntity saveQuote(QuoteCapitalizationTitleEntity quote) {
        return quoteCapitalizationTitleRepository.save(quote);
    }

    @Override
    protected void validateQuote(QuoteCapitalizationTitleEntity quote) {
        super.validateQuote(quote);

        var paymentType = quote.getData().getQuoteData().getPaymentType();

        if((paymentType.equals(PaymentTypeEnum.MENSAL) && quote.getData().getQuoteData().getSinglePayment() != null) ||
        (paymentType.equals(PaymentTypeEnum.UNICO) && quote.getData().getQuoteData().getMonthlyPayment() != null)){
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NAO_INFORMADO: Payment type and payment are incompatible");
        }
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
