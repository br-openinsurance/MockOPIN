package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
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
class PensionPlanServiceSpec extends CleanupSpecification {

    @Inject
    PensionPlanService pensionPlanService

    @Shared
    PensionPlanContractEntity testPensionPlanContract

    @Shared
    PensionPlanContractClaimEntity testPensionPlanContractClaim

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    @Shared
    ConsentPensionPlanContractEntity testConsentPensionPlanContract

    @Shared
    PensionPlanContractWithdrawalEntity testPensionPlanContractWithdrawal

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.PENSION_PLAN_READ,
                    EnumConsentPermission.PENSION_PLAN_MOVEMENTS_READ,
                    EnumConsentPermission.PENSION_PLAN_PORTABILITIES_READ,
                    EnumConsentPermission.PENSION_PLAN_CONTRACTINFO_READ,
                    EnumConsentPermission.PENSION_PLAN_CLAIM,
                    EnumConsentPermission.PENSION_PLAN_WITHDRAWALS_READ,
            )
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            testPensionPlanContract = pensionPlanContractRepository.save(TestEntityDataFactory.aPensionPlanContract("pension-plan-1", testAccountHolder.getAccountHolderId()))
            consentPensionPlanContractRepository.save(new ConsentPensionPlanContractEntity(testConsent, testPensionPlanContract))
            testPensionPlanContractClaim = pensionPlanContractClaimRepository.save(TestEntityDataFactory.aPensionPlanContractClaim(testPensionPlanContract.getPensionPlanContractId()))
            testPensionPlanContractWithdrawal = pensionPlanContractWithdrawalRepository.save(TestEntityDataFactory.aPensionPlanContractWithdrawal(testPensionPlanContract.getPensionPlanContractId()))
            runSetup = false
        }
    }

    def "we can get contracts" () {
        when:
        def response = pensionPlanService.getContracts(Pageable.from(0, 1), testConsent.getConsentId().toString())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get contracts V2" () {
        when:
        def response = pensionPlanService.getContractsV2(Pageable.from(0, 1), testConsent.getConsentId().toString())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
    }

    def "we can get a contract info" () {
        when:
        def response = pensionPlanService.getContractInfo(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a contract info V2" () {
        when:
        def response = pensionPlanService.getContractInfoV2(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
    }

    def "we can get a contract's claims" () {
        when:
        def response = pensionPlanService.getContractClaims(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a contract's claims V2" () {
        when:
        def response = pensionPlanService.getContractClaimsV2(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a contract's withdrawals" () {
        when:
        def response = pensionPlanService.getContractWithdrawals(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a contract's withdrawals V2" () {
        when:
        def response = pensionPlanService.getContractWithdrawalsV2(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a contract's movements" () {
        when:
        def response = pensionPlanService.getContractMovements(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
    }

    def "we can get a contract's portabilities" () {
        when:
        def response = pensionPlanService.getContractPortabilities(testPensionPlanContract.getPensionPlanContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

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
