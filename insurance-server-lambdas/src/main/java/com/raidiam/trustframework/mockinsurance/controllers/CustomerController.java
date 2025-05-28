package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.CustomerService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured({"CUSTOMERS_MANAGE"})
@Controller("/open-insurance/customers")
public class CustomerController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerController.class);

    @Inject
    private CustomerService service;

    @Get("/v1/personal/identifications")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponsePersonalCustomersIdentification getPersonalIdentifications(@NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting personal identifications for consent id {} v1", consentId);
        var response = service.getPersonalIdentifications(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved personal identifications for consent id {}", consentId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/personal/qualifications")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponsePersonalCustomersQualification getPersonalQualifications(@NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting personal qualifications for consent id {} v1", consentId);
        var response = service.getPersonalQualifications(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved personal qualifications for consent id {}", consentId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/personal/complimentary-information")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponsePersonalCustomersComplimentaryInformation getPersonalComplimentaryInfo(@NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting personal complimentary info for consent id {} v1", consentId);
        var response = service.getPersonalComplimentaryInfo(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved personal complimentary info for consent id {}", consentId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/business/identifications")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseBusinessCustomersIdentification getBusinessIdentifications(@NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting business identifications for consent id {} v1", consentId);
        var response = service.getBusinessIdentifications(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved business identifications for consent id {}", consentId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/business/qualifications")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseBusinessCustomersQualification getBusinessQualifications(@NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting business qualifications for consent id {} v1", consentId);
        var response = service.getBusinessQualifications(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved business qualifications for consent id {}", consentId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get("/v1/business/complimentary-information")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseBusinessCustomersComplimentaryInformation getBusinessComplimentaryInfo(@NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Getting business complimentary info for consent id {} v1", consentId);
        var response = service.getBusinessComplimentaryInfo(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        LOG.info("Retrieved business complimentary info for consent id {}", consentId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }
}
