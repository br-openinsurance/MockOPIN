package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.LifePensionContractEntity;
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
public interface LifePensionContractRepository extends PageableRepository<LifePensionContractEntity, UUID> {
    Page<LifePensionContractEntity> findByAccountHolderAccountHolderId(UUID accountHolderId, Pageable pageable);

    @Join(value="accountHolder", type = Join.Type.FETCH)
    List<LifePensionContractEntity> findByAccountHolderUserId(@NotNull String userId);

    Optional<LifePensionContractEntity> findByLifePensionContractId(UUID lifePensionId);
}