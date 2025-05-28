package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.HousingPolicyPremiumEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HousingPolicyPremiumRepository extends PageableRepository<HousingPolicyPremiumEntity, UUID> {
    @Join(value="housingPolicy", type = Join.Type.FETCH)
    Optional<HousingPolicyPremiumEntity> findByHousingPolicyId(@NotNull UUID housingPolicyId);
}