package com.raidiam.trustframework.mockinsurance.fapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpHeaders
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.server.exceptions.response.Error as HttpError
import io.micronaut.http.server.exceptions.response.ErrorContext
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.authentication.AuthorizationException
import spock.lang.Specification

import static io.micronaut.http.HttpMethod.GET
import static io.micronaut.http.HttpMethod.PATCH
import static io.micronaut.http.HttpMethod.POST
import static io.micronaut.http.HttpStatus.BAD_REQUEST
import static io.micronaut.http.HttpStatus.FORBIDDEN
import static io.micronaut.http.HttpStatus.OK
import static io.micronaut.http.HttpStatus.UNPROCESSABLE_ENTITY

class ErrorResponseHandlerSpec extends Specification {

    def objectMapper = new ObjectMapper()
    def insuranceLambdaUtils = Mock(InsuranceLambdaUtils)
    def handler = new ErrorResponseHandler(objectMapper, insuranceLambdaUtils)

    def "POST creation endpoints return a single error object for 422s only on v1.x (#description)"() {
        given: "a POST request to a creation endpoint that fails validation"
            def errorContext = errorContextFor(POST, path)

        and: "the endpoint responds with a 422 Unprocessable Entity status"
            def response = mockResponse(UNPROCESSABLE_ENTITY)

        and: "the JSON body the handler writes to the response is captured"
            def capturedJson = captureJsonBody(response)

        when: "the error handler processes the response"
            handler.processResponse(errorContext, response)

        then: "the errors field is a single object for v1.x paths and an array for v2.x paths, per spec"
            objectMapper.readTree(capturedJson.value).get("errors").isArray() == expectArray

        where:
            description                     | path                                                          | expectArray
            "withdrawal v1"                 | "/open-insurance/withdrawal/v1/capitalization-title/request" | false
            "withdrawal v2"                 | "/open-insurance/withdrawal/v2/capitalization-title/request" | true
            "claim-notification v1"         | "/open-insurance/claim-notification/v1/request/damage/abc"   | false
            "claim-notification v2"         | "/open-insurance/claim-notification/v2/request/damage/abc"   | true
            "endorsement v1"                | "/open-insurance/endorsement/v1/request/abc"                 | false
            "endorsement v2"                | "/open-insurance/endorsement/v2/request/abc"                 | true
            "quote- v1 (pre-existing rule)" | "/open-insurance/quote-auto/v1/request"                      | false
            "quote- v2 (pre-existing rule)" | "/open-insurance/quote-auto/v2/request"                      | true
            "unrelated domain"              | "/open-insurance/policy-info/v1/policies"                    | true
    }

    def "the v1 single-error-object rule does not apply to non-POST requests"() {
        given: "a non-POST request to a v1 withdrawal endpoint that fails validation"
            def errorContext = errorContextFor(PATCH, "/open-insurance/withdrawal/v1/capitalization-title/request")

        and: "the endpoint responds with a 422 Unprocessable Entity status"
            def response = mockResponse(UNPROCESSABLE_ENTITY)

        and: "the JSON body the handler writes to the response is captured"
            def capturedJson = captureJsonBody(response)

        when: "the error handler processes the response"
            handler.processResponse(errorContext, response)

        then: "the errors field stays an array, since the single-error-object rule only applies to POST requests"
            objectMapper.readTree(capturedJson.value).get("errors").isArray()
    }

    def "the v1 single-error-object rule does not apply outside 422 responses"() {
        given: "a POST request to a v1 withdrawal endpoint"
            def errorContext = errorContextFor(POST, "/open-insurance/withdrawal/v1/capitalization-title/request")

        and: "the endpoint responds with a 400 Bad Request status"
            def response = mockResponse(BAD_REQUEST)

        and: "the JSON body the handler writes to the response is captured"
            def capturedJson = captureJsonBody(response)

        when: "the error handler processes the response"
            handler.processResponse(errorContext, response)

        then: "the errors field stays an array, since the single-error-object rule only applies to 422 responses"
            objectMapper.readTree(capturedJson.value).get("errors").isArray()
    }

    def "the consents/v2 single-error-object rule applies regardless of HTTP method"() {
        given: "a non-POST request to the consents/v2 endpoint that fails validation"
            def errorContext = errorContextFor(GET, "/open-insurance/consents/v2/consents/abc")

        and: "the endpoint responds with a 422 Unprocessable Entity status"
            def response = mockResponse(UNPROCESSABLE_ENTITY)

        and: "the JSON body the handler writes to the response is captured"
            def capturedJson = captureJsonBody(response)

        when: "the error handler processes the response"
            handler.processResponse(errorContext, response)

        then: "the errors field is a single object, since consents/v2 always uses this rule"
            !objectMapper.readTree(capturedJson.value).get("errors").isArray()
    }

    def "a forbidden access-denied cause produces a generic 403 error instead of the original message"() {
        given: "a request that was denied because the authenticated user is forbidden from the resource"
            def rootCause = new AuthorizationException(Stub(Authentication))
            def errorContext = errorContextFor(GET, "/open-insurance/policy-info/v1/policies", rootCause)

        and: "a response for the handler to write the error body into"
            def response = mockResponse(OK)

        and: "the JSON body the handler writes to the response is captured"
            def capturedJson = captureJsonBody(response)

        when: "the error handler processes the response"
            handler.processResponse(errorContext, response)

        then: "the errors item is a generic 403 Forbidden error, not the original error message"
            def errorItem = objectMapper.readTree(capturedJson.value).get("errors").get(0)
            errorItem.get("code").asText() == String.valueOf(FORBIDDEN.code)
            errorItem.get("title").asText() == FORBIDDEN.toString()
            errorItem.get("detail").asText() == FORBIDDEN.toString()
    }

    def "a non-forbidden authorization cause still uses the original error message"() {
        given: "a request that was denied due to missing authentication, not because it was forbidden"
            def rootCause = new AuthorizationException(null)
            def errorContext = errorContextFor(GET, "/open-insurance/policy-info/v1/policies", rootCause)

        and: "a response for the handler to write the error body into"
            def response = mockResponse(OK)

        and: "the JSON body the handler writes to the response is captured"
            def capturedJson = captureJsonBody(response)

        when: "the error handler processes the response"
            handler.processResponse(errorContext, response)

        then: "the errors item still reflects the original error message, not a generic forbidden error"
            def errorItem = objectMapper.readTree(capturedJson.value).get("errors").get(0)
            errorItem.get("code").asText() == "ERRO_IDEMPOTENCIA"
            errorItem.get("detail").asText() == "something went wrong"
    }

    private static class JsonCapture {
        String value
    }

    private ErrorContext errorContextFor(HttpMethod method, String path, Throwable rootCause = null) {
        def httpError = Mock(HttpError) {
            getMessage() >> "ERRO_IDEMPOTENCIA: something went wrong"
        }
        def request = Mock(HttpRequest) {
            getPath() >> path
            getMethod() >> method
        }
        return Mock(ErrorContext) {
            getRequest() >> request
            getRootCause() >> Optional.ofNullable(rootCause)
            getErrors() >> [httpError]
        }
    }

    private MutableHttpResponse mockResponse(HttpStatus status) {
        def response = Mock(MutableHttpResponse)
        response.getStatus() >> status
        response.getHeaders() >> Mock(MutableHttpHeaders)
        return response
    }

    private JsonCapture captureJsonBody(MutableHttpResponse response) {
        def capture = new JsonCapture()
        response.body(_) >> { args -> capture.value = args[0] as String; return response }
        return capture
    }
}
