package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.ClaimNotificationDamageEntity;
import com.raidiam.trustframework.mockinsurance.domain.ClaimNotificationPersonEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/open-insurance/claim-notification")
public class ClaimNotificationController extends BaseInsuranceController {

    @Inject
    ClaimNotificationDamageService claimNotificationDamageService;

    @Inject
    ClaimNotificationPersonService claimNotificationPersonService;

    private static final Logger LOG = LoggerFactory.getLogger(ClaimNotificationController.class);

    private static final String REDIRECT_LINK = "https://www.raidiam.com/";

    @Post("/v1/request/damage/{consentId}")
    @Status(HttpStatus.CREATED)
    @Secured({"CLAIM_NOTIFICATION_REQUEST_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    public ResponseClaimNotificationDamage createClaimNotificationRequestDamageV1(
            @PathVariable("consentId") String consentId,
            @Body CreateClaimNotificationDamage body,
            HttpRequest<?> request
    ) {
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        LOG.info("Creating new claim notification damage for client {}", clientId);
        ClaimNotificationDamageEntity entity = ClaimNotificationDamageEntity.fromRequest(body, clientId, consentId);
        return claimNotificationDamageService.createClaimNotification(entity).toResponse(REDIRECT_LINK);
    }

    @Post("/v1/request/person/{consentId}")
    @Status(HttpStatus.CREATED)
    @Secured({"CLAIM_NOTIFICATION_REQUEST_MANAGE"})
    @XFapiInteractionIdRequired
    @RequiredAuthenticationGrant(AuthenticationGrant.CLIENT_CREDENTIALS)
    @Idempotent
    public ResponseClaimNotificationPerson createClaimNotificationRequestPersonV1(
            @PathVariable("consentId") String consentId,
            @Body CreateClaimNotificationPerson body,
            HttpRequest<?> request
    ) {
        var clientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        LOG.info("Creating new claim notification person for client {}", clientId);
        return claimNotificationPersonService.createClaimNotification(
                ClaimNotificationPersonEntity.fromRequest(body, clientId, consentId)).toResponse(REDIRECT_LINK);
    }
}


