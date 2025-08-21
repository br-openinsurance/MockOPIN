package com.raidiam.trustframework.mockinsurance.cleanups

import com.raidiam.trustframework.mockinsurance.repository.AccountHolderRepository
import com.raidiam.trustframework.mockinsurance.repository.BeneficiaryInfoRepository
import com.raidiam.trustframework.mockinsurance.repository.CoinsurerRepository
import com.raidiam.trustframework.mockinsurance.repository.ConsentFinancialRiskPolicyRepository
import com.raidiam.trustframework.mockinsurance.repository.ConsentRepository
import com.raidiam.trustframework.mockinsurance.repository.DeductibleRepository
import com.raidiam.trustframework.mockinsurance.repository.EndorsementRepository
import com.raidiam.trustframework.mockinsurance.repository.FinancialRiskPolicyClaimCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.FinancialRiskPolicyClaimRepository
import com.raidiam.trustframework.mockinsurance.repository.FinancialRiskPolicyCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.FinancialRiskPolicyInsuredObjectCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.FinancialRiskPolicyInsuredObjectRepository
import com.raidiam.trustframework.mockinsurance.repository.FinancialRiskPolicyPremiumCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.FinancialRiskPolicyPremiumRepository
import com.raidiam.trustframework.mockinsurance.repository.FinancialRiskPolicyRepository
import com.raidiam.trustframework.mockinsurance.repository.IntermediaryRepository
import com.raidiam.trustframework.mockinsurance.repository.POSRepository
import com.raidiam.trustframework.mockinsurance.repository.PaymentRepository
import com.raidiam.trustframework.mockinsurance.repository.PersonalInfoRepository
import com.raidiam.trustframework.mockinsurance.repository.PrincipalInfoRepository
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class CleanupFinancialRiskSpecification extends Specification {
    Logger LOG = LoggerFactory.getLogger(CleanupFinancialRiskSpecification.class)

    @Inject
    AccountHolderRepository accountHolderRepository

    @Inject
    ConsentRepository consentRepository

    @Inject
    FinancialRiskPolicyRepository financialRiskPolicyRepository

    @Inject
    BeneficiaryInfoRepository beneficiaryInfoRepository

    @Inject
    CoinsurerRepository coinsurerRepository

    @Inject
    FinancialRiskPolicyClaimCoverageRepository financialRiskPolicyClaimCoverageRepository

    @Inject
    DeductibleRepository deductibleRepository

    @Inject
    POSRepository posRepository

    @Inject
    FinancialRiskPolicyCoverageRepository financialRiskPolicyCoverageRepository

    @Inject
    FinancialRiskPolicyInsuredObjectRepository financialRiskPolicyInsuredObjectRepository

    @Inject
    FinancialRiskPolicyInsuredObjectCoverageRepository financialRiskPolicyInsuredObjectCoverageRepository

    @Inject
    FinancialRiskPolicyPremiumCoverageRepository financialRiskPolicyPremiumCoverageRepository

    @Inject
    IntermediaryRepository intermediaryRepository

    @Inject
    PaymentRepository paymentRepository

    @Inject
    PersonalInfoRepository personalInfoRepository

    @Inject
    PrincipalInfoRepository principalInfoRepository

    @Inject
    ConsentFinancialRiskPolicyRepository consentFinancialRiskPolicyRepository

    @Inject
    FinancialRiskPolicyClaimRepository financialRiskPolicyClaimRepository

    @Inject
    FinancialRiskPolicyPremiumRepository financialRiskPolicyPremiumRepository

    @Shared
    boolean runSetup = true

    @Shared
    boolean runCleanup = false

    def cleanup() {
        if (runCleanup) {
            LOG.info("Running Cleanup")
            accountHolderRepository.deleteAll()
            consentRepository.deleteAll()
            financialRiskPolicyRepository.deleteAll()
            beneficiaryInfoRepository.deleteAll()
            coinsurerRepository.deleteAll()
            financialRiskPolicyClaimCoverageRepository.deleteAll()
            posRepository.deleteAll()
            deductibleRepository.deleteAll()
            financialRiskPolicyCoverageRepository.deleteAll()
            financialRiskPolicyInsuredObjectRepository.deleteAll()
            financialRiskPolicyInsuredObjectCoverageRepository.deleteAll()
            financialRiskPolicyPremiumCoverageRepository.deleteAll()
            intermediaryRepository.deleteAll()
            paymentRepository.deleteAll()
            personalInfoRepository.deleteAll()
            principalInfoRepository.deleteAll()
            consentFinancialRiskPolicyRepository.deleteAll()
            financialRiskPolicyClaimRepository.deleteAll()
            financialRiskPolicyPremiumRepository.deleteAll()
            runCleanup = false
            runSetup = true
        }
    }
}
