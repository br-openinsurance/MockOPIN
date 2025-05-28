package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.BusinessComplimentaryInformationEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BusinessComplimentaryInformationRepository extends PageableRepository<BusinessComplimentaryInformationEntity, UUID> {
    List<BusinessComplimentaryInformationEntity> findByAccountHolderAccountHolderId(UUID accountHolderId);
}