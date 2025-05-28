package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.PersonalIdentificationEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonalIdentificationRepository extends PageableRepository<PersonalIdentificationEntity, UUID> {
    List<PersonalIdentificationEntity> findByAccountHolderAccountHolderId(UUID accountHolderId);
}