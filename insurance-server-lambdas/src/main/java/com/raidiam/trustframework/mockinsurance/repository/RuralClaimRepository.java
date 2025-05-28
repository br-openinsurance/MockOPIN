package com.raidiam.trustframework.mockinsurance.repository;

import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.RuralClaimEntity;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

@Repository
public interface RuralClaimRepository extends PageableRepository<RuralClaimEntity, UUID> {
    @Join(value="policy", type = Join.Type.FETCH)
    Page<RuralClaimEntity> findByPolicyId(@NotNull UUID policyId, Pageable pageable);
}
