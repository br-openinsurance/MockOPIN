package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.ClaimNotificationDamageEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimNotificationDamageRepository extends PageableRepository<ClaimNotificationDamageEntity, UUID> {
    Optional<ClaimNotificationDamageEntity> findByConsentId(String id);
}