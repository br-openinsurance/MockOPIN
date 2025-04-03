package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialLeadEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuotePatrimonialLeadRepository extends PageableRepository<QuotePatrimonialLeadEntity, UUID> {
    Optional<QuotePatrimonialLeadEntity> findByConsentId(String consentId);
}
