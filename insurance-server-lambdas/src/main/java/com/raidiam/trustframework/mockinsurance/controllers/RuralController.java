package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.aop.LogInvocation;
import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseInsuranceResponseV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceRuralClaimsV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceRuralPolicyInfoV2;
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

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured({"RURAL_MANAGE"})
@Controller("/open-insurance/insurance-rural")
public class RuralController extends BaseInsuranceController {

    @Inject
    private RuralService ruralService;

    @Get("/v2/insurance-rural")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public BaseInsuranceResponseV2 getPoliciesV2(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        var response = ruralService.getPoliciesV2(callerInfo.getConsentId(), adjustedPageable);

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        
        return response;
    }

    @Get("/v2/insurance-rural/{policyId}/policy-info")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseInsuranceRuralPolicyInfoV2 getPolicyInfoV2(@PathVariable("policyId") UUID policyId, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        var response = ruralService.getPolicyInfoV2(policyId, callerInfo.getConsentId());

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());

        return response;
    }

    @Get("/v2/insurance-rural/{policyId}/premium")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseInsuranceRuralPremium getPremium(@PathVariable("policyId") UUID policyId, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        var response = ruralService.getPremium(policyId, callerInfo.getConsentId());

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());

        return response;
    }

    @Get("/v2/insurance-rural/{policyId}/claim")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseInsuranceRuralClaimsV2 getClaimsV2(@PathVariable("policyId") UUID policyId, HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);

        var response = ruralService.getClaimsV2(policyId, callerInfo.getConsentId(), adjustedPageable);

        InsuranceLambdaUtils.decorateResponse(response::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), response.getMeta().getTotalPages());
        
        return response;
    }

    


}
