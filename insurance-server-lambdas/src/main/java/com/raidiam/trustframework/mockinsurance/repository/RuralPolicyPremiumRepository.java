package com.raidiam.trustframework.mockinsurance.repository;

import java.util.Optional;
import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.RuralPolicyPremiumEntity;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

@Repository
public interface RuralPolicyPremiumRepository extends PageableRepository<RuralPolicyPremiumEntity, UUID> {
    @Join(value="ruralPolicy", type = Join.Type.FETCH)
    Optional<RuralPolicyPremiumEntity> findByRuralPolicyId(@NotNull UUID housingPolicyId);
}
