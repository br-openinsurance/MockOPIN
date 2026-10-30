package com.raidiam.trustframework.mockinsurance.utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import spock.lang.Specification

import static com.raidiam.trustframework.mockinsurance.utils.LogUtils.logIfPresent
import static com.raidiam.trustframework.mockinsurance.utils.LogUtils.logObject

class LogUtilsSpec extends Specification {

    def "logIfPresent does not log when value is null or empty"() {
        given:
        def logger = Mock(Logger)

        when:
        logIfPresent("message: {}", value, logger)

        then:
        0 * logger.info(*_)

        where:
        value << [null, ""]
    }

    def "logIfPresent logs when value is present"() {
        given:
        def logger = Mock(Logger)

        when:
        logIfPresent("message: {}", "someValue", logger)

        then:
        1 * logger.info("message: {}", "someValue")
    }

    def "logObject does nothing when debug is not enabled"() {
        given:
        def logger = Mock(Logger) { isDebugEnabled() >> false }
        def mapper = Mock(ObjectMapper)

        when:
        logObject(mapper, "some object", logger)

        then:
        0 * mapper.writeValueAsString(_)
        0 * logger.debug(*_)
        0 * logger.error(*_)
    }

    def "logObject logs the serialized object when debug is enabled"() {
        given:
        def logger = Mock(Logger) { isDebugEnabled() >> true }
        def mapper = new ObjectMapper()

        when:
        logObject(mapper, [key: "value"], logger)

        then:
        1 * logger.debug("{} - {}", "LinkedHashMap", '{"key":"value"}')
    }

    def "logObject logs an error when serialization fails"() {
        given:
        def logger = Mock(Logger) { isDebugEnabled() >> true }
        def mapper = new ObjectMapper()

        when:
        logObject(mapper, new Object() {}, logger)

        then:
        1 * logger.error("{} - Error writing object as JSON: ", "", _ as Exception)
    }
}
