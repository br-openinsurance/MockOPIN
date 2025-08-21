package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.TestRequestDataFactory
import com.raidiam.trustframework.mockinsurance.domain.EndorsementEntity
import com.raidiam.trustframework.mockinsurance.domain.IdempotencyEntity
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseEndorsement
import com.raidiam.trustframework.mockinsurance.repository.IdempotencyRepository
import com.raidiam.trustframework.mockinsurance.services.EndorsementService
import com.raidiam.trustframework.mockinsurance.services.OverrideService
import io.micronaut.context.ApplicationContext
import io.micronaut.function.aws.proxy.MockLambdaContext
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class EndorsementControllerSpec extends Specification {
    @Inject
    EndorsementService endorsementService

    @MockBean(EndorsementService)
    EndorsementService endorsementService() {
        Spy(EndorsementService)
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

    private static Context lambdaContext = new MockLambdaContext()
    def mapper = new ObjectMapper()

    ApiGatewayProxyRequestEventFunction handler

    @Inject
    ApplicationContext applicationContext

    def setup () {
        mapper.findAndRegisterModules()
        handler = new ApiGatewayProxyRequestEventFunction(applicationContext)
    }

    def "We can create an endorsement" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def entity = TestEntityDataFactory.anEndorsement(consentId)
        endorsementService.createEndorsement(_ as EndorsementEntity) >> entity
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()
        def req = TestRequestDataFactory.createEndorsementRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/endorsement/v1/request/'+consentId, HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "endorsement", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseEndorsement resp = mapper.readValue(response.body, ResponseEndorsement)
        resp.getData().getProtocolNumber() != null
        resp.getData().getProtocolDateTime() != null
        resp.getData().getEndorsementType().toString() == req.getData().getEndorsementType().toString()
        resp.getLinks() != null
        resp.getLinks().getRedirect() == "https://www.raidiam.com/"

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can't create an endorsement without an idempotency key" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def entity = TestEntityDataFactory.anEndorsement(consentId)
        endorsementService.createEndorsement(_ as EndorsementEntity) >> entity

        def req = TestRequestDataFactory.createEndorsementRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/endorsement/v1/request/'+consentId, HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "endorsement", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.UNPROCESSABLE_ENTITY.code

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can't create a endorsement with an invalid idempotency key" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def entity = TestEntityDataFactory.anEndorsement(consentId)
        endorsementService.createEndorsement(_ as EndorsementEntity) >> entity

        def req = TestRequestDataFactory.createEndorsementRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/endorsement/v1/request/'+consentId, HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", "invalid_key", "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "endorsement", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.UNPROCESSABLE_ENTITY.code

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can't create an endorsement with invalid idempotency payload" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def entity = TestEntityDataFactory.anEndorsement(consentId)
        endorsementService.createEndorsement(_ as EndorsementEntity) >> entity

        def idempotencyRecord = new IdempotencyEntity()
        idempotencyRecord.setIdempotencyId(UUID.randomUUID().toString())
        idempotencyRecord.setRequest("random_request")
        idempotencyRecord.setResponse("random_response")
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.ofNullable(idempotencyRecord)

        def req = TestRequestDataFactory.createEndorsementRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/endorsement/v1/request/'+consentId, HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "endorsement", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.UNPROCESSABLE_ENTITY.code

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "A non-successful response does not create an idempotency record" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        endorsementService.createEndorsement(_ as EndorsementEntity) >> {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "UNPROCESSABLE_ENTITY: UNPROCESSABLE_ENTITY")
        }

        def idempotencyKey = UUID.randomUUID().toString()
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()
        
        def req = TestRequestDataFactory.createEndorsementRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/endorsement/v1/request/'+consentId, HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", idempotencyKey, "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "endorsement", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.UNPROCESSABLE_ENTITY.code
        idempotencyRepository.findByIdempotencyId(idempotencyKey).isEmpty()
    }
}