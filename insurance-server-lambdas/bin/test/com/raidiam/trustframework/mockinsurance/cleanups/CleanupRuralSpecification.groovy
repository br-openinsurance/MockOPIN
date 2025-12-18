package com.raidiam.trustframework.mockinsurance.cleanups

import com.raidiam.trustframework.mockinsurance.repository.AccountHolderRepository
import com.raidiam.trustframework.mockinsurance.repository.BeneficiaryInfoRepository
import com.raidiam.trustframework.mockinsurance.repository.CoinsurerRepository
import com.raidiam.trustframework.mockinsurance.repository.ConsentRepository
import com.raidiam.trustframework.mockinsurance.repository.ConsentRuralPolicyRepository
import com.raidiam.trustframework.mockinsurance.repository.DeductibleRepository
import com.raidiam.trustframework.mockinsurance.repository.IntermediaryRepository
import com.raidiam.trustframework.mockinsurance.repository.POSRepository
import com.raidiam.trustframework.mockinsurance.repository.PaymentRepository
import com.raidiam.trustframework.mockinsurance.repository.PersonalInfoRepository
import com.raidiam.trustframework.mockinsurance.repository.PrincipalInfoRepository
import com.raidiam.trustframework.mockinsurance.repository.RuralPolicyBranchInsuredObjectRepository
import com.raidiam.trustframework.mockinsurance.repository.RuralPolicyClaimCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.RuralPolicyClaimRepository
import com.raidiam.trustframework.mockinsurance.repository.RuralPolicyCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.RuralPolicyInsuredObjectCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.RuralPolicyInsuredObjectRepository
import com.raidiam.trustframework.mockinsurance.repository.RuralPolicyPremiumCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.RuralPolicyPremiumRepository
import com.raidiam.trustframework.mockinsurance.repository.RuralPolicyRepository
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class CleanupRuralSpecification extends Specification {
    Logger LOG = LoggerFactory.getLogger(CleanupRuralSpecification.class)

    @Inject
    AccountHolderRepository accountHolderRepository

    @Inject
    ConsentRepository consentRepository

    @Inject
    PersonalInfoRepository personalInfoRepository

    @Inject
    BeneficiaryInfoRepository beneficiaryInfoRepository

    @Inject
    PrincipalInfoRepository principalInfoRepository

    @Inject
    IntermediaryRepository intermediaryRepository

    @Inject
    CoinsurerRepository coinsurerRepository

    @Inject
    DeductibleRepository deductibleRepository

    @Inject
    POSRepository posRepository

    @Inject
    PaymentRepository paymentRepository

    @Inject
    RuralPolicyRepository ruralPolicyRepository

    @Inject
    RuralPolicyCoverageRepository ruralPolicyCoverageRepository

    @Inject
    RuralPolicyInsuredObjectRepository ruralPolicyInsuredObjectRepository

    @Inject
    RuralPolicyInsuredObjectCoverageRepository ruralPolicyInsuredObjectCoverageRepository

    @Inject
    RuralPolicyBranchInsuredObjectRepository ruralPolicyBranchInsuredObjectRepository

    @Inject
    RuralPolicyClaimRepository ruralPolicyClaimRepository

    @Inject
    RuralPolicyClaimCoverageRepository ruralPolicyClaimCoverageRepository

    @Inject
    RuralPolicyPremiumRepository ruralPolicyPremiumRepository

    @Inject
    RuralPolicyPremiumCoverageRepository ruralPolicyPremiumCoverageRepository

    @Inject
    ConsentRuralPolicyRepository consentRuralPolicyRepository

    @Shared
    boolean runSetup = true

    @Shared
    boolean runCleanup = false

    def cleanup() {
        if (runCleanup) {
            LOG.info("Running Cleanup - Rural API")
            accountHolderRepository.deleteAll()
            consentRepository.deleteAll()
            personalInfoRepository.deleteAll()
            beneficiaryInfoRepository.deleteAll()
            principalInfoRepository.deleteAll()
            intermediaryRepository.deleteAll()
            coinsurerRepository.deleteAll()
            deductibleRepository.deleteAll()
            posRepository.deleteAll()
            paymentRepository.deleteAll()
            ruralPolicyRepository.deleteAll()
            ruralPolicyCoverageRepository.deleteAll()
            ruralPolicyInsuredObjectRepository.deleteAll()
            ruralPolicyInsuredObjectCoverageRepository.deleteAll()
            ruralPolicyBranchInsuredObjectRepository.deleteAll()
            ruralPolicyClaimRepository.deleteAll()
            ruralPolicyClaimCoverageRepository.deleteAll()
            ruralPolicyPremiumRepository.deleteAll()
            ruralPolicyPremiumCoverageRepository.deleteAll()
            consentRuralPolicyRepository.deleteAll()
            runCleanup = false
            runSetup = true
        }
    }
}
