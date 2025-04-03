package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.QuoteTransportLeadEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.QuoteTransportLeadService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/open-insurance/quote-transport")
public class QuoteTransportController extends BaseInsuranceController {

    @Inject
    QuoteTransportLeadService quoteTransportLeadService;

    private static final Logger LOG = LoggerFactory.getLogger(QuoteTransportController.class);

    @Post("/v1/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_TRANSPORT_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuoteLead createLeadQuoteV1(
            @Body QuoteRequestTransportLead body,
            @NotNull HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote transport for client {}", clientId);

        var responseQuoteTransportLead = quoteTransportLeadService.createQuote(QuoteTransportLeadEntity.fromRequest(body, clientId)).toResponse();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(responseQuoteTransportLead::setLinks, responseQuoteTransportLead::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, responseQuoteTransportLead);

        return responseQuoteTransportLead;
    }

    @Patch("/v1/lead/request/{consentId}")
    @Secured({"QUOTE_TRANSPORT_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseRevokeQuotePatch patchLeadQuoteV1(@PathVariable("consentId") String consentId, @Body RevokeQuotePatchPayload body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Patching quote transport for consent id");
        return quoteTransportLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }
}
