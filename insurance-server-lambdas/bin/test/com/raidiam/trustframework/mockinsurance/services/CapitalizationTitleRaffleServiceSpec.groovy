package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.AccountHolderEntity
import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentPermission
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentStatus
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
public class CapitalizationTitleRaffleServiceSpec extends CleanupSpecification {

    @Inject
    CapitalizationTitleRaffleService capitalizationTitleRaffleService

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.QUOTE_CAPITALIZATION_TITLE_RAFFLE_CREATE)
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            runSetup = false
        }
    }

    def "We can create a raffle"() {
        given:
        def raffle = TestEntityDataFactory.aCapitalizationTitleRaffle()

        when:
        def newRaffle = capitalizationTitleRaffleService.createRaffle(raffle, testConsent.getConsentId())
        def getConsent = consentRepository.findByConsentId(testConsent.getConsentId()).get()

        then:
        noExceptionThrown()
        newRaffle.raffleId != null
        newRaffle.clientId == "random_client_id"
        getConsent.status == EnumConsentStatus.CONSUMED.toString()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
