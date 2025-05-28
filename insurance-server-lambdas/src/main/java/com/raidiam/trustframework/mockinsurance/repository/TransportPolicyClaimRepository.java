package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyClaimEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

@Repository
public interface TransportPolicyClaimRepository extends PageableRepository<TransportPolicyClaimEntity, String> {
    @Join(value="transportPolicy", type = Join.Type.FETCH)
    Page<TransportPolicyClaimEntity> findByTransportPolicyId(@NotNull String transportPolicyId, Pageable pageable);
}