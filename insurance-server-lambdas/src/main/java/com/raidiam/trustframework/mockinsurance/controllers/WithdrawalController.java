package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.auth.AuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.auth.RequiredAuthenticationGrant;
import com.raidiam.trustframework.mockinsurance.domain.WithdrawalCapitalizationTitleEntity;
import com.raidiam.trustframework.mockinsurance.domain.WithdrawalPensionEntity;
import com.raidiam.trustframework.mockinsurance.domain.WithdrawalPensionLeadEntity;
import com.raidiam.trustframework.mockinsurance.fapi.Idempotent;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.fapi.XFapiInteractionIdRequired;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.services.WithdrawalService;
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
@Controller("/open-insurance/withdrawal")
public class WithdrawalController extends BaseInsuranceController {

    private static final Logger LOG = LoggerFactory.getLogger(WithdrawalController.class);

    private static final String REDIRECT_LINK = "https://www.raidiam.com/";

    @Inject
    WithdrawalService withdrawalService;

    @Post("/v1/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"PENSION_WITHDRAWAL_LEAD_CREATE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseWithdrawalLead createWithdrawalLeadV1(@Body RequestPensionWithdrawal body, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        String clientId = callerInfo.getClientId();
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Creating new withdrawal lead v1 for client {}", clientId);

        var entity = WithdrawalPensionLeadEntity.fromRequest(body, consentId, clientId);
        var resp = withdrawalService.createPensionWithdrawalLead(entity).toResponse();
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }

    @Post("/v1/pension/request")
    @Status(HttpStatus.CREATED)
    @Secured({"PENSION_WITHDRAWAL_CREATE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponsePensionWithdrawal createPensionWithdrawalV1(@Body RequestPensionWithdrawal body, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        String clientId = callerInfo.getClientId();
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Creating new pension withdrawal v1 for client {}", clientId);

        var entity = WithdrawalPensionEntity.fromRequest(body, consentId, clientId);
        var resp = withdrawalService.createPensionWithdrawal(entity).toResponse(REDIRECT_LINK);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }

    @Post("/v1/capitalization-title/request")
    @Status(HttpStatus.CREATED)
    @Secured({"CAPITALIZATION_TITLE_WITHDRAWAL_CREATE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    public ResponseCapitalizationTitleWithdrawal createCapitalizationTitleWithdrawalV1(@Body RequestCapitalizationTitleWithdrawal body, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        String clientId = callerInfo.getClientId();
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Creating new capitalization title withdrawal v1 for client {}", clientId);

        var entity = WithdrawalCapitalizationTitleEntity.fromRequest(body, consentId, clientId);
        var resp = withdrawalService.createCapitalizationTitleWithdrawal(entity).toResponse(REDIRECT_LINK);
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, appBaseUrl + request.getPath());
        return resp;
    }

    @Post("/v2/lead/request")
    @Status(HttpStatus.CREATED)
    @Secured({"PENSION_WITHDRAWAL_LEAD_CREATE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @ResponseErrorWithRequestDateTime
    public ResponseWithdrawalLeadV2 createWithdrawalLeadV2(@Body RequestPensionWithdrawalV2 body, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        String clientId = callerInfo.getClientId();
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Creating new withdrawal lead v2 for client {}", clientId);

        var entity = WithdrawalPensionLeadEntity.fromRequestV2(body, consentId, clientId);
        var saved = withdrawalService.createPensionWithdrawalLead(entity);
        var resp = saved.toResponseV2(body.getData());
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }

    @Post("/v2/pension/request")
    @Status(HttpStatus.CREATED)
    @Secured({"PENSION_WITHDRAWAL_CREATE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @ResponseErrorWithRequestDateTime
    public ResponsePensionWithdrawalV2 createPensionWithdrawalV2(@Body RequestPensionWithdrawalV2 body, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        String clientId = callerInfo.getClientId();
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Creating new pension withdrawal v2 for client {}", clientId);

        var entity = WithdrawalPensionEntity.fromRequestV2(body, consentId, clientId);
        var resp = withdrawalService.createPensionWithdrawal(entity).toResponseV2(REDIRECT_LINK);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }

    @Post("/v2/capitalization-title/request")
    @Status(HttpStatus.CREATED)
    @Secured({"CAPITALIZATION_TITLE_WITHDRAWAL_CREATE"})
    @XFapiInteractionIdRequired
    @Idempotent
    @RequiredAuthenticationGrant(AuthenticationGrant.AUTHORISATION_CODE)
    @ResponseErrorWithRequestDateTime
    public ResponseCapitalizationTitleWithdrawalV2 createCapitalizationTitleWithdrawalV2(@Body RequestCapitalizationTitleWithdrawalV2 body, HttpRequest<?> request) {
        var callerInfo = InsuranceLambdaUtils.getRequestMeta(request);
        String clientId = callerInfo.getClientId();
        String consentId = InsuranceLambdaUtils.getConsentIdFromRequest(request);
        LOG.info("Creating new capitalization title withdrawal v2 for client {}", clientId);

        var entity = WithdrawalCapitalizationTitleEntity.fromRequestV2(body, consentId, clientId);
        var resp = withdrawalService.createCapitalizationTitleWithdrawal(entity).toResponseV2(REDIRECT_LINK);
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, appBaseUrl + request.getPath());
        return resp;
    }
}
