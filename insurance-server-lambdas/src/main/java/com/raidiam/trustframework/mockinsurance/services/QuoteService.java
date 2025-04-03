package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.QuoteEntity;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Optional;

public abstract class QuoteService<T extends QuoteEntity> extends BaseInsuranceService {
    private static final String WEBHOOK_ENDPOINT_TEMPLATE = "/open-insurance/webhook/v1/quote/v1/request/%s/quote-status";

    protected abstract Optional<T> getQuoteByConsentId(String consentId);
    protected abstract T saveQuote(T quote);
    protected abstract Logger getLogger();

    @Inject
    private WebhookService webhookService;

    public T createQuote(T quote) {
        this.validateQuote(quote);

        return this.saveQuote(quote);
    }

    protected void validateQuote(T quote) {
        if (StringUtils.isBlank(quote.getConsentId())) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: consent id was not informed");
        }
    }

    protected void validateAmountDetails(AmountDetails amountDetails) {
        if (StringUtils.isBlank(amountDetails.getAmount())) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NAO_INFORMADO: Amount missing from amount details");
        }

        if (amountDetails.getUnitType() == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "NAO_INFORMADO: Unit type missing from amount details");
        }
    }

    /**
     * Fetch a quote, mimicking the processing an insurer would perform based on the quote information.
     */
    public T getQuote(String consentId, String clientId) {
        T quote = this.getQuoteByConsentId(consentId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Quote for consent id " + consentId + " not found"));

        if (QuoteStatusEnum.RCVD.toString().equals(quote.getStatus())) {
            this.getLogger().info("quote status is RCVD");

            if (quote.shouldReject()) {
                this.getLogger().info("the quote should be rejected, moving to RJCT");
                quote.setStatus(QuoteStatusEnum.RJCT.toString());
                this.sendNotification(consentId, clientId);
                return this.saveQuote(quote);
            }

            this.getLogger().info("moving to ACPT");
            quote.setStatus(QuoteStatusEnum.ACPT.toString());
            this.sendNotification(consentId, clientId);
            return this.saveQuote(quote);
        }

        return quote;
    }

    public T patchQuote(PatchQuotePayload req, String consentId, String clientId) {
        T quote = this.getQuote(consentId, clientId);

        String entityId;
        if (RevokeQuotePatchPayloadDataAuthor.IdentificationTypeEnum.CNPJ.equals(req.getData().getAuthor().getIdentificationType())) {
            this.getLogger().info("Patching quote for business");
            entityId = quote.getBusinessCnpj();
        } else {
            this.getLogger().info("Patching quote for person");
            entityId = quote.getPersonCpf();
        }

        if (entityId == null || !entityId.equals(req.getData().getAuthor().getIdentificationNumber())) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: invalid identificationNumber");
        }

        if (PatchQuotePayloadData.StatusEnum.CANC.equals(req.getData().getStatus())) {
            quote.setStatus(QuoteStatusEnum.CANC.toString());
            this.getLogger().info("the quote was cancelled");
            return this.saveQuote(quote);
        }

        if (!QuoteStatusEnum.ACPT.toString().equals(quote.getStatus())) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: the quote was not accepted");
        }

        if (!quote.getQuoteId().toString().equals(req.getData().getInsurerQuoteId())) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: invalid insurerQuoteId");
        }

        quote.setStatus(QuoteStatusEnum.ACKN.toString());
        this.getLogger().info("the quote was acknowledged");
        return this.saveQuote(quote);
    }

    protected void sendNotification(String consentId, String clientId) {
        this.webhookService.notify(clientId, String.format(WEBHOOK_ENDPOINT_TEMPLATE, consentId));
    }
}
