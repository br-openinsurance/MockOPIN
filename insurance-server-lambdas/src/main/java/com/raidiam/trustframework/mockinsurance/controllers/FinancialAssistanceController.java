package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceFinancialAssistance;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceFinancialAssistanceContractInfo;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceFinancialAssistanceMovements;
import com.raidiam.trustframework.mockinsurance.services.FinancialAssistanceService;
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
@Secured({"FINANCIAL_ASSISTANCE_MANAGE", "OPENID"})
@Controller("/open-insurance/insurance-financial-assistance")
public class FinancialAssistanceController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(FinancialAssistanceController.class);

    @Inject
    private FinancialAssistanceService service;

    @Get("/v1/insurance-financial-assistance/contracts")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceFinancialAssistance getContracts(Pageable pageable, @NotNull HttpRequest<?> request) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting contracts for consent id {} v1", consentId);
        ResponseInsuranceFinancialAssistance response = service.getContracts(pageable, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved contracts for consent id {}", consentId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-financial-assistance/{contractId}/contract-info")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceFinancialAssistanceContractInfo getContractInfo(@NotNull HttpRequest<?> request,
                                                                              @PathVariable String contractId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting contract info for contract id {} v1", consentId);
        ResponseInsuranceFinancialAssistanceContractInfo response = service.getContractInfo(contractId, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved contract info for certificate id {}", contractId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-financial-assistance/{contractId}/movements")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceFinancialAssistanceMovements getMovements(Pageable pageable, @NotNull HttpRequest<?> request,
                                                              @PathVariable String contractId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting movements for contract id {} v1", consentId);
        ResponseInsuranceFinancialAssistanceMovements response = service.getContractMovements(contractId, consentId, pageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved movements for contract id {}", contractId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }
}
