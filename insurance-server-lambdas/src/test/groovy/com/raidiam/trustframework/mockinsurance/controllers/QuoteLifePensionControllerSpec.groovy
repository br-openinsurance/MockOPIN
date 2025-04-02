package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.TestRequestDataFactory
import com.raidiam.trustframework.mockinsurance.domain.QuoteLifePensionLeadEntity
import com.raidiam.trustframework.mockinsurance.domain.QuoteLifePensionEntity
import com.raidiam.trustframework.mockinsurance.models.generated.PatchQuotePayload
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatus
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatusEnum
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteLead
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuotePatch
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuotePatchData
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteLifePension
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseRevokeQuotePatch
import com.raidiam.trustframework.mockinsurance.models.generated.RevokeQuotePatchPayload
import com.raidiam.trustframework.mockinsurance.repository.IdempotencyRepository
import com.raidiam.trustframework.mockinsurance.services.QuoteLifePensionLeadService
import com.raidiam.trustframework.mockinsurance.services.QuoteLifePensionService
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
class QuoteLifePensionControllerSpec extends Specification {

    private static Context lambdaContext = new MockLambdaContext()
    def mapper = new ObjectMapper()

    @Inject
    QuoteLifePensionLeadService quoteLifePensionLeadService

    @MockBean(QuoteLifePensionLeadService)
    QuoteLifePensionLeadService quoteLifePensionLeadService() {
        Spy(QuoteLifePensionLeadService)
    }

    @Inject
    QuoteLifePensionService quoteLifePensionService

    @MockBean(QuoteLifePensionService)
    QuoteLifePensionService quoteLifePensionService() {
        Spy(QuoteLifePensionService)
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

    def "We can create a quote life pension lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteLifePensionLead(consentId)
        quoteLifePensionLeadService.createQuote(_ as QuoteLifePensionLeadEntity) >> quote
        idempotencyRepository.findByIdempotencyId(_ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuoteLifePensionLeadRequest()
        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/contract-life-pension/v1/lead/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of(
                        "x-idempotency-key", UUID.randomUUID().toString(),
                        "x-fapi-interaction-id", UUID.randomUUID().toString()
                ))
        AuthHelper.authorize(scopes: "contract-life-pension-lead", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuoteLead resp = mapper.readValue(response.body, ResponseQuoteLead)
        resp.getData().getStatus() == QuoteStatus.StatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can revoke a quote life pension lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteLifePensionLead(consentId)

        quoteLifePensionLeadService.patchQuote(_ as RevokeQuotePatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.revokeQuotePatchRequest(quote.getQuoteId())
        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent("/open-insurance/contract-life-pension/v1/lead/request/${consentId}", HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "contract-life-pension-lead", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseRevokeQuotePatch resp = mapper.readValue(response.body, ResponseRevokeQuotePatch)
        resp.getData().getStatus() == resp.getData().getStatus().CANC

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can create a quote life pension" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteLifePension(consentId)
        quoteLifePensionService.createQuote(_ as QuoteLifePensionEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuoteLifePensionRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/contract-life-pension/v1/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "contract-life-pension", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuoteLifePension resp = mapper.readValue(response.body, ResponseQuoteLifePension)
        resp.getData().getStatus().toString() == "RCVD"

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can't create a quote life pension without a consent id" () {
        given:
        def req = TestRequestDataFactory.createQuoteLifePensionRequest()
        req.data.consentId = null

        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/contract-life-pension/v1/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "contract-life-pension", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.UNPROCESSABLE_ENTITY.code
    }

    def "we can fetch a quote life pension" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteLifePension(consentId)
        quoteLifePensionService.getQuote(_ as String, _ as String) >> quote

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/contract-life-pension/v1/request/'+consentId+"/quote-status", HttpMethod.GET)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "contract-life-pension", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuoteLifePension resp = mapper.readValue(response.body, ResponseQuoteLifePension)
        resp.getData().getStatus().toString() == "RCVD"

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can patch a quote life pension" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteLifePension(UUID.randomUUID(), consentId)
        quote.status = QuoteStatusEnum.ACKN.toString()
        quoteLifePensionService.patchQuote(_ as PatchQuotePayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.patchQuoteRequest(quote.getQuoteId())

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/contract-life-pension/v1/request/'+consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "contract-life-pension", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuotePatch resp = mapper.readValue(response.body, ResponseQuotePatch)
        resp.getData().getStatus() == ResponseQuotePatchData.StatusEnum.ACKN

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }
}
