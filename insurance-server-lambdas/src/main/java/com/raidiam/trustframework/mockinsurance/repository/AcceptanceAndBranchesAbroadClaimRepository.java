package com.raidiam.trustframework.mockinsurance.repository;

import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.AcceptanceAndBranchesAbroadClaimEntity;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

@Repository
public interface AcceptanceAndBranchesAbroadClaimRepository extends PageableRepository<AcceptanceAndBranchesAbroadClaimEntity, UUID> {
    @Join(value="policy", type = Join.Type.FETCH)
    Page<AcceptanceAndBranchesAbroadClaimEntity> findByPolicyId(@NotNull UUID policyId, Pageable pageable);
}
