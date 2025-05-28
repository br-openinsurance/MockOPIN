package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.BusinessIdentificationEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BusinessIdentificationRepository extends PageableRepository<BusinessIdentificationEntity, UUID> {
    List<BusinessIdentificationEntity> findByAccountHolderAccountHolderId(UUID accountHolderId);
}
