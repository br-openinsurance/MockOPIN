package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.ConsentFinancialAssistanceContractEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

@Repository
public interface ConsentFinancialAssistanceContractRepository extends PageableRepository<ConsentFinancialAssistanceContractEntity, Integer> {

    @Join(value="financialAssistanceContract", type=Join.Type.LEFT_FETCH)
    @Join(value="consent", type=Join.Type.LEFT_FETCH)
    Page<ConsentFinancialAssistanceContractEntity> findByConsentIdOrderByCreatedAtAsc(@NotNull String consentId, Pageable pageable);
}
