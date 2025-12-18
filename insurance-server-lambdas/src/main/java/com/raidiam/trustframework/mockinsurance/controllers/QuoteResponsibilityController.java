package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.QuoteResponsibilityLeadEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.QuoteResponsibilityLeadService;
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
@Controller("/open-insurance/quote-responsibility")
public class QuoteResponsibilityController extends BaseInsuranceController {

    @Inject
    QuoteResponsibilityLeadService quoteResponsibilityLeadService;

    private static final Logger LOG = LoggerFactory.getLogger(QuoteResponsibilityController.class);

    @Post("/v1/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_RESPONSIBILITY_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuote createLeadQuoteV1(
            @Body QuoteRequestResponsibilityLead body,
            @NotNull HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote responsibility for client {}", clientId);


        var responseQuoteResponsibilityLead = quoteResponsibilityLeadService.createQuote(QuoteResponsibilityLeadEntity.fromRequest(body, clientId)).toResponse();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(responseQuoteResponsibilityLead::setLinks, responseQuoteResponsibilityLead::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, responseQuoteResponsibilityLead);

        return responseQuoteResponsibilityLead;
    }

    @Patch("/v1/lead/request/{consentId}")
    @Secured({"QUOTE_RESPONSIBILITY_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseRevokePatch patchLeadQuoteV1(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Patching quote responsibility for consent id");
        return quoteResponsibilityLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }

    @Post("/v2/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_RESPONSIBILITY_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuote createLeadQuoteV2(
            @Body QuoteRequestResponsibilityLeadV2 body,
            @NotNull HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote responsibility for client {}", clientId);


        var responseQuoteResponsibilityLead = quoteResponsibilityLeadService.createQuote(QuoteResponsibilityLeadEntity.fromRequestV2(body, clientId)).toResponse();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(responseQuoteResponsibilityLead::setLinks, responseQuoteResponsibilityLead::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, responseQuoteResponsibilityLead);

        return responseQuoteResponsibilityLead;
    }

    @Patch("/v2/lead/request/{consentId}")
    @Secured({"QUOTE_RESPONSIBILITY_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseRevokePatch patchLeadQuoteV2(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Patching quote responsibility for consent id");
        return quoteResponsibilityLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }
}
