package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.aop.LogInvocation;
import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.CapitalizationTitleRaffleEntity;
import com.raidiam.trustframework.mockinsurance.domain.QuoteCapitalizationTitleEntity;
import com.raidiam.trustframework.mockinsurance.domain.QuoteCapitalizationTitleLeadEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.CapitalizationTitleRaffleService;
import com.raidiam.trustframework.mockinsurance.services.QuoteCapitalizationTitleLeadService;
import com.raidiam.trustframework.mockinsurance.services.QuoteCapitalizationTitleService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/open-insurance/quote-capitalization-title")
public class QuoteCapitalizationTitleController extends BaseInsuranceController {

    private static final String REDIRECT_LINK = "https://www.raidiam.com/";

    @Inject
    QuoteCapitalizationTitleLeadService quoteCapitalizationTitleLeadService;

    @Inject
    QuoteCapitalizationTitleService quoteCapitalizationTitleService;

    @Inject
    CapitalizationTitleRaffleService capitalizationTitleRaffleService;

    @Post("/v2/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_CAPITALIZATION_TITLE_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuote createLeadQuoteV2(@Body QuoteRequestCapitalizationTitleLeadV2 body, @NotNull HttpRequest<?> request) {

        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        var response = quoteCapitalizationTitleLeadService.createQuote(QuoteCapitalizationTitleLeadEntity.fromRequestV2(body, callerInfo.getClientId())).toResponse();

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());

        return response;
    }

    @Patch("/v2/lead/request/{consentId}")
    @Secured({"QUOTE_CAPITALIZATION_TITLE_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseRevokePatch patchLeadQuoteV2(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {

        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        return quoteCapitalizationTitleLeadService.patchQuote(body, consentId, callerInfo.getClientId()).toRevokePatchResponse();
    }

    @Post("/v2/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_CAPITALIZATION_TITLE_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuoteCapitalizationTitleV2 createQuoteV2(@Body QuoteRequestCapitalizationTitleV2 body, HttpRequest<?> request) {

        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        var response = quoteCapitalizationTitleService.createQuote(QuoteCapitalizationTitleEntity.fromRequestV2(body, callerInfo.getClientId())).toResponseV2();

        var selfLink = String.format("%s/open-insurance/quote-capitalization-title/v2/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, selfLink);

        return response;
    }

    @Get("/v2/request/{consentId}/quote-status")
    @Secured({"QUOTE_CAPITALIZATION_TITLE_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuoteCapitalizationTitleV2 getQuoteV2(@PathVariable("consentId") String consentId, HttpRequest<?> request) {

        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        var response = quoteCapitalizationTitleService.getQuote(consentId, callerInfo.getClientId()).toResponseV2();

        var selfLink = String.format("%s/open-insurance/quote-capitalization-title/v2/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, selfLink);

        return response;
    }

    @Patch("/v2/request/{consentId}")
    @Secured({"QUOTE_CAPITALIZATION_TITLE_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponsePatchRedirectLink patchQuoteV2(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {

        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        return quoteCapitalizationTitleService.patchQuote(body, consentId, callerInfo.getClientId()).toRedirectLinkPatchResponse(REDIRECT_LINK);
    }

    @Post("/v2/raffle/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_CAPITALIZATION_TITLE_RAFFLE_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @Idempotent
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseCapitalizationTitleRaffle createRaffleV2(@Body RequestCapitalizationTitleRaffle body, @NotNull HttpRequest<?> request){
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        var response = capitalizationTitleRaffleService.createRaffle(CapitalizationTitleRaffleEntity.fromRequest(body, callerInfo.getClientId()), callerInfo.getConsentId()).toResponse();

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());

        return response;
    }
}
