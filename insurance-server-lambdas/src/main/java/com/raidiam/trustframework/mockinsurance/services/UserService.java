package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.*;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseResourceList;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

@Singleton
@Transactional
public class UserService extends BaseInsuranceService {
    public ResponseResourceList getCapitalizationTitlePlans (@NotNull String userId) {
        var accounts = capitalizationTitleRepository.findByAccountHolderUserId(userId);
        return new ResponseResourceList().data(accounts
                .stream()
                .map(CapitalizationTitlePlanEntity::mapResourceDto)
                .toList());
    }

    public ResponseResourceList getFinancialRiskPolicies(@NotNull String userId) {
        var accounts = financialRiskPolicyRepository.findByAccountHolderUserId(userId);
        return new ResponseResourceList().data(accounts
                .stream()
                .map(FinancialRiskPolicyEntity::mapResourceDTO)
                .toList());
    }

    public ResponseResourceList getHousingPolicies(@NotNull String userId) {
        var accounts = housingPolicyRepository.findByAccountHolderUserId(userId);
        return new ResponseResourceList().data(accounts
                .stream()
                .map(HousingPolicyEntity::mapResourceDTO)
                .toList());
    }

    public ResponseResourceList getResponsibilityPolicies(@NotNull String userId) {
        var accounts = responsibilityPolicyRepository.findByAccountHolderUserId(userId);
        return new ResponseResourceList().data(accounts
                .stream()
                .map(ResponsibilityPolicyEntity::mapResourceDTO)
                .toList());
    }

    public ResponseResourceList getPersonPolicies(@NotNull String userId) {
        var accounts = personPolicyRepository.findByAccountHolderUserId(userId);
        return new ResponseResourceList().data(accounts
                .stream()
                .map(PersonPolicyEntity::mapResourceDTO)
                .toList());
    }

    public ResponseResourceList getLifePensionContracts (@NotNull String userId) {
        var accounts = lifePensionContractRepository.findByAccountHolderUserId(userId);
        return new ResponseResourceList().data(accounts
                .stream()
                .map(LifePensionContractEntity::mapResourceDTO)
                .toList());
    }

    public ResponseResourceList getPensionPlanContracts(@NotNull String userId) {
        var accounts = pensionPlanContractRepository.findByAccountHolderUserId(userId);
        return new ResponseResourceList().data(accounts
                .stream()
                .map(PensionPlanContractEntity::mapResourceDTO)
                .toList());
    }

    public ResponseResourceList getAcceptanceAndBranchesAbroadPolicies (@NotNull String userId) {
        var accounts = acceptanceAndBranchesAbroadPolicyRepository.findByAccountHolderUserId(userId);
        return new ResponseResourceList().data(accounts
                .stream()
                .map(AcceptanceAndBranchesAbroadPolicyEntity::mapResourceDTO)
                .toList());
    }

    public ResponseResourceList getPatrimonialPolicies (@NotNull String userId) {
        var accounts = patrimonialPolicyRepository.findByAccountHolderUserId(userId);
        return new ResponseResourceList().data(accounts
                .stream()
                .map(PatrimonialPolicyEntity::mapResourceDTO)
                .toList());
    }

    public ResponseResourceList getRuralPolicies (@NotNull String userId) {
        var accounts = ruralPolicyRepository.findByAccountHolderUserId(userId);
        return new ResponseResourceList().data(accounts
                .stream()
                .map(RuralPolicyEntity::mapResourceDTO)
                .toList());
    }

    public ResponseResourceList getFinancialAssistanceContracts (@NotNull String userId) {
        var accounts = financialAssistanceContractRepository.findByAccountHolderUserId(userId);
        return new ResponseResourceList().data(accounts
                .stream()
                .map(FinancialAssistanceContractEntity::mapResourceDTO)
                .toList());
    }

    public ResponseResourceList getAutoPolicies(@NotNull String userId) {
        var accounts = autoPolicyRepository.findByAccountHolderUserId(userId);
        return new ResponseResourceList().data(accounts
                .stream()
                .map(AutoPolicyEntity::mapResourceDTO)
                .toList());
    }

    public ResponseResourceList getTransportPolicies(@NotNull String userId) {
        var accounts = transportPolicyRepository.findByAccountHolderUserId(userId);
        return new ResponseResourceList().data(accounts
                .stream()
                .map(TransportPolicyEntity::mapResourceDTO)
                .toList());
    }
}