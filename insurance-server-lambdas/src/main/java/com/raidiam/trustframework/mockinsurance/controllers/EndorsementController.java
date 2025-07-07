package com.raidiam.trustframework.mockinsurance.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     private static final Logger LOG = LoggerFactory.getLogger(EndorsementController.class);

    @Inject
    private EndorsementService endorsementService;

    @Post("/v1/request/{consentId}")
    @Status(HttpStatus.CREATED)
    @Secured({"ENDORSEMENT_REQUEST_MANAGE"})
    @XFapiInteractionIdRequired
    @ResponseErrorWithRequestDateTime
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseEndorsement createEndorsement(@Body CreateEndorsement body, @PathVariable("consentId") String consentId, HttpRequest<?> request) {

        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        String clientId = callerInfo.getClientId();
        LOG.info("Creating new endorsement for client {}", clientId);
        InsuranceLambdaUtils.logObject(mapper, body);

        ResponseEndorsement response = endorsementService.createEndorsement(EndorsementEntity.fromRequest(body, consentId, clientId)).toResponse();

        InsuranceLambdaUtils.decorateResponse(response::setLinks, appBaseUrl + request.getPath());
        InsuranceLambdaUtils.logObject(mapper, response);

        return response;
    }
}
