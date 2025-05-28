package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.LifePensionContractMovementBenefitEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Repository
public interface LifePensionContractMovementBenefitRepository extends PageableRepository<LifePensionContractMovementBenefitEntity, UUID> {
    @Join(value="lifePensionContract", type = Join.Type.FETCH)
    Page<LifePensionContractMovementBenefitEntity> findByLifePensionContractId(@NotNull UUID lifePensionContractId, Pageable pageable);
}