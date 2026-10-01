package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.aop.LogInvocation;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.EndorsementEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.CreateEndorsement;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseEndorsement;
import com.raidiam.trustframework.mockinsurance.services.EndorsementService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/open-insurance/endorsement")
public class EndorsementController extends BaseInsuranceController {

    private static final String REDIRECT_LINK = "https://www.raidiam.com/";

    @Inject
    private EndorsementService endorsementService;

    @Post("/v2/request/{consentId}")
    @Status(HttpStatus.CREATED)
    @Secured({"ENDORSEMENT_REQUEST_MANAGE"})
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @LogInvocation
    public ResponseEndorsement createEndorsementV2(@Body CreateEndorsement body, @PathVariable("consentId") String consentId, HttpRequest<?> request) {

        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        String clientId = callerInfo.getClientId();

        return endorsementService.createEndorsement(EndorsementEntity.fromRequest(body, consentId, clientId)).toResponse(REDIRECT_LINK);
    }
}
