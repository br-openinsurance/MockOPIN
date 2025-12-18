package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.QuoteAcceptanceAndBranchesAbroadLeadEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.QuoteAcceptanceAndBranchesAbroadLeadService;
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
@Secured({"QUOTE_ACCEPTANCE_AND_BRANCHES_ABROAD_LEAD_MANAGE"})
@Controller("/open-insurance/quote-acceptance-and-branches-abroad")
public class QuoteAcceptanceAndBranchesAbroadController extends BaseInsuranceController {

    @Inject
    private QuoteAcceptanceAndBranchesAbroadLeadService quoteAcceptanceAndBranchesAbroadLeadService;

    private static final Logger LOG = LoggerFactory.getLogger(QuoteAcceptanceAndBranchesAbroadController.class);

    @Post("/v1/lead/request")
    @Status(HttpStatus.CREATED)
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuote createLeadQuoteV1(
            @Body QuoteRequestAcceptanceAndBranchesAbroadLead body,
            @NotNull HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote acceptance and branches abroad lead for client {}", clientId);
        var response = quoteAcceptanceAndBranchesAbroadLeadService.createQuote(QuoteAcceptanceAndBranchesAbroadLeadEntity.fromRequest(body, clientId)).toResponse();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Patch("/v1/lead/request/{consentId}")
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseRevokePatch patchLeadQuoteV1(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote acceptance and branches abroad lead for consent id");
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quoteAcceptanceAndBranchesAbroadLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }

    @Post("/v2/lead/request")
    @Status(HttpStatus.CREATED)
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    public ResponseQuote createLeadQuoteV2(
            @Body QuoteRequestAcceptanceAndBranchesAbroadLeadV2 body,
            @NotNull HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote acceptance and branches abroad lead for client {}", clientId);
        var response = quoteAcceptanceAndBranchesAbroadLeadService.createQuote(QuoteAcceptanceAndBranchesAbroadLeadEntity.fromRequestV2(body, clientId)).toResponse();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Patch("/v2/lead/request/{consentId}")
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    public ResponseRevokePatch patchLeadQuoteV2(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {
        LOG.info("Patching quote acceptance and branches abroad lead for consent id");
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quoteAcceptanceAndBranchesAbroadLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }
}
