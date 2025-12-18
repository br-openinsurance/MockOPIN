package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseInsuranceResponse;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseInsuranceResponseV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceResponsibilityClaims;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceResponsibilityClaimsV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceResponsibilityPolicyInfo;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceResponsibilityPolicyInfoV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceResponsibilityPremium;
import com.raidiam.trustframework.mockinsurance.services.ResponsibilityService;
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
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured({"RESPONSIBILITY_MANAGE"})
@Controller("/open-insurance/insurance-responsibility")
public class ResponsibilityController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(ResponsibilityController.class);

    @Inject
    private ResponsibilityService service;

    @Get("/v1/insurance-responsibility")
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

    @Get("/v2/insurance-responsibility")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public BaseInsuranceResponseV2 getPoliciesV2(Pageable pageable, @NotNull HttpRequest<?> request) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting policies for consent id {} v2", consentId);
        BaseInsuranceResponseV2 response = service.getPoliciesV2(pageable, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved policies for consent id {}", consentId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-responsibility/{policyId}/policy-info")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceResponsibilityPolicyInfo getPersonalQualifications(@NotNull HttpRequest<?> request,
                                                                              @PathVariable UUID policyId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting policy info for policy id {} v1", consentId);
        ResponseInsuranceResponsibilityPolicyInfo response = service.getPolicyInfo(policyId, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved policy info for policy id {}", policyId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v2/insurance-responsibility/{policyId}/policy-info")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceResponsibilityPolicyInfoV2 getPersonalQualificationsV2(@NotNull HttpRequest<?> request,
                                                                              @PathVariable UUID policyId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting policy info for policy id {} v2", consentId);
        ResponseInsuranceResponsibilityPolicyInfoV2 response = service.getPolicyInfoV2(policyId, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved policy info for policy id {}", policyId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v{version}/insurance-responsibility/{policyId}/premium")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceResponsibilityPremium getPremium(@PathVariable("version") @Min(1) @Max(2) int version, @NotNull HttpRequest<?> request, @PathVariable UUID policyId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting premium for policy id {} v{}", consentId, version);
        ResponseInsuranceResponsibilityPremium response = service.getPolicyPremium(policyId, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved premium for policy id {}", policyId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-responsibility/{policyId}/claim")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceResponsibilityClaims getClaims(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                 @PathVariable UUID policyId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting claims for policy id {} v1", consentId);
        ResponseInsuranceResponsibilityClaims response = service.getPolicyClaims(policyId, consentId, pageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved claims for policy id {}", policyId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v2/insurance-responsibility/{policyId}/claim")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceResponsibilityClaimsV2 getClaimsV2(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                 @PathVariable UUID policyId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting claims for policy id {} v2", consentId);
        ResponseInsuranceResponsibilityClaimsV2 response = service.getPolicyClaimsV2(policyId, consentId, pageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved claims for policy id {}", policyId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }
}
