package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseInsuranceResponse;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsurancePatrimonialClaims;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsurancePatrimonialPolicyInfo;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsurancePatrimonialPremium;
import com.raidiam.trustframework.mockinsurance.services.PatrimonialService;
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
@Secured({"PATRIMONIAL_MANAGE"})
@Controller("/open-insurance/insurance-patrimonial")
public class PatrimonialController extends BaseInsuranceController {

    @Inject
    private PatrimonialService patrimonialService;

    private static final Logger LOG = LoggerFactory.getLogger(PatrimonialController.class);

    @Get("/v1/insurance-patrimonial")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public BaseInsuranceResponse getPoliciesV1(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching patrimonial policies for client {}", callerInfo.getClientId());

        var response = patrimonialService.getPolicies(callerInfo.getConsentId(), adjustedPageable);

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);
        
        return response;
    }

    @Get("/v1/insurance-patrimonial/{policyId}/policy-info")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePatrimonialPolicyInfo getPolicyInfoV1(@PathVariable("policyId") UUID policyId, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching patrimonial policy info for client {}", callerInfo.getClientId());

        var response = patrimonialService.getPolicyInfo(policyId, callerInfo.getConsentId());

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);

        return response;
    }

    @Get("/v1/insurance-patrimonial/{policyId}/premium")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePatrimonialPremium getPremiumV1(@PathVariable("policyId") UUID policyId, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching patrimonial policy premium for client {}", callerInfo.getClientId());

        var response = patrimonialService.getPremium(policyId, callerInfo.getConsentId());

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);

        return response;
    }

    @Get("/v1/insurance-patrimonial/{policyId}/claim")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePatrimonialClaims getClaimsV1(@PathVariable("policyId") UUID policyId, HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching patrimonial policy claims for client {}", callerInfo.getClientId());

        var response = patrimonialService.getClaims(policyId, callerInfo.getConsentId(), adjustedPageable);

        InsuranceLambdaUtils.decorateResponse(response::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), response.getMeta().getTotalPages());
        InsuranceLambdaUtils.logObject(mapper, response);
        
        return response;
    }

    


}
