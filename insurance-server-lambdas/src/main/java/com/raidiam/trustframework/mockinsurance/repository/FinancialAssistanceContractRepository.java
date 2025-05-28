package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.FinancialAssistanceContractEntity;
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
public interface FinancialAssistanceContractRepository extends PageableRepository<FinancialAssistanceContractEntity, String> {
    Page<FinancialAssistanceContractEntity> findByAccountHolderAccountHolderId(UUID accountHolderId, Pageable pageable);

    @Join(value="accountHolder", type = Join.Type.FETCH)
    List<FinancialAssistanceContractEntity> findByAccountHolderUserId(@NotNull String userId);

    Optional<FinancialAssistanceContractEntity> findByFinancialAssistanceContractId(String lifePensionId);
}