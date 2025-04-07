package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuoteLifePensionEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuoteLifePensionRepository extends PageableRepository<QuoteLifePensionEntity, UUID> {
    Optional<QuoteLifePensionEntity> findByConsentId(String id);
}