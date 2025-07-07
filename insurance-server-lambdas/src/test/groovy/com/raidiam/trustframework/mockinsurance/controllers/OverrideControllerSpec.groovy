package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.domain.OverrideResponseEntity
import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.services.OverrideService
import io.micronaut.context.ApplicationContext
import io.micronaut.function.aws.proxy.MockLambdaContext
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(environments = "test_override")
class OverrideControllerSpec extends Specification {
    @Inject
    OverrideService overrideService

    @MockBean(OverrideService)
    OverrideService overrideService() {
        Mock(OverrideService)
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

    def "We can override" () {
        given:
        def req = new OverridePayload().data(new OverrideData())
        overrideService.override(_ as OverridePayload, _ as String) >> new OverridePayload()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/override', HttpMethod.PUT)
                .withBody(json)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "override", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    def "We can fetch with override" () {
        given:
        def req = new OverridePayload().data(new OverrideData())
        overrideService.override(_ as OverridePayload, _ as String) >> new OverridePayload()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/override', HttpMethod.PUT)
                .withBody(json)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "override", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    def "We can fetch with override" () {
        given:
        def overrideEntity = new OverrideResponseEntity()
        overrideEntity.setResponse(new OverrideDataResponse().body("test"))
        overrideService.getOverride(_ as String, _ as String, _ as String) >> Optional.of(overrideEntity)

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/consents/v2/consents/test', HttpMethod.GET)
        AuthHelper.authorize(scopes: "consents", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }
}
