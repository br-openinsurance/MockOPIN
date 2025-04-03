package com.raidiam.trustframework.mockinsurance.repository;

import java.util.Optional;
import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.QuoteAutoEntity;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

@Repository
public interface QuoteAutoRepository extends PageableRepository<QuoteAutoEntity, UUID> {
    Optional<QuoteAutoEntity> findByConsentId(String consentId);
}
