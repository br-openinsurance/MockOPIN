package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.PersonalQualificationEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonalQualificationRepository extends PageableRepository<PersonalQualificationEntity, UUID> {
    List<PersonalQualificationEntity> findByAccountHolderAccountHolderId(UUID accountHolderId);
}