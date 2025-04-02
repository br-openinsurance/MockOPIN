package com.raidiam.trustframework.mockinsurance

import com.nimbusds.jose.Payload
import com.nimbusds.jose.PlainHeader
import com.nimbusds.jose.PlainObject
import com.raidiam.trustframework.mockinsurance.auth.SimpleAuthorisation
import io.micronaut.core.convert.DefaultMutableConversionService
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyServletRequest
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyServletResponse
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpRequest
import io.micronaut.http.simple.SimpleHttpHeaders
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.filters.AuthenticationFetcher
import io.micronaut.core.convert.ConversionService

import io.reactivex.Flowable
import org.reactivestreams.Publisher
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class SimpleAuthorisationSpec extends Specification {

    AuthenticationFetcher auth = new SimpleAuthorisation(null)

    @Shared
    def mockEvent = AwsProxyHelper.buildBasicEvent("", HttpMethod.GET)
    @Shared
    def mockRequest = new ApiGatewayProxyServletRequest(mockEvent, Mock(ApiGatewayProxyServletResponse<String>), Mock(ConversionService), () -> null)

    def "If no token provided, no authorisation is returned"() {
        expect:
        auth.fetchAuthentication(request) == Flowable.empty()

        where:

        request << [Stub(HttpRequest) { getHeaders() >> Mock(HttpHeaders) }, mockRequest]
    }

    @Unroll
    def "When a lambda provides a token"() {

        given:
        def orgId = 'd7eb7040-cbd6-473b-a48d-09df37297e4e'
        def ssId = 'a841c374-deb5-4958-8643-b3829192fc95'
        def event = AwsProxyHelper.buildBasicEvent('/test', HttpMethod.GET)
        AuthHelper.authorize(scopes: scope, org_id: orgId, software_id:ssId, event)
        var conversion = new DefaultMutableConversionService();
        def request = new ApiGatewayProxyServletRequest(event, Mock(ApiGatewayProxyServletResponse<String>), conversion, () -> null)

        when:
        Publisher<Authentication> result = auth.fetchAuthentication(request)

        then:
        Authentication authentication
        result.subscribe {
            authentication = it
        }
        authentication.name == "ALLOWED"
        authentication.attributes['roles'] == roles
        request.getAttribute('clientId').get() == 'client1'
        request.getAttribute('orgId').get() == orgId
        request.getAttribute('ssId').get() == ssId

        where:
        scope  | roles
        'consents'  | ['CONSENTS_MANAGE']

    }

    def "When http provides a token"() {

        given:
        PlainObject plainObject = new PlainObject(new PlainHeader(), new Payload([scope: scope]))
        def bearerToken = plainObject.serialize()
        HttpRequest request = Stub(HttpRequest)
        request.getHeaders() >> new SimpleHttpHeaders(["Authorization": "Bearer ${bearerToken}".toString()], null)
        when:
        Publisher<Authentication> result = auth.fetchAuthentication(request)

        then:
        Authentication authentication
        result.subscribe {
            authentication = it
        }
        authentication.name == "ALLOWED"
        authentication.attributes['roles'] == roles

        where:
        scope  | roles
        'consents'  | ['CONSENTS_MANAGE']
    }

    def "When http provides not a bearer token"() {

        given:
        PlainObject plainObject = new PlainObject(new PlainHeader(), new Payload([scope: scope]))
        def bearerToken = plainObject.serialize()
        HttpRequest request = Stub(HttpRequest)
        request.getHeaders() >> new SimpleHttpHeaders(["Authorization": "Basic ${bearerToken}".toString()], null)
        when:
        Publisher<Authentication> result = auth.fetchAuthentication(request)

        then:
        result == Flowable.empty()

        where:
        scope  | roles
        'consents'  | ['CONSENTS_MANAGE']
        'payments'  | ['PAYMENTS_MANAGE']
        'op:payments'  | ['PAYMENTS_FULL_MANAGE']
        'accounts'  | ['ACCOUNTS_READ']
        'consents payments' | ['CONSENTS_MANAGE', 'PAYMENTS_MANAGE']
    }

    def "When http provides not a unparsable token"() {

        given:
        PlainObject plainObject = new PlainObject(new PlainHeader(), new Payload([scope: scope]))
        def bearerToken = plainObject.serialize()
        HttpRequest request = Stub(HttpRequest)
        request.getHeaders() >> new SimpleHttpHeaders(["Authorization": "Bearer tewteewrwe".toString()], null)
        when:
        Publisher<Authentication> result = auth.fetchAuthentication(request)

        then:
        result == Flowable.empty()

        where:
        scope  | roles
        'consents'  | ['CONSENTS_MANAGE']
        'payments'  | ['PAYMENTS_MANAGE']
        'op:payments'  | ['PAYMENTS_FULL_MANAGE']
        'accounts'  | ['ACCOUNTS_READ']
        'consents payments' | ['CONSENTS_MANAGE', 'PAYMENTS_MANAGE']
    }

}
