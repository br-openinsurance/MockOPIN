package com.raidiam.trustframework.mockinsurance.repository;

import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.PersonPolicyPremiumCoverageEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

@Repository
public interface PersonPolicyPremiumCoverageRepository extends PageableRepository<PersonPolicyPremiumCoverageEntity, UUID> {
}
