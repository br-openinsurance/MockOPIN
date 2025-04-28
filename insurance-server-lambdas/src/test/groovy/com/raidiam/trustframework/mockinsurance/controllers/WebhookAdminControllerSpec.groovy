package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseWebhook
import com.raidiam.trustframework.mockinsurance.models.generated.UpdateWebhook
import com.raidiam.trustframework.mockinsurance.services.WebhookService
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
class WebhookAdminControllerSpec extends Specification {

    @Inject
    WebhookService webhookService

    @MockBean(WebhookService)
    WebhookService webhookService() {
        Spy(WebhookService)
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

    def "We can create a webhook" () {
        given:
        def entity = TestEntityDataFactory.aWebhook()
        webhookService.updateWebhook(_ as UpdateWebhook, _ as String) >> new ResponseWebhook().clientId(entity.getClientId()).webhookUri(entity.getWebhookUri())
        def req = new UpdateWebhook().webhookUri(entity.getWebhookUri())

        String json = mapper.writeValueAsString(req)
        String path = String.format('/admin/webhook/%s', entity.getClientId())
        def event = AwsProxyHelper.buildBasicEvent(path, HttpMethod.PUT)
                .withBody(json)
                .withHeaders(Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "op:admin", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
        ResponseWebhook resp = mapper.readValue(response.body, ResponseWebhook)
        resp.clientId == entity.clientId
        resp.webhookUri == entity.webhookUri

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

}