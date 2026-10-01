package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.aop.LogInvocation;
import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.QuoteFinancialRiskLeadEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.QuoteFinancialRiskLeadService;
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
@Controller("/open-insurance/quote-financial-risk")
public class QuoteFinancialRiskController extends BaseInsuranceController {

    @Inject
    QuoteFinancialRiskLeadService quoteFinancialRiskLeadService;

    @Post("/v1/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_FINANCIAL_RISK_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @LogInvocation
    public ResponseQuote createLeadQuoteV1(
            @Body QuoteRequestFinancialRiskLead body,
            @NotNull HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");

        var response = quoteFinancialRiskLeadService.createQuote(QuoteFinancialRiskLeadEntity.fromRequest(body, clientId)).toResponse();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());

        return response;
    }

    @Patch("/v1/lead/request/{consentId}")
    @Secured({"QUOTE_FINANCIAL_RISK_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @LogInvocation
    public ResponseRevokePatch patchLeadQuoteV1(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quoteFinancialRiskLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }

    @Post("/v2/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_FINANCIAL_RISK_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseQuote createLeadQuoteV2(
            @Body QuoteRequestFinancialRiskLeadV2 body,
            @NotNull HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");

        var response = quoteFinancialRiskLeadService.createQuote(QuoteFinancialRiskLeadEntity.fromRequestV2(body, clientId)).toResponse();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());

        return response;
    }

    @Patch("/v2/lead/request/{consentId}")
    @Secured({"QUOTE_FINANCIAL_RISK_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @LogInvocation
    public ResponseRevokePatch patchLeadQuoteV2(@PathVariable("consentId") String consentId, @Body RevokePatchPayload body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        return quoteFinancialRiskLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }
}
