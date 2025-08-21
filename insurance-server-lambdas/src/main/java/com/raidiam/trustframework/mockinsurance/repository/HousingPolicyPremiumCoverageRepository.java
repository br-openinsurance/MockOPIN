package com.raidiam.trustframework.mockinsurance.repository;

import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.HousingPolicyPremiumCoverageEntity;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

@Repository
public interface HousingPolicyPremiumCoverageRepository extends PageableRepository<HousingPolicyPremiumCoverageEntity, UUID> {
    @Join(value="housingPolicyPremium", type = Join.Type.FETCH)
    Page<HousingPolicyPremiumCoverageEntity> findByHousingPolicyPremiumId(@NotNull UUID housingPolicyPremiumId, Pageable pageable);
}