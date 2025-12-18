package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.TestRequestDataFactory
import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialBusinessEntity
import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialCondominiumEntity
import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialDiverseRisksEntity
import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialHomeEntity
import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialLeadEntity
import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.repository.IdempotencyRepository
import com.raidiam.trustframework.mockinsurance.services.OverrideService
import com.raidiam.trustframework.mockinsurance.services.QuotePatrimonialBusinessService
import com.raidiam.trustframework.mockinsurance.services.QuotePatrimonialCondominiumService
import com.raidiam.trustframework.mockinsurance.services.QuotePatrimonialDiverseRisksService
import com.raidiam.trustframework.mockinsurance.services.QuotePatrimonialHomeService
import com.raidiam.trustframework.mockinsurance.services.QuotePatrimonialLeadService
import io.micronaut.context.ApplicationContext
import io.micronaut.function.aws.proxy.MockLambdaContext
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(environments = "test")
class QuotePatrimonialControllerSpec extends Specification {

    private static Context lambdaContext = new MockLambdaContext()
    def mapper = new ObjectMapper()

    @Inject
    QuotePatrimonialLeadService quotePatrimonialLeadService

    @MockBean(QuotePatrimonialLeadService)
    QuotePatrimonialLeadService quotePatrimonialLeadService() {
        Spy(QuotePatrimonialLeadService)
    }

    @MockBean(OverrideService)
    OverrideService overrideService() {
        def mock = Mock(OverrideService)
        mock.getOverride(_ as String, _ as String, _ as String) >> Optional.empty()
        return mock
    }

    @Inject
    QuotePatrimonialBusinessService quotePatrimonialBusinessService

    @MockBean(QuotePatrimonialBusinessService)
    QuotePatrimonialBusinessService quotePatrimonialBusinessService() {
        Spy(QuotePatrimonialBusinessService)
    }

    @Inject
    QuotePatrimonialHomeService quotePatrimonialHomeService

    @MockBean(QuotePatrimonialHomeService)
    QuotePatrimonialHomeService quotePatrimonialHomeService() {
        Spy(QuotePatrimonialHomeService)
    }

    @Inject
    QuotePatrimonialCondominiumService quotePatrimonialCondominiumService

    @MockBean(QuotePatrimonialCondominiumService)
    QuotePatrimonialCondominiumService quotePatrimonialCondominiumService() {
        Spy(QuotePatrimonialCondominiumService)
    }

    @Inject
    QuotePatrimonialDiverseRisksService quotePatrimonialDiverseRisksService

    @MockBean(QuotePatrimonialDiverseRisksService)
    QuotePatrimonialDiverseRisksService quotePatrimonialDiverseRisksService() {
        Spy(QuotePatrimonialDiverseRisksService)
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

    def "We can create a quote patrimonial lead" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialLead(consentId)
        quotePatrimonialLeadService.createQuote(_ as QuotePatrimonialLeadEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePatrimonialLeadRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/lead/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-lead", event)

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

    def "we can revoke a quote patrimonial lead" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialLead(consentId)
        quotePatrimonialLeadService.patchQuote(_ as RevokePatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.revokeQuotePatchRequest(quote.getQuoteId())
        String json = mapper.writeValueAsString(req)

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/lead/request/' + consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-lead", event)

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

    def "we can create a quote patrimonial business" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)
        quotePatrimonialBusinessService.createQuote(_ as QuotePatrimonialBusinessEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePatrimonialBusinessRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/business/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-business", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuotePatrimonialBusiness resp = mapper.readValue(response.body, ResponseQuotePatrimonialBusiness)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can't create a quote patrimonial business without a consent id" () {
        given:
        def req = TestRequestDataFactory.createQuotePatrimonialBusinessRequest()
        req.data.consentId = null

        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/business/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-business", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.UNPROCESSABLE_ENTITY.code
    }

    def "we can fetch a quote patrimonial business" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)
        quotePatrimonialBusinessService.getQuote(_ as String, _ as String) >> quote

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/business/request/'+consentId+"/quote-status", HttpMethod.GET)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-business", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuotePatrimonialBusiness resp = mapper.readValue(response.body, ResponseQuotePatrimonialBusiness)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can patch a quote patrimonial business" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(UUID.randomUUID(), consentId)
        quote.status = QuoteStatusEnum.ACKN.toString()
        quotePatrimonialBusinessService.patchQuote(_ as PatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.patchQuoteRequest(quote.getQuoteId())

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/business/request/'+consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-business", event)

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

    def "we can create a quote patrimonial home" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialHome(consentId)
        quotePatrimonialHomeService.createQuote(_ as QuotePatrimonialHomeEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePatrimonialHomeRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/home/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-home", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuotePatrimonialHome resp = mapper.readValue(response.body, ResponseQuotePatrimonialHome)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can fetch a quote patrimonial home" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialHome(consentId)
        quotePatrimonialHomeService.getQuote(_ as String, _ as String) >> quote

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/home/request/'+consentId+"/quote-status", HttpMethod.GET)
            .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-home", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuotePatrimonialHome resp = mapper.readValue(response.body, ResponseQuotePatrimonialHome)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can patch a quote patrimonial home" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialHome(UUID.randomUUID(), consentId)
        quote.status = QuoteStatusEnum.ACKN.toString()
        quotePatrimonialHomeService.patchQuote(_ as PatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.patchQuoteRequest(quote.getQuoteId())

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/home/request/'+consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-home", event)

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

    def "we can create a quote patrimonial condominium" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialCondominium(consentId)
        quotePatrimonialCondominiumService.createQuote(_ as QuotePatrimonialCondominiumEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePatrimonialCondominiumRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/condominium/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-condominium", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuotePatrimonialCondominium resp = mapper.readValue(response.body, ResponseQuotePatrimonialCondominium)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can fetch a quote patrimonial condominium" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialCondominium(consentId)
        quotePatrimonialCondominiumService.getQuote(_ as String, _ as String) >> quote

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/condominium/request/'+consentId+"/quote-status", HttpMethod.GET)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-condominium", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuotePatrimonialCondominium resp = mapper.readValue(response.body, ResponseQuotePatrimonialCondominium)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can patch a quote patrimonial condominium" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialCondominium(UUID.randomUUID(), consentId)
        quote.status = QuoteStatusEnum.ACKN.toString()
        quotePatrimonialCondominiumService.patchQuote(_ as PatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.patchQuoteRequest(quote.getQuoteId())

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/condominium/request/'+consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-condominium", event)

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

    def "we can create a quote patrimonial diverse risks" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialDiverseRisks(consentId)
        quotePatrimonialDiverseRisksService.createQuote(_ as QuotePatrimonialDiverseRisksEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePatrimonialDiverseRisksRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/diverse-risks/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-diverse-risks", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuotePatrimonialDiverseRisks resp = mapper.readValue(response.body, ResponseQuotePatrimonialDiverseRisks)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can fetch a quote patrimonial diverse risks" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialDiverseRisks(consentId)
        quotePatrimonialDiverseRisksService.getQuote(_ as String, _ as String) >> quote

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/diverse-risks/request/'+consentId+"/quote-status", HttpMethod.GET)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-diverse-risks", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuotePatrimonialDiverseRisks resp = mapper.readValue(response.body, ResponseQuotePatrimonialDiverseRisks)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can patch a quote patrimonial diverse risks" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialDiverseRisks(UUID.randomUUID(), consentId)
        quote.status = QuoteStatusEnum.ACKN.toString()
        quotePatrimonialDiverseRisksService.patchQuote(_ as PatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.patchQuoteRequest(quote.getQuoteId())

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v1/diverse-risks/request/'+consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-diverse-risks", event)

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




    def "We can create a quote patrimonial lead v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialLead(consentId)
        quotePatrimonialLeadService.createQuote(_ as QuotePatrimonialLeadEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePatrimonialLeadRequestV2()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v2/lead/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-lead", event)

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

    def "we can revoke a quote patrimonial lead v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialLead(consentId)
        quotePatrimonialLeadService.patchQuote(_ as RevokePatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.revokeQuotePatchRequest(quote.getQuoteId())
        String json = mapper.writeValueAsString(req)

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v2/lead/request/' + consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-lead", event)

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

    def "we can create a quote patrimonial business v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)
        quotePatrimonialBusinessService.createQuote(_ as QuotePatrimonialBusinessEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePatrimonialBusinessRequestV2()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v2/business/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-business", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuotePatrimonialBusinessV2 resp = mapper.readValue(response.body, ResponseQuotePatrimonialBusinessV2)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can fetch a quote patrimonial business v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)
        quotePatrimonialBusinessService.getQuote(_ as String, _ as String) >> quote

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v2/business/request/'+consentId+"/quote-status", HttpMethod.GET)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-business", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuotePatrimonialBusinessV2 resp = mapper.readValue(response.body, ResponseQuotePatrimonialBusinessV2)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can patch a quote patrimonial business v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(UUID.randomUUID(), consentId)
        quote.status = QuoteStatusEnum.ACKN.toString()
        quotePatrimonialBusinessService.patchQuote(_ as PatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.patchQuoteRequest(quote.getQuoteId())

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v2/business/request/'+consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-business", event)

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

    def "we can create a quote patrimonial home v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialHome(consentId)
        quotePatrimonialHomeService.createQuote(_ as QuotePatrimonialHomeEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePatrimonialHomeRequestV2()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v2/home/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-home", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuotePatrimonialHomeV2 resp = mapper.readValue(response.body, ResponseQuotePatrimonialHomeV2)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can fetch a quote patrimonial home v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialHome(consentId)
        quotePatrimonialHomeService.getQuote(_ as String, _ as String) >> quote

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v2/home/request/'+consentId+"/quote-status", HttpMethod.GET)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-home", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuotePatrimonialHomeV2 resp = mapper.readValue(response.body, ResponseQuotePatrimonialHomeV2)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can patch a quote patrimonial home v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialHome(UUID.randomUUID(), consentId)
        quote.status = QuoteStatusEnum.ACKN.toString()
        quotePatrimonialHomeService.patchQuote(_ as PatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.patchQuoteRequest(quote.getQuoteId())

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v2/home/request/'+consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-home", event)

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

    def "we can create a quote patrimonial condominium v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialCondominium(consentId)
        quotePatrimonialCondominiumService.createQuote(_ as QuotePatrimonialCondominiumEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePatrimonialCondominiumRequestV2()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v2/condominium/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-condominium", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuotePatrimonialCondominium resp = mapper.readValue(response.body, ResponseQuotePatrimonialCondominium)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can fetch a quote patrimonial condominium v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialCondominium(consentId)
        quotePatrimonialCondominiumService.getQuote(_ as String, _ as String) >> quote

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v2/condominium/request/'+consentId+"/quote-status", HttpMethod.GET)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-condominium", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuotePatrimonialCondominiumV2 resp = mapper.readValue(response.body, ResponseQuotePatrimonialCondominiumV2)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can patch a quote patrimonial condominium v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialCondominium(UUID.randomUUID(), consentId)
        quote.status = QuoteStatusEnum.ACKN.toString()
        quotePatrimonialCondominiumService.patchQuote(_ as PatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.patchQuoteRequest(quote.getQuoteId())

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v2/condominium/request/'+consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-condominium", event)

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

    def "we can create a quote patrimonial diverse risks v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialDiverseRisks(consentId)
        quotePatrimonialDiverseRisksService.createQuote(_ as QuotePatrimonialDiverseRisksEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuotePatrimonialDiverseRisksRequestV2()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v2/diverse-risks/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-diverse-risks", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.CREATED.code
        response.body != null
        ResponseQuotePatrimonialDiverseRisksV2 resp = mapper.readValue(response.body, ResponseQuotePatrimonialDiverseRisksV2)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can fetch a quote patrimonial diverse risks v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialDiverseRisks(consentId)
        quotePatrimonialDiverseRisksService.getQuote(_ as String, _ as String) >> quote

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v2/diverse-risks/request/'+consentId+"/quote-status", HttpMethod.GET)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-diverse-risks", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseQuotePatrimonialDiverseRisksV2 resp = mapper.readValue(response.body, ResponseQuotePatrimonialDiverseRisksV2)
        resp.getData().getStatus() == QuoteStatusEnum.RCVD

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "we can patch a quote patrimonial diverse risks v2" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialDiverseRisks(UUID.randomUUID(), consentId)
        quote.status = QuoteStatusEnum.ACKN.toString()
        quotePatrimonialDiverseRisksService.patchQuote(_ as PatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.patchQuoteRequest(quote.getQuoteId())

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-patrimonial/v2/diverse-risks/request/'+consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-patrimonial-diverse-risks", event)

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
