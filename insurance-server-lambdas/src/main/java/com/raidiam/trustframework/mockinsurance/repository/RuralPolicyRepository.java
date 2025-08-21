package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.RuralPolicyEntity;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RuralPolicyRepository extends PageableRepository<RuralPolicyEntity, UUID> {
    Optional<RuralPolicyEntity> findByRuralPolicyId(UUID policyId);

    @Join(value="accountHolder", type = Join.Type.FETCH)
    List<RuralPolicyEntity> findByAccountHolderUserId(@NotNull String userId);

    Page<RuralPolicyEntity> findByAccountHolderAccountHolderId(UUID accountHolderId, Pageable pageable);
}