package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.TestRequestDataFactory
import com.raidiam.trustframework.mockinsurance.domain.QuoteAcceptanceAndBranchesAbroadLeadEntity
import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.repository.IdempotencyRepository
import com.raidiam.trustframework.mockinsurance.services.QuoteAcceptanceAndBranchesAbroadLeadService
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
class QuoteAcceptanceAndBranchesAbroadControllerSpec extends Specification {

    private static Context lambdaContext = new MockLambdaContext()
    def mapper = new ObjectMapper()

    @Inject
    QuoteAcceptanceAndBranchesAbroadLeadService quoteAcceptanceAndBranchesAbroadLeadService

    @MockBean(QuoteAcceptanceAndBranchesAbroadLeadService)
    QuoteAcceptanceAndBranchesAbroadLeadService quoteAcceptanceAndBranchesAbroadLeadService() {
        Spy(QuoteAcceptanceAndBranchesAbroadLeadService)
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

    def "We can create a quote lead" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteAcceptanceAndBranchesAbroadLead(consentId)
        quoteAcceptanceAndBranchesAbroadLeadService.createQuote(_ as QuoteAcceptanceAndBranchesAbroadLeadEntity) >> quote
        idempotencyRepository.findByIdempotencyId( _ as String) >> Optional.empty()

        def req = TestRequestDataFactory.createQuoteAcceptanceAndBranchesAbroadLeadRequest()

        String json = mapper.writeValueAsString(req)
        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-acceptance-and-branches-abroad/v1/lead/request', HttpMethod.POST)
                .withBody(json)
                .withHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString(), "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-acceptance-and-branches-abroad-lead", event)

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

    def "we can revoke a quote lead" () {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteAcceptanceAndBranchesAbroadLead(consentId)
        quoteAcceptanceAndBranchesAbroadLeadService.patchQuote(_ as RevokePatchPayload, _ as String, _ as String) >> quote

        def req = TestRequestDataFactory.revokeQuotePatchRequest(quote.getQuoteId())
        String json = mapper.writeValueAsString(req)

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/quote-acceptance-and-branches-abroad/v1/lead/request/' + consentId, HttpMethod.PATCH)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "quote-acceptance-and-branches-abroad-lead", event)

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

}