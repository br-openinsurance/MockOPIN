package com.raidiam.trustframework.mockinsurance

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.core.convert.ConversionService
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyServletRequest
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyServletResponse
import io.micronaut.http.HttpMethod
import spock.lang.Specification

class AwsProxyHelper extends Specification {
    def static mapper = new ObjectMapper().findAndRegisterModules()

    def static X_FAPI_INTERACTION_ID_KEY = "x-fapi-interaction-id"
    def static X_IDEMPOTENCY_KEY_KEY = "x-idempotency-key"

    static APIGatewayProxyRequestEvent buildBasicEvent() {
        return new APIGatewayProxyRequestEvent()
                .withRequestContext(new APIGatewayProxyRequestEvent.ProxyRequestContext())
    }

    static APIGatewayProxyRequestEvent buildBasicEvent(String path, HttpMethod method) {
        return buildBasicEvent()
                .withPath(path)
                .withHttpMethod(method.toString())
    }

    static APIGatewayProxyRequestEvent buildEventWithHeaders(String path, HttpMethod method, Map<String, String> headers) {
        return buildBasicEvent(path, method).withHeaders(headers)
    }

    static APIGatewayProxyRequestEvent buildEventWithBody(String path, HttpMethod method, Object body) {
        return buildBasicEvent(path, method).withBody(inputToString(body))
    }

    static APIGatewayProxyRequestEvent buildEventWithBodyAndHeaders(String path, HttpMethod method, Object body, Map<String, String> headers) {
        return buildEventWithHeaders(path, method, headers).withBody(inputToString(body))
    }

    static APIGatewayProxyRequestEvent buildAuthorisedEventWithBody(String path, HttpMethod method, String body) {
        def event = buildEventWithBodyAndHeaders(path, method, body,
                Map.of(X_IDEMPOTENCY_KEY_KEY, UUID.randomUUID().toString()))

        AuthHelper.authorize(scopes: "payments", org_id: "issuer", event)
        return event
    }

    static APIGatewayProxyRequestEvent buildAuthorisedPaymentManagerEventWithBody(String path, HttpMethod method, String body) {
        def event = buildEventWithBodyAndHeaders(path, method, body,
                Map.of(X_IDEMPOTENCY_KEY_KEY, UUID.randomUUID().toString(),
                       X_FAPI_INTERACTION_ID_KEY, UUID.randomUUID().toString()))

        AuthHelper.authorize(scopes: "payments op:payments", org_id: "issuer", event)
        return event
    }

    static APIGatewayProxyRequestEvent buildJwtEventWithScopes(String path, HttpMethod method, String scopes) {
        def event = buildBasicEvent(path, method)
                .withHeaders(Map.of(
                        X_IDEMPOTENCY_KEY_KEY, UUID.randomUUID().toString(),
                        "Content-Type", "application/jwt",
                        "Accept", "application/jwt",
                        X_FAPI_INTERACTION_ID_KEY, UUID.randomUUID().toString()
                ))

        AuthHelper.authorize(scopes: scopes, org_id: "issuer", event)
        return event
    }

    static APIGatewayProxyRequestEvent buildJwtEvent(String path, HttpMethod method) {
        return buildJwtEventWithScopes(path, method, "payments")
    }

    static APIGatewayProxyRequestEvent buildJwtEventWithBodyNoInteractionId(String path, HttpMethod method, String body) {
        def event = buildBasicEvent(path, method)
                .withHeaders(Map.of(
                        X_IDEMPOTENCY_KEY_KEY, UUID.randomUUID().toString(),
                        "Content-Type", "application/jwt",
                        "Accept", "application/jwt",
                ))
                .withBody(body)

        AuthHelper.authorize(scopes: "payments", org_id: "issuer", event)
        return event
    }

    static APIGatewayProxyRequestEvent buildJwtEventWithBody(String path, HttpMethod method, String body) {
        return buildJwtEvent(path, method).withBody(body)
    }

    static APIGatewayProxyRequestEvent buildManagerJwtEvent(String path, HttpMethod method) {
        return buildJwtEventWithScopes(path, method, "payments op:payments")
    }

    ApiGatewayProxyServletRequest<?> buildServletRequest(APIGatewayProxyRequestEvent event, Object body) {
        return new ApiGatewayProxyServletRequest(event,
                Mock(ApiGatewayProxyServletResponse),
                Mock(ConversionService, { convertRequired(_, _) >> body }),
                (a, b) -> body)
    }

    ApiGatewayProxyServletRequest<?> buildServletRequestWithBody(Object body) {
        def requestEvent = buildEventWithBody(
                "/", HttpMethod.POST, body)
        return buildServletRequest(requestEvent, body)
    }

    ApiGatewayProxyServletRequest<?> buildServletRequestWithContentType(String contentType) {
        def requestEvent = buildEventWithHeaders(
                "/", HttpMethod.POST,
                Map.of("Content-Type", contentType))
        return buildServletRequest(requestEvent, null)
    }

    ApiGatewayProxyServletRequest<?> buildServletRequestWithBodyAndContentType(Object body, String contentType) {
        def requestEvent = buildEventWithBodyAndHeaders(
                "/", HttpMethod.POST, body,
                Map.of("Content-Type", contentType))
        return new ApiGatewayProxyServletRequest(requestEvent,
                Mock(ApiGatewayProxyServletResponse),
                Mock(ConversionService, {convertRequired(_, _) >> body}),
                (a, b) -> body)
    }

    static String inputToString(Object body) {
        if(body instanceof String) {
            return body
        } else {
            return mapper.writeValueAsString(body)
        }
    }

}
