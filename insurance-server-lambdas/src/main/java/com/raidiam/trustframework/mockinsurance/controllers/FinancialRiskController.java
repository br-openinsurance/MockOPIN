package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.FinancialRiskService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured({"FINANCIAL_RISK_MANAGE"})
@Controller("/open-insurance/insurance-financial-risk")
public class FinancialRiskController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(FinancialRiskController.class);

    @Inject
    private FinancialRiskService service;

    @Get("/v1/insurance-financial-risk")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public BaseInsuranceResponse getPolicies(Pageable pageable, @NotNull HttpRequest<?> request) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting policies for consent id {} v1", consentId);
        BaseInsuranceResponse response = service.getPolicies(pageable, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved policies for consent id {}", consentId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-financial-risk/{policyId}/policy-info")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceFinancialRiskPolicyInfo getPersonalQualifications(@NotNull HttpRequest<?> request,
                                                                              @PathVariable UUID policyId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting policy info for policy id {} v1", consentId);
        ResponseInsuranceFinancialRiskPolicyInfo response = service.getPolicyInfo(policyId, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved policy info for policy id {}", policyId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-financial-risk/{policyId}/premium")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceFinancialRiskPremium getPremium(@NotNull HttpRequest<?> request, @PathVariable UUID policyId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting premium for policy id {} v1", consentId);
        ResponseInsuranceFinancialRiskPremium response = service.getPolicyPremium(policyId, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved premium for policy id {}", policyId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-financial-risk/{policyId}/claim")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceFinancialRiskClaims getClaims(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                 @PathVariable UUID policyId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting claims for policy id {} v1", consentId);
        ResponseInsuranceFinancialRiskClaims response = service.getPolicyClaims(policyId, consentId, pageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved claims for policy id {}", policyId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }
}
