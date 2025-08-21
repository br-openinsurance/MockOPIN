package com.raidiam.trustframework.mockinsurance.cleanups

import com.raidiam.trustframework.mockinsurance.repository.AccountHolderRepository
import com.raidiam.trustframework.mockinsurance.repository.BeneficiaryInfoRepository
import com.raidiam.trustframework.mockinsurance.repository.ConsentRepository
import com.raidiam.trustframework.mockinsurance.repository.ConsentHousingPolicyRepository
import com.raidiam.trustframework.mockinsurance.repository.DeductibleRepository
import com.raidiam.trustframework.mockinsurance.repository.IntermediaryRepository
import com.raidiam.trustframework.mockinsurance.repository.POSRepository
import com.raidiam.trustframework.mockinsurance.repository.PaymentRepository
import com.raidiam.trustframework.mockinsurance.repository.PersonalInfoRepository
import com.raidiam.trustframework.mockinsurance.repository.HousingPolicyBranchInsuredObjectRepository
import com.raidiam.trustframework.mockinsurance.repository.HousingPolicyBranchInsuredObjectLenderRepository
import com.raidiam.trustframework.mockinsurance.repository.HousingPolicyBranchInsuredRepository
import com.raidiam.trustframework.mockinsurance.repository.HousingPolicyClaimCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.HousingPolicyClaimRepository
import com.raidiam.trustframework.mockinsurance.repository.HousingPolicyInsuredObjectCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.HousingPolicyInsuredObjectRepository
import com.raidiam.trustframework.mockinsurance.repository.HousingPolicyPremiumCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.HousingPolicyPremiumRepository
import com.raidiam.trustframework.mockinsurance.repository.HousingPolicyRepository
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class CleanupHousingSpecification extends Specification {
    Logger LOG = LoggerFactory.getLogger(CleanupHousingSpecification.class)

    @Inject
    AccountHolderRepository accountHolderRepository

    @Inject
    ConsentRepository consentRepository

    @Inject
    PersonalInfoRepository personalInfoRepository

    @Inject
    BeneficiaryInfoRepository beneficiaryInfoRepository

    @Inject
    IntermediaryRepository intermediaryRepository

    @Inject
    DeductibleRepository deductibleRepository

    @Inject
    POSRepository posRepository

    @Inject
    PaymentRepository paymentRepository

    @Inject
    HousingPolicyRepository housingPolicyRepository

    @Inject
    HousingPolicyInsuredObjectRepository housingPolicyInsuredObjectRepository

    @Inject
    HousingPolicyInsuredObjectCoverageRepository housingPolicyInsuredObjectCoverageRepository

    @Inject
    HousingPolicyBranchInsuredObjectRepository housingPolicyBranchInsuredObjectRepository

    @Inject
    HousingPolicyBranchInsuredObjectLenderRepository housingPolicyBranchInsuredObjectLenderRepository

    @Inject
    HousingPolicyBranchInsuredRepository housingPolicyBranchInsuredRepository

    @Inject
    HousingPolicyClaimRepository housingPolicyClaimRepository

    @Inject
    HousingPolicyClaimCoverageRepository housingPolicyClaimCoverageRepository

    @Inject
    HousingPolicyPremiumRepository housingPolicyPremiumRepository

    @Inject
    HousingPolicyPremiumCoverageRepository housingPolicyPremiumCoverageRepository

    @Inject
    ConsentHousingPolicyRepository consentHousingPolicyRepository

    @Shared
    boolean runSetup = true

    @Shared
    boolean runCleanup = false

    def cleanup() {
        if (runCleanup) {
            LOG.info("Running Cleanup - Housing API")
            accountHolderRepository.deleteAll()
            consentRepository.deleteAll()
            personalInfoRepository.deleteAll()
            beneficiaryInfoRepository.deleteAll()
            intermediaryRepository.deleteAll()
            deductibleRepository.deleteAll()
            posRepository.deleteAll()
            paymentRepository.deleteAll()
            housingPolicyRepository.deleteAll()
            housingPolicyInsuredObjectRepository.deleteAll()
            housingPolicyInsuredObjectCoverageRepository.deleteAll()
            housingPolicyBranchInsuredObjectRepository.deleteAll()
            housingPolicyBranchInsuredObjectLenderRepository.deleteAll()
            housingPolicyBranchInsuredRepository.deleteAll()
            housingPolicyClaimRepository.deleteAll()
            housingPolicyClaimCoverageRepository.deleteAll()
            housingPolicyPremiumRepository.deleteAll()
            housingPolicyPremiumCoverageRepository.deleteAll()
            consentHousingPolicyRepository.deleteAll()
            runCleanup = false
            runSetup = true
        }
    }
}
