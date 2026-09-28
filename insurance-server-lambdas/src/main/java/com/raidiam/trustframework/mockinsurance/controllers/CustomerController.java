package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.aop.LogInvocation;
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
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured({"CUSTOMERS_MANAGE"})
@Controller("/open-insurance/customers")
public class CustomerController extends BaseInsuranceController {

    @Inject
    private CustomerService service;

    @Get("/v1/personal/identifications")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponsePersonalCustomersIdentification getPersonalIdentifications(@NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        var response = service.getPersonalIdentifications(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        return response;
    }

    @Get("/v2/personal/identifications")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponsePersonalCustomersIdentificationV2 getPersonalIdentificationsV2(@NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        var response = service.getPersonalIdentificationsV2(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        return response;
    }

    @Get("/v{version}/personal/qualifications")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponsePersonalCustomersQualification getPersonalQualifications(@PathVariable("version") @Min(1) @Max(2) int version, @NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        var response = service.getPersonalQualifications(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        return response;
    }

    @Get("/v{version}/personal/complimentary-information")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponsePersonalCustomersComplimentaryInformation getPersonalComplimentaryInfo(@PathVariable("version") @Min(1) @Max(2) int version, @NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        var response = service.getPersonalComplimentaryInfo(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        return response;
    }

    @Get("/v1/business/identifications")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseBusinessCustomersIdentification getBusinessIdentifications(@NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        var response = service.getBusinessIdentifications(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        return response;
    }

    @Get("/v2/business/identifications")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseBusinessCustomersIdentificationV2 getBusinessIdentificationsV2(@NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        var response = service.getBusinessIdentificationsV2(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        return response;
    }

    @Get("/v{version}/business/qualifications")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseBusinessCustomersQualification getBusinessQualifications(@PathVariable("version") @Min(1) @Max(2) int version, @NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        var response = service.getBusinessQualifications(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        return response;
    }

    @Get("/v1/business/complimentary-information")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseBusinessCustomersComplimentaryInformation getBusinessComplimentaryInfo(@NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        var response = service.getBusinessComplimentaryInfo(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        return response;
    }

    @Get("/v2/business/complimentary-information")
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseBusinessCustomersComplimentaryInformationV2 getBusinessComplimentaryInfoV2(@NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        var response = service.getBusinessComplimentaryInfoV2(consentId);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(response::setLinks, response::setMeta, appBaseUrl + request.getPath());
        return response;
    }
}
