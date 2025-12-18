package com.raidiam.trustframework.mockinsurance.cleanups


import com.raidiam.trustframework.mockinsurance.repository.*
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

class CleanupSpecification extends Specification {
    Logger LOG = LoggerFactory.getLogger(CleanupSpecification.class)

    @Inject
    AccountHolderRepository accountHolderRepository

    @Inject
    ConsentRepository consentRepository

    @Inject
    EndorsementRepository endorsementRepository

    @Inject
    ClaimNotificationDamageRepository claimNotificationDamageRepository

    @Inject
    ClaimNotificationPersonRepository claimNotificationPersonRepository

    @Inject
    QuotePatrimonialLeadRepository quotePatrimonialLeadRepository

    @Inject
    QuotePatrimonialBusinessRepository quotePatrimonialBusinessRepository

    @Inject
    QuotePatrimonialHomeRepository quotePatrimonialHomeRepository

    @Inject
    QuotePatrimonialCondominiumRepository quotePatrimonialCondominiumRepository

    @Inject
    QuotePatrimonialDiverseRisksRepository quotePatrimonialDiverseRisksRepository

    @Inject
    QuoteFinancialRiskLeadRepository quoteFinancialRiskLeadRepository

    @Inject
    QuoteHousingLeadRepository quoteHousingLeadRepository

    @Inject
    QuoteResponsibilityLeadRepository quoteResponsibilityLeadRepository

    @Inject
    QuoteRuralLeadRepository quoteRuralLeadRepository

    @Inject
    QuoteAutoRepository quoteAutoRepository

    @Inject
    QuoteAutoLeadRepository quoteAutoLeadRepository

    @Inject
    QuoteLifePensionRepository quoteLifePensionRepository

    @Inject
    QuoteLifePensionLeadRepository quoteLifePensionLeadRepository

    @Inject
    QuotePersonLifeRepository quotePersonLifeRepository

    @Inject
    QuotePersonLifeRepository quotePersonTravelRepository

    @Inject
    QuotePersonLeadRepository quotePersonLeadRepository

    @Inject
    QuoteCapitalizationTitleRepository quoteCapitalizationTitleRepository

    @Inject
    QuoteCapitalizationTitleLeadRepository quoteCapitalizationTitleLeadRepository

    @Inject
    CapitalizationTitleRaffleRepository capitalizationTitleRaffleRepository

    @Inject
    PersonalIdentificationRepository personalIdentificationRepository

    @Inject
    PersonalQualificationRepository personalQualificationRepository

    @Inject
    PersonalComplimentaryInformationRepository personalComplimentaryInformationRepository

    @Inject
    BusinessIdentificationRepository businessIdentificationRepository

    @Inject
    BusinessQualificationRepository businessQualificationRepository

    @Inject
    BusinessComplimentaryInformationRepository businessComplimentaryInformationRepository

    @Inject
    HousingPolicyRepository housingPolicyRepository

    @Inject
    ConsentHousingPolicyRepository consentHousingPolicyRepository

    @Inject
    ResponsibilityPolicyRepository responsibilityPolicyRepository

    @Inject
    ConsentResponsibilityPolicyRepository consentResponsibilityPolicyRepository

    @Inject
    ResponsibilityPolicyClaimRepository responsibilityPolicyClaimRepository

    @Inject
    ResponsibilityPolicyPremiumRepository responsibilityPolicyPremiumRepository

    @Inject
    PersonPolicyRepository personPolicyRepository

    @Inject
    ConsentPersonPolicyRepository consentPersonPolicyRepository

    @Inject
    PersonPolicyClaimRepository personPolicyClaimRepository

    @Inject
    PersonPolicyPremiumRepository personPolicyPremiumRepository

    @Inject
    LifePensionContractRepository lifePensionContractRepository

    @Inject
    ConsentLifePensionContractRepository consentLifePensionContractRepository

    @Inject
    LifePensionContractClaimRepository lifePensionContractClaimRepository

    @Inject
    LifePensionContractPortabilityRepository lifePensionContractPortabilityRepository

    @Inject
    LifePensionContractWithdrawalRepository lifePensionContractWithdrawalRepository

    @Inject
    LifePensionContractMovementBenefitRepository lifePensionContractMovementBenefitRepository

    @Inject
    LifePensionContractMovementContributionRepository lifePensionContractMovementContributionRepository

    @Inject
    PensionPlanContractRepository pensionPlanContractRepository

    @Inject
    ConsentPensionPlanContractRepository consentPensionPlanContractRepository

    @Inject
    PensionPlanContractClaimRepository pensionPlanContractClaimRepository

    @Inject
    PensionPlanContractPortabilityRepository pensionPlanContractPortabilityRepository

    @Inject
    PensionPlanContractWithdrawalRepository pensionPlanContractWithdrawalRepository

    @Inject
    PensionPlanContractMovementBenefitRepository pensionPlanContractMovementBenefitRepository

    @Inject
    PensionPlanContractMovementContributionRepository pensionPlanContractMovementContributionRepository

    @Inject
    AcceptanceAndBranchesAbroadPolicyRepository acceptanceAndBranchesAbroadPolicyRepository

    @Inject
    AcceptanceAndBranchesAbroadClaimRepository acceptanceAndBranchesAbroadClaimRepository

    @Inject
    ConsentAcceptanceAndBranchesAbroadPolicyRepository consentAcceptanceAndBranchesAbroadPolicyRepository

    @Inject
    PatrimonialPolicyRepository patrimonialPolicyRepository

    @Inject
    PatrimonialClaimRepository patrimonialClaimRepository

    @Inject
    ConsentPatrimonialPolicyRepository consentPatrimonialPolicyRepository

    @Inject
    AutoPolicyRepository autoPolicyRepository

    @Inject
    AutoPolicyClaimRepository autoPolicyClaimRepository

    @Inject
    ConsentAutoPolicyRepository consentAutoPolicyRepository

    @Inject
    TransportPolicyRepository transportPolicyRepository

    @Inject
    TransportPolicyClaimRepository transportPolicyClaimRepository

    @Inject
    ConsentTransportPolicyRepository consentTransportPolicyRepository

    @Inject
    CapitalizationTitlePlanRepository capitalizationTitlePlanRepository

    @Inject
    ConsentCapitalizationTitlePlanRepository consentCapitalizationTitlePlanRepository

    @Inject
    FinancialAssistanceContractRepository financialAssistanceContractRepository

    @Inject
    ConsentFinancialAssistanceContractRepository consentFinancialAssistanceContractRepository

    @Inject
    FinancialRiskPolicyRepository financialRiskPolicyRepository

    @Inject
    ConsentFinancialRiskPolicyRepository consentFinancialRiskPolicyRepository

    @Inject
    RuralPolicyRepository ruralPolicyRepository

    @Inject
    ConsentRuralPolicyRepository consentRuralPolicyRepository

    @Inject
    WebhookRepository webhookRepository

    @Inject
    OverrideResponseRepository overrideResponseRepository

    @Inject
    DynamicFieldsRepository dynamicFieldsRepository

    @Shared
    boolean runSetup = true

    @Shared
    boolean runCleanup = false

    def cleanup() {
        if (runCleanup) {
            LOG.info("Running Cleanup")
            accountHolderRepository.deleteAll()
            consentRepository.deleteAll()
            endorsementRepository.deleteAll()
            claimNotificationDamageRepository.deleteAll()
            quotePatrimonialLeadRepository.deleteAll()
            quotePatrimonialBusinessRepository.deleteAll()
            quotePatrimonialHomeRepository.deleteAll()
            quotePatrimonialCondominiumRepository.deleteAll()
            quotePatrimonialDiverseRisksRepository.deleteAll()
            quoteFinancialRiskLeadRepository.deleteAll()
            quoteHousingLeadRepository.deleteAll()
            quoteResponsibilityLeadRepository.deleteAll()
            quoteRuralLeadRepository.deleteAll()
            quoteAutoRepository.deleteAll()
            quoteAutoLeadRepository.deleteAll()
            quoteLifePensionRepository.deleteAll()
            quoteLifePensionLeadRepository.deleteAll()
            quotePersonLifeRepository.deleteAll()
            quotePersonTravelRepository.deleteAll()
            quoteCapitalizationTitleRepository.deleteAll()
            quoteCapitalizationTitleLeadRepository.deleteAll()
            capitalizationTitleRaffleRepository.deleteAll()
            personalIdentificationRepository.deleteAll()
            personalQualificationRepository.deleteAll()
            personalComplimentaryInformationRepository.deleteAll()
            businessIdentificationRepository.deleteAll()
            businessQualificationRepository.deleteAll()
            businessComplimentaryInformationRepository.deleteAll()
            webhookRepository.deleteAll()
            housingPolicyRepository.deleteAll()
            consentHousingPolicyRepository.deleteAll()
            responsibilityPolicyRepository.deleteAll()
            consentResponsibilityPolicyRepository.deleteAll()
            responsibilityPolicyClaimRepository.deleteAll()
            responsibilityPolicyPremiumRepository.deleteAll()
            personPolicyRepository.deleteAll()
            consentPersonPolicyRepository.deleteAll()
            personPolicyClaimRepository.deleteAll()
            personPolicyPremiumRepository.deleteAll()
            lifePensionContractPortabilityRepository.deleteAll()
            lifePensionContractMovementBenefitRepository.deleteAll()
            lifePensionContractClaimRepository.deleteAll()
            lifePensionContractMovementContributionRepository.deleteAll()
            acceptanceAndBranchesAbroadPolicyRepository.deleteAll()
            acceptanceAndBranchesAbroadClaimRepository.deleteAll()
            consentAcceptanceAndBranchesAbroadPolicyRepository.deleteAll()
            patrimonialPolicyRepository.deleteAll()
            patrimonialClaimRepository.deleteAll()
            consentPatrimonialPolicyRepository.deleteAll()
            autoPolicyRepository.deleteAll()
            autoPolicyClaimRepository.deleteAll()
            consentAutoPolicyRepository.deleteAll()
            pensionPlanContractPortabilityRepository.deleteAll()
            pensionPlanContractMovementBenefitRepository.deleteAll()
            pensionPlanContractClaimRepository.deleteAll()
            pensionPlanContractMovementContributionRepository.deleteAll()
            pensionPlanContractWithdrawalRepository.deleteAll()
            consentPensionPlanContractRepository.deleteAll()
            transportPolicyRepository.deleteAll()
            transportPolicyClaimRepository.deleteAll()
            consentTransportPolicyRepository.deleteAll()
            capitalizationTitlePlanRepository.deleteAll()
            consentCapitalizationTitlePlanRepository.deleteAll()
            financialAssistanceContractRepository.deleteAll()
            consentFinancialAssistanceContractRepository.deleteAll()
            financialRiskPolicyRepository.deleteAll()
            consentFinancialRiskPolicyRepository.deleteAll()
            ruralPolicyRepository.deleteAll()
            consentRuralPolicyRepository.deleteAll()
            overrideResponseRepository.deleteAll()
            runCleanup = false
            runSetup = true
        }
    }
}
