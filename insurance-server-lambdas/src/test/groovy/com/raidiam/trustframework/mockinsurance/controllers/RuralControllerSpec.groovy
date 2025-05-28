package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.services.RuralService
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils
import io.micronaut.context.ApplicationContext
import io.micronaut.data.model.Pageable
import io.micronaut.function.aws.proxy.MockLambdaContext
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class RuralControllerSpec extends Specification {
    @Inject
    RuralService ruralService

    @MockBean(RuralService)
    RuralService ruralService() {
        Mock(RuralService)
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

    def "We can fetch policies" () {
        given:
        def resp = new BaseInsuranceResponse().data(List.of())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        ruralService.getPolicies(_ as String, _ as Pageable) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-rural/v1/insurance-rural/', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-rural consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch a policy info" () {
        given:
        def resp = new ResponseInsuranceRuralPolicyInfo().data(new InsuranceRuralPolicyInfo())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        ruralService.getPolicyInfo(_ as UUID, _ as String) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-rural/v1/insurance-rural/'+UUID.randomUUID().toString()+'/policy-info', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-rural consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch a policy's premium" () {
        given:
        def resp = new ResponseInsuranceRuralPremium().data(new InsuranceRuralPremium())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        ruralService.getPremium(_ as UUID, _ as String) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-rural/v1/insurance-rural/'+UUID.randomUUID().toString()+'/premium', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-rural consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch a policy's claims" () {
        given:
        def resp = new ResponseInsuranceRuralClaims().data(List.of())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        ruralService.getClaims(_ as UUID, _ as String, _ as Pageable) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-rural/v1/insurance-rural/'+UUID.randomUUID().toString()+'/claim', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-rural consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }
}
