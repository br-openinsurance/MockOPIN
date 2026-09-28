package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.aop.LogInvocation;
import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.LifePensionService;
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
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured({"LIFE_PENSION_MANAGE"})
@Controller("/open-insurance/insurance-life-pension")
public class LifePensionController extends BaseInsuranceController {

    @Inject
    private LifePensionService service;

    @Get("/v2/insurance-life-pension/contracts")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseInsuranceLifePensionV2 getContractsV2(Pageable pageable, @NotNull HttpRequest<?> request) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        ResponseInsuranceLifePensionV2 response = service.getContractsV2(adjustedPageable, consentId);
        InsuranceLambdaUtils.decorateResponse(response::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), response.getMeta().getTotalPages());
        return response;
    }

    @Get("/v2/insurance-life-pension/{certificateId}/contract-info")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseInsuranceLifePensionContractInfoV2 getPersonalQualificationsV2(@NotNull HttpRequest<?> request,
                                                                              @PathVariable UUID certificateId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        ResponseInsuranceLifePensionContractInfoV2 response = service.getContractInfoV2(certificateId, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        return response;
    }

    @Get("/v2/insurance-life-pension/{certificateId}/movements")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseInsuranceLifePensionMovements getMovements(Pageable pageable, @NotNull HttpRequest<?> request,
                                                              @PathVariable UUID certificateId) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        ResponseInsuranceLifePensionMovements response = service.getContractMovements(certificateId, consentId, adjustedPageable);
        // Calculates total movements by summing benefits and contributions
        // This is acceptable for the current mock setup, but may not reflect real pagination behavior when total movements exceed maxPageSize
        int totalMovements = response.getData().getMovementBenefits().size() + response.getData().getMovementContributions().size();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath(), totalMovements, maxPageSize);

        return response;
    }

    @Get("/v2/insurance-life-pension/{certificateId}/portabilities")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseInsuranceLifePensionPortabilitiesV2 getPortabilitiesV2(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                      @PathVariable UUID certificateId) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        ResponseInsuranceLifePensionPortabilitiesV2 response = service.getContractPortabilitiesV2(certificateId, consentId, adjustedPageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        return response;
    }

    @Get("/v2/insurance-life-pension/{certificateId}/withdrawals")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseInsuranceLifePensionWithdrawalV2 getWithdrawalsV2(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                 @PathVariable UUID certificateId) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        ResponseInsuranceLifePensionWithdrawalV2 response = service.getContractWithdrawalsV2(certificateId, consentId, adjustedPageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        return response;
    }

    @Get("/v2/insurance-life-pension/{certificateId}/claim")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseInsuranceLifePensionClaimV2 getClaimsV2(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                 @PathVariable UUID certificateId) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        ResponseInsuranceLifePensionClaimV2 response = service.getContractClaimsV2(certificateId, consentId, adjustedPageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        return response;
    }
}
