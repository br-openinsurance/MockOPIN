package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuoteEntity;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Optional;

public abstract class QuoteLeadService<T extends QuoteEntity> extends BaseInsuranceService {
    protected abstract Optional<T> getQuoteByConsentId(String consentId);
    protected abstract T saveQuote(T quote);
    protected abstract Logger getLogger();

    public T createQuote(T quote) {
        this.validateQuote(quote);

        return this.saveQuote(quote);
    }

    protected void validateQuote(T quote) {
        if (StringUtils.isBlank(quote.getConsentId())) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: consent id was not informed");
        }
    }

    public T getQuote(String consentId, String clientId) {
        return this.getQuoteByConsentId(consentId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Quote lead for consent id " + consentId + " not found"));
    }

    public T patchQuote(RevokePatchPayload req, String consentId, String clientId) {
        T quote = this.getQuote(consentId, clientId);

        String entityId = RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CNPJ.equals(req.getData().getAuthor().getIdentificationType()) ?
                quote.getBusinessCnpj() : quote.getPersonCpf();
        this.getLogger().info("Patching quote lead for {}", entityId);
        if (entityId == null || !entityId.equals(req.getData().getAuthor().getIdentificationNumber())) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: invalid identificationNumber");
        }

        quote.setStatus(QuoteStatusEnum.CANC.toString());
        this.getLogger().info("the quote lead was cancelled");
        return this.saveQuote(quote);
    }
}
