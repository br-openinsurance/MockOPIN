package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuoteLifePensionLeadEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuoteLifePensionLeadRepository extends PageableRepository<QuoteLifePensionLeadEntity, UUID> {
    Optional<QuoteLifePensionLeadEntity> findByConsentId(String consentId);
}