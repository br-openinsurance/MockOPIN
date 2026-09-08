package com.raidiam.trustframework.mockinsurance.cleanups

import com.raidiam.trustframework.mockinsurance.repository.AccountHolderRepository
import com.raidiam.trustframework.mockinsurance.repository.BeneficiaryInfoRepository
import com.raidiam.trustframework.mockinsurance.repository.ConsentPersonPolicyRepository
import com.raidiam.trustframework.mockinsurance.repository.ConsentRepository
import com.raidiam.trustframework.mockinsurance.repository.IntermediaryRepository
import com.raidiam.trustframework.mockinsurance.repository.PaymentRepository
import com.raidiam.trustframework.mockinsurance.repository.PersonalInfoRepository
import com.raidiam.trustframework.mockinsurance.repository.PersonPolicyClaimCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.PersonPolicyClaimRepository
import com.raidiam.trustframework.mockinsurance.repository.PersonPolicyInsuredObjectCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.PersonPolicyInsuredObjectRepository
import com.raidiam.trustframework.mockinsurance.repository.PersonPolicyPremiumCoverageRepository
import com.raidiam.trustframework.mockinsurance.repository.PersonPolicyPremiumRepository
import com.raidiam.trustframework.mockinsurance.repository.PersonPolicyRepository
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class CleanupPersonSpecification extends Specification {
    Logger LOG = LoggerFactory.getLogger(CleanupPersonSpecification.class)

    @Inject
    AccountHolderRepository accountHolderRepository

    @Inject
    ConsentRepository consentRepository

    @Inject
    PersonPolicyRepository personPolicyRepository

    @Inject
    ConsentPersonPolicyRepository consentPersonPolicyRepository

    @Inject
    PersonPolicyClaimRepository personPolicyClaimRepository

    @Inject
    PersonPolicyClaimCoverageRepository personPolicyClaimCoverageRepository

    @Inject
    PersonPolicyPremiumRepository personPolicyPremiumRepository

    @Inject
    PersonPolicyPremiumCoverageRepository personPolicyPremiumCoverageRepository

    @Inject
    PersonPolicyInsuredObjectRepository personPolicyInsuredObjectRepository

    @Inject
    PersonPolicyInsuredObjectCoverageRepository personPolicyInsuredObjectCoverageRepository

    @Inject
    BeneficiaryInfoRepository beneficiaryInfoRepository

    @Inject
    IntermediaryRepository intermediaryRepository

    @Inject
    PaymentRepository paymentRepository

    @Inject
    PersonalInfoRepository personalInfoRepository

    @Shared
    boolean runSetup = true

    @Shared
    boolean runCleanup = false

    def cleanup() {
        if (runCleanup) {
            LOG.info("Running Cleanup")
            accountHolderRepository.deleteAll()
            consentRepository.deleteAll()
            personPolicyRepository.deleteAll()
            consentPersonPolicyRepository.deleteAll()
            personPolicyClaimCoverageRepository.deleteAll()
            personPolicyClaimRepository.deleteAll()
            personPolicyPremiumCoverageRepository.deleteAll()
            personPolicyPremiumRepository.deleteAll()
            personPolicyInsuredObjectCoverageRepository.deleteAll()
            personPolicyInsuredObjectRepository.deleteAll()
            beneficiaryInfoRepository.deleteAll()
            intermediaryRepository.deleteAll()
            paymentRepository.deleteAll()
            personalInfoRepository.deleteAll()
            runCleanup = false
            runSetup = true
        }
    }
}
