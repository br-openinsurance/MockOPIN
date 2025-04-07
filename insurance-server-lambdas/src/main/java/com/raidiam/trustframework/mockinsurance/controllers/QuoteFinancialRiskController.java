package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.QuoteFinancialRiskLeadEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteRequestFinancialRiskLead;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteLead;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseRevokeQuotePatch;
import com.raidiam.trustframework.mockinsurance.models.generated.RevokeQuotePatchPayload;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/open-insurance/quote-financial-risk")
public class QuoteFinancialRiskController extends BaseInsuranceController {

    @Inject
    QuoteFinancialRiskLeadService quoteFinancialRiskLeadService;

    private static final Logger LOG = LoggerFactory.getLogger(QuoteFinancialRiskController.class);

    @Post("/v1/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"QUOTE_FINANCIAL_RISK_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuoteLead createLeadQuoteV1(
            @Body QuoteRequestFinancialRiskLead body,
            @NotNull HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote financial risk for client {}", clientId);

        var response = quoteFinancialRiskLeadService.createQuote(QuoteFinancialRiskLeadEntity.fromRequest(body, clientId)).toResponse();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);

        return response;
    }

    @Patch("/v1/lead/request/{consentId}")
    @Secured({"QUOTE_FINANCIAL_RISK_LEAD_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseRevokeQuotePatch patchLeadQuoteV1(@PathVariable("consentId") String consentId, @Body RevokeQuotePatchPayload body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Patching quote financial risk for consent id");
        return quoteFinancialRiskLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }
}
