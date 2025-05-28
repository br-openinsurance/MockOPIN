package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.FinancialAssistanceContractMovementEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Repository
public interface FinancialAssistanceContractMovementRepository extends PageableRepository<FinancialAssistanceContractMovementEntity, UUID> {
    @Join(value="financialAssistanceContract", type = Join.Type.FETCH)
    Page<FinancialAssistanceContractMovementEntity> findByFinancialAssistanceContractId(@NotNull String financialAssistanceContractId, Pageable pageable);
}