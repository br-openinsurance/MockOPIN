package com.raidiam.trustframework.mockinsurance.repository;

import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.RuralPolicyPremiumCoverageEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

@Repository
public interface RuralPolicyPremiumCoverageRepository extends PageableRepository<RuralPolicyPremiumCoverageEntity, UUID> {
}
