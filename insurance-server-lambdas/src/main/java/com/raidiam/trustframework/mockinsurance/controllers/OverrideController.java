package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.models.generated.OverridePayload;
import com.raidiam.trustframework.mockinsurance.services.OverrideService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/override")
@Secured({"OVERRIDE_MANAGE"})
public class OverrideController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(OverrideController.class);

    @Inject
    OverrideService overrideService;

    @Put
    public OverridePayload updateWebhookUri(@Body OverridePayload req, @NotNull HttpRequest<?> request) {
        String clientId = InsuranceLambdaUtils.getClientIdFromRequest(request);
        LOG.info("Registering new override response for client {}", clientId);
        return overrideService.override(req, clientId);
    }
}