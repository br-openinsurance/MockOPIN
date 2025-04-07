package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.QuoteRuralLeadEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteRequestRuralLead;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteLead;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseRevokeQuotePatch;
import com.raidiam.trustframework.mockinsurance.models.generated.RevokeQuotePatchPayload;
import com.raidiam.trustframework.mockinsurance.services.QuoteRuralLeadService;
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
@Controller("/open-insurance/quote-rural")
@Secured({"QUOTE_RURAL_LEAD_MANAGE"})
public class QuoteRuralController extends BaseInsuranceController {

    @Inject
    QuoteRuralLeadService quoteRuralLeadService;

    private static final Logger LOG = LoggerFactory.getLogger(QuoteRuralController.class);

    @Post("/v1/lead/request")
    @Status(HttpStatus.CREATED)
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseQuoteLead createLeadQuoteV1(@Body QuoteRequestRuralLead body, @NotNull HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Creating new quote rural for client {}", clientId);

        var resp = quoteRuralLeadService.createQuote(QuoteRuralLeadEntity.fromRequest(body, clientId)).toResponse();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, resp);

        return resp;
    }

    @Patch("/v1/lead/request/{consentId}")
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public ResponseRevokeQuotePatch patchLeadQuoteV1(@PathVariable("consentId") String consentId, @Body RevokeQuotePatchPayload body, HttpRequest<?> request) {
        String clientId = (String) request.getAttribute("clientId").orElse("");
        LOG.info("Patching quote rural for consent id {}", consentId);
        return quoteRuralLeadService.patchQuote(body, consentId, clientId).toRevokePatchResponse();
    }
}