package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.ConsentLifePensionContractEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

@Repository
public interface ConsentLifePensionContractRepository extends PageableRepository<ConsentLifePensionContractEntity, Integer> {

    @Join(value="lifePensionContract", type=Join.Type.LEFT_FETCH)
    @Join(value="consent", type=Join.Type.LEFT_FETCH)
    Page<ConsentLifePensionContractEntity> findByConsentIdOrderByCreatedAtAsc(@NotNull String consentId, Pageable pageable);
}
