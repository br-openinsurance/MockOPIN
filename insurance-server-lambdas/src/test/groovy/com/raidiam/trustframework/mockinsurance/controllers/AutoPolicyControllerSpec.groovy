package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.services.AutoPolicyService
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
class AutoPolicyControllerSpec extends Specification {
    @Inject
    AutoPolicyService autoPolicyService

    @MockBean(AutoPolicyService)
    AutoPolicyService autoPolicyService() {
        Mock(AutoPolicyService)
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
        autoPolicyService.getPolicies(_ as String, _ as Pageable) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-auto/v1/insurance-auto', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-auto consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

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
        def resp = new ResponseInsuranceAutoPolicyInfo().data(new InsuranceAutoPolicyInfo())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        autoPolicyService.getPolicyInfo(_ as String, _ as String) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-auto/v1/insurance-auto/'+UUID.randomUUID().toString()+'/policy-info', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-auto consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

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
        def resp = new ResponseInsuranceAutoPremium().data(new InsuranceAutoPremium())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        autoPolicyService.getPolicyPremium(_ as String, _ as String) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-auto/v1/insurance-auto/'+UUID.randomUUID().toString()+'/premium', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-auto consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

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
        def resp = new ResponseInsuranceAutoClaims().data(List.of())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        autoPolicyService.getPolicyClaims(_ as String, _ as String, _ as Pageable) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-auto/v1/insurance-auto/'+UUID.randomUUID().toString()+'/claim', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-auto consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }
}
