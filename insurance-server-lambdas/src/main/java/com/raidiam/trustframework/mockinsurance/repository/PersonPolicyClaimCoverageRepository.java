package com.raidiam.trustframework.mockinsurance.repository;

import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.PersonPolicyClaimCoverageEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

@Repository
public interface PersonPolicyClaimCoverageRepository extends PageableRepository<PersonPolicyClaimCoverageEntity, UUID> {
}
