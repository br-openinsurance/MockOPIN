package com.raidiam.trustframework.mockinsurance.repository;

import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.HousingPolicyClaimCoverageEntity;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

@Repository
public interface HousingPolicyClaimCoverageRepository extends PageableRepository<HousingPolicyClaimCoverageEntity, UUID> {
    @Join(value="housingPolicyClaim", type = Join.Type.FETCH)
    Page<HousingPolicyClaimCoverageEntity> findByHousingPolicyClaimId(@NotNull UUID housingPolicyClaimId, Pageable pageable);
}