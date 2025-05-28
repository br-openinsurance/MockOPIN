package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.PatrimonialPolicyEntity;

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
public interface PatrimonialPolicyRepository extends PageableRepository<PatrimonialPolicyEntity, UUID> {
    Optional<PatrimonialPolicyEntity> findByPolicyId(UUID policyId);

    @Join(value="accountHolder", type = Join.Type.FETCH)
    List<PatrimonialPolicyEntity> findByAccountHolderUserId(@NotNull String userId);

    Page<PatrimonialPolicyEntity> findByAccountHolderAccountHolderId(UUID accountHolderId, Pageable pageable);
}