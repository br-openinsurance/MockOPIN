package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuoteHousingLeadEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuoteHousingLeadRepository extends PageableRepository<QuoteHousingLeadEntity, UUID> {
    Optional<QuoteHousingLeadEntity> findByConsentId(String consentId);
}