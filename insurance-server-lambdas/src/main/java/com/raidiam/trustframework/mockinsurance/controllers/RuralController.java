package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseInsuranceResponse;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceRuralClaims;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceRuralPolicyInfo;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceRuralPremium;
import com.raidiam.trustframework.mockinsurance.services.RuralService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;

import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured({"RURAL_MANAGE"})
@Controller("/open-insurance/insurance-rural")
public class RuralController extends BaseInsuranceController {

    @Inject
    private RuralService ruralService;

    private static final Logger LOG = LoggerFactory.getLogger(RuralController.class);

    @Get("/v1/insurance-rural")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public BaseInsuranceResponse getPoliciesV1(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching rural policies for client {}", callerInfo.getClientId());

        var response = ruralService.getPolicies(callerInfo.getConsentId(), adjustedPageable);

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);
        
        return response;
    }

    @Get("/v1/insurance-rural/{policyId}/policy-info")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceRuralPolicyInfo getPolicyInfoV1(@PathVariable("policyId") UUID policyId, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching rural policy info for client {}", callerInfo.getClientId());

        var response = ruralService.getPolicyInfo(policyId, callerInfo.getConsentId());

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);

        return response;
    }

    @Get("/v1/insurance-rural/{policyId}/premium")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceRuralPremium getPremiumV1(@PathVariable("policyId") UUID policyId, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching rural policy premium for client {}", callerInfo.getClientId());

        var response = ruralService.getPremium(policyId, callerInfo.getConsentId());

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);

        return response;
    }

    @Get("/v1/insurance-rural/{policyId}/claim")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceRuralClaims getClaimsV1(@PathVariable("policyId") UUID policyId, HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching rural policy claims for client {}", callerInfo.getClientId());

        var response = ruralService.getClaims(policyId, callerInfo.getConsentId(), adjustedPageable);

        InsuranceLambdaUtils.decorateResponse(response::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), response.getMeta().getTotalPages());
        InsuranceLambdaUtils.logObject(mapper, response);
        
        return response;
    }

    


}
