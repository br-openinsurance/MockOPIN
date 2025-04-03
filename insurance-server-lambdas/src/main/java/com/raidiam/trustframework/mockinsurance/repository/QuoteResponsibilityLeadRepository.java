package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuoteResponsibilityLeadEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuoteResponsibilityLeadRepository extends PageableRepository<QuoteResponsibilityLeadEntity, UUID> {
    Optional<QuoteResponsibilityLeadEntity> findByConsentId(String consentId);
}
