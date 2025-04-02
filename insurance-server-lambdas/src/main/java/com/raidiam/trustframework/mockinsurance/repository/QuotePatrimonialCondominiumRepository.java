package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialCondominiumEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuotePatrimonialCondominiumRepository extends PageableRepository<QuotePatrimonialCondominiumEntity, UUID> {
    Optional<QuotePatrimonialCondominiumEntity> findByConsentId(String id);
}