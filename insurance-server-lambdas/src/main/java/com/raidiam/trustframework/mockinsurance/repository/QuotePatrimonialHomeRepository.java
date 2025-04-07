package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialHomeEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuotePatrimonialHomeRepository extends PageableRepository<QuotePatrimonialHomeEntity, UUID> {
    Optional<QuotePatrimonialHomeEntity> findByConsentId(String id);
}