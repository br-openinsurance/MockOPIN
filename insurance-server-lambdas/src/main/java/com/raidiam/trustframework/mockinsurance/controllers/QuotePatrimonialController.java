package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.aop.LogInvocation;
import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.*;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.*;
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
@Controller("/open-insurance/quote-patrimonial")
public class QuotePatrimonialController extends BaseInsuranceController {

    private static final String REDIRECT_LINK = "https://www.raidiam.com/";

    @Inject
    private QuotePatrimonialBusinessService quotePatrimonialBusinessService;

    @Inject
    private QuotePatrimonialHomeService quotePatrimonialHomeService;

    @Inject
    private QuotePatrimonialCondominiumService quotePatrimonialCondominiumService;

    @Inject
    private QuotePatrimonialDiverseRisksService quotePatrimonialDiverseRisksService;

    @Inject
    private QuotePatrimonialLeadService quotePatrimonialLeadService;


    @Post("/v2/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_PATRIMONIAL_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuote createLeadQuoteV2(
            @Body QuoteRequestPatrimonialLeadV2 body,
            @NotNull HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");

        var response = quotePatrimonialLeadService.createQuote(QuotePatrimonialLeadEntity.fromRequestV2(body, clientId)).toResponse();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        return response;
    }

    @Patch("/v2/lead/request/{consentId}")
    @Secured({"QUOTE_PATRIMONIAL_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseRevokePatch patchLeadQuoteV2(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quotePatrimonialLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }

    @Post("/v2/business/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_PATRIMONIAL_BUSINESS_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuotePatrimonialBusinessV2 createBusinessQuoteV2(@Body QuoteRequestPatrimonialBusinessV2 body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        var resp = quotePatrimonialBusinessService.createQuote(QuotePatrimonialBusinessEntity.fromRequestV2(body, clientId)).toResponseV2();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v2/business/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Get("/v2/business/request/{consentId}/quote-status")
    @Secured({"QUOTE_PATRIMONIAL_BUSINESS_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuotePatrimonialBusinessV2 getBusinessQuoteV2(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        var resp = quotePatrimonialBusinessService.getQuote(consentId, clientId).toResponseV2();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v2/business/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Patch("/v2/business/request/{consentId}")
    @Secured({"QUOTE_PATRIMONIAL_BUSINESS_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponsePatch patchBusinessQuoteV2(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quotePatrimonialBusinessService.patchQuote(body, consentId, clientId).toPatchResponse(REDIRECT_LINK);
    }

    @Post("/v2/home/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_PATRIMONIAL_HOME_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuotePatrimonialHomeV2 createHomeQuoteV2(@Body QuoteRequestPatrimonialHomeV2 body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        var resp = quotePatrimonialHomeService.createQuote(QuotePatrimonialHomeEntity.fromRequestV2(body, clientId)).toResponseV2();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v2/home/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Get("/v2/home/request/{consentId}/quote-status")
    @Secured({"QUOTE_PATRIMONIAL_HOME_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuotePatrimonialHomeV2 getHomeQuoteV2(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        var resp = quotePatrimonialHomeService.getQuote(consentId, clientId).toResponseV2();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v2/home/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Patch("/v2/home/request/{consentId}")
    @Secured({"QUOTE_PATRIMONIAL_HOME_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponsePatch patchHomeQuoteV2(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quotePatrimonialHomeService.patchQuote(body, consentId, clientId).toPatchResponse(REDIRECT_LINK);
    }

    @Post("/v2/condominium/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_PATRIMONIAL_CONDOMINIUM_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuotePatrimonialCondominiumV2 createCondominiumQuoteV2(@Body QuoteRequestPatrimonialCondominiumV2 body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        var resp = quotePatrimonialCondominiumService.createQuote(QuotePatrimonialCondominiumEntity.fromRequestV2(body, clientId)).toResponseV2();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v2/condominium/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Get("/v2/condominium/request/{consentId}/quote-status")
    @Secured({"QUOTE_PATRIMONIAL_CONDOMINIUM_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuotePatrimonialCondominiumV2 getCondominiumQuoteV2(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        var resp = quotePatrimonialCondominiumService.getQuote(consentId, clientId).toResponseV2();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v2/condominium/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Patch("/v2/condominium/request/{consentId}")
    @Secured({"QUOTE_PATRIMONIAL_CONDOMINIUM_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponsePatch patchCondominiumQuoteV2(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quotePatrimonialCondominiumService.patchQuote(body, consentId, clientId).toPatchResponse(REDIRECT_LINK);
    }

    @Post("/v2/diverse-risks/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_PATRIMONIAL_DIVERSE_RISKS_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuotePatrimonialDiverseRisksV2 createDiverseRisksQuoteV2(@Body QuoteRequestPatrimonialDiverseRisksV2 body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        var resp = quotePatrimonialDiverseRisksService.createQuote(QuotePatrimonialDiverseRisksEntity.fromRequestV2(body, clientId)).toResponseV2();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v2/diverse-risks/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Get("/v2/diverse-risks/request/{consentId}/quote-status")
    @Secured({"QUOTE_PATRIMONIAL_DIVERSE_RISKS_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuotePatrimonialDiverseRisksV2 getDiverseRisksQuoteV2(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        var resp = quotePatrimonialDiverseRisksService.getQuote(consentId, clientId).toResponseV2();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v2/diverse-risks/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Patch("/v2/diverse-risks/request/{consentId}")
    @Secured({"QUOTE_PATRIMONIAL_DIVERSE_RISKS_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponsePatch patchDiverseRisksQuoteV2(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quotePatrimonialDiverseRisksService.patchQuote(body, consentId, clientId).toPatchResponse(REDIRECT_LINK);
    }
}
