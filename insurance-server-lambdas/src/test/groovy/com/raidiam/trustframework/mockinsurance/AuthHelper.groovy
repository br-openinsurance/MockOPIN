package com.raidiam.trustframework.mockinsurance

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This class injects an OAuth token into requests, to allow us to
 * test the behaviour of our security annotations
 */
class AuthHelper {
    static final Logger LOG = LoggerFactory.getLogger(AuthHelper.class)
    static ObjectMapper MAPPER = new ObjectMapper()

    static void authorize(params, APIGatewayProxyRequestEvent event) {
        def scopes = String.valueOf(params['scopes'])
        def accessToken = [
                subject: "user@bank.com",
                scope: scopes,
                active: true,
                token_type: "Bearer",
                exp: 1571660599,
                client_id: "client1",
                org_id: params['org_id'],
                software_id: params['software_id'],
        ]
        event.getRequestContext().setAuthorizer(Map.of("access_token", MAPPER.writeValueAsString(accessToken)))
        LOG.info("AuthHelper token - {}", MAPPER.writeValueAsString(accessToken))
    }

    static void authorizeAuthorizationCodeGrant(params, APIGatewayProxyRequestEvent event) {
        def scopes = String.valueOf(params['scopes'])
        def accessToken = [
                sub: "user@bank.com",
                scope: scopes,
                active: true,
                token_type: "Bearer",
                exp: 1571660599,
                client_id: "client1",
                org_id: params['org_id'],
                software_id: params['software_id'],
        ]
        event.getRequestContext().setAuthorizer(Map.of("access_token", MAPPER.writeValueAsString(accessToken)))
        LOG.info("AuthHelper token - {}", MAPPER.writeValueAsString(accessToken))
    }

    static void authorizeClientCredentialsGrant(params, APIGatewayProxyRequestEvent event) {
        def scopes = String.valueOf(params['scopes'])
        def accessToken = [
                scope: scopes,
                active: true,
                token_type: "Bearer",
                exp: 1571660599,
                client_id: "client1",
                org_id: params['org_id'],
                software_id: params['software_id'],
        ]
        event.getRequestContext().setAuthorizer(Map.of("access_token", MAPPER.writeValueAsString(accessToken)))
        LOG.info("AuthHelper token - {}", MAPPER.writeValueAsString(accessToken))
    }
}
