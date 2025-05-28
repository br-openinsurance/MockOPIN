package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.LifePensionContractPortabilityInfoEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Repository
public interface LifePensionContractPortabilityRepository extends PageableRepository<LifePensionContractPortabilityInfoEntity, UUID> {
    @Join(value="lifePensionContract", type = Join.Type.FETCH)
    Page<LifePensionContractPortabilityInfoEntity> findByLifePensionContractId(@NotNull UUID lifePensionContractId,
                                                                                  Pageable pageable);
}