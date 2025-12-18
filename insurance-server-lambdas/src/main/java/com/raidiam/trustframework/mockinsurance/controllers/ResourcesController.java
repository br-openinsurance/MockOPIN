package com.raidiam.trustframework.mockinsurance.controllers;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.security.annotation.Secured;
import jakarta.validation.constraints.NotNull;

@Secured({"RESOURCES_READ"})
@Controller("/open-insurance/resources")
public class ResourcesController extends BaseInsuranceController {

    private static final Logger LOG = LoggerFactory.getLogger(ResourcesController.class);

    @Inject
    ResourcesService resourcesService;

    @Value("${mockinsurance.max-page-size}")
    int maxPageSize;

    @Get(value = "/v2/resources")
    public ResponseResourceList getResourcesV2(Pageable pageable, @NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Looking up all resources for consent id {} v2", consentId);
        Pageable adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var response = resourcesService.getResourceList(adjustedPageable, consentId);
        InsuranceLambdaUtils.decorateResponse(response::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), response.getMeta().getTotalPages());
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get(value = "/v3/resources")
    public ResponseResourceListV3 getResourcesV3(Pageable pageable, @NotNull HttpRequest<?> request) {
        var consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Looking up all resources for consent id {} v3", consentId);
        Pageable adjustedPageable = InsuranceLambdaUtils.adjustPageable(pageable, request, maxPageSize);
        var response = resourcesService.getResourceListV3(adjustedPageable, consentId);
        InsuranceLambdaUtils.decorateResponse(response::setLinks, adjustedPageable.getSize(), appBaseUrl + request.getPath(), adjustedPageable.getNumber(), response.getMeta().getTotalPages());
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

}
