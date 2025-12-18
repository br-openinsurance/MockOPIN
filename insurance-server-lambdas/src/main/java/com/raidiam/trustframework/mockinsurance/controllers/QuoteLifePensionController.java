package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.QuoteLifePensionLeadEntity;
import com.raidiam.trustframework.mockinsurance.domain.QuoteLifePensionEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.QuoteLifePensionLeadService;
import com.raidiam.trustframework.mockinsurance.services.QuoteLifePensionService;
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
@Controller("/open-insurance/contract-life-pension")
public class QuoteLifePensionController extends BaseInsuranceController {

    @Inject
    QuoteLifePensionLeadService quoteLifePensionLeadService;

    @Inject
    QuoteLifePensionService quoteLifePensionService;

    private static final Logger LOG = LoggerFactory.getLogger(QuoteLifePensionController.class);

    private static final String REDIRECT_LINK = "https://www.raidiam.com/";

    @Post("/v1/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_LIFE_PENSION_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuote createLeadQuoteV1(
            @Body QuoteRequestLifePensionLead body,
            @NotNull HttpRequest<?> request) {
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        LOG.info("Creating new quote life pension for client {}", clientId);
        var ResponseQuote = quoteLifePensionLeadService.createQuote(QuoteLifePensionLeadEntity.fromRequest(body, clientId)).toResponse();

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(ResponseQuote::setLinks, ResponseQuote::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, ResponseQuote);

        return ResponseQuote;
    }

    @Patch("/v1/lead/request/{consentId}")
    @Secured({"QUOTE_LIFE_PENSION_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseRevokePatch patchLeadQuoteV1(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote life pension for consent id");
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        return quoteLifePensionLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }

    @Post("/v1/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_LIFE_PENSION_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    public QuoteStatusLifePension createBusinessQuoteV1(@Body RequestContractLifePension body, HttpRequest<?> request) {
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        LOG.info("Creating new quote life pension for client {}", clientId);
        var resp = quoteLifePensionService.createQuote(QuoteLifePensionEntity.fromRequest(body, clientId)).toResponse();
        var selfLink = String.format("%s/open-insurance/contract-life-pension/v1/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Get("/v1/request/{consentId}/quote-status")
    @Secured({"QUOTE_LIFE_PENSION_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public QuoteStatusLifePension getBusinessQuoteV1(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        LOG.info("Fetching quote life pension for consent id {}", consentId);
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        var resp = quoteLifePensionService.getQuote(consentId, clientId).toResponse();
        var selfLink = String.format("%s/open-insurance/contract-life-pension/v1/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Patch("/v1/request/{consentId}")
    @Secured({"QUOTE_LIFE_PENSION_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponsePatchLifePension patchBusinessQuoteV1(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote life pension for consent id {}", consentId);
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        return quoteLifePensionService.patchQuote(body, consentId, clientId).toResponsePatchLifePension(REDIRECT_LINK);
    }













    @Post("/v2/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_LIFE_PENSION_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuote createLeadQuoteV2(
            @Body QuoteRequestLifePensionLeadV2 body,
            @NotNull HttpRequest<?> request) {
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        LOG.info("Creating new quote life pension for client {}", clientId);
        var resp = quoteLifePensionLeadService.createQuote(QuoteLifePensionLeadEntity.fromRequestV2(body, clientId)).toResponse();

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, resp);

        return resp;
    }

    @Patch("/v2/lead/request/{consentId}")
    @Secured({"QUOTE_LIFE_PENSION_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseRevokePatch patchLeadQuoteV2(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote life pension for consent id");
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        return quoteLifePensionLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }

    @Post("/v2/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_LIFE_PENSION_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    public QuoteStatusLifePensionV2 createBusinessQuoteV2(@Body RequestContractLifePensionV2 body, HttpRequest<?> request) {
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        LOG.info("Creating new quote life pension for client {}", clientId);
        var resp = quoteLifePensionService.createQuote(QuoteLifePensionEntity.fromRequestV2(body, clientId)).toResponseV2();
        var selfLink = String.format("%s/open-insurance/contract-life-pension/v2/request/%s/quote-status", appBaseUrl, body.getData().getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Get("/v2/request/{consentId}/quote-status")
    @Secured({"QUOTE_LIFE_PENSION_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public QuoteStatusLifePensionV2 getBusinessQuoteV2(@PathVariable("consentId") String consentId, HttpRequest<?> request) {
        LOG.info("Fetching quote life pension for consent id {}", consentId);
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        var resp = quoteLifePensionService.getQuote(consentId, clientId).toResponseV2();
        var selfLink = String.format("%s/open-insurance/contract-life-pension/v2/request/%s/quote-status", appBaseUrl, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, selfLink);
        return resp;
    }

    @Patch("/v2/request/{consentId}")
    @Secured({"QUOTE_LIFE_PENSION_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponsePatchLifePension patchBusinessQuoteV2(@PathVariable("consentId") String consentId, @Body PatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote life pension for consent id {}", consentId);
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        return quoteLifePensionService.patchQuote(body, consentId, clientId).toResponsePatchLifePension(REDIRECT_LINK);
    }
}
