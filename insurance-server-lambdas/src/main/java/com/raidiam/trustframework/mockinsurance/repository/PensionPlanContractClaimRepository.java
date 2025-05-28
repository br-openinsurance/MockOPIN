package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.PensionPlanContractClaimEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Repository
public interface PensionPlanContractClaimRepository extends PageableRepository<PensionPlanContractClaimEntity, UUID> {
    @Join(value="pensionPlanContract", type = Join.Type.FETCH)
    Page<PensionPlanContractClaimEntity> findByPensionPlanContractId(@NotNull String pensionPlanContractId, Pageable pageable);
}