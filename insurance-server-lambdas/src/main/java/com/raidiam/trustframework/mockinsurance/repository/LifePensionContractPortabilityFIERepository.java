package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.LifePensionContractPortabilityFIEEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.UUID;

@Repository
public interface LifePensionContractPortabilityFIERepository extends PageableRepository<LifePensionContractPortabilityFIEEntity, UUID> {
}
