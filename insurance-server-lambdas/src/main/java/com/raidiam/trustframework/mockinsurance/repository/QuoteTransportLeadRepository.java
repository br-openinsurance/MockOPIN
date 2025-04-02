package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuoteTransportLeadEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuoteTransportLeadRepository extends PageableRepository<QuoteTransportLeadEntity, UUID> {
    Optional<QuoteTransportLeadEntity> findByConsentId(String consentId);
}
