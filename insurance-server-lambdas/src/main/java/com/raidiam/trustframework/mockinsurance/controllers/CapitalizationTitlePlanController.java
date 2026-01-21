package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceCapitalizationTitle;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceCapitalizationTitleEvent;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceCapitalizationTitlePlanInfo;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceCapitalizationTitlePlanInfoV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceCapitalizationTitleSettlement;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceCapitalizationTitleV2;
import com.raidiam.trustframework.mockinsurance.services.CapitalizationTitleService;
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

import java.util.UUID;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/open-insurance/insurance-capitalization-title")
@Secured({"CAPITALIZATION_TITLE_MANAGE"})
public class CapitalizationTitlePlanController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(CapitalizationTitlePlanController.class);

    @Inject
    CapitalizationTitleService capitalizationTitleService;

    @Get("/v1/insurance-capitalization-title/plans")
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceCapitalizationTitle getPlans(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching capitalization title plans for client {}", meta.getClientId());

        var resp = capitalizationTitleService.getPlans(meta.getConsentId(), adjustedPageable);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }

    @Get("/v2/insurance-capitalization-title/plans")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceCapitalizationTitleV2 getPlansV2(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching capitalization title plans for client {}", meta.getClientId());

        var resp = capitalizationTitleService.getPlansV2(meta.getConsentId(), adjustedPageable);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }

    @Get("/v1/insurance-capitalization-title/{planId}/plan-info")
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceCapitalizationTitlePlanInfo getPlanInfo(HttpRequest<?> request, @PathVariable("planId") UUID planId) {
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching capitalization title plan info for client {}", meta.getClientId());
        var resp = capitalizationTitleService.getPlanInfo(planId, meta.getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }

    @Get("/v2/insurance-capitalization-title/{planId}/plan-info")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceCapitalizationTitlePlanInfoV2 getPlanInfoV2(HttpRequest<?> request, @PathVariable("planId") UUID planId) {
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching capitalization title plan info for client {}", meta.getClientId());
        var resp = capitalizationTitleService.getPlanInfoV2(planId, meta.getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }

    @Get("/v{version}/insurance-capitalization-title/{planId}/events")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceCapitalizationTitleEvent getPlanEvents(HttpRequest<?> request, @PathVariable("version") @Min(1) @Max(2) int version, @PathVariable("planId") UUID planId, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching capitalization title plan events for client {}", meta.getClientId());
        var resp = capitalizationTitleService.getPlanEvents(planId, meta.getConsentId(), adjustedPageable);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }

    @Get("/v{version}/insurance-capitalization-title/{planId}/settlements")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceCapitalizationTitleSettlement getPlanSettlements(HttpRequest<?> request, @PathVariable("version") @Min(1) @Max(2) int version, @PathVariable("planId") UUID planId, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        LOG.info("Fetching capitalization title plan settlements for client {}", meta.getClientId());
        var resp = capitalizationTitleService.getPlanSettlements(planId, meta.getConsentId(), adjustedPageable);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }
}
