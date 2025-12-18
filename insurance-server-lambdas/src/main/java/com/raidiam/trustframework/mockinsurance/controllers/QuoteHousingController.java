package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.QuoteHousingLeadEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.QuoteHousingLeadService;
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
@Controller("/open-insurance/quote-housing")
public class QuoteHousingController extends BaseInsuranceController {

    @Inject
    QuoteHousingLeadService quoteHousingLeadService;

    private static final Logger LOG = LoggerFactory.getLogger(QuoteHousingController.class);

    @Post("/v1/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_HOUSING_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuote createLeadQuoteV1(
            @Body QuoteRequestHousingLead body,
            @NotNull HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote financial risk for client {}", clientId);
        var ResponseQuote = quoteHousingLeadService.createQuote(QuoteHousingLeadEntity.fromRequest(body, clientId)).toResponse();

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(ResponseQuote::setLinks, ResponseQuote::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, ResponseQuote);

        return ResponseQuote;
    }

    @Patch("/v1/lead/request/{consentId}")
    @Secured({"QUOTE_HOUSING_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseRevokePatch patchLeadQuoteV1(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote financial risk for consent id");
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quoteHousingLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }

    @Post("/v2/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_HOUSING_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuote createLeadQuoteV2(
            @Body QuoteRequestHousingLeadV2 body,
            @NotNull HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote financial risk for client {}", clientId);
        var resp = quoteHousingLeadService.createQuote(QuoteHousingLeadEntity.fromRequestV2(body, clientId)).toResponse();

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, resp);

        return resp;
    }

    @Patch("/v2/lead/request/{consentId}")
    @Secured({"QUOTE_HOUSING_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseRevokePatch patchLeadQuoteV2(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote financial risk for consent id");
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quoteHousingLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }
}
