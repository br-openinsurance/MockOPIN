package com.raidiam.trustframework.mockinsurance.repository;

import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.domain.RuralPolicyInsuredObjectEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

@Repository
public interface RuralPolicyInsuredObjectRepository extends PageableRepository<RuralPolicyInsuredObjectEntity, UUID> {
}
