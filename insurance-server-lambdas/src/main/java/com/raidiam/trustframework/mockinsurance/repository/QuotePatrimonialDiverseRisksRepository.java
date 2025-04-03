package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialDiverseRisksEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuotePatrimonialDiverseRisksRepository extends PageableRepository<QuotePatrimonialDiverseRisksEntity, UUID> {
    Optional<QuotePatrimonialDiverseRisksEntity> findByConsentId(String id);
}