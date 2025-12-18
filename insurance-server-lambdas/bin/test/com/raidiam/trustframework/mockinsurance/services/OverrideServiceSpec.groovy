package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.models.generated.OverrideData
import com.raidiam.trustframework.mockinsurance.models.generated.OverrideDataResponse
import com.raidiam.trustframework.mockinsurance.models.generated.OverridePayload
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class OverrideServiceSpec extends CleanupSpecification {
    @Inject
    OverrideService overrideService

    def "we can create and fetch a response override" () {
        when:
        def req = new OverridePayload()
                .data(new OverrideData()
                        .method(OverrideData.MethodEnum.GET)
                        .path("/open-insurance")
                        .response(new OverrideDataResponse()
                                .body("test")))

        overrideService.override(req, "client_id")
        overrideService.override(req, "client_id")

        then:
        noExceptionThrown()

        when:
        def overrideEntityOp = overrideService.getOverride("client_id", "/open-insurance", "GET")

        then:
        overrideEntityOp.isPresent()
        overrideEntityOp.get().getClientId() == "client_id"
    }

    def "we can't fetch an expired response override" () {
        when:
        def req = new OverridePayload()
                .data(new OverrideData()
                        .method(OverrideData.MethodEnum.GET)
                        .path("/open-insurance")
                        .timeout(0)
                        .response(new OverrideDataResponse()
                                .body("test")))

        overrideService.override(req, "client_id")

        then:
        noExceptionThrown()

        when:
        def overrideEntityOp = overrideService.getOverride("client_id", "/open-insurance", "GET")

        then:
        overrideEntityOp.isEmpty()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}