package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.FinancialRiskPolicyEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinancialRiskPolicyRepository extends PageableRepository<FinancialRiskPolicyEntity, UUID> {
    Page<FinancialRiskPolicyEntity> findByAccountHolderAccountHolderId(UUID accountHolderId, Pageable pageable);

    @Join(value="accountHolder", type = Join.Type.FETCH)
    List<FinancialRiskPolicyEntity> findByAccountHolderUserId(@NotNull String userId);

    Optional<FinancialRiskPolicyEntity> findByFinancialRiskPolicyId(UUID financialRiskId);
}