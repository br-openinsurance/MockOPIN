package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.TestRequestDataFactory
import com.raidiam.trustframework.mockinsurance.domain.ClaimNotificationDamageEntity
import com.raidiam.trustframework.mockinsurance.domain.ClaimNotificationPersonEntity
import com.raidiam.trustframework.mockinsurance.repository.IdempotencyRepository
import com.raidiam.trustframework.mockinsurance.services.ClaimNotificationDamageService
import com.raidiam.trustframework.mockinsurance.services.ClaimNotificationPersonService
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

@MicronautTest
class ClaimNotificationControllerSpec extends Specification {

    private static Context lambdaContext = new MockLambdaContext()
    def mapper = new ObjectMapper()

    @Inject
    ClaimNotificationDamageService claimNotificationDamageService

    @MockBean(ClaimNotificationDamageService)
    ClaimNotificationDamageService claimNotificationDamageService() {
        Mock(ClaimNotificationDamageService)
    }

    @MockBean(OverrideService)
    OverrideService overrideService() {
        def mock = Mock(OverrideService)
        mock.getOverride(_ as String, _ as String, _ as String) >> Optional.empty()
        return mock
    }

    @Inject
    ClaimNotificationPersonService claimNotificationPersonService

    @MockBean(ClaimNotificationPersonService)
    ClaimNotificationPersonService claimNotificationPersonService() {
        Mock(ClaimNotificationPersonService)
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

    def "we can create a claim notification damage" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def claim = TestEntityDataFactory.aClaimNotificationDamage(consentId)
        claimNotificationDamageService.createClaimNotification(_ as ClaimNotificationDamageEntity) >> claim
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createClaimNotificationDamageRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent("/open-insurance/claim-notification/v1/request/damage/${consentId}", HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "claim-notification", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can create a claim notification person" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def claim = TestEntityDataFactory.aClaimNotificationPerson(consentId)
        claimNotificationPersonService.createClaimNotification(_ as ClaimNotificationPersonEntity) >> claim
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createClaimNotificationPersonRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent("/open-insurance/claim-notification/v1/request/person/${consentId}", HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "claim-notification", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can create a claim notification damage v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def claim = TestEntityDataFactory.aClaimNotificationDamage(consentId)
        claimNotificationDamageService.createClaimNotification(_ as ClaimNotificationDamageEntity) >> claim
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createClaimNotificationDamageRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent("/open-insurance/claim-notification/v2/request/damage/${consentId}", HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "claim-notification", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can create a claim notification person v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def claim = TestEntityDataFactory.aClaimNotificationPerson(consentId)
        claimNotificationPersonService.createClaimNotification(_ as ClaimNotificationPersonEntity) >> claim
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createClaimNotificationPersonRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent("/open-insurance/claim-notification/v2/request/person/${consentId}", HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "claim-notification", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }
}
