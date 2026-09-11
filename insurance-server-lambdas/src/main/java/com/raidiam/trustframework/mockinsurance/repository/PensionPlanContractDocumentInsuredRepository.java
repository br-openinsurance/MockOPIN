package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.PensionPlanContractDocumentInsuredEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.UUID;

@Repository
public interface PensionPlanContractDocumentInsuredRepository extends PageableRepository<PensionPlanContractDocumentInsuredEntity, UUID> {
}
