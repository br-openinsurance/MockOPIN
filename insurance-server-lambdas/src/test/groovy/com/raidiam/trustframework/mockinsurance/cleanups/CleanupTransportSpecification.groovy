package com.raidiam.trustframework.mockinsurance.cleanups

import com.raidiam.trustframework.mockinsurance.repository.AccountHolderRepository
import com.raidiam.trustframework.mockinsurance.repository.BeneficiaryInfoRepository
import com.raidiam.trustframework.mockinsurance.repository.CoinsurerRepository
import com.raidiam.trustframework.mockinsurance.repository.ConsentRepository
import com.raidiam.trustframework.mockinsurance.repository.ConsentTransportPolicyRepository
import com.raidiam.trustframework.mockinsurance.repository.DeductibleRepository
import com.raidiam.trustframework.mockinsurance.repository.IntermediaryRepository
import com.raidiam.trustframework.mockinsurance.repository.POSRepository
import com.raidiam.trustframework.mockinsurance.repository.PaymentRepository
import com.raidiam.trustframework.mockinsurance.repository.PersonalInfoRepository
import com.raidiam.trustframework.mockinsurance.repository.PrincipalInfoRepository
import com.raidiam.trustframework.mockinsurance.repository.TransportPolicyClaimCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.TransportPolicyClaimRepository
import com.raidiam.trustframework.mockinsurance.repository.TransportPolicyCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.TransportPolicyEndorsementRepository
import com.raidiam.trustframework.mockinsurance.repository.TransportPolicyInsuredObjectCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.TransportPolicyInsuredObjectRepository
import com.raidiam.trustframework.mockinsurance.repository.TransportPolicyPremiumCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.TransportPolicyPremiumRepository
import com.raidiam.trustframework.mockinsurance.repository.TransportPolicyRepository
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class CleanupTransportSpecification extends Specification {
    Logger LOG = LoggerFactory.getLogger(CleanupTransportSpecification.class)

    @Inject
    AccountHolderRepository accountHolderRepository

    @Inject
    ConsentRepository consentRepository

    @Inject
    TransportPolicyRepository transportPolicyRepository

    @Inject
    ConsentTransportPolicyRepository consentTransportPolicyRepository

    @Inject
    TransportPolicyClaimRepository transportPolicyClaimRepository

    @Inject
    TransportPolicyClaimCoverageRepository transportPolicyClaimCoverageRepository

    @Inject
    TransportPolicyCoverageRepository transportPolicyCoverageRepository

    @Inject
    TransportPolicyEndorsementRepository transportPolicyEndorsementRepository

    @Inject
    TransportPolicyInsuredObjectRepository transportPolicyInsuredObjectRepository

    @Inject
    TransportPolicyInsuredObjectCoverageRepository transportPolicyInsuredObjectCoverageRepository

    @Inject
    TransportPolicyPremiumRepository transportPolicyPremiumRepository

    @Inject
    TransportPolicyPremiumCoverageRepository transportPolicyPremiumCoverageRepository

    @Inject
    PaymentRepository paymentRepository

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

    @Shared
    boolean runSetup = true

    @Shared
    boolean runCleanup = false

    def cleanup() {
        if (runCleanup) {
            LOG.info("Running Cleanup")
            accountHolderRepository.deleteAll()
            consentRepository.deleteAll()
            transportPolicyInsuredObjectCoverageRepository.deleteAll()
            transportPolicyInsuredObjectRepository.deleteAll()
            transportPolicyPremiumCoverageRepository.deleteAll()
            transportPolicyPremiumRepository.deleteAll()
            transportPolicyCoverageRepository.deleteAll()
            transportPolicyEndorsementRepository.deleteAll()
            transportPolicyClaimCoverageRepository.deleteAll()
            transportPolicyClaimRepository.deleteAll()
            transportPolicyRepository.deleteAll()
            consentTransportPolicyRepository.deleteAll()
            personalInfoRepository.deleteAll()
            beneficiaryInfoRepository.deleteAll()
            principalInfoRepository.deleteAll()
            intermediaryRepository.deleteAll()
            coinsurerRepository.deleteAll()
            deductibleRepository.deleteAll()
            posRepository.deleteAll()
            paymentRepository.deleteAll()
            runCleanup = false
            runSetup = true
        }
    }
}
