package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.QuotePersonLifeEntity;
import com.raidiam.trustframework.mockinsurance.domain.QuotePersonLeadEntity;
import com.raidiam.trustframework.mockinsurance.domain.QuotePersonTravelEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.QuotePersonLeadService;
import com.raidiam.trustframework.mockinsurance.services.QuotePersonLifeService;
import com.raidiam.trustframework.mockinsurance.services.QuotePersonTravelService;
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
@Controller("/open-insurance/quote-person")
public class QuotePersonController extends BaseInsuranceController {

    @Inject
    QuotePersonLeadService quotePersonLeadService;

    @Inject
    QuotePersonLifeService quotePersonLifeService;

    @Inject
    QuotePersonTravelService quotePersonTravelService;

    private static final Logger LOG = LoggerFactory.getLogger(QuotePersonController.class);

    private static final String REDIRECT_LINK = "https://www.raidiam.com/";

    @Post("/v1/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_PERSON_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuote createLeadQuoteV1(
            @Body QuoteRequestPersonLead body,
            @NotNull HttpRequest<?> request) {
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        LOG.info("Creating new quote person lead for client {}", clientId);
        var ResponseQuote = quotePersonLeadService.createQuote(QuotePersonLeadEntity.fromRequest(body, clientId)).toResponse();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(ResponseQuote::setLinks, ResponseQuote::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, ResponseQuote);

        return ResponseQuote;
    }

    @Patch("/v1/lead/request/{consentId}")
    @Secured({"QUOTE_PERSON_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseRevokePatch patchLeadQuoteV1(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote person lead for consent id");
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        return quotePersonLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }

    @Post("/v1/life/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_PERSON_LIFE_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    public ResponseQuoteStatusPersonLife createQuoteV1(@Body QuoteRequestPersonLife body, HttpRequest<?> request) {
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        LOG.info("Creating new quote person life for client {}", clientId);
        var resp = quotePersonLifeService.createQuote(QuotePersonLifeEntity.fromRequest(body, clientId)).toResponse();
        var selfLink = String.format("%s/open-insurance/quote-person/v1/life/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Get("/v1/life/request/{consentId}/quote-status")
    @Secured({"QUOTE_PERSON_LIFE_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuoteStatusPersonLife getQuoteV1(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        LOG.info("Fetching quote person life for consent id {}", consentId);
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        var resp = quotePersonLifeService.getQuote(consentId, clientId).toResponse();
        var selfLink = String.format("%s/open-insurance/quote-person/v1/life/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Patch("/v1/life/request/{consentId}")
    @Secured({"QUOTE_PERSON_LIFE_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponsePatch patchQuoteV1(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote person life for consent id {}", consentId);
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        return quotePersonLifeService.patchQuote(body, consentId, clientId).toPatchResponse(REDIRECT_LINK);
    }

    @Post("/v1/travel/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_PERSON_TRAVEL_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    public ResponseQuoteStatusPersonTravel createTravelQuoteV1(@Body QuoteRequestPersonTravel body, HttpRequest<?> request) {
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        LOG.info("Creating new quote person travel for client {}", clientId);
        var resp = quotePersonTravelService.createQuote(QuotePersonTravelEntity.fromRequest(body, clientId)).toResponse();
        var selfLink = String.format("%s/open-insurance/quote-person/v1/travel/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Get("/v1/travel/request/{consentId}/quote-status")
    @Secured({"QUOTE_PERSON_TRAVEL_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuoteStatusPersonTravel getTravelQuoteV1(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        LOG.info("Fetching quote person travel for consent id {}", consentId);
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        var resp = quotePersonTravelService.getQuote(consentId, clientId).toResponse();
        var selfLink = String.format("%s/open-insurance/quote-person/v1/travel/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Patch("/v1/travel/request/{consentId}")
    @Secured({"QUOTE_PERSON_TRAVEL_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponsePatch patchTravelQuoteV1(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote person travel for consent id {}", consentId);
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        return quotePersonTravelService.patchQuote(body, consentId, clientId).toPatchResponse(REDIRECT_LINK);
    }
}
