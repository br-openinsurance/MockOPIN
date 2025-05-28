package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.PersonPolicyPremiumEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonPolicyPremiumRepository extends PageableRepository<PersonPolicyPremiumEntity, UUID> {
    @Join(value="personPolicy", type = Join.Type.FETCH)
    Optional<PersonPolicyPremiumEntity> findByPersonPolicyId(@NotNull UUID personPolicyId);
}