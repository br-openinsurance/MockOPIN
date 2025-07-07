package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.OverrideResponseEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OverrideResponseRepository extends PageableRepository<OverrideResponseEntity, UUID> {
    Optional<OverrideResponseEntity> findByClientIdAndPathAndMethod(String clientId, String path, String method);
}

