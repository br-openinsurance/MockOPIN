package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.aop.LogInvocation;
import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceCapitalizationTitleEvent;
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

import java.util.UUID;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/open-insurance/insurance-capitalization-title")
@Secured({"CAPITALIZATION_TITLE_MANAGE"})
public class CapitalizationTitlePlanController extends BaseInsuranceController {

    @Inject
    CapitalizationTitleService capitalizationTitleService;

    @Get("/v2/insurance-capitalization-title/plans")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseInsuranceCapitalizationTitleV2 getPlansV2(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);

        var resp = capitalizationTitleService.getPlansV2(meta.getConsentId(), adjustedPageable);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }

    @Get("/v2/insurance-capitalization-title/{planId}/plan-info")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseInsuranceCapitalizationTitlePlanInfoV2 getPlanInfoV2(HttpRequest<?> request, @PathVariable("planId") UUID planId) {
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        var resp = capitalizationTitleService.getPlanInfoV2(planId, meta.getConsentId());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }

    @Get("/v2/insurance-capitalization-title/{planId}/events")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseInsuranceCapitalizationTitleEvent getPlanEvents(HttpRequest<?> request, @PathVariable("planId") UUID planId, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        var resp = capitalizationTitleService.getPlanEvents(planId, meta.getConsentId(), adjustedPageable);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }

    @Get("/v2/insurance-capitalization-title/{planId}/settlements")
    @ResponseErrorWithRequestDateTime
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseInsuranceCapitalizationTitleSettlement getPlanSettlements(HttpRequest<?> request, @PathVariable("planId") UUID planId, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var meta = InsuranceLambdaUtils.getRequestMeta(request);
        var resp = capitalizationTitleService.getPlanSettlements(planId, meta.getConsentId(), adjustedPageable);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }
}
