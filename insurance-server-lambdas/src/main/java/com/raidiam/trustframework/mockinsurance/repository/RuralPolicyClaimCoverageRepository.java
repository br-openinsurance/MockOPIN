package com.raidiam.trustframework.mockinsurance.repository;

import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.RuralPolicyClaimCoverageEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

@Repository
public interface RuralPolicyClaimCoverageRepository extends PageableRepository<RuralPolicyClaimCoverageEntity, UUID> {
}
