package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.repository.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import jakarta.inject.Inject;

abstract class BaseInsuranceService {

    @Inject
    AccountHolderRepository accountHolderRepository;

    @Inject
    ConsentRepository consentRepository;

    @Inject
    EndorsementRepository endorsementRepository;

    @Inject
    ClaimNotificationDamageRepository claimNotificationDamageRepository;

    @Inject
    ClaimNotificationPersonRepository claimNotificationPersonRepository;

    @Inject
    QuotePatrimonialBusinessRepository quotePatrimonialBusinessRepository;

    @Inject
    QuotePatrimonialHomeRepository quotePatrimonialHomeRepository;

    @Inject
    QuotePatrimonialCondominiumRepository quotePatrimonialCondominiumRepository;

    @Inject
    QuotePatrimonialDiverseRisksRepository quotePatrimonialDiverseRisksRepository;

    @Inject
    QuotePatrimonialLeadRepository quotePatrimonialLeadRepository;

    @Inject
    ConsentPatrimonialPolicyRepository consentPatrimonialPolicyRepository;

    @Inject
    PatrimonialPolicyRepository patrimonialPolicyRepository;

    @Inject
    PatrimonialClaimRepository patrimonialClaimRepository;

    @Inject
    ConsentRuralPolicyRepository consentRuralPolicyRepository;

    @Inject
    RuralPolicyRepository ruralPolicyRepository;

    @Inject
    RuralPolicyInsuredObjectRepository ruralPolicyInsuredObjectRepository;
    
    @Inject
    RuralPolicyInsuredObjectCoverageRepository ruralPolicyInsuredObjectCoverageRepository;
    
    @Inject
    RuralPolicyCoverageRepository ruralPolicyCoverageRepository;

    @Inject
    RuralPolicyBranchInsuredObjectRepository ruralPolicyBranchInsuredObjectRepository;

    @Inject
    RuralPolicyClaimRepository ruralPolicyClaimRepository;

    @Inject
    RuralPolicyClaimCoverageRepository ruralPolicyClaimCoverageRepository;

    @Inject
    RuralPolicyPremiumRepository ruralPolicyPremiumRepository;

    @Inject
    RuralPolicyPremiumCoverageRepository ruralPolicyPremiumCoverageRepository;

    @Inject
    QuoteFinancialRiskLeadRepository quoteFinancialRiskLeadRepository;

    @Inject
    QuoteAcceptanceAndBranchesAbroadLeadRepository quoteAcceptanceAndBranchesAbroadLeadRepository;

    @Inject
    ConsentAcceptanceAndBranchesAbroadPolicyRepository consentAcceptanceAndBranchesAbroadPolicyRepository;

    @Inject
    AcceptanceAndBranchesAbroadPolicyRepository acceptanceAndBranchesAbroadPolicyRepository;

    @Inject
    AcceptanceAndBranchesAbroadClaimRepository acceptanceAndBranchesAbroadClaimRepository;

    @Inject
    QuoteHousingLeadRepository quoteHousingLeadRepository;

    @Inject
    QuoteLifePensionLeadRepository quoteLifePensionLeadRepository;

    @Inject
    QuoteLifePensionRepository quoteLifePensionRepository;

    @Inject
    QuoteResponsibilityLeadRepository quoteResponsibilityLeadRepository;

    @Inject
    QuoteTransportLeadRepository quoteTransportLeadRepository;

    @Inject
    QuoteRuralLeadRepository quoteRuralLeadRepository;

    @Inject
    QuoteAutoLeadRepository quoteAutoLeadRepository;

    @Inject
    QuoteAutoRepository quoteAutoRepository;

    @Inject
    QuoteCapitalizationTitleLeadRepository quoteCapitalizationTitleLeadRepository;

    @Inject
    QuoteCapitalizationTitleRepository quoteCapitalizationTitleRepository;

    @Inject
    CapitalizationTitleRaffleRepository capitalizationTitleRaffleRepository;

    @Inject
    QuotePersonLifeRepository quotePersonLifeRepository;

    @Inject
    QuotePersonLeadRepository quotePersonLeadRepository;

    @Inject
    QuotePersonTravelRepository quotePersonTravelRepository;

    @Inject
    PersonalIdentificationRepository personalIdentificationRepository;

    @Inject
    PersonalQualificationRepository personalQualificationRepository;

    @Inject
    PersonalComplimentaryInformationRepository personalComplimentaryInformationRepository;

    @Inject
    BusinessIdentificationRepository businessIdentificationRepository;

    @Inject
    BusinessQualificationRepository businessQualificationRepository;

    @Inject
    BusinessComplimentaryInformationRepository businessComplimentaryInformationRepository;

    @Inject
    CapitalizationTitlePlanRepository capitalizationTitleRepository;

    @Inject
    ConsentCapitalizationTitlePlanRepository consentCapitalizationTitlePlanRepository;

    @Inject
    CapitalizationTitlePlanEventRepository capitalizationTitlePlanEventRepository;

    @Inject
    CapitalizationTitlePlanSettlementRepository capitalizationTitlePlanSettlementRepository;

    @Inject
    ConsentLifePensionContractRepository consentLifePensionContractRepository;

    @Inject
    FinancialRiskPolicyRepository financialRiskPolicyRepository;

    @Inject
    FinancialRiskPolicyClaimRepository financialRiskPolicyClaimRepository;

    @Inject
    FinancialRiskPolicyPremiumRepository financialRiskPolicyPremiumRepository;

    @Inject
    ConsentFinancialRiskPolicyRepository consentFinancialRiskPolicyRepository;

    @Inject
    HousingPolicyRepository housingPolicyRepository;

    @Inject
    HousingPolicyInsuredObjectRepository housingPolicyInsuredObjectRepository;

    @Inject
    HousingPolicyInsuredObjectCoverageRepository housingPolicyInsuredObjectCoverageRepository;

    @Inject
    HousingPolicyBranchInsuredObjectRepository housingPolicyBranchInsuredObjectRepository;

    @Inject
    HousingPolicyBranchInsuredObjectLenderRepository housingPolicyBranchInsuredObjectLenderRepository;

    @Inject
    HousingPolicyBranchInsuredRepository housingPolicyBranchInsuredRepository;

    @Inject
    HousingPolicyClaimRepository housingPolicyClaimRepository;

    @Inject
    HousingPolicyClaimCoverageRepository housingPolicyClaimCoverageRepository;

    @Inject
    HousingPolicyPremiumRepository housingPolicyPremiumRepository;

    @Inject
    HousingPolicyPremiumCoverageRepository housingPolicyPremiumCoverageRepository;

    @Inject
    ConsentHousingPolicyRepository consentHousingPolicyRepository;

    @Inject
    ResponsibilityPolicyRepository responsibilityPolicyRepository;

    @Inject
    ResponsibilityPolicyClaimRepository responsibilityPolicyClaimRepository;

    @Inject
    ResponsibilityPolicyPremiumRepository responsibilityPolicyPremiumRepository;

    @Inject
    ConsentResponsibilityPolicyRepository consentResponsibilityPolicyRepository;

    @Inject
    PersonPolicyRepository personPolicyRepository;

    @Inject
    PersonPolicyClaimRepository personPolicyClaimRepository;

    @Inject
    PersonPolicyPremiumRepository personPolicyPremiumRepository;

    @Inject
    ConsentPersonPolicyRepository consentPersonPolicyRepository;

    @Inject
    LifePensionContractRepository lifePensionContractRepository;

    @Inject
    LifePensionContractWithdrawalRepository lifePensionContractWithdrawalRepository;

    @Inject
    LifePensionContractClaimRepository lifePensionContractClaimRepository;

    @Inject
    LifePensionContractPortabilityRepository lifePensionContractPortabilityRepository;

    @Inject
    LifePensionContractMovementBenefitRepository lifePensionContractMovementBenefitRepository;

    @Inject
    LifePensionContractMovementContributionRepository lifePensionContractMovementContributionRepository;

    @Inject
    PensionPlanContractRepository pensionPlanContractRepository;

    @Inject
    PensionPlanContractWithdrawalRepository pensionPlanContractWithdrawalRepository;

    @Inject
    PensionPlanContractClaimRepository pensionPlanContractClaimRepository;

    @Inject
    PensionPlanContractPortabilityRepository pensionPlanContractPortabilityRepository;

    @Inject
    PensionPlanContractMovementBenefitRepository pensionPlanContractMovementBenefitRepository;

    @Inject
    PensionPlanContractMovementContributionRepository pensionPlanContractMovementContributionRepository;

    @Inject
    ConsentPensionPlanContractRepository consentPensionPlanContractRepository;

    @Inject
    FinancialAssistanceContractRepository financialAssistanceContractRepository;

    @Inject
    FinancialAssistanceContractMovementRepository financialAssistanceContractMovementRepository;

    @Inject
    ConsentFinancialAssistanceContractRepository consentFinancialAssistanceContractRepository;

    @Inject
    AutoPolicyRepository autoPolicyRepository;

    @Inject
    AutoPolicyClaimRepository autoPolicyClaimRepository;

    @Inject
    ConsentAutoPolicyRepository consentAutoPolicyRepository;

    @Inject
    TransportPolicyRepository transportPolicyRepository;

    @Inject
    TransportPolicyClaimRepository transportPolicyClaimRepository;

    @Inject
    ConsentTransportPolicyRepository consentTransportPolicyRepository;

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    BeneficiaryInfoRepository beneficiaryInfoRepository;

    @Inject
    PersonalInfoRepository personalInfoRepository;

    @Inject
    IntermediaryRepository intermediaryRepository;

    @Inject
    PrincipalInfoRepository principalInfoRepository;

    @Inject
    CoinsurerRepository coinsurerRepository;

    @Inject
    DynamicFieldsRepository dynamicFieldsRepository;

    @Inject
    WebhookRepository webhookRepository;

    @Inject
    OverrideResponseRepository overrideResponseRepository;

    @Inject
    InsuranceLambdaUtils insuranceLambdaUtils;
}
