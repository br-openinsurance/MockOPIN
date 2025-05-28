package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.PersonPolicyClaimEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Repository
public interface PersonPolicyClaimRepository extends PageableRepository<PersonPolicyClaimEntity, UUID> {
    @Join(value="personPolicy", type = Join.Type.FETCH)
    Page<PersonPolicyClaimEntity> findByPersonPolicyId(@NotNull UUID personPolicyId, Pageable pageable);
}