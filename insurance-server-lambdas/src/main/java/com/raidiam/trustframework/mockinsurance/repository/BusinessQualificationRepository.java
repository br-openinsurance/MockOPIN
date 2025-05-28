package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.BusinessQualificationEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BusinessQualificationRepository extends PageableRepository<BusinessQualificationEntity, UUID> {
    List<BusinessQualificationEntity> findByAccountHolderAccountHolderId(UUID accountHolderId);
}