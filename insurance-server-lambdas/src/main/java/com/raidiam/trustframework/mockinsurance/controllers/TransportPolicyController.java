package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseInsuranceResponse;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseInsuranceResponseV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceTransportClaims;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceTransportClaimsV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceTransportPolicyInfo;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceTransportPolicyInfoV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceTransportPremium;
import com.raidiam.trustframework.mockinsurance.services.TransportPolicyService;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/open-insurance/insurance-transport")
@Secured({"TRANSPORT_MANAGE"})
public class TransportPolicyController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(TransportPolicyController.class);

    @Inject
    TransportPolicyService transportPolicyService;

    @Get("/v1/insurance-transport")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public BaseInsuranceResponse getPolicies(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching transport polices for client {}", meta.getClientId());

        var resp = transportPolicyService.getPolicies(meta.getConsentId(), adjustedPageable);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }

    @Get("/v2/insurance-transport")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public BaseInsuranceResponseV2 getPoliciesV2(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching transport polices for client {}", meta.getClientId());

        var resp = transportPolicyService.getPoliciesV2(meta.getConsentId(), adjustedPageable);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }

    @Get("/v1/insurance-transport/{policyId}/policy-info")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceTransportPolicyInfo getPolicyInfo(HttpRequest<?> request, @PathVariable("policyId") String policyId) {
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching transport policy info for client {}", meta.getClientId());
        var resp = transportPolicyService.getPolicyInfo(policyId, meta.getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }

    @Get("/v2/insurance-transport/{policyId}/policy-info")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceTransportPolicyInfoV2 getPolicyInfoV2(HttpRequest<?> request, @PathVariable("policyId") String policyId) {
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching transport policy info for client {}", meta.getClientId());
        var resp = transportPolicyService.getPolicyInfoV2(policyId, meta.getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }

    @Get("/v{version}/insurance-transport/{policyId}/premium")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceTransportPremium getPolicyPremium(@PathVariable("version") @Min(1) @Max(2) int version, HttpRequest<?> request, @PathVariable("policyId") String policyId) {
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching transport policy premium for client {}", meta.getClientId());
        var resp = transportPolicyService.getPolicyPremium(policyId, meta.getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }

    @Get("/v1/insurance-transport/{policyId}/claim")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceTransportClaims getPolicyClaims(HttpRequest<?> request, @PathVariable("policyId") String policyId, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching transport policy claims for client {}", meta.getClientId());
        var resp = transportPolicyService.getPolicyClaims(policyId, meta.getConsentId(), adjustedPageable);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }

    @Get("/v2/insurance-transport/{policyId}/claim")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceTransportClaimsV2 getPolicyClaimsV2(HttpRequest<?> request, @PathVariable("policyId") String policyId, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching transport policy claims for client {}", meta.getClientId());
        var resp = transportPolicyService.getPolicyClaimsV2(policyId, meta.getConsentId(), adjustedPageable);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }
}
