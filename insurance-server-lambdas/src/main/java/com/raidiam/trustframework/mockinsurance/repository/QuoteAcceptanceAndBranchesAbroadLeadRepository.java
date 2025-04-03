package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.QuoteAcceptanceAndBranchesAbroadLeadEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuoteAcceptanceAndBranchesAbroadLeadRepository extends PageableRepository<QuoteAcceptanceAndBranchesAbroadLeadEntity, UUID> {
    Optional<QuoteAcceptanceAndBranchesAbroadLeadEntity> findByConsentId(String id);
}