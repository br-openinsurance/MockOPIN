package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuoteFinancialRiskLeadEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuoteFinancialRiskLeadRepository extends PageableRepository<QuoteFinancialRiskLeadEntity, UUID> {
    Optional<QuoteFinancialRiskLeadEntity> findByConsentId(String consentId);
}
