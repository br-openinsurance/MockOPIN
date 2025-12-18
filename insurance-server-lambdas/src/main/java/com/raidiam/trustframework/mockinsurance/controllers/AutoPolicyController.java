package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseInsuranceResponse;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseInsuranceResponseV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceAutoClaims;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceAutoClaimsV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceAutoPolicyInfo;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceAutoPolicyInfoV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceAutoPremium;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceAutoPremiumV2;
import com.raidiam.trustframework.mockinsurance.services.AutoPolicyService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/open-insurance/insurance-auto")
@Secured({"AUTO_MANAGE"})
public class AutoPolicyController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(AutoPolicyController.class);

    @Inject
    AutoPolicyService autoPolicyService;

    @Get("/v1/insurance-auto")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public BaseInsuranceResponse getPolicies(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching auto polices for client {}", meta.getClientId());

        var resp = autoPolicyService.getPolicies(meta.getConsentId(), adjustedPageable);
        
        
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }

    @Get("/v2/insurance-auto")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public BaseInsuranceResponseV2 getPoliciesV2(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching auto polices for client {}", meta.getClientId());

        var resp = autoPolicyService.getPoliciesV2(meta.getConsentId(), adjustedPageable);
        
        
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }

    @Get("/v1/insurance-auto/{policyId}/policy-info")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceAutoPolicyInfo getPolicyInfo(HttpRequest<?> request, @PathVariable("policyId") String policyId) {
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching auto policy info for client {}", meta.getClientId());
        var resp = autoPolicyService.getPolicyInfo(policyId, meta.getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }

    @Get("/v2/insurance-auto/{policyId}/policy-info")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceAutoPolicyInfoV2 getPolicyInfoV2(HttpRequest<?> request, @PathVariable("policyId") String policyId) {
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching auto policy info for client {}", meta.getClientId());
        var resp = autoPolicyService.getPolicyInfoV2(policyId, meta.getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }

    @Get("/v1/insurance-auto/{policyId}/premium")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceAutoPremium getPolicyPremium(HttpRequest<?> request, @PathVariable("policyId") String policyId) {
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching auto policy premium for client {}", meta.getClientId());
        var resp = autoPolicyService.getPolicyPremium(policyId, meta.getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }

    @Get("/v2/insurance-auto/{policyId}/premium")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceAutoPremiumV2 getPolicyPremiumV2(HttpRequest<?> request, @PathVariable("policyId") String policyId) {
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching auto policy premium for client {}", meta.getClientId());
        var resp = autoPolicyService.getPolicyPremiumV2(policyId, meta.getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }

    @Get("/v1/insurance-auto/{policyId}/claim")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceAutoClaims getPolicyClaims(HttpRequest<?> request, @PathVariable("policyId") String policyId, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching auto policy claims for client {}", meta.getClientId());
        var resp = autoPolicyService.getPolicyClaims(policyId, meta.getConsentId(), adjustedPageable);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }

    @Get("/v2/insurance-auto/{policyId}/claim")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceAutoClaimsV2 getPolicyClaimsV2(HttpRequest<?> request, @PathVariable("policyId") String policyId, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching auto policy claims for client {}", meta.getClientId());
        var resp = autoPolicyService.getPolicyClaimsV2(policyId, meta.getConsentId(), adjustedPageable);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }
}
