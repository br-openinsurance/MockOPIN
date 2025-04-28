package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.models.generated.*
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
public class CapitalizationTitleRaffleServiceSpec extends CleanupSpecification {

    @Inject
    CapitalizationTitleRaffleService capitalizationTitleRaffleService

    def "We can create a raffle"() {
        given:
        def raffle = TestEntityDataFactory.aCapitalizationTitleRaffle()

        when:
        def newRaffle = capitalizationTitleRaffleService.createRaffle(raffle)

        then:
        noExceptionThrown()
        newRaffle.raffleId != null
        newRaffle.clientId == "random_client_id"
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
