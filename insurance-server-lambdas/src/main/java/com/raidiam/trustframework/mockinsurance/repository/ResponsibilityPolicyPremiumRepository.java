package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.ResponsibilityPolicyPremiumEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResponsibilityPolicyPremiumRepository extends PageableRepository<ResponsibilityPolicyPremiumEntity, UUID> {
    @Join(value="responsibilityPolicy", type = Join.Type.FETCH)
    Optional<ResponsibilityPolicyPremiumEntity> findByResponsibilityPolicyId(@NotNull UUID responsibilityPolicyId);
}