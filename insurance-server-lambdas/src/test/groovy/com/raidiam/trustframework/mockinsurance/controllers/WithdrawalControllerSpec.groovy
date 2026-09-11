package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.TestRequestDataFactory
import com.raidiam.trustframework.mockinsurance.domain.WithdrawalCapitalizationTitleEntity
import com.raidiam.trustframework.mockinsurance.domain.WithdrawalPensionEntity
import com.raidiam.trustframework.mockinsurance.domain.WithdrawalPensionLeadEntity
import com.raidiam.trustframework.mockinsurance.repository.IdempotencyRepository
import com.raidiam.trustframework.mockinsurance.services.OverrideService
import com.raidiam.trustframework.mockinsurance.services.WithdrawalService
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
class WithdrawalControllerSpec extends Specification {

    private static Context lambdaContext = new MockLambdaContext()
    def mapper = new ObjectMapper()

    @Inject
    WithdrawalService withdrawalService

    @MockBean(WithdrawalService)
    WithdrawalService withdrawalService() {
        Mock(WithdrawalService)
    }

    @MockBean(OverrideService)
    OverrideService overrideService() {
        def mock = Mock(OverrideService)
        mock.getOverride(_ as String, _ as String, _ as String) >> Optional.empty()
        return mock
    }

    @Inject
    IdempotencyRepository idempotencyRepository

    @MockBean(IdempotencyRepository)
    IdempotencyRepository idempotencyRepository() {
        Mock(IdempotencyRepository)
    }

    ApiGatewayProxyRequestEventFunction handler

    @Inject
    ApplicationContext applicationContext

    def setup() {
        mapper.findAndRegisterModules()
        handler = new ApiGatewayProxyRequestEventFunction(applicationContext)
    }

    def "we can create a pension withdrawal lead v1"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def lead = TestEntityDataFactory.aWithdrawalPensionLead("client1", consentId)
        withdrawalService.createPensionWithdrawalLead(_ as WithdrawalPensionLeadEntity) >> lead
        idempotencyRepository.findByIdempotencyId(_ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createPensionWithdrawalRequest()
        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent("/open-insurance/withdrawal/v1/lead/request", HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "consent:"+consentId+" withdrawal-pension-lead", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can create a pension withdrawal v1"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def withdrawal = TestEntityDataFactory.aWithdrawalPension("client1", consentId)
        withdrawalService.createPensionWithdrawal(_ as WithdrawalPensionEntity) >> withdrawal
        idempotencyRepository.findByIdempotencyId(_ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createPensionWithdrawalRequest()
        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent("/open-insurance/withdrawal/v1/pension/request", HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "consent:"+consentId+" withdrawal-pension", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can create a capitalization title withdrawal v1"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def withdrawal = TestEntityDataFactory.aWithdrawalCapitalizationTitle("client1", consentId)
        withdrawalService.createCapitalizationTitleWithdrawal(_ as WithdrawalCapitalizationTitleEntity) >> withdrawal
        idempotencyRepository.findByIdempotencyId(_ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createCapitalizationTitleWithdrawalRequest()
        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent("/open-insurance/withdrawal/v1/capitalization-title/request", HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "consent:"+consentId+" withdrawal-capitalization-title", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can create a pension withdrawal lead v2"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def lead = TestEntityDataFactory.aWithdrawalPensionLead("client1", consentId)
        withdrawalService.createPensionWithdrawalLead(_ as WithdrawalPensionLeadEntity) >> lead
        idempotencyRepository.findByIdempotencyId(_ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createPensionWithdrawalV2Request()
        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent("/open-insurance/withdrawal/v2/lead/request", HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "consent:"+consentId+" withdrawal-pension-lead", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can create a pension withdrawal v2"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def withdrawal = TestEntityDataFactory.aWithdrawalPensionV2("client1", consentId)
        withdrawalService.createPensionWithdrawal(_ as WithdrawalPensionEntity) >> withdrawal
        idempotencyRepository.findByIdempotencyId(_ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createPensionWithdrawalV2Request()
        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent("/open-insurance/withdrawal/v2/pension/request", HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "consent:"+consentId+" withdrawal-pension", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can create a capitalization title withdrawal v2"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def withdrawal = TestEntityDataFactory.aWithdrawalCapitalizationTitle("client1", consentId)
        withdrawalService.createCapitalizationTitleWithdrawal(_ as WithdrawalCapitalizationTitleEntity) >> withdrawal
        idempotencyRepository.findByIdempotencyId(_ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createCapitalizationTitleWithdrawalV2Request()
        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent("/open-insurance/withdrawal/v2/capitalization-title/request", HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "consent:"+consentId+" withdrawal-capitalization-title", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }
}
