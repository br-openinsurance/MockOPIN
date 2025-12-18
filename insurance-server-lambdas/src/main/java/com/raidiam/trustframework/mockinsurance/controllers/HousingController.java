package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseInsuranceResponse;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseInsuranceResponseV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceHousingClaims;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceHousingClaimsV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceHousingPolicyInfo;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceHousingPolicyInfoV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceHousingPremium;
import com.raidiam.trustframework.mockinsurance.services.HousingService;
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
@Secured({"HOUSING_MANAGE"})
@Controller("/open-insurance/insurance-housing")
public class HousingController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(HousingController.class);

    @Inject
    private HousingService service;

    @Get("/v1/insurance-housing")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public BaseInsuranceResponse getPolicies(Pageable pageable, @NotNull HttpRequest<?> request) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting policies for consent id {} v1", consentId);
        BaseInsuranceResponse response = service.getPolicies(adjustedPageable, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved policies for consent id {}", consentId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v2/insurance-housing")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public BaseInsuranceResponseV2 getPoliciesV2(Pageable pageable, @NotNull HttpRequest<?> request) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting policies for consent id {} v2", consentId);
        BaseInsuranceResponseV2 response = service.getPoliciesV2(adjustedPageable, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved policies for consent id {}", consentId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-housing/{policyId}/policy-info")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceHousingPolicyInfo getPersonalQualifications(@NotNull HttpRequest<?> request,
                                                                              @PathVariable UUID policyId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting policy info for policy id {} v1", consentId);
        ResponseInsuranceHousingPolicyInfo response = service.getPolicyInfo(policyId, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved policy info for policy id {}", policyId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v2/insurance-housing/{policyId}/policy-info")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceHousingPolicyInfoV2 getPersonalQualificationsV2(@NotNull HttpRequest<?> request,
                                                                              @PathVariable UUID policyId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting policy info for policy id {} v1", consentId);
        ResponseInsuranceHousingPolicyInfoV2 response = service.getPolicyInfoV2(policyId, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved policy info for policy id {}", policyId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v{version}/insurance-housing/{policyId}/premium")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceHousingPremium getPremium(@PathVariable("version") @Min(1) @Max(2) int version, @NotNull HttpRequest<?> request, @PathVariable UUID policyId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting premium for policy id {} v{}", consentId, version);
        ResponseInsuranceHousingPremium response = service.getPolicyPremium(policyId, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved premium for policy id {}", policyId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-housing/{policyId}/claim")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceHousingClaims getClaims(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                 @PathVariable UUID policyId) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting claims for policy id {} v1", consentId);
        ResponseInsuranceHousingClaims response = service.getPolicyClaims(policyId, consentId, adjustedPageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved claims for policy id {}", policyId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v2/insurance-housing/{policyId}/claim")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceHousingClaimsV2 getClaimsV2(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                 @PathVariable UUID policyId) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting claims for policy id {} v2", consentId);
        ResponseInsuranceHousingClaimsV2 response = service.getPolicyClaimsV2(policyId, consentId, adjustedPageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved claims for policy id {}", policyId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }
}
