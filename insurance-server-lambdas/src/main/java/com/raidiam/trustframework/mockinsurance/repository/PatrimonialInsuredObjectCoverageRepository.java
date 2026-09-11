package com.raidiam.trustframework.mockinsurance.repository;

import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.PatrimonialInsuredObjectCoverageEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

@Repository
public interface PatrimonialInsuredObjectCoverageRepository extends PageableRepository<PatrimonialInsuredObjectCoverageEntity, UUID> {
}
