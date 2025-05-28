package com.raidiam.trustframework.mockinsurance.controllers;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured({"LIFE_PENSION_MANAGE"})
@Controller("/open-insurance/insurance-life-pension")
public class LifePensionController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(LifePensionController.class);

    @Inject
    private LifePensionService service;

    @Get("/v1/insurance-life-pension/contracts")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceLifePension getContracts(Pageable pageable, @NotNull HttpRequest<?> request) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting contracts for consent id {} v1", consentId);
        ResponseInsuranceLifePension response = service.getContracts(pageable, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved contracts for consent id {}", consentId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-life-pension/{certificateId}/contract-info")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceLifePensionContractInfo getPersonalQualifications(@NotNull HttpRequest<?> request,
                                                                              @PathVariable UUID certificateId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting contract info for certificate id {} v1", consentId);
        ResponseInsuranceLifePensionContractInfo response = service.getContractInfo(certificateId, consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved contract info for certificate id {}", certificateId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-life-pension/{certificateId}/movements")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceLifePensionMovements getMovements(Pageable pageable, @NotNull HttpRequest<?> request,
                                                              @PathVariable UUID certificateId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting movements for certificate id {} v1", consentId);
        ResponseInsuranceLifePensionMovements response = service.getContractMovements(certificateId, consentId, pageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved movements for certificate id {}", certificateId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-life-pension/{certificateId}/portabilities")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceLifePensionPortabilities getPortabilities(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                      @PathVariable UUID certificateId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting portabilities for certificate id {} v1", consentId);
        ResponseInsuranceLifePensionPortabilities response = service.getContractPortabilities(certificateId, consentId, pageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved portabilities for certificate id {}", certificateId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-life-pension/{certificateId}/withdrawals")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceLifePensionWithdrawal getWithdrawals(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                 @PathVariable UUID certificateId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting withdrawals for certificate id {} v1", consentId);
        ResponseInsuranceLifePensionWithdrawal response = service.getContractWithdrawals(certificateId, consentId, pageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved withdrawals for certificate id {}", certificateId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/insurance-life-pension/{certificateId}/claim")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseInsuranceLifePensionClaim getClaims(Pageable pageable, @NotNull HttpRequest<?> request,
                                                                 @PathVariable UUID certificateId) {
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting claims for certificate id {} v1", consentId);
        ResponseInsuranceLifePensionClaim response = service.getContractClaims(certificateId, consentId, pageable);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved claims for certificate id {}", certificateId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }
}
