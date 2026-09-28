package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.aop.LogInvocation;
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

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/override")
@Secured({"OVERRIDE_MANAGE"})
public class OverrideController extends BaseInsuranceController {

    @Inject
    OverrideService overrideService;

    @Put
    @LogInvocation
    public OverridePayload updateWebhookUri(@Body OverridePayload req, @NotNull HttpRequest<?> request) {
        String clientId = InsuranceLambdaUtils.getClientIdFromRequest(request);
        return overrideService.override(req, clientId);
    }
}