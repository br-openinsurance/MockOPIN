package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuoteRuralLeadEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuoteRuralLeadRepository extends PageableRepository<QuoteRuralLeadEntity, UUID> {
    Optional<QuoteRuralLeadEntity> findByConsentId(String consentId);
}
