package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.FinancialRiskPolicyPremiumEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinancialRiskPolicyPremiumRepository extends PageableRepository<FinancialRiskPolicyPremiumEntity, UUID> {
    @Join(value="financialRiskPolicy", type = Join.Type.FETCH)
    Optional<FinancialRiskPolicyPremiumEntity> findByFinancialRiskPolicyId(@NotNull UUID financialRiskPolicyId);
}