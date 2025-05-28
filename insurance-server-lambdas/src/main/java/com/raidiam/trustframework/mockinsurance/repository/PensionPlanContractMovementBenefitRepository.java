package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.PensionPlanContractMovementBenefitEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Repository
public interface PensionPlanContractMovementBenefitRepository extends PageableRepository<PensionPlanContractMovementBenefitEntity, UUID> {
    @Join(value="pensionPlanContract", type = Join.Type.FETCH)
    Page<PensionPlanContractMovementBenefitEntity> findByPensionPlanContractId(@NotNull String pensionPlanContractId, Pageable pageable);
}