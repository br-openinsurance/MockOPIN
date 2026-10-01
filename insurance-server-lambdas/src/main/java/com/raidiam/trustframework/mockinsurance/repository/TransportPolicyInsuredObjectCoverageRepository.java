package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyInsuredObjectCoverageEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.UUID;

@Repository
public interface TransportPolicyInsuredObjectCoverageRepository extends PageableRepository<TransportPolicyInsuredObjectCoverageEntity, UUID> {
}
