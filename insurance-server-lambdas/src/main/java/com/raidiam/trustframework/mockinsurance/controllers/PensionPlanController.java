package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.PensionPlanService;
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

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured({"PENSION_PLAN_MANAGE"})
@Controller("/open-insurance/insurance-pension-plan")
public class PensionPlanController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(PensionPlanController.class);

    @Inject
    private PensionPlanService service;

    @Get("/v1/insurance-pension-plan/contracts")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePensionPlan getContracts(Pageable pageable, @NotNull HttpRequest<?> request) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting contracts for consent id {} v1", consentId);
        ResponseInsurancePensionPlan response = service.getContracts(adjustedPageable, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved contracts for consent id {}", consentId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v2/insurance-pension-plan/contracts")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePensionPlanV2 getContractsV2(Pageable pageable, @NotNull HttpRequest<?> request) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting contracts for consent id {} v2", consentId);
        ResponseInsurancePensionPlanV2 response = service.getContractsV2(adjustedPageable, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved contracts for consent id {}", consentId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-pension-plan/{contractId}/contract-info")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePensionPlanContractInfo getPersonalQualifications(@NotNull HttpRequest<?> request,
                                                                              @PathVariable String contractId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting contract info for contract id {} v1", consentId);
        ResponseInsurancePensionPlanContractInfo response = service.getContractInfo(contractId, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved contract info for contract id {}", contractId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v2/insurance-pension-plan/{contractId}/contract-info")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePensionPlanContractInfoV2 getPersonalQualificationsV2(@NotNull HttpRequest<?> request,
                                                                              @PathVariable String contractId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting contract info for contract id {} v2", consentId);
        ResponseInsurancePensionPlanContractInfoV2 response = service.getContractInfoV2(contractId, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved contract info for contract id {}", contractId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v{version}/insurance-pension-plan/{contractId}/movements")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePensionPlanMovements getMovements(@PathVariable("version") @Min(1) @Max(2) int version, Pageable pageable, @NotNull HttpRequest<?> request,
                                                              @PathVariable String contractId) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting movements for contract id {} v{}", consentId, version);
        ResponseInsurancePensionPlanMovements response = service.getContractMovements(contractId, consentId, adjustedPageable);

        // Calculates total movements by summing benefits and contributions
        // This is acceptable for the current mock setup, but may not reflect real pagination behavior when total movements exceed maxPageSize
        int totalMovements = response.getData().getMovementBenefits().size() + response.getData().getMovementContributions().size();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath(), totalMovements, maxPageSize);

        LOG.info("Retrieved movements for contract id {}", contractId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v{version}/insurance-pension-plan/{contractId}/portabilities")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePensionPlanPortabilities getPortabilities(@PathVariable("version") @Min(1) @Max(2) int version, Pageable pageable, @NotNull HttpRequest<?> request,
                                                                      @PathVariable String contractId) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting portabilities for contract id {} v{}", consentId, version);
        ResponseInsurancePensionPlanPortabilities response = service.getContractPortabilities(contractId, consentId, adjustedPageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved portabilities for contract id {}", contractId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-pension-plan/{contractId}/withdrawals")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePensionPlanWithdrawals getWithdrawals(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                 @PathVariable String contractId) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting withdrawals for contract id {} v1", consentId);
        ResponseInsurancePensionPlanWithdrawals response = service.getContractWithdrawals(contractId, consentId, adjustedPageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved withdrawals for contract id {}", contractId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v2/insurance-pension-plan/{contractId}/withdrawals")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePensionPlanWithdrawalsV2 getWithdrawalsV2(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                 @PathVariable String contractId) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting withdrawals for contract id {} v2", consentId);
        ResponseInsurancePensionPlanWithdrawalsV2 response = service.getContractWithdrawalsV2(contractId, consentId, adjustedPageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved withdrawals for contract id {}", contractId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-pension-plan/{contractId}/claim")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePensionPlanClaim getClaims(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                 @PathVariable String contractId) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting claims for contract id {} v1", consentId);
        ResponseInsurancePensionPlanClaim response = service.getContractClaims(contractId, consentId, adjustedPageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved claims for contract id {}", contractId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v2/insurance-pension-plan/{contractId}/claim")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePensionPlanClaimV2 getClaimsV2(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                 @PathVariable String contractId) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting claims for contract id {} v2", consentId);
        ResponseInsurancePensionPlanClaimV2 response = service.getContractClaimsV2(contractId, consentId, adjustedPageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved claims for contract id {}", contractId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }
}
