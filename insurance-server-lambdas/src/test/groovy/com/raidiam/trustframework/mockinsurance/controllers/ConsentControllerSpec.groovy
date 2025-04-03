package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.TestRequestDataFactory
import com.raidiam.trustframework.mockinsurance.domain.IdempotencyEntity
import com.raidiam.trustframework.mockinsurance.models.generated.CreateConsent
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentStatus
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseConsent
import com.raidiam.trustframework.mockinsurance.models.generated.UpdateConsent
import com.raidiam.trustframework.mockinsurance.repository.IdempotencyRepository
import com.raidiam.trustframework.mockinsurance.services.ConsentService
import com.raidiam.trustframework.mockinsurance.utils.PermissionGroup
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

import java.time.OffsetDateTime

@MicronautTest
class ConsentControllerSpec extends Specification {
    @Inject
    ConsentService consentService

    @MockBean(ConsentService)
    ConsentService consentService() {
        Spy(ConsentService)
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

    def "We can create a consent" () {
        given:
        def entity = TestEntityDataFactory.aConsent(UUID.randomUUID(), "random_client_id")
        consentService.createConsent(_ as CreateConsent, _ as String) >> entity
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()
        def req = TestRequestDataFactory.createConsentRequest(
                "random_cpf",
                "CPF",
                OffsetDateTime.now().plusDays(1),
                PermissionGroup.PERSONAL_REGISTRATION.getPermissions().toList()
        )

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/consents/v2/consents', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "consents", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseConsent resp = mapper.readValue(response.body, ResponseConsent)
        resp.getData().getStatus() == EnumConsentStatus.AWAITING_AUTHORISATION

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can't create a consent without an idempotency key" () {
        given:
        def entity = TestEntityDataFactory.aConsent(UUID.randomUUID(), "random_client_id")
        consentService.createConsent(_ as CreateConsent, _ as String) >> entity

        def req = TestRequestDataFactory.createConsentRequest(
                "random_cpf",
                "CPF",
                OffsetDateTime.now().plusDays(1),
                PermissionGroup.PERSONAL_REGISTRATION.getPermissions().toList()
        )

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/consents/v2/consents', HttpMethod.POST)
                .withBody(json)
        AuthHelper.authorize(scopes: "consents", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.UNPROCESSABLE_ENTITY.code

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can't create a consent with an invalid idempotency key" () {
        given:
        def entity = TestEntityDataFactory.aConsent(UUID.randomUUID(), "random_client_id")
        consentService.createConsent(_ as CreateConsent, _ as String) >> entity

        def req = TestRequestDataFactory.createConsentRequest(
                "random_cpf",
                "CPF",
                OffsetDateTime.now().plusDays(1),
                PermissionGroup.PERSONAL_REGISTRATION.getPermissions().toList()
        )

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/consents/v2/consents', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", "invalid_key"))
        AuthHelper.authorize(scopes: "consents", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.UNPROCESSABLE_ENTITY.code

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can't create a consent with invalid idempotency payload" () {
        given:
        def entity = TestEntityDataFactory.aConsent(UUID.randomUUID(), "random_client_id")
        consentService.createConsent(_ as CreateConsent, _ as String) >> entity

        def idempotencyRecord = new IdempotencyEntity()
        idempotencyRecord.setIdempotencyId(UUID.randomUUID().toString())
        idempotencyRecord.setRequest("random_request")
        idempotencyRecord.setResponse("random_response")
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.ofNullable(idempotencyRecord)
        def req = TestRequestDataFactory.createConsentRequest(
                "random_cpf",
                "CPF",
                OffsetDateTime.now().plusDays(1),
                PermissionGroup.PERSONAL_REGISTRATION.getPermissions().toList()
        )

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/consents/v2/consents', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "consents", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.UNPROCESSABLE_ENTITY.code

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can update a consent" () {
        given:
        def accountHolderEntity = TestEntityDataFactory.anAccountHolder("random_cpf", "CPF")
        def entity = TestEntityDataFactory.aConsent(accountHolderEntity.accountHolderId, "random_client_id")
        entity.accountHolder = accountHolderEntity
        consentService.updateConsent(_ as String, _ as UpdateConsent) >> entity
        def req = new UpdateConsent()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/consents/v2/consents/' + entity.getConsentId(), HttpMethod.PUT).withBody(json)
        AuthHelper.authorize(scopes: "op:consent", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseConsent resp = mapper.readValue(response.body, ResponseConsent)
        resp.getData().getStatus() == EnumConsentStatus.AWAITING_AUTHORISATION

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch a consent" () {
        given:
        def entity = TestEntityDataFactory.aConsent(UUID.randomUUID(), "random_client_id")
        consentService.getConsent(_ as String, _ as String) >> entity

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/consents/v2/consents/' + entity.getConsentId(), HttpMethod.GET)
        AuthHelper.authorize(scopes: "consents", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseConsent resp = mapper.readValue(response.body, ResponseConsent)
        resp.getData().getStatus() == EnumConsentStatus.AWAITING_AUTHORISATION

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can delete a consent" () {
        given:
        consentService.deleteConsent(_ as String, _ as String) >> {}

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/consents/v2/consents/random_consent_id', HttpMethod.DELETE)
        AuthHelper.authorize(scopes: "consents", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.NO_CONTENT.code

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "A non-successful response does not create an idempotency record" () {
        given:
        def idempotencyKey = UUID.randomUUID().toString()
        consentService.createConsent(_ as CreateConsent, _ as String) >> {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "UNPROCESSABLE_ENTITY: UNPROCESSABLE_ENTITY")
        }
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()
        def req = TestRequestDataFactory.createConsentRequest(
                "random_cpf",
                "CPF",
                OffsetDateTime.now().plusDays(1),
                PermissionGroup.PERSONAL_REGISTRATION.getPermissions().toList()
        )

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/consents/v2/consents', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", idempotencyKey))
        AuthHelper.authorize(scopes: "consents", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.UNPROCESSABLE_ENTITY.code
        idempotencyRepository.findByIdempotencyId(idempotencyKey).isEmpty()
    }
}