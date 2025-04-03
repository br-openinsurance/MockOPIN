package com.raidiam.trustframework.mockinsurance.repository;

import java.util.Optional;
import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.QuoteAutoLeadEntity;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

@Repository
public interface QuoteAutoLeadRepository extends PageableRepository<QuoteAutoLeadEntity, UUID> {
    Optional<QuoteAutoLeadEntity> findByConsentId(String consentId);
}
