package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseResourceList
import com.raidiam.trustframework.mockinsurance.services.UserService
import io.micronaut.context.ApplicationContext
import io.micronaut.function.aws.proxy.MockLambdaContext
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class UserControllerSpec extends Specification {
    @Inject
    UserService userService

    @MockBean(UserService)
    UserService userService() {
        Spy(UserService)
    }

    private static Context lambdaContext = new MockLambdaContext()
    def mapper = new ObjectMapper()

    ApiGatewayProxyRequestEventFunction handler

    @Inject
    ApplicationContext applicationContext

    def setup () {
        mapper.findAndRegisterModules()
        handler = new ApiGatewayProxyRequestEventFunction(applicationContext)
    }

    def "We can fetch capitalization titles" () {
        given:
        userService.getCapitalizationTitlePlans(_ as String) >> new ResponseResourceList()

        def event = AwsProxyHelper.buildBasicEvent('/user/usuario1/capitalization-title-plans', HttpMethod.GET)
        AuthHelper.authorize(scopes: "op:admin", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    def "We can fetch life pensions" () {
        given:
        userService.getLifePensionContracts(_ as String) >> new ResponseResourceList()

        def event = AwsProxyHelper.buildBasicEvent('/user/usuario1/life-pension-contracts', HttpMethod.GET)
        AuthHelper.authorize(scopes: "op:admin", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    def "We can fetch pensions plans" () {
        given:
        userService.getPensionPlanContracts(_ as String) >> new ResponseResourceList()

        def event = AwsProxyHelper.buildBasicEvent('/user/usuario1/pension-plan-contracts', HttpMethod.GET)
        AuthHelper.authorize(scopes: "op:admin", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    def "We can fetch financial risks" () {
        given:
        userService.getFinancialRiskPolicies(_ as String) >> new ResponseResourceList()

        def event = AwsProxyHelper.buildBasicEvent('/user/usuario1/financial-risk-policies', HttpMethod.GET)
        AuthHelper.authorize(scopes: "op:admin", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    def "We can fetch housings" () {
        given:
        userService.getHousingPolicies(_ as String) >> new ResponseResourceList()

        def event = AwsProxyHelper.buildBasicEvent('/user/usuario1/housing-policies', HttpMethod.GET)
        AuthHelper.authorize(scopes: "op:admin", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    def "We can fetch responsibilities" () {
        given:
        userService.getResponsibilityPolicies(_ as String) >> new ResponseResourceList()

        def event = AwsProxyHelper.buildBasicEvent('/user/usuario1/responsibility-policies', HttpMethod.GET)
        AuthHelper.authorize(scopes: "op:admin", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    def "We can fetch persons" () {
        given:
        userService.getPersonPolicies(_ as String) >> new ResponseResourceList()

        def event = AwsProxyHelper.buildBasicEvent('/user/usuario1/person-policies', HttpMethod.GET)
        AuthHelper.authorize(scopes: "op:admin", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    def "We can fetch financial assistance contracts" () {
        given:
        userService.getFinancialAssistanceContracts(_ as String) >> new ResponseResourceList()

        def event = AwsProxyHelper.buildBasicEvent('/user/usuario1/financial-assistance-contracts', HttpMethod.GET)
        AuthHelper.authorize(scopes: "op:admin", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    def "We can fetch acceptance and branches abroad policies" () {
        given:
        userService.getAcceptanceAndBranchesAbroadPolicies(_ as String) >> new ResponseResourceList()

        def event = AwsProxyHelper.buildBasicEvent('/user/usuario1/acceptance-and-branches-abroad-policies', HttpMethod.GET)
        AuthHelper.authorize(scopes: "op:admin", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    def "We can fetch patrimonial policies" () {
        given:
        userService.getPatrimonialPolicies(_ as String) >> new ResponseResourceList()

        def event = AwsProxyHelper.buildBasicEvent('/user/usuario1/patrimonial-policies', HttpMethod.GET)
        AuthHelper.authorize(scopes: "op:admin", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    def "We can fetch rural policies" () {
        given:
        userService.getRuralPolicies(_ as String) >> new ResponseResourceList()

        def event = AwsProxyHelper.buildBasicEvent('/user/usuario1/rural-policies', HttpMethod.GET)
        AuthHelper.authorize(scopes: "op:admin", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    def "We can fetch auto policies" () {
        given:
        userService.getAutoPolicies(_ as String) >> new ResponseResourceList()

        def event = AwsProxyHelper.buildBasicEvent('/user/usuario1/auto-policies', HttpMethod.GET)
        AuthHelper.authorize(scopes: "op:admin", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    def "We can fetch transport policies" () {
        given:
        userService.getTransportPolicies(_ as String) >> new ResponseResourceList()

        def event = AwsProxyHelper.buildBasicEvent('/user/usuario1/transport-policies', HttpMethod.GET)
        AuthHelper.authorize(scopes: "op:admin", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }
}
