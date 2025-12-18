package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.TestRequestDataFactory
import com.raidiam.trustframework.mockinsurance.domain.QuotePersonLifeEntity
import com.raidiam.trustframework.mockinsurance.domain.QuotePersonTravelEntity
import com.raidiam.trustframework.mockinsurance.domain.QuotePersonLeadEntity
import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.repository.IdempotencyRepository
import com.raidiam.trustframework.mockinsurance.services.OverrideService
import com.raidiam.trustframework.mockinsurance.services.QuotePersonLeadService
import com.raidiam.trustframework.mockinsurance.services.QuotePersonLifeService
import com.raidiam.trustframework.mockinsurance.services.QuotePersonTravelService
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
class QuotePersonControllerSpec extends Specification {

    private static Context lambdaContext = new MockLambdaContext()
    def mapper = new ObjectMapper()

    @Inject
    QuotePersonLeadService quotePersonLeadService

    @MockBean(QuotePersonLeadService)
    QuotePersonLeadService quotePersonLeadService() {
        Spy(QuotePersonLeadService)
    }

    @MockBean(OverrideService)
    OverrideService overrideService() {
        def mock = Mock(OverrideService)
        mock.getOverride(_ as String, _ as String, _ as String) >> Optional.empty()
        return mock
    }

    @Inject
    QuotePersonLifeService quotePersonLifeService

    @Inject
    QuotePersonTravelService quotePersonTravelService

    @MockBean(QuotePersonLifeService)
    QuotePersonLifeService quotePersonLifeService() {
        Spy(QuotePersonLifeService)
    }

    @MockBean(QuotePersonTravelService)
    QuotePersonTravelService quotePersonTravelService() {
        Spy(QuotePersonTravelService)
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

    def "We can create a quote person lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonLead(consentId)
        quotePersonLeadService.createQuote(_ as QuotePersonLeadEntity) >> quote
        idempotencyRepository.findByIdempotencyId(_ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePersonLeadRequest()
        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v1/lead/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of(
                        "x-idempotency-key", UUID.randomUUID().toString(),
                        "x-fapi-interaction-id", UUID.randomUUID().toString()
                ))
        AuthHelper.authorize(scopes: "quote-person-lead", event)

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

    def "We can revoke a quote person lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonLead(consentId)

        quotePersonLeadService.patchQuote(_ as RevokePatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.revokeQuotePatchRequest(quote.getQuoteId())
        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent("/open-insurance/quote-person/v1/lead/request/${consentId}", HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-lead", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseRevokePatch resp = mapper.readValue(response.body, ResponseRevokePatch)
        resp.getData().getStatus() == resp.getData().getStatus().CANC

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can create a quote person life" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonLife(consentId)
        quotePersonLifeService.createQuote(_ as QuotePersonLifeEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePersonLifeRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v1/life/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-life", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuoteStatusPersonLife resp = mapper.readValue(response.body, ResponseQuoteStatusPersonLife)
        resp.getData().getStatus().toString() == "RCVD"

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can't create a quote person life without a consent id" () {
        given:
        def req = TestRequestDataFactory.createQuotePersonLifeRequest()
        req.data.consentId = null

        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v1/life/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-life", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.UNPROCESSABLE_ENTITY.code
    }

    def "we can fetch a quote person life" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonLife(consentId)
        quotePersonLifeService.getQuote(_ as String, _ as String) >> quote

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v1/life/request/'+consentId+"/quote-status", HttpMethod.GET)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-life", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuoteStatusPersonLife resp = mapper.readValue(response.body, ResponseQuoteStatusPersonLife)
        resp.getData().getStatus().toString() == "RCVD"

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can patch a quote person life" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonLife(UUID.randomUUID(), consentId)
        quote.status = QuoteStatusEnum.ACKN.toString()
        quotePersonLifeService.patchQuote(_ as PatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.patchQuoteRequest(quote.getQuoteId())

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v1/life/request/'+consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-life", event)

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

    def "we can create a quote person travel" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonTravel(consentId)
        quotePersonTravelService.createQuote(_ as QuotePersonTravelEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePersonTravelRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v1/travel/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-travel", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuoteStatusPersonTravel resp = mapper.readValue(response.body, ResponseQuoteStatusPersonTravel)
        resp.getData().getStatus().toString() == "RCVD"

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can't create a quote person travel without a consent id" () {
        given:
        def req = TestRequestDataFactory.createQuotePersonTravelRequest()
        req.data.consentId = null

        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v1/travel/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-travel", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.UNPROCESSABLE_ENTITY.code
    }

    def "we can fetch a quote person travel" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonTravel(consentId)
        quotePersonTravelService.getQuote(_ as String, _ as String) >> quote

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v1/travel/request/'+consentId+"/quote-status", HttpMethod.GET)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-travel", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuoteStatusPersonTravel resp = mapper.readValue(response.body, ResponseQuoteStatusPersonTravel)
        resp.getData().getStatus().toString() == "RCVD"

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can patch a quote person travel" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonTravel(UUID.randomUUID(), consentId)
        quote.status = QuoteStatusEnum.ACKN.toString()
        quotePersonTravelService.patchQuote(_ as PatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.patchQuoteRequest(quote.getQuoteId())

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v1/travel/request/'+consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-travel", event)

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




    def "We can create a quote person lead v2"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonLead(consentId)
        quotePersonLeadService.createQuote(_ as QuotePersonLeadEntity) >> quote
        idempotencyRepository.findByIdempotencyId(_ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePersonLeadRequestV2()
        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v2/lead/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of(
                        "x-idempotency-key", UUID.randomUUID().toString(),
                        "x-fapi-interaction-id", UUID.randomUUID().toString()
                ))
        AuthHelper.authorize(scopes: "quote-person-lead", event)

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

    def "We can revoke a quote person lead v2"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonLead(consentId)

        quotePersonLeadService.patchQuote(_ as RevokePatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.revokeQuotePatchRequest(quote.getQuoteId())
        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent("/open-insurance/quote-person/v2/lead/request/${consentId}", HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-lead", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseRevokePatch resp = mapper.readValue(response.body, ResponseRevokePatch)
        resp.getData().getStatus() == resp.getData().getStatus().CANC

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can create a quote person life v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonLife(consentId)
        quotePersonLifeService.createQuote(_ as QuotePersonLifeEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePersonLifeRequestV2()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v2/life/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-life", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuoteStatusPersonLife resp = mapper.readValue(response.body, ResponseQuoteStatusPersonLife)
        resp.getData().getStatus().toString() == "RCVD"

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can fetch a quote person life v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonLife(consentId)
        quotePersonLifeService.getQuote(_ as String, _ as String) >> quote

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v2/life/request/'+consentId+"/quote-status", HttpMethod.GET)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-life", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuotePersonLifeV2 resp = mapper.readValue(response.body, ResponseQuotePersonLifeV2)
        resp.getData().getStatus().toString() == "RCVD"

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can patch a quote person life v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonLife(UUID.randomUUID(), consentId)
        quote.status = QuoteStatusEnum.ACKN.toString()
        quotePersonLifeService.patchQuote(_ as PatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.patchQuoteRequest(quote.getQuoteId())

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v2/life/request/'+consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-life", event)

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

    def "we can create a quote person travel v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonTravel(consentId)
        quotePersonTravelService.createQuote(_ as QuotePersonTravelEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePersonTravelRequestV2()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v2/travel/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-travel", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuoteStatusPersonTravel resp = mapper.readValue(response.body, ResponseQuoteStatusPersonTravel)
        resp.getData().getStatus().toString() == "RCVD"

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can fetch a quote person travel v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonTravel(consentId)
        quotePersonTravelService.getQuote(_ as String, _ as String) >> quote

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v2/travel/request/'+consentId+"/quote-status", HttpMethod.GET)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-travel", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuotePersonTravelV2 resp = mapper.readValue(response.body, ResponseQuotePersonTravelV2)
        resp.getData().getStatus().toString() == "RCVD"

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can patch a quote person travel v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePersonTravel(UUID.randomUUID(), consentId)
        quote.status = QuoteStatusEnum.ACKN.toString()
        quotePersonTravelService.patchQuote(_ as PatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.patchQuoteRequest(quote.getQuoteId())

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-person/v2/travel/request/'+consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-person-travel", event)

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
