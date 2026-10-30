package com.raidiam.trustframework.mockinsurance.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.classic.util.LogbackMDCAdapter
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

import java.nio.charset.StandardCharsets

import static com.raidiam.trustframework.mockinsurance.logging.LogRedactor.REDACTED
import static com.raidiam.trustframework.mockinsurance.logging.LogRedactor.redact

class LogRedactorSpec extends Specification {

    private static final String JWT = "eyJhbGciOiJQUzI1NiJ9.eyJzdWIiOiJ1c2VyIn0.c2lnbmF0dXJl"

    def "redacts credentials and personal identifiers"() {
        expect:
        redact(input) == expected

        where:
        input                                                  | expected
        '{"access_token":"abc.def","token_type":"Bearer"}'     | '{"access_token":"' + REDACTED + '","token_type":"Bearer"}'
        '{"Password" : "hunter2"}'                             | '{"Password" : "' + REDACTED + '"}'
        '{"client_secret":"s3cr3t","client_id":"client-1"}'    | '{"client_secret":"' + REDACTED + '","client_id":"client-1"}'
        'grant_type=refresh_token&refresh_token=xyz&scope=a'   | 'grant_type=refresh_token&refresh_token=' + REDACTED + '&scope=a'
        'Authorization: Bearer abc123-_.~+/=='                 | 'Authorization: Bearer ' + REDACTED
        'Authorization: Basic dXNlcjpwYXNz'                    | 'Authorization: Basic ' + REDACTED
        "Request JWT ${JWT} received"                          | "Request JWT ${REDACTED} received"
        '{"identification":"76109277673","rel":"CPF"}'         | '{"identification":"' + REDACTED + '","rel":"CPF"}'
        'cpf 761.092.776-73'                                   | 'cpf ' + REDACTED
        '{"identification":"50685362006773","rel":"CNPJ"}'     | '{"identification":"' + REDACTED + '","rel":"CNPJ"}'
        'cnpj 50.685.362/0067-73'                              | 'cnpj ' + REDACTED
    }

    def "leaves non-sensitive content untouched"() {
        expect:
        redact(input) == input

        where:
        input << [
                null,
                "",
                "Checking notification trigger for status AUTHORISED",
                "consent urn:raidiambank:c5a6e2b4-1a9f-4b3e-8d2c-123456789012",
                '{"amount":"100.00","page":1,"pageSize":25}',
                "epoch millis 1727180000000 and seconds 1727180000",
                '{"token_type":"Bearer"}'
        ]
    }

    def "encoder redacts the message and stack trace"() {
        given:
        def encoder = new JsonLoggingEncoder()
        def context = new LoggerContext()
        context.setMDCAdapter(new LogbackMDCAdapter())
        def logger = context.getLogger("test")
        def event = new LoggingEvent("fqcn", logger, Level.INFO,
                "user {} token {}", new RuntimeException("bad token ${JWT}"), ["76109277673", JWT] as Object[])

        when:
        def json = new ObjectMapper().readValue(new String(encoder.encode(event), StandardCharsets.UTF_8), Map)

        then:
        json.message == "user ${REDACTED} token ${REDACTED}"
        json.stack.contains("bad token ${REDACTED}")
        !json.stack.contains(JWT)
    }
}
