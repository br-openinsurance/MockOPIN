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
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting contracts for consent id {} v1", consentId);
        ResponseInsurancePensionPlan response = service.getContracts(pageable, consentId);
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

    @Get("/v1/insurance-pension-plan/{contractId}/movements")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePensionPlanMovements getMovements(Pageable pageable, @NotNull HttpRequest<?> request,
                                                              @PathVariable String contractId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting movements for contract id {} v1", consentId);
        ResponseInsurancePensionPlanMovements response = service.getContractMovements(contractId, consentId, pageable);

        // Calculates total movements by summing benefits and contributions
        // This is acceptable for the current mock setup, but may not reflect real pagination behavior when total movements exceed maxPageSize
        int totalMovements = response.getData().getMovementBenefits().size() + response.getData().getMovementContributions().size();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath(), totalMovements, maxPageSize);

        LOG.info("Retrieved movements for contract id {}", contractId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-pension-plan/{contractId}/portabilities")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsurancePensionPlanPortabilities getPortabilities(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                      @PathVariable String contractId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting portabilities for contract id {} v1", consentId);
        ResponseInsurancePensionPlanPortabilities response = service.getContractPortabilities(contractId, consentId, pageable);
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
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting withdrawals for contract id {} v1", consentId);
        ResponseInsurancePensionPlanWithdrawals response = service.getContractWithdrawals(contractId, consentId, pageable);
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
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting claims for contract id {} v1", consentId);
        ResponseInsurancePensionPlanClaim response = service.getContractClaims(contractId, consentId, pageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved claims for contract id {}", contractId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }
}
