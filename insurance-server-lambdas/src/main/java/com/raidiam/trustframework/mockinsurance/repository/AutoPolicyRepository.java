package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.AutoPolicyEntity;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoPolicyRepository extends PageableRepository<AutoPolicyEntity, String> {
    Optional<AutoPolicyEntity> findByAutoPolicyId(String autoPolicyId);

    @Join(value="accountHolder", type = Join.Type.FETCH)
    List<AutoPolicyEntity> findByAccountHolderUserId(@NotNull String userId);
}
