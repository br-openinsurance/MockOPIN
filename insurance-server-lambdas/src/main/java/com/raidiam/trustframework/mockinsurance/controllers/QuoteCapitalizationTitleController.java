package com.raidiam.trustframework.mockinsurance.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.CapitalizationTitleRaffleEntity;
import com.raidiam.trustframework.mockinsurance.domain.QuoteCapitalizationTitleEntity;
import com.raidiam.trustframework.mockinsurance.domain.QuoteCapitalizationTitleLeadEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.PatchQuotePayload;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteRequestCapitalizationTitle;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteRequestCapitalizationTitleLead;
import com.raidiam.trustframework.mockinsurance.models.generated.RequestCapitalizationTitleRaffle;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseCapitalizationTitleRaffle;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteCapitalizationTitle;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteLead;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuotePatch;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseRevokeQuotePatch;
import com.raidiam.trustframework.mockinsurance.models.generated.RevokeQuotePatchPayload;
import com.raidiam.trustframework.mockinsurance.services.CapitalizationTitleRaffleService;
import com.raidiam.trustframework.mockinsurance.services.QuoteCapitalizationTitleLeadService;
import com.raidiam.trustframework.mockinsurance.services.QuoteCapitalizationTitleService;
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
@Controller("/open-insurance/quote-capitalization-title")
public class QuoteCapitalizationTitleController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(QuoteCapitalizationTitleController.class);

    private static final String REDIRECT_LINK = "https://www.raidiam.com/";

    @Inject
    QuoteCapitalizationTitleLeadService quoteCapitalizationTitleLeadService;

    @Inject
    QuoteCapitalizationTitleService quoteCapitalizationTitleService;

    @Inject
    CapitalizationTitleRaffleService capitalizationTitleRaffleService;

    @Post("/v1/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_CAPITALIZATION_TITLE_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuoteLead createLeadQuoteV1(@Body QuoteRequestCapitalizationTitleLead body, @NotNull HttpRequest<?> request) {
        
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Creating new quote capitalization title lead for client {}", callerInfo.getClientId());

        var response = quoteCapitalizationTitleLeadService.createQuote(QuoteCapitalizationTitleLeadEntity.fromRequest(body, callerInfo.getClientId())).toResponse();

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);

        return response;
    }

    @Patch("/v1/lead/request/{consentId}")
    @Secured({"QUOTE_CAPITALIZATION_TITLE_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseRevokeQuotePatch patchLeadQuoteV1(@PathVariable("consentId") String consentId, @Body RevokeQuotePatchPayload body, HttpRequest<?> request) {
        
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Patching quote capitalization title lead for consent id {}", consentId);

        return quoteCapitalizationTitleLeadService.patchQuote(body, consentId, callerInfo.getClientId()).toRevokePatchResponse();
    }

    @Post("/v1/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_CAPITALIZATION_TITLE_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    public ResponseQuoteCapitalizationTitle createQuoteV1(@Body QuoteRequestCapitalizationTitle body, HttpRequest<?> request) {
        
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Creating new quote capitalization title for client {}", callerInfo.getClientId());

        var response = quoteCapitalizationTitleService.createQuote(QuoteCapitalizationTitleEntity.fromRequest(body, callerInfo.getClientId())).toResponse();
        
        var selfLink = String.format("%s/open-insurance/quote-capitalization-title/v1/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, selfLink);
        
        return response;
    }

    @Get("/v1/request/{consentId}/quote-status")
    @Secured({"QUOTE_CAPITALIZATION_TITLE_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuoteCapitalizationTitle getQuoteV1(@PathVariable("consentId") String consentId, HttpRequest<?> request) {

        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching quote capitalization title for consent id {}", consentId);
        
        var response = quoteCapitalizationTitleService.getQuote(consentId, callerInfo.getClientId()).toResponse();
        
        var selfLink = String.format("%s/open-insurance/quote-capitalization-title/v1/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, selfLink);
        
        return response;
    }

    @Patch("/v1/request/{consentId}")
    @Secured({"QUOTE_CAPITALIZATION_TITLE_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuotePatch patchQuoteV1(@PathVariable("consentId") String consentId, @Body PatchQuotePayload body, HttpRequest<?> request) {
        
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Patching quote capitalization title for consent id {}", consentId);
        
        return quoteCapitalizationTitleService.patchQuote(body, consentId, callerInfo.getClientId()).toPatchResponse(REDIRECT_LINK);
    }

    @Post("/v1/raffle/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_CAPITALIZATION_TITLE_RAFFLE_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    public ResponseCapitalizationTitleRaffle createRaffleV1(@Body RequestCapitalizationTitleRaffle body, @NotNull HttpRequest<?> request){
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Creating new capitalization title raffle for client {}", callerInfo.getClientId());

        var response = capitalizationTitleRaffleService.createRaffle(CapitalizationTitleRaffleEntity.fromRequest(body, callerInfo.getClientId())).toResponse();

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);

        return response;
    }
}
