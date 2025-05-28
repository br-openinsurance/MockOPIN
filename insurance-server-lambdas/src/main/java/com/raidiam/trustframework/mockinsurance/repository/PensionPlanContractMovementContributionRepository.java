package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.PensionPlanContractMovementContributionEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Repository
public interface PensionPlanContractMovementContributionRepository extends PageableRepository<PensionPlanContractMovementContributionEntity, UUID> {
    @Join(value="pensionPlanContract", type = Join.Type.FETCH)
    Page<PensionPlanContractMovementContributionEntity> findByPensionPlanContractId(@NotNull String pensionPlanContractId, Pageable pageable);
}