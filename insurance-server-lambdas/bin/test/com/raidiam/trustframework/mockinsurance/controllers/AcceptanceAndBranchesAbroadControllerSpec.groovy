package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.services.AcceptanceAndBranchesAbroadService
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

@MicronautTest()
class AcceptanceAndBranchesAbroadControllerSpec extends Specification {
    @Inject
    AcceptanceAndBranchesAbroadService acceptanceAndBranchesAbroadService

    @MockBean(AcceptanceAndBranchesAbroadService)
    AcceptanceAndBranchesAbroadService acceptanceAndBranchesAbroadService() {
        Mock(AcceptanceAndBranchesAbroadService)
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
        acceptanceAndBranchesAbroadService.getPolicies(_ as String, _ as Pageable) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-acceptance-and-branches-abroad/v1/insurance-acceptance-and-branches-abroad/', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-acceptance-and-branches-abroad consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

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
        acceptanceAndBranchesAbroadService.getPoliciesV2(_ as String, _ as Pageable) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-acceptance-and-branches-abroad/v2/insurance-acceptance-and-branches-abroad/', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-acceptance-and-branches-abroad consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

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
        def resp = new ResponseInsuranceAcceptanceAndBranchesAbroadPolicyInfo().data(new InsuranceAcceptanceAndBranchesAbroadPolicyInfo())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        acceptanceAndBranchesAbroadService.getPolicyInfo(_ as UUID, _ as String) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-acceptance-and-branches-abroad/v1/insurance-acceptance-and-branches-abroad/'+UUID.randomUUID().toString()+'/policy-info', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-acceptance-and-branches-abroad consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

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
        def resp = new ResponseInsuranceAcceptanceAndBranchesAbroadPolicyInfoV2().data(new InsuranceAcceptanceAndBranchesAbroadPolicyInfoV2())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        acceptanceAndBranchesAbroadService.getPolicyInfoV2(_ as UUID, _ as String) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-acceptance-and-branches-abroad/v2/insurance-acceptance-and-branches-abroad/'+UUID.randomUUID().toString()+'/policy-info', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-acceptance-and-branches-abroad consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

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
        def resp = new ResponseInsuranceAcceptanceAndBranchesAbroadPremium().data(new InsuranceAcceptanceAndBranchesAbroadPremium())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        acceptanceAndBranchesAbroadService.getPremium(_ as UUID, _ as String) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-acceptance-and-branches-abroad/v1/insurance-acceptance-and-branches-abroad/'+UUID.randomUUID().toString()+'/premium', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-acceptance-and-branches-abroad consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

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
        def resp = new ResponseInsuranceAcceptanceAndBranchesAbroadPremiumV2().data(new InsuranceAcceptanceAndBranchesAbroadPremiumV2())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        acceptanceAndBranchesAbroadService.getPremiumV2(_ as UUID, _ as String) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-acceptance-and-branches-abroad/v2/insurance-acceptance-and-branches-abroad/'+UUID.randomUUID().toString()+'/premium', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-acceptance-and-branches-abroad consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

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
        def resp = new ResponseInsuranceAcceptanceAndBranchesAbroadClaims().data(List.of())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        acceptanceAndBranchesAbroadService.getClaims(_ as UUID, _ as String, _ as Pageable) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-acceptance-and-branches-abroad/v1/insurance-acceptance-and-branches-abroad/'+UUID.randomUUID().toString()+'/claim', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-acceptance-and-branches-abroad consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

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
        def resp = new ResponseInsuranceAcceptanceAndBranchesAbroadClaimsV2().data(List.of())
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(resp::setLinks, resp::setMeta, "https://example.com")
        acceptanceAndBranchesAbroadService.getClaimsV2(_ as UUID, _ as String, _ as Pageable) >> resp

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/insurance-acceptance-and-branches-abroad/v2/insurance-acceptance-and-branches-abroad/'+UUID.randomUUID().toString()+'/claim', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "insurance-acceptance-and-branches-abroad consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }
}
