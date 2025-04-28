package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuotePersonLeadEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuotePersonLeadRepository extends PageableRepository<QuotePersonLeadEntity, UUID> {
    Optional<QuotePersonLeadEntity> findByConsentId(String id);
}