package com.raidiam.trustframework.mockinsurance.controllers;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/open-insurance/quote-patrimonial")
public class QuotePatrimonialController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(QuotePatrimonialController.class);

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


    @Post("/v1/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_PATRIMONIAL_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuote createLeadQuoteV1(
            @Body QuoteRequestPatrimonialLead body,
            @NotNull HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote patrimonial lead for client {}", clientId);

        var response = quotePatrimonialLeadService.createQuote(QuotePatrimonialLeadEntity.fromRequest(body, clientId)).toResponse();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Patch("/v1/lead/request/{consentId}")
    @Secured({"QUOTE_PATRIMONIAL_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseRevokePatch patchLeadQuoteV1(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote patrimonial lead for consent id");
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quotePatrimonialLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }

    @Post("/v1/business/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_PATRIMONIAL_BUSINESS_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    public ResponseQuotePatrimonialBusiness createBusinessQuoteV1(@Body QuoteRequestPatrimonialBusiness body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote patrimonial business for client {}", clientId);
        var resp = quotePatrimonialBusinessService.createQuote(QuotePatrimonialBusinessEntity.fromRequest(body, clientId)).toResponse();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v1/business/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Get("/v1/business/request/{consentId}/quote-status")
    @Secured({"QUOTE_PATRIMONIAL_BUSINESS_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuotePatrimonialBusiness getBusinessQuoteV1(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        LOG.info("Fetching quote patrimonial business for consent id {}", consentId);
        String clientId = (String) request.getAttribute("clientId").orElse("");
        var resp = quotePatrimonialBusinessService.getQuote(consentId, clientId).toResponse();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v1/business/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Patch("/v1/business/request/{consentId}")
    @Secured({"QUOTE_PATRIMONIAL_BUSINESS_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponsePatch patchBusinessQuoteV1(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote patrimonial business for consent id {}", consentId);
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quotePatrimonialBusinessService.patchQuote(body, consentId, clientId).toPatchResponse(REDIRECT_LINK);
    }

    @Post("/v1/home/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_PATRIMONIAL_HOME_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    public ResponseQuotePatrimonialHome createHomeQuoteV1(@Body QuoteRequestPatrimonialHome body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote patrimonial home for client {}", clientId);
        var resp = quotePatrimonialHomeService.createQuote(QuotePatrimonialHomeEntity.fromRequest(body, clientId)).toResponse();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v1/home/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Get("/v1/home/request/{consentId}/quote-status")
    @Secured({"QUOTE_PATRIMONIAL_HOME_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuotePatrimonialHome getHomeQuoteV1(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        LOG.info("Fetching quote patrimonial home for consent id {}", consentId);
        String clientId = (String) request.getAttribute("clientId").orElse("");
        var resp = quotePatrimonialHomeService.getQuote(consentId, clientId).toResponse();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v1/home/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Patch("/v1/home/request/{consentId}")
    @Secured({"QUOTE_PATRIMONIAL_HOME_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponsePatch patchHomeQuoteV1(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote patrimonial home for consent id {}", consentId);
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quotePatrimonialHomeService.patchQuote(body, consentId, clientId).toPatchResponse(REDIRECT_LINK);
    }

    @Post("/v1/condominium/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_PATRIMONIAL_CONDOMINIUM_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    public ResponseQuotePatrimonialCondominium createCondominiumQuoteV1(@Body QuoteRequestPatrimonialCondominium body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote patrimonial condominium for client {}", clientId);
        var resp = quotePatrimonialCondominiumService.createQuote(QuotePatrimonialCondominiumEntity.fromRequest(body, clientId)).toResponse();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v1/condominium/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Get("/v1/condominium/request/{consentId}/quote-status")
    @Secured({"QUOTE_PATRIMONIAL_CONDOMINIUM_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuotePatrimonialCondominium getCondominiumQuoteV1(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        LOG.info("Fetching quote patrimonial condominium for consent id {}", consentId);
        String clientId = (String) request.getAttribute("clientId").orElse("");
        var resp = quotePatrimonialCondominiumService.getQuote(consentId, clientId).toResponse();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v1/condominium/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Patch("/v1/condominium/request/{consentId}")
    @Secured({"QUOTE_PATRIMONIAL_CONDOMINIUM_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponsePatch patchCondominiumQuoteV1(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote patrimonial condominium for consent id {}", consentId);
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quotePatrimonialCondominiumService.patchQuote(body, consentId, clientId).toPatchResponse(REDIRECT_LINK);
    }

    @Post("/v1/diverse-risks/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_PATRIMONIAL_DIVERSE_RISKS_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    public ResponseQuotePatrimonialDiverseRisks createDiverseRisksQuoteV1(@Body QuoteRequestPatrimonialDiverseRisks body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote patrimonial diverse risks for client {}", clientId);
        var resp = quotePatrimonialDiverseRisksService.createQuote(QuotePatrimonialDiverseRisksEntity.fromRequest(body, clientId)).toResponse();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v1/diverse-risks/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Get("/v1/diverse-risks/request/{consentId}/quote-status")
    @Secured({"QUOTE_PATRIMONIAL_DIVERSE_RISKS_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuotePatrimonialDiverseRisks getDiverseRisksQuoteV1(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        LOG.info("Fetching quote patrimonial diverse risks for consent id {}", consentId);
        String clientId = (String) request.getAttribute("clientId").orElse("");
        var resp = quotePatrimonialDiverseRisksService.getQuote(consentId, clientId).toResponse();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v1/diverse-risks/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Patch("/v1/diverse-risks/request/{consentId}")
    @Secured({"QUOTE_PATRIMONIAL_DIVERSE_RISKS_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponsePatch patchDiverseRisksQuoteV1(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote patrimonial diverse risks for consent id {}", consentId);
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quotePatrimonialDiverseRisksService.patchQuote(body, consentId, clientId).toPatchResponse(REDIRECT_LINK);
    }

    @Post("/v2/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_PATRIMONIAL_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    public ResponseQuote createLeadQuoteV2(
            @Body QuoteRequestPatrimonialLeadV2 body,
            @NotNull HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote patrimonial lead for client {}", clientId);

        var response = quotePatrimonialLeadService.createQuote(QuotePatrimonialLeadEntity.fromRequestV2(body, clientId)).toResponse();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Patch("/v2/lead/request/{consentId}")
    @Secured({"QUOTE_PATRIMONIAL_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    public ResponseRevokePatch patchLeadQuoteV2(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote patrimonial lead for consent id");
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
    public ResponseQuotePatrimonialBusinessV2 createBusinessQuoteV2(@Body QuoteRequestPatrimonialBusinessV2 body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote patrimonial business for client {}", clientId);
        var resp = quotePatrimonialBusinessService.createQuote(QuotePatrimonialBusinessEntity.fromRequestV2(body, clientId)).toResponseV2();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v1/business/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Get("/v2/business/request/{consentId}/quote-status")
    @Secured({"QUOTE_PATRIMONIAL_BUSINESS_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    public ResponseQuotePatrimonialBusinessV2 getBusinessQuoteV2(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        LOG.info("Fetching quote patrimonial business for consent id {}", consentId);
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
    public ResponsePatch patchBusinessQuoteV2(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote patrimonial business for consent id {}", consentId);
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
    public ResponseQuotePatrimonialHomeV2 createHomeQuoteV2(@Body QuoteRequestPatrimonialHomeV2 body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote patrimonial home for client {}", clientId);
        var resp = quotePatrimonialHomeService.createQuote(QuotePatrimonialHomeEntity.fromRequestV2(body, clientId)).toResponseV2();
        var selfLink = String.format("%s/open-insurance/quote-patrimonial/v1/home/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Get("/v2/home/request/{consentId}/quote-status")
    @Secured({"QUOTE_PATRIMONIAL_HOME_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    public ResponseQuotePatrimonialHomeV2 getHomeQuoteV2(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        LOG.info("Fetching quote patrimonial home for consent id {}", consentId);
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
    public ResponsePatch patchHomeQuoteV2(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote patrimonial home for consent id {}", consentId);
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
    public ResponseQuotePatrimonialCondominiumV2 createCondominiumQuoteV2(@Body QuoteRequestPatrimonialCondominiumV2 body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote patrimonial condominium for client {}", clientId);
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
    public ResponseQuotePatrimonialCondominiumV2 getCondominiumQuoteV2(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        LOG.info("Fetching quote patrimonial condominium for consent id {}", consentId);
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
    public ResponsePatch patchCondominiumQuoteV2(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote patrimonial condominium for consent id {}", consentId);
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
    public ResponseQuotePatrimonialDiverseRisksV2 createDiverseRisksQuoteV2(@Body QuoteRequestPatrimonialDiverseRisksV2 body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote patrimonial diverse risks for client {}", clientId);
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
    public ResponseQuotePatrimonialDiverseRisksV2 getDiverseRisksQuoteV2(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        LOG.info("Fetching quote patrimonial diverse risks for consent id {}", consentId);
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
    public ResponsePatch patchDiverseRisksQuoteV2(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote patrimonial diverse risks for consent id {}", consentId);
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quotePatrimonialDiverseRisksService.patchQuote(body, consentId, clientId).toPatchResponse(REDIRECT_LINK);
    }
}
