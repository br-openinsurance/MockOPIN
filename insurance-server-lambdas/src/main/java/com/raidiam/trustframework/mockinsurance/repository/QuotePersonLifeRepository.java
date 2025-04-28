package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuotePersonLifeEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuotePersonLifeRepository extends PageableRepository<QuotePersonLifeEntity, UUID> {
    Optional<QuotePersonLifeEntity> findByConsentId(String id);
}