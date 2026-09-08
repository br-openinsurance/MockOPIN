package com.raidiam.trustframework.mockinsurance.repository;

import java.util.Optional;
import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.PatrimonialPremiumEntity;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

@Repository
public interface PatrimonialPremiumRepository extends PageableRepository<PatrimonialPremiumEntity, UUID> {
    @Join(value = "patrimonialPolicy", type = Join.Type.FETCH)
    Optional<PatrimonialPremiumEntity> findByPolicyId(@NotNull UUID policyId);
}
