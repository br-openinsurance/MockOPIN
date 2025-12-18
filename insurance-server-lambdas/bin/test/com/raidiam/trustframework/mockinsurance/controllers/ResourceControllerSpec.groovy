package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.models.generated.Meta
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseResourceList
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseResourceListV3
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseResourceListData
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseResourceListV3Data
import com.raidiam.trustframework.mockinsurance.services.OverrideService
import com.raidiam.trustframework.mockinsurance.services.ResourcesService
import io.micronaut.context.ApplicationContext
import io.micronaut.data.model.Pageable
import io.micronaut.function.aws.proxy.MockLambdaContext
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(transactional = false, environments = "test")
class ResourceControllerSpec extends Specification {

    private static Context lambdaContext = new MockLambdaContext()

    def mapper = new ObjectMapper()

    @Inject
    ResourcesService resourcesService

    @MockBean(ResourcesService)
    ResourcesService resourcesService(){
        Mock(ResourcesService)
    }

    @MockBean(OverrideService)
    OverrideService overrideService() {
        def mock = Mock(OverrideService)
        mock.getOverride(_ as String, _ as String, _ as String) >> Optional.empty()
        return mock
    }

    ApiGatewayProxyRequestEventFunction handler

    @Inject
    ApplicationContext applicationContext

    def setup() {
        mapper.findAndRegisterModules()
        handler = new ApiGatewayProxyRequestEventFunction(applicationContext)
    }

    def cleanup() {
        handler.close()
    }

    def "we get a 403 if there is no consent id"() {
        given:
        def event = AwsProxyHelper.buildEventWithHeaders("/open-insurance/resources/v2/resources", HttpMethod.GET,
                Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "resources", event)

        resourcesService.getResourceList(_ as Pageable, _ as String) >> { throw new HttpStatusException(HttpStatus.NOT_FOUND, "Consent not found") }

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.FORBIDDEN.code
        response.body
    }

    def "we get a 403 if there is no consent id in V3"() {
        given:
        def event = AwsProxyHelper.buildEventWithHeaders("/open-insurance/resources/v3/resources", HttpMethod.GET,
                Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "resources", event)

        resourcesService.getResourceListV3(_ as Pageable, _ as String) >> { throw new HttpStatusException(HttpStatus.NOT_FOUND, "Consent not found") }

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.FORBIDDEN.code
        response.body
    }

    def "we get a 403 if the consent isn't found"() {
        given:
        def event =AwsProxyHelper.buildEventWithHeaders("/open-insurance/resources/v2/resources", HttpMethod.GET,
                Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "resources consent:12345", event)

        resourcesService.getResourceList(_ as Pageable, _ as String) >> { throw new HttpStatusException(HttpStatus.FORBIDDEN, "Consent not found") }

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.FORBIDDEN.code
        response.body
    }

    def "we get a 403 if the consent isn't found in V3"() {
        given:
        def event =AwsProxyHelper.buildEventWithHeaders("/open-insurance/resources/v3/resources", HttpMethod.GET,
                Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "resources consent:12345", event)

        resourcesService.getResourceListV3(_ as Pageable, _ as String) >> { throw new HttpStatusException(HttpStatus.FORBIDDEN, "Consent not found") }

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.FORBIDDEN.code
        response.body
    }

    void "we can get a resource response"() {
        given:
        def event = AwsProxyHelper.buildEventWithHeaders("/open-insurance/resources/v2/resources", HttpMethod.GET,
                Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "resources consent:urn:raidiaminsurance:1234", event)

        def resource = new ResponseResourceListData()
                .type(ResponseResourceListData.TypeEnum.CAPITALIZATION_TITLES)
                .status(ResponseResourceListData.StatusEnum.AVAILABLE)
                .resourceId(UUID.randomUUID().toString())

        resourcesService.getResourceList(_ as Pageable, _ as String) >>
                new ResponseResourceList()
                        .data(List.of(resource))
                        .meta(new Meta()
                                 .totalPages(0)
                                 .totalRecords(0))

        when:
        def response = handler.handleRequest(event, lambdaContext)


        then:
        response.statusCode == HttpStatus.OK.code
        response.body
    }

    void "we can get a resource V3 response"() {
        given:
        def event = AwsProxyHelper.buildEventWithHeaders("/open-insurance/resources/v3/resources", HttpMethod.GET,
                Map.of("x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorize(scopes: "resources consent:urn:raidiaminsurance:1234", event)

        def resource = new ResponseResourceListV3Data()
                .type(ResponseResourceListV3Data.TypeEnum.CAPITALIZATION_TITLES)
                .status(ResponseResourceListV3Data.StatusEnum.AVAILABLE)
                .resourceId(UUID.randomUUID().toString())

        resourcesService.getResourceListV3(_ as Pageable, _ as String) >>
                new ResponseResourceListV3()
                        .data(List.of(resource))
                        .meta(new Meta()
                                 .totalPages(0)
                                 .totalRecords(0))

        when:
        def response = handler.handleRequest(event, lambdaContext)


        then:
        response.statusCode == HttpStatus.OK.code
        response.body
    }
}
