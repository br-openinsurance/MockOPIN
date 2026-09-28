package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.aop.LogInvocation;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.services.ResourcesService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseResourceList;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseResourceListV3;
import io.micronaut.context.annotation.Value;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;

import io.micronaut.security.annotation.Secured;
import jakarta.validation.constraints.NotNull;

@Secured({"RESOURCES_READ"})
@Controller("/open-insurance/resources")
public class ResourcesController extends BaseInsuranceController {

    @Inject
    ResourcesService resourcesService;

    @Value("${mockinsurance.max-page-size}")
    int maxPageSize;

    @Get(value = "/v2/resources")
    @LogInvocation
    public ResponseResourceList getResourcesV2(Pageable pageable, @NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        Pageable adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var response = resourcesService.getResourceList(adjustedPageable, consentId);
        InsuranceLambdaUtils.decorateResponse(response::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), response.getMeta().getTotalPages());
        return response;
    }

    @Get(value = "/v3/resources")
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseResourceListV3 getResourcesV3(Pageable pageable, @NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        Pageable adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var response = resourcesService.getResourceListV3(adjustedPageable, consentId);
        InsuranceLambdaUtils.decorateResponse(response::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), response.getMeta().getTotalPages());
        return response;
    }

}
