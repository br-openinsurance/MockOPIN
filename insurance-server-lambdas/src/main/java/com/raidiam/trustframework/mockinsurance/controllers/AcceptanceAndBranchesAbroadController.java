package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseInsuranceResponse;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseInsuranceResponseV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceAcceptanceAndBranchesAbroadClaims;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceAcceptanceAndBranchesAbroadClaimsV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceAcceptanceAndBranchesAbroadPolicyInfo;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceAcceptanceAndBranchesAbroadPolicyInfoV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceAcceptanceAndBranchesAbroadPremium;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceAcceptanceAndBranchesAbroadPremiumV2;
import com.raidiam.trustframework.mockinsurance.services.AcceptanceAndBranchesAbroadService;
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
@Secured({"ACCEPTANCE_AND_BRANCHES_ABROAD_MANAGE"})
@Controller("/open-insurance/insurance-acceptance-and-branches-abroad")
public class AcceptanceAndBranchesAbroadController extends BaseInsuranceController {

    @Inject
    private AcceptanceAndBranchesAbroadService acceptanceAndBranchesAbroadService;

    private static final Logger LOG = LoggerFactory.getLogger(AcceptanceAndBranchesAbroadController.class);

    @Get("/v1/insurance-acceptance-and-branches-abroad")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public BaseInsuranceResponse getPoliciesV1(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching acceptance and branches abroad policies for client {}", callerInfo.getClientId());

        var response = acceptanceAndBranchesAbroadService.getPolicies(callerInfo.getConsentId(), adjustedPageable);

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);
        
        return response;
    }

    @Get("/v2/insurance-acceptance-and-branches-abroad")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public BaseInsuranceResponseV2 getPoliciesV2(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching acceptance and branches abroad policies for client {}", callerInfo.getClientId());

        var response = acceptanceAndBranchesAbroadService.getPoliciesV2(callerInfo.getConsentId(), adjustedPageable);

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);
        
        return response;
    }

    @Get("/v1/insurance-acceptance-and-branches-abroad/{policyId}/policy-info")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceAcceptanceAndBranchesAbroadPolicyInfo getPolicyInfoV1(@PathVariable("policyId") UUID policyId, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching acceptance and branches abroad policy info for client {}", callerInfo.getClientId());

        var response = acceptanceAndBranchesAbroadService.getPolicyInfo(policyId, callerInfo.getConsentId());

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);

        return response;
    }

    @Get("/v2/insurance-acceptance-and-branches-abroad/{policyId}/policy-info")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceAcceptanceAndBranchesAbroadPolicyInfoV2 getPolicyInfoV2(@PathVariable("policyId") UUID policyId, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching acceptance and branches abroad policy info for client {}", callerInfo.getClientId());

        var response = acceptanceAndBranchesAbroadService.getPolicyInfoV2(policyId, callerInfo.getConsentId());

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);

        return response;
    }

    @Get("/v1/insurance-acceptance-and-branches-abroad/{policyId}/premium")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceAcceptanceAndBranchesAbroadPremium getPremiumV1(@PathVariable("policyId") UUID policyId, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching acceptance and branches abroad policy premium for client {}", callerInfo.getClientId());

        var response = acceptanceAndBranchesAbroadService.getPremium(policyId, callerInfo.getConsentId());

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);

        return response;
    }

    @Get("/v2/insurance-acceptance-and-branches-abroad/{policyId}/premium")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceAcceptanceAndBranchesAbroadPremiumV2 getPremiumV2(@PathVariable("policyId") UUID policyId, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching acceptance and branches abroad policy premium for client {}", callerInfo.getClientId());

        var response = acceptanceAndBranchesAbroadService.getPremiumV2(policyId, callerInfo.getConsentId());

        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);

        return response;
    }

    @Get("/v1/insurance-acceptance-and-branches-abroad/{policyId}/claim")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceAcceptanceAndBranchesAbroadClaims getClaimsV1(@PathVariable("policyId") UUID policyId, HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching acceptance and branches abroad policy claims for client {}", callerInfo.getClientId());

        var response = acceptanceAndBranchesAbroadService.getClaims(policyId, callerInfo.getConsentId(), adjustedPageable);

        InsuranceLambdaUtils.decorateResponse(response::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), response.getMeta().getTotalPages());
        InsuranceLambdaUtils.logObject(mapper, response);
        
        return response;
    }

    @Get("/v2/insurance-acceptance-and-branches-abroad/{policyId}/claim")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceAcceptanceAndBranchesAbroadClaimsV2 getClaimsV2(@PathVariable("policyId") UUID policyId, HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching acceptance and branches abroad policy claims for client {}", callerInfo.getClientId());

        var response = acceptanceAndBranchesAbroadService.getClaimsV2(policyId, callerInfo.getConsentId(), adjustedPageable);

        InsuranceLambdaUtils.decorateResponse(response::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), response.getMeta().getTotalPages());
        InsuranceLambdaUtils.logObject(mapper, response);
        
        return response;
    }

    


}
