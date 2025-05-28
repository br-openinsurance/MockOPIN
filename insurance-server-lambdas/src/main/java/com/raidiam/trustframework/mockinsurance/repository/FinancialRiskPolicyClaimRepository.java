package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.FinancialRiskPolicyClaimEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Repository
public interface FinancialRiskPolicyClaimRepository extends PageableRepository<FinancialRiskPolicyClaimEntity, UUID> {
    @Join(value="financialRiskPolicy", type = Join.Type.FETCH)
    Page<FinancialRiskPolicyClaimEntity> findByFinancialRiskPolicyId(@NotNull UUID financialRiskPolicyId, Pageable pageable);
}