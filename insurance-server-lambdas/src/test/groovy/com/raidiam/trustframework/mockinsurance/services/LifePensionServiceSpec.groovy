package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.*
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentPermission
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentStatus
import io.micronaut.data.model.Pageable
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class LifePensionServiceSpec extends CleanupSpecification {

    @Inject
    LifePensionService lifePensionService

    @Shared
    LifePensionContractEntity testLifePensionContract

    @Shared
    LifePensionContractClaimEntity testLifePensionContractClaim

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    @Shared
    ConsentLifePensionContractEntity testConsentLifePensionContract

    @Shared
    LifePensionContractWithdrawalEntity testLifePensionContractWithdrawal

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.LIFE_PENSION_READ,
                    EnumConsentPermission.LIFE_PENSION_MOVEMENTS_READ,
                    EnumConsentPermission.LIFE_PENSION_PORTABILITIES_READ,
                    EnumConsentPermission.LIFE_PENSION_CONTRACTINFO_READ,
                    EnumConsentPermission.LIFE_PENSION_CLAIM,
                    EnumConsentPermission.LIFE_PENSION_WITHDRAWALS_READ,
            )
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            testLifePensionContract = lifePensionContractRepository.save(TestEntityDataFactory.aLifePensionContract(testAccountHolder.getAccountHolderId()))
            consentLifePensionContractRepository.save(new ConsentLifePensionContractEntity(testConsent, testLifePensionContract))
            testLifePensionContractClaim = lifePensionContractClaimRepository.save(TestEntityDataFactory.aLifePensionContractClaim(testLifePensionContract.getLifePensionContractId()))
            testLifePensionContractWithdrawal = lifePensionContractWithdrawalRepository.save(TestEntityDataFactory.aLifePensionContractWithdrawal(testLifePensionContract.getLifePensionContractId()))
            runSetup = false
        }
    }

    def "we can get contracts" () {
        when:
        def response = lifePensionService.getContracts(Pageable.from(0, 1), testConsent.getConsentId().toString())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get a contract info" () {
        when:
        def response = lifePensionService.getContractInfo(testLifePensionContract.getLifePensionContractId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a contract's claims" () {
        when:
        def response = lifePensionService.getContractClaims(testLifePensionContract.getLifePensionContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a contract's withdrawals" () {
        when:
        def response = lifePensionService.getContractWithdrawals(testLifePensionContract.getLifePensionContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a contract's movements" () {
        when:
        def response = lifePensionService.getContractMovements(testLifePensionContract.getLifePensionContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a contract's portabilities" () {
        when:
        def response = lifePensionService.getContractPortabilities(testLifePensionContract.getLifePensionContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
