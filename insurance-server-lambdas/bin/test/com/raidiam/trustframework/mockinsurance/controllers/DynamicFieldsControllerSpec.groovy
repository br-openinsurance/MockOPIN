package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.models.generated.DynamicFieldList
import com.raidiam.trustframework.mockinsurance.models.generated.DynamicFieldListV2
import com.raidiam.trustframework.mockinsurance.models.generated.DynamicFieldsCapitalizationList
import com.raidiam.trustframework.mockinsurance.models.generated.DynamicFieldsCapitalizationListV2
import com.raidiam.trustframework.mockinsurance.services.DynamicFieldsService
import com.raidiam.trustframework.mockinsurance.services.OverrideService
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils
import io.micronaut.context.ApplicationContext
import io.micronaut.data.model.Pageable
import io.micronaut.function.aws.proxy.MockLambdaContext
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class DynamicFieldsControllerSpec extends Specification {

    @Inject
    DynamicFieldsService dynamicFieldsService

    @MockBean(DynamicFieldsService)
    DynamicFieldsService dynamicFieldsService() {
        Mock(DynamicFieldsService)
    }

    @MockBean(OverrideService)
    OverrideService overrideService() {
        def mock = Mock(OverrideService)
        mock.getOverride(_ as String, _ as String, _ as String) >> Optional.empty()
        return mock
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

    void "We can get a dynamic fields damage & person response"() {
        given:
        def resp = new DynamicFieldList().data(List.of())
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, resp::setMeta, "https://www.example.com/examplepage", 3, 5)
        dynamicFieldsService.getDamageAndPerson(_ as Pageable) >> resp

        def event = AwsProxyHelper.buildEventWithHeaders("/open-insurance/dynamic-fields/v1/damage-and-person", HttpMethod.GET,
                Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "dynamic-fields consent:urn:raidiaminsurance:1234", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)


        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    void "We can get a dynamic fields capitalization title response"() {
        given:
        def resp = new DynamicFieldsCapitalizationList().data(List.of())
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, resp::setMeta, "https://www.example.com/examplepage", 3, 5)
        dynamicFieldsService.getCapitalizationTitle() >> resp

        def event = AwsProxyHelper.buildEventWithHeaders("/open-insurance/dynamic-fields/v1/capitalization-title", HttpMethod.GET,
                Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "dynamic-fields consent:urn:raidiaminsurance:1234", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)


        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    void "We can get a dynamic fields damage & person response v2"() {
        given:
        def resp = new DynamicFieldListV2().data(List.of())
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, resp::setMeta, "https://www.example.com/examplepage", 3, 5)
        dynamicFieldsService.getDamageAndPersonV2(_ as Pageable) >> resp

        def event = AwsProxyHelper.buildEventWithHeaders("/open-insurance/dynamic-fields/v2/damage-and-person", HttpMethod.GET,
                Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "dynamic-fields consent:urn:raidiaminsurance:1234", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)


        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }

    void "We can get a dynamic fields capitalization title response v2"() {
        given:
        def resp = new DynamicFieldsCapitalizationListV2().data(List.of())
        InsuranceLambdaUtils.decorateResponse(resp::setLinks, resp::setMeta, "https://www.example.com/examplepage", 3, 5)
        dynamicFieldsService.getCapitalizationTitleV2() >> resp

        def event = AwsProxyHelper.buildEventWithHeaders("/open-insurance/dynamic-fields/v2/capitalization-title", HttpMethod.GET,
                Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "dynamic-fields consent:urn:raidiaminsurance:1234", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)


        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null
    }
}
