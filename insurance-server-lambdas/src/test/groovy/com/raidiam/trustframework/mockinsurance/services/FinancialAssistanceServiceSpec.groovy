package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupFinancialAssistanceSpecification
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
class FinancialAssistanceServiceSpec extends CleanupFinancialAssistanceSpecification {
    @Inject
    FinancialAssistanceService financialAssistanceService

    @Shared
    FinancialAssistanceContractEntity testFinancialAssistanceContract

    @Shared
    FinancialAssistanceContractMovementEntity testFinancialAssistanceContractMovement

    @Shared
    FinancialAssistanceContractInsuredEntity testFinancialAssistanceContractInsured

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    ConsentEntity testConsent

    @Shared
    ConsentFinancialAssistanceContractEntity testConsentFinancialAssistanceContract

    def setup() {
        if (runSetup) {
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            testConsent = TestEntityDataFactory.aConsent(testAccountHolder.getAccountHolderId(),
                    EnumConsentPermission.FINANCIAL_ASSISTANCE_READ,
                    EnumConsentPermission.FINANCIAL_ASSISTANCE_CONTRACTINFO_READ,
                    EnumConsentPermission.FINANCIAL_ASSISTANCE_MOVEMENTS_READ,
            )
            testConsent.setStatus(EnumConsentStatus.AUTHORISED.toString())
            testConsent = consentRepository.save(testConsent)
            testFinancialAssistanceContract = financialAssistanceContractRepository.save(TestEntityDataFactory.aFinancialAssistanceContract(testAccountHolder.getAccountHolderId()))
            testFinancialAssistanceContractMovement = financialAssistanceContractMovementRepository.save(TestEntityDataFactory.aFinancialAssistanceContractMovement(testFinancialAssistanceContract.getFinancialAssistanceContractId()))
            testFinancialAssistanceContractInsured = financialAssistanceContractInsuredRepository.save(TestEntityDataFactory.aFinancialAssistanceContractInsured(testFinancialAssistanceContract.getFinancialAssistanceContractId()))
            consentFinancialAssistanceContractRepository.save(new ConsentFinancialAssistanceContractEntity(testConsent, testFinancialAssistanceContract))
            runSetup = false
        }
    }

    def "we can get contracts" () {
        when:
        def response = financialAssistanceService.getContracts(Pageable.from(0, 1), testConsent.getConsentId().toString())

        then:
        response.getData()
        response.getData().size() == 1
        response.getData().first()
        response.getData().first().getBrand().getName() == "Mock"
        response.getData().first().getBrand().getCompanies().first().getCnpjNumber() == "12345678901234"
        response.getData().first().getBrand().getCompanies().first().getContracts().first().getContractId() == testFinancialAssistanceContract.getFinancialAssistanceContractId()
    }

    def "we can get a contract info" () {
        when:
        def response = financialAssistanceService.getContractInfo(testFinancialAssistanceContract.getFinancialAssistanceContractId(), testConsent.getConsentId().toString())

        then:
        response.getData() != null
        response.getData().getContractId() == testFinancialAssistanceContract.getFinancialAssistanceContractId()
        response.getData().getCertificateId() == testFinancialAssistanceContract.getCertificateId()
        response.getData().getInterestRate().getAmount() == testFinancialAssistanceContract.getInterestRateAmount()
        response.getData().getCounterInstallments().getPeriodicity().toString() == testFinancialAssistanceContract.getCounterInstallmentPeriodicity()
        response.getData().getInsureds().first().getName() == testFinancialAssistanceContractInsured.getName()
    }

    def "we can get a contract's claims" () {
        when:
        def response = financialAssistanceService.getContractMovements(testFinancialAssistanceContract.getFinancialAssistanceContractId(), testConsent.getConsentId().toString(), Pageable.from(0, 1))

        then:
        response.getData() != null
        response.getData().first().getRemainingCounterInstallmentsQuantity() == testFinancialAssistanceContractMovement.getRemainingCounterInstallmentsQuantity()
        response.getData().first().getLifePensionPmBacAmount().getAmount() == testFinancialAssistanceContractMovement.getLifePensionPmBacAmount()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
