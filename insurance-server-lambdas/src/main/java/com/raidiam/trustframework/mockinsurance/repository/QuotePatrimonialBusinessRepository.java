package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialBusinessEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuotePatrimonialBusinessRepository extends PageableRepository<QuotePatrimonialBusinessEntity, UUID> {
    Optional<QuotePatrimonialBusinessEntity> findByConsentId(String id);
}