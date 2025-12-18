package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.DynamicFieldsService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/open-insurance/dynamic-fields")
@Secured("DYNAMIC_FIELDS_READ")
public class DynamicFieldsController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(DynamicFieldsController.class);

    @Inject
    DynamicFieldsService dynamicFieldsService;

    @Get(value ="/v1/damage-and-person", produces = {"application/json"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public DynamicFieldList getDamageAndPersonFields(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        LOG.info("Fetching dynamic damage and person fields");

        var resp = dynamicFieldsService.getDamageAndPerson(adjustedPageable);
        InsuranceLambdaUtils.logObject(mapper, resp);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }

    @Get(value = "/v1/capitalization-title", produces = {"application/json"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public DynamicFieldsCapitalizationList getCapitalizationTitleFields(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        LOG.info("Fetching dynamic capitalization title fields");
        var resp = dynamicFieldsService.getCapitalizationTitle();
        InsuranceLambdaUtils.logObject(mapper, resp);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), 1, 1);
        return resp;
    }

    @Get(value ="/v2/damage-and-person", produces = {"application/json"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public DynamicFieldListV2 getDamageAndPersonFieldsV2(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        LOG.info("Fetching dynamic damage and person fields");

        var resp = dynamicFieldsService.getDamageAndPersonV2(adjustedPageable);
        InsuranceLambdaUtils.logObject(mapper, resp);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), resp.getMeta().getTotalPages());
        return resp;
    }

    @Get(value = "/v2/capitalization-title", produces = {"application/json"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    public DynamicFieldsCapitalizationListV2 getCapitalizationTitleFieldsV2(HttpRequest<?> request, Pageable pageable) {
        var adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        LOG.info("Fetching dynamic capitalization title fields");
        var resp = dynamicFieldsService.getCapitalizationTitleV2();
        InsuranceLambdaUtils.logObject(mapper, resp);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), 1, 1);
        return resp;
    }
}
