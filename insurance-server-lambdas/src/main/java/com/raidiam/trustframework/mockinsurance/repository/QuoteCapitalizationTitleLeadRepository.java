package com.raidiam.trustframework.mockinsurance.repository;

import java.util.Optional;
import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.QuoteCapitalizationTitleLeadEntity;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

@Repository
public interface QuoteCapitalizationTitleLeadRepository extends PageableRepository<QuoteCapitalizationTitleLeadEntity, UUID> {
    Optional<QuoteCapitalizationTitleLeadEntity> findByConsentId(String consentId);
}
