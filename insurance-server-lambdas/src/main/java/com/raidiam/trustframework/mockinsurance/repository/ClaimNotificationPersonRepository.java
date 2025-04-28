package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.ClaimNotificationPersonEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimNotificationPersonRepository extends PageableRepository<ClaimNotificationPersonEntity, UUID> {
    Optional<ClaimNotificationPersonEntity> findByConsentId(String id);
}