package com.raidiam.trustframework.mockinsurance.repository;

import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.HousingPolicyBranchInsuredEntity;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

@Repository
public interface HousingPolicyBranchInsuredRepository extends PageableRepository<HousingPolicyBranchInsuredEntity, UUID> {
    @Join(value="housingPolicy", type = Join.Type.FETCH)
    Page<HousingPolicyBranchInsuredEntity> findByHousingPolicyId(@NotNull UUID housingPolicyId, Pageable pageable);
}