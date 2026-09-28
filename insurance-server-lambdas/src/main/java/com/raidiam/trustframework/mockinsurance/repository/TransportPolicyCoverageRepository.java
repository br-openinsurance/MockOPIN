package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyCoverageEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.UUID;

@Repository
public interface TransportPolicyCoverageRepository extends PageableRepository<TransportPolicyCoverageEntity, UUID> {
}
