package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.services.HousingService
import com.raidiam.trustframework.mockinsurance.services.OverrideService
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
class HousingControllerSpec extends Specification {
    @Inject
    HousingService housingService

    @MockBean(HousingService)
    HousingService housingService() {
        Mock(HousingService)
    }

    @MockBean(OverrideService)
    OverrideService overrideService() {
        def mock = Mock(OverrideService)
        mock.getOverride(_ as String, _ as String, _ as String) >> Optional.empty()
        return mock
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
        housingService.getPolicies(_ as Pageable, _ as String) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-housing/v1/insurance-housing', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-housing consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch policies V2" () {
        given:
        def resp = new BaseInsuranceResponseV2().data(List.of())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        housingService.getPoliciesV2(_ as Pageable, _ as String) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-housing/v2/insurance-housing', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-housing consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

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
        def resp = new ResponseInsuranceHousingPolicyInfo().data(
                new InsuranceHousingPolicyInfoData()
        )
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        housingService.getPolicyInfo(_ as UUID, _ as String) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-housing/v1/insurance-housing/'+UUID.randomUUID().toString()+'/policy-info', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-housing consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch a policy info V2" () {
        given:
        def resp = new ResponseInsuranceHousingPolicyInfoV2().data(
                new InsuranceHousingPolicyInfoDataV2()
        )
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        housingService.getPolicyInfoV2(_ as UUID, _ as String) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-housing/v2/insurance-housing/'+UUID.randomUUID().toString()+'/policy-info', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-housing consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

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
        def resp = new ResponseInsuranceHousingClaims().data(List.of())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        housingService.getPolicyClaims(_ as UUID, _ as String, _ as Pageable) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-housing/v1/insurance-housing/'+UUID.randomUUID().toString()+'/claim', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-housing consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch a policy's claims V2" () {
        given:
        def resp = new ResponseInsuranceHousingClaimsV2().data(List.of())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        housingService.getPolicyClaimsV2(_ as UUID, _ as String, _ as Pageable) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-housing/v2/insurance-housing/'+UUID.randomUUID().toString()+'/claim', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-housing consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

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
        def resp = new ResponseInsuranceHousingPremium().data(
                new InsuranceHousingPremium()
        )
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        housingService.getPolicyPremium(_ as UUID, _ as String) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-housing/v1/insurance-housing/'+UUID.randomUUID().toString()+'/premium', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-housing consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch a policy's premium V2" () {
        given:
        def resp = new ResponseInsuranceHousingPremium().data(
                new InsuranceHousingPremium()
        )
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        housingService.getPolicyPremium(_ as UUID, _ as String) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-housing/v2/insurance-housing/'+UUID.randomUUID().toString()+'/premium', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-housing consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }
}
