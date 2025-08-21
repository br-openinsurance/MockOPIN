package com.raidiam.trustframework.mockinsurance.repository;

import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.HousingPolicyInsuredObjectCoverageEntity;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

@Repository
public interface HousingPolicyInsuredObjectCoverageRepository extends PageableRepository<HousingPolicyInsuredObjectCoverageEntity, UUID> {
    @Join(value="housingPolicyInsuredObject", type = Join.Type.FETCH)
    Page<HousingPolicyInsuredObjectCoverageEntity> findByHousingPolicyInsuredObjectId(@NotNull UUID housingPolicyInsuredObjectId, Pageable pageable);
}