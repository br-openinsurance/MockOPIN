package com.raidiam.trustframework.mockinsurance.repository;

import java.util.Optional;
import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.QuoteCapitalizationTitleEntity;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

@Repository
public interface QuoteCapitalizationTitleRepository extends PageableRepository<QuoteCapitalizationTitleEntity, UUID> {
    Optional<QuoteCapitalizationTitleEntity> findByConsentId(String consentId);
}
