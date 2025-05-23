package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuotePersonTravelEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuotePersonTravelRepository extends PageableRepository<QuotePersonTravelEntity, UUID> {
    Optional<QuotePersonTravelEntity> findByConsentId(String id);
}