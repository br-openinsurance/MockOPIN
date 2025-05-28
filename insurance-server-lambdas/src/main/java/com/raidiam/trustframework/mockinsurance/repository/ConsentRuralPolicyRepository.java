package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.ConsentRuralPolicyEntity;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

@Repository
public interface ConsentRuralPolicyRepository extends PageableRepository<ConsentRuralPolicyEntity, Integer> {
    
    @Join(value="policy", type = Join.Type.FETCH)
    @Join(value="consent", type = Join.Type.FETCH)
    Page<ConsentRuralPolicyEntity> findByConsentConsentIdOrderByCreatedAtAsc(@NotNull String consentId, Pageable pageable);
}
