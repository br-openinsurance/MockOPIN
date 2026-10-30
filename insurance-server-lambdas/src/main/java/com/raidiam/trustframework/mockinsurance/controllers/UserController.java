package com.raidiam.trustframework.mockinsurance.controllers;

import com.raidiam.trustframework.mockinsurance.aop.LogInvocation;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseResourceList;
import com.raidiam.trustframework.mockinsurance.services.UserService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;

@Secured("ADMIN_FULL_MANAGE")
@Controller("/user/{userId}")
public class UserController extends BaseInsuranceController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Get(value = "/capitalization-title-plans", produces = {"application/json"})
    @LogInvocation
    public ResponseResourceList getCapitalizationTitlePlans(@PathVariable("userId") String userId, HttpRequest<?> request) {
        return userService.getCapitalizationTitlePlans(userId);
    }

    @Get(value = "/financial-risk-policies", produces = {"application/json"})
    @LogInvocation
    public ResponseResourceList getFinancialRiskPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        return userService.getFinancialRiskPolicies(userId);
    }

    @Get(value = "/housing-policies", produces = {"application/json"})
    @LogInvocation
    public ResponseResourceList getHousingPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        return userService.getHousingPolicies(userId);
    }

    @Get(value = "/responsibility-policies", produces = {"application/json"})
    @LogInvocation
    public ResponseResourceList getResponsibilityPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        return userService.getResponsibilityPolicies(userId);
    }

    @Get(value = "/person-policies", produces = {"application/json"})
    @LogInvocation
    public ResponseResourceList getPersonPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        return userService.getPersonPolicies(userId);
    }

    @Get(value = "/life-pension-contracts", produces = {"application/json"})
    @LogInvocation
    public ResponseResourceList getLifePensionContracts(@PathVariable("userId") String userId, HttpRequest<?> request) {
        return userService.getLifePensionContracts(userId);
    }

    @Get(value = "/pension-plan-contracts", produces = {"application/json"})
    @LogInvocation
    public ResponseResourceList getPensionPlanContracts(@PathVariable("userId") String userId, HttpRequest<?> request) {
        return userService.getPensionPlanContracts(userId);
    }

    @Get(value = "/acceptance-and-branches-abroad-policies", produces = {"application/json"})
    @LogInvocation
    public ResponseResourceList getAcceptanceAndBranchesAbroadPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        return userService.getAcceptanceAndBranchesAbroadPolicies(userId);
    }
  
    @Get(value = "/patrimonial-policies", produces = {"application/json"})
    @LogInvocation
    public ResponseResourceList getPatrimonialPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        return userService.getPatrimonialPolicies(userId);
    }
  
    @Get(value = "/rural-policies", produces = {"application/json"})
    @LogInvocation
    public ResponseResourceList getRuralPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        return userService.getRuralPolicies(userId);
    }
  
    @Get(value = "/financial-assistance-contracts", produces = {"application/json"})
    @LogInvocation
    public ResponseResourceList getFinancialAssistanceContracts(@PathVariable("userId") String userId, HttpRequest<?> request) {
        return userService.getFinancialAssistanceContracts(userId);
    }

    @Get(value = "/auto-policies", produces = {"application/json"})
    @LogInvocation
    public ResponseResourceList getAutoPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        return userService.getAutoPolicies(userId);
    }

    @Get(value = "/transport-policies", produces = {"application/json"})
    @LogInvocation
    public ResponseResourceList getTransportPolicies(@PathVariable("userId") String userId, HttpRequest<?> request) {
        return userService.getTransportPolicies(userId);
    }
}

