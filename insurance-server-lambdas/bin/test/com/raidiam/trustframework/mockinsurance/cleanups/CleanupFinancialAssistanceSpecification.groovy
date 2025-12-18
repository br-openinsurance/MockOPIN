package com.raidiam.trustframework.mockinsurance.cleanups

import com.raidiam.trustframework.mockinsurance.repository.AccountHolderRepository
import com.raidiam.trustframework.mockinsurance.repository.ConsentFinancialAssistanceContractRepository
import com.raidiam.trustframework.mockinsurance.repository.ConsentRepository
import com.raidiam.trustframework.mockinsurance.repository.FinancialAssistanceContractInsuredRepository
import com.raidiam.trustframework.mockinsurance.repository.FinancialAssistanceContractMovementRepository
import com.raidiam.trustframework.mockinsurance.repository.FinancialAssistanceContractRepository
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class CleanupFinancialAssistanceSpecification  extends Specification {
    Logger LOG = LoggerFactory.getLogger(CleanupFinancialAssistanceSpecification.class)

    @Inject
    AccountHolderRepository accountHolderRepository

    @Inject
    ConsentRepository consentRepository

    @Inject
    FinancialAssistanceContractRepository financialAssistanceContractRepository

    @Inject
    FinancialAssistanceContractMovementRepository financialAssistanceContractMovementRepository

    @Inject
    FinancialAssistanceContractInsuredRepository financialAssistanceContractInsuredRepository

    @Inject
    ConsentFinancialAssistanceContractRepository consentFinancialAssistanceContractRepository

    @Shared
    boolean runSetup = true

    @Shared
    boolean runCleanup = false

    def cleanup() {
        if (runCleanup) {
            LOG.info("Running Cleanup")
            accountHolderRepository.deleteAll()
            consentRepository.deleteAll()
            financialAssistanceContractRepository.deleteAll()
            financialAssistanceContractMovementRepository.deleteAll()
            financialAssistanceContractInsuredRepository.deleteAll()
            consentFinancialAssistanceContractRepository.deleteAll()
            runCleanup = false
            runSetup = true
        }
    }
}
