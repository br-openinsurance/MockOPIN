package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.FinancialRiskPolicyCoverageEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.UUID;

@Repository
public interface FinancialRiskPolicyCoverageRepository extends PageableRepository<FinancialRiskPolicyCoverageEntity, UUID> {
}