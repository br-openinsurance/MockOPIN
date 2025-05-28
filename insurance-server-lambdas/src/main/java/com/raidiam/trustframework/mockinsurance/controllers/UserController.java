package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.models.generated.ResponseResourceList;
import com.raidiam.trustframework.mockinsurance.services.UserService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Secured("ADMIN_FULL_MANAGE")
@Controller("/user/{userId}")
public class UserController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Inject
    private UserService userService;

    @Get(value = "/capitalization-title-plans", produces = {"application/json"})
    public ResponseResourceList getCapitalizationTitlePlans(@PathVariable("userId") String userId, HttpRequest<?> request) {
        LOG.info("Getting capitalization title plans for user id {}", userId);
        var response =  userService.getCapitalizationTitlePlans(userId);
        LOG.info("Retrieved capitalization title plans for user id {}", userId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get(value = "/financial-risk-policies", produces = {"application/json"})
    public ResponseResourceList getFinancialRiskPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        LOG.info("Getting financial risk policies for user id {}", userId);
        var response =  userService.getFinancialRiskPolicies(userId);
        LOG.info("Retrieved financial risk policies for user id {}", userId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get(value = "/housing-policies", produces = {"application/json"})
    public ResponseResourceList getHousingPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        LOG.info("Getting housing policies for user id {}", userId);
        var response =  userService.getHousingPolicies(userId);
        LOG.info("Retrieved housing policies for user id {}", userId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get(value = "/responsibility-policies", produces = {"application/json"})
    public ResponseResourceList getResponsibilityPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        LOG.info("Getting responsibility policies for user id {}", userId);
        var response =  userService.getResponsibilityPolicies(userId);
        LOG.info("Retrieved responsibility policies for user id {}", userId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get(value = "/person-policies", produces = {"application/json"})
    public ResponseResourceList getPersonPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        LOG.info("Getting person policies for user id {}", userId);
        var response =  userService.getPersonPolicies(userId);
        LOG.info("Retrieved person policies for user id {}", userId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get(value = "/life-pension-contracts", produces = {"application/json"})
    public ResponseResourceList getLifePensionContracts(@PathVariable("userId") String userId, HttpRequest<?> request) {
        LOG.info("Getting life pension contracts for user id {}", userId);
        var response =  userService.getLifePensionContracts(userId);
        LOG.info("Retrieved life pension contracts for user id {}", userId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get(value = "/pension-plan-contracts", produces = {"application/json"})
    public ResponseResourceList getPensionPlanContracts(@PathVariable("userId") String userId, HttpRequest<?> request) {
        LOG.info("Getting pension plan contracts for user id {}", userId);
        var response =  userService.getPensionPlanContracts(userId);
        LOG.info("Retrieved life pension contracts for user id {}", userId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get(value = "/acceptance-and-branches-abroad-policies", produces = {"application/json"})
    public ResponseResourceList getAcceptanceAndBranchesAbroadPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        LOG.info("Getting acceptance and branches abroad policies for user id {}", userId);
        var response =  userService.getAcceptanceAndBranchesAbroadPolicies(userId);
        LOG.info("Retrieved acceptance and branches abroad policies for user id {}", userId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }
  
    @Get(value = "/patrimonial-policies", produces = {"application/json"})
    public ResponseResourceList getPatrimonialPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        LOG.info("Getting life patrimonial policies for user id {}", userId);
        var response =  userService.getPatrimonialPolicies(userId);
        LOG.info("Retrieved patrimonial policies for user id {}", userId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }
  
    @Get(value = "/rural-policies", produces = {"application/json"})
    public ResponseResourceList getRuralPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        LOG.info("Getting life rural policies for user id {}", userId);
        var response =  userService.getRuralPolicies(userId);
        LOG.info("Retrieved rural policies for user id {}", userId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }
  
    @Get(value = "/financial-assistance-contracts", produces = {"application/json"})
    public ResponseResourceList getFinancialAssistanceContracts(@PathVariable("userId") String userId, HttpRequest<?> request) {
        LOG.info("Getting life financial assistance contracts for user id {}", userId);
        var response =  userService.getFinancialAssistanceContracts(userId);
        LOG.info("Retrieved financial assistance contracts for user id {}", userId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get(value = "/auto-policies", produces = {"application/json"})
    public ResponseResourceList getAutoPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        LOG.info("Getting auto policies for user id {}", userId);
        var response =  userService.getAutoPolicies(userId);
        LOG.info("Retrieved auto policies for user id {}", userId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }

    @Get(value = "/transport-policies", produces = {"application/json"})
    public ResponseResourceList getTransportPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        LOG.info("Getting transport policies for user id {}", userId);
        var response =  userService.getTransportPolicies(userId);
        LOG.info("Retrieved transport policies for user id {}", userId);
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }
}

