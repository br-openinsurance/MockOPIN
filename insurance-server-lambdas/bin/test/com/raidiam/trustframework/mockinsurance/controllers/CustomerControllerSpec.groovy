package com.raidiam.trustframework.mockinsurance.controllers

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.AuthHelper
import com.raidiam.trustframework.mockinsurance.AwsProxyHelper
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseBusinessCustomersComplimentaryInformation
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseBusinessCustomersComplimentaryInformationV2
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseBusinessCustomersIdentification
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseBusinessCustomersIdentificationV2
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseBusinessCustomersQualification
import com.raidiam.trustframework.mockinsurance.models.generated.ResponsePersonalCustomersComplimentaryInformation
import com.raidiam.trustframework.mockinsurance.models.generated.ResponsePersonalCustomersIdentification
import com.raidiam.trustframework.mockinsurance.models.generated.ResponsePersonalCustomersIdentificationV2
import com.raidiam.trustframework.mockinsurance.models.generated.ResponsePersonalCustomersQualification
import com.raidiam.trustframework.mockinsurance.services.CustomerService
import com.raidiam.trustframework.mockinsurance.services.OverrideService
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
class CustomerControllerSpec extends Specification {
    @Inject
    CustomerService customerService

    @MockBean(CustomerService)
    CustomerService customerService() {
        Spy(CustomerService)
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

    def "We can fetch personal identifications" () {
        given:
        customerService.getPersonalIdentifications(_ as String) >> new ResponsePersonalCustomersIdentification()

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/customers/v1/personal/identifications', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "customers consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch personal identifications V2" () {
        given:
        customerService.getPersonalIdentificationsV2(_ as String) >> new ResponsePersonalCustomersIdentificationV2()

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/customers/v2/personal/identifications', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "customers consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch personal qualifications" () {
        given:
        customerService.getPersonalQualifications(_ as String) >> new ResponsePersonalCustomersQualification()

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/customers/v1/personal/qualifications', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "customers consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch personal qualifications V2" () {
        given:
        customerService.getPersonalQualifications(_ as String) >> new ResponsePersonalCustomersQualification()

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/customers/v2/personal/qualifications', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "customers consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch personal complimentary info" () {
        given:
        customerService.getPersonalComplimentaryInfo(_ as String) >> new ResponsePersonalCustomersComplimentaryInformation()

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/customers/v1/personal/complimentary-information', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "customers consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch personal complimentary info V2" () {
        given:
        customerService.getPersonalComplimentaryInfo(_ as String) >> new ResponsePersonalCustomersComplimentaryInformation()

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/customers/v2/personal/complimentary-information', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "customers consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch business identifications" () {
        given:
        customerService.getBusinessIdentifications(_ as String) >> new ResponseBusinessCustomersIdentification()

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/customers/v1/business/identifications', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "customers consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch business identifications V2" () {
        given:
        customerService.getBusinessIdentificationsV2(_ as String) >> new ResponseBusinessCustomersIdentificationV2()

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/customers/v2/business/identifications', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "customers consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch business qualifications" () {
        given:
        customerService.getBusinessQualifications(_ as String) >> new ResponseBusinessCustomersQualification()

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/customers/v1/business/qualifications', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "customers consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch business qualifications V2" () {
        given:
        customerService.getBusinessQualifications(_ as String) >> new ResponseBusinessCustomersQualification()

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/customers/v2/business/qualifications', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "customers consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch business complimentary info" () {
        given:
        customerService.getBusinessComplimentaryInfo(_ as String) >> new ResponseBusinessCustomersComplimentaryInformation()

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/customers/v1/business/complimentary-information', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "customers consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

    def "We can fetch business complimentary info V2" () {
        given:
        customerService.getBusinessComplimentaryInfoV2(_ as String) >> new ResponseBusinessCustomersComplimentaryInformationV2()

        def event = AwsProxyHelper.buildBasicEvent('/open-insurance/customers/v2/business/complimentary-information', HttpMethod.GET)
                .withHeaders(Map.of( "x-fapi-interaction-id", UUID.randomUUID().toString()))
        AuthHelper.authorizeAuthorizationCodeGrant(scopes: "customers consent:urn:raidiaminsurance:bf43d0e5-7bc2-4a5b-b6da-19d43fabd991", event)

        when:
        def response = handler.handleRequest(event, lambdaContext)

        then:
        response.statusCode == HttpStatus.OK.code
        response.body != null

        and:
        response.multiValueHeaders.containsKey('x-fapi-interaction-id')
    }

}
