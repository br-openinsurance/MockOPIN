package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.aop.LogInvocation;
import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.ClaimNotificationDamageEntity;
import com.raidiam.trustframework.mockinsurance.domain.ClaimNotificationPersonEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.ClaimNotificationDamageService;
import com.raidiam.trustframework.mockinsurance.services.ClaimNotificationPersonService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/open-insurance/claim-notification")
public class ClaimNotificationController extends BaseInsuranceController {

    @Inject
    ClaimNotificationDamageService claimNotificationDamageService;

    @Inject
    ClaimNotificationPersonService claimNotificationPersonService;

    private static final String REDIRECT_LINK = "https://www.raidiam.com/";

    @Post("/v2/request/damage/{consentId}")
    @Status(HttpStatus.CREATED)
    @Secured({"CLAIM_NOTIFICATION_REQUEST_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @Idempotent
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseClaimNotificationDamageV2 createClaimNotificationRequestDamageV2(
            @PathVariable("consentId") String consentId,
            @Body CreateClaimNotificationDamageV2 body,
            HttpRequest<?> request
    ) {
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        ClaimNotificationDamageEntity entity = ClaimNotificationDamageEntity.fromRequestV2(body, clientId, consentId);
        return claimNotificationDamageService.createClaimNotification(entity).toResponseV2(REDIRECT_LINK);
    }

    @Post("/v2/request/person/{consentId}")
    @Status(HttpStatus.CREATED)
    @Secured({"CLAIM_NOTIFICATION_REQUEST_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @Idempotent
    @ResponseErrorWithRequestDateTime
    @LogInvocation
    public ResponseClaimNotificationPersonV2 createClaimNotificationRequestPersonV2(
            @PathVariable("consentId") String consentId,
            @Body CreateClaimNotificationPersonV2 body,
            HttpRequest<?> request
    ) {
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        return claimNotificationPersonService.createClaimNotification(
                ClaimNotificationPersonEntity.fromRequestV2(body, clientId, consentId)).toResponseV2(REDIRECT_LINK);
    }
}


