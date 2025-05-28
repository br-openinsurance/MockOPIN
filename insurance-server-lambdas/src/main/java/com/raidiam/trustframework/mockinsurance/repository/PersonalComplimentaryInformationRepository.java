package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.PersonalComplimentaryInformationEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonalComplimentaryInformationRepository extends PageableRepository<PersonalComplimentaryInformationEntity, UUID> {
    List<PersonalComplimentaryInformationEntity> findByAccountHolderAccountHolderId(UUID accountHolderId);
}
