package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.TestRequestDataFactory
import com.raidiam.trustframework.mockinsurance.domain.QuoteCapitalizationTitleEntity
import com.raidiam.trustframework.mockinsurance.domain.QuoteCapitalizationTitleLeadEntity
import com.raidiam.trustframework.mockinsurance.domain.CapitalizationTitleRaffleEntity
import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.repository.IdempotencyRepository
import com.raidiam.trustframework.mockinsurance.services.OverrideService
import com.raidiam.trustframework.mockinsurance.services.QuoteCapitalizationTitleService
import com.raidiam.trustframework.mockinsurance.services.QuoteCapitalizationTitleLeadService
import com.raidiam.trustframework.mockinsurance.services.CapitalizationTitleRaffleService
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
public class QuoteCapitalizationTitleControllerSpec extends Specification {

    private static Context lambdaContext = new MockLambdaContext()
    def mapper = new ObjectMapper()

    @Inject
    QuoteCapitalizationTitleLeadService quoteCapitalizationTitleLeadService

    @MockBean(QuoteCapitalizationTitleLeadService)
    QuoteCapitalizationTitleLeadService quoteCapitalizationTitleLeadService() {
        Spy(QuoteCapitalizationTitleLeadService)
    }

    @MockBean(OverrideService)
    OverrideService overrideService() {
        def mock = Mock(OverrideService)
        mock.getOverride(_ as String, _ as String, _ as String) >> Optional.empty()
        return mock
    }

    @Inject
    QuoteCapitalizationTitleService quoteCapitalizationTitleService

    @MockBean(QuoteCapitalizationTitleService)
    QuoteCapitalizationTitleService quoteCapitalizationTitleService() {
        Spy(QuoteCapitalizationTitleService)
    }

    @Inject
    CapitalizationTitleRaffleService capitalizationTitleRaffleService

    @MockBean(CapitalizationTitleRaffleService)
    CapitalizationTitleRaffleService capitalizationTitleRaffleService() {
        Spy(CapitalizationTitleRaffleService)
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

    def setup () {
        mapper.findAndRegisterModules()
        handler = new ApiGatewayProxyRequestEventFunction(applicationContext)
    }

    def "We can create a quote capitalization title lead" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteCapitalizationTitleLead(consentId)
        quoteCapitalizationTitleLeadService.createQuote(_ as QuoteCapitalizationTitleLeadEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuoteCapitalizationTitleLeadRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-capitalization-title/v1/lead/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-capitalization-title-lead", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuote resp = mapper.readValue(response.body, ResponseQuote)
        resp.getData().getStatus() == QuoteStatus.StatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can revoke a quote capitalization title lead" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteCapitalizationTitleLead(consentId)
        quoteCapitalizationTitleLeadService.patchQuote(_ as RevokePatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.revokeQuotePatchRequest(quote.getQuoteId())
        String json = mapper.writeValueAsString(req)

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-capitalization-title/v1/lead/request/' + consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-capitalization-title-lead", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseRevokePatch resp = mapper.readValue(response.body, ResponseRevokePatch)
        resp.getData().getStatus() == ResponseRevokePatchData.StatusEnum.CANC

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can create a quote capitalization title" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteCapitalizationTitle(consentId)
        quoteCapitalizationTitleService.createQuote(_ as QuoteCapitalizationTitleEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuoteCapitalizationTitleRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-capitalization-title/v1/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-capitalization-title", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuoteCapitalizationTitle resp = mapper.readValue(response.body, ResponseQuoteCapitalizationTitle)
        resp.getData().getStatus() == ResponseQuoteCapitalizationTitleData.StatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can create a capitalization title raffle" () {
        given:
        def raffle = TestEntityDataFactory.aCapitalizationTitleRaffle()
        capitalizationTitleRaffleService.createRaffle(_ as CapitalizationTitleRaffleEntity, _ as String) >> raffle
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createCapitalizationTitleRaffleRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-capitalization-title/v1/raffle/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "quote-capitalization-title-raffle consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseCapitalizationTitleRaffle resp = mapper.readValue(response.body, ResponseCapitalizationTitleRaffle)
        resp.getData().getRafflePaymentInformation().getRaffleResult() == RafflePaymentInformationCapitalizationTitleRaffle.RaffleResultEnum.CONTEMPLADO

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can't create a quote capitalization title without a consent id" () {
        given:
        def req = TestRequestDataFactory.createQuoteCapitalizationTitleRequest()
        req.data.consentId = null

        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-capitalization-title/v1/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-capitalization-title", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.UNPROCESSABLE_ENTITY.code
    }

    def "we can fetch a quote capitalization title" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteCapitalizationTitle(consentId)
        quoteCapitalizationTitleService.getQuote(_ as String, _ as String) >> quote

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-capitalization-title/v1/request/'+consentId+"/quote-status", HttpMethod.GET)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-capitalization-title", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuoteCapitalizationTitle resp = mapper.readValue(response.body, ResponseQuoteCapitalizationTitle)
        resp.getData().getStatus() == ResponseQuoteCapitalizationTitleData.StatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can patch a quote capitalization title" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteCapitalizationTitle(UUID.randomUUID(), consentId)
        quote.status = QuoteStatusEnum.ACKN.toString()
        quoteCapitalizationTitleService.patchQuote(_ as PatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.patchQuoteRequest(quote.getQuoteId())

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-capitalization-title/v1/request/'+consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-capitalization-title", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponsePatch resp = mapper.readValue(response.body, ResponsePatch)
        resp.getData().getStatus() == ResponsePatchData.StatusEnum.ACKN

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }
}
