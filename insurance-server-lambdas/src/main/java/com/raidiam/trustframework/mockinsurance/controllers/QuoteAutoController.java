package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.aop.LogInvocation;
import com.raidiam.trustframework.mockinsurance.models.generated.*;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.QuoteAutoEntity;
import com.raidiam.trustframework.mockinsurance.domain.QuoteAutoLeadEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.services.QuoteAutoLeadService;
import com.raidiam.trustframework.mockinsurance.services.QuoteAutoService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/open-insurance/quote-auto")
public class QuoteAutoController extends BaseInsuranceController {
    
    private static final String REDIRECT_LINK = "https://www.raidiam.com/";

    @Inject
    QuoteAutoLeadService quoteAutoLeadService;

    @Inject
    QuoteAutoService quoteAutoService;

    @Post("/v2/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_AUTO_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuote createLeadQuoteV2(@Body QuoteRequestAutoLeadV2 body, @NotNull HttpRequest<?> request) {

        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        var response = quoteAutoLeadService.createQuote(QuoteAutoLeadEntity.fromRequestV2(body, callerInfo.getClientId())).toResponse();

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());

        return response;
    }

    @Patch("/v2/lead/request/{consentId}")
    @Secured({"QUOTE_AUTO_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @LogInvocation
    public ResponseRevokePatch patchLeadQuoteV2(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {

        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        return quoteAutoLeadService.patchQuote(body, consentId, callerInfo.getClientId()).toRevokePatchResponse();
    }

    @Post("/v2/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_AUTO_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuoteAutoV2 createQuoteV2(@Body QuoteRequestAutoV2 body, HttpRequest<?> request) {

        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        var response = quoteAutoService.createQuote(QuoteAutoEntity.fromRequestV2(body, callerInfo.getClientId())).toResponseV2();

        var selfLink = String.format("%s/open-insurance/quote-auto/v2/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, selfLink);

        return response;
    }

    @Get("/v2/request/{consentId}/quote-status")
    @Secured({"QUOTE_AUTO_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @LogInvocation
    public ResponseQuoteAutoV2 getQuoteV2(@PathVariable("consentId") String consentId, HttpRequest<?> request) {

        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        var response = quoteAutoService.getQuote(consentId, callerInfo.getClientId()).toResponseV2();

        var selfLink = String.format("%s/open-insurance/quote-auto/v2/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, selfLink);

        return response;
    }

    @Patch("/v2/request/{consentId}")
    @Secured({"QUOTE_AUTO_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @LogInvocation
    public ResponsePatch patchQuoteV2(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {

        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        return quoteAutoService.patchQuote(body, consentId, callerInfo.getClientId()).toPatchResponse(REDIRECT_LINK);
    }
}
