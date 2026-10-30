package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyPremiumEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransportPolicyPremiumRepository extends PageableRepository<TransportPolicyPremiumEntity, UUID> {
    @Join(value="transportPolicy", type = Join.Type.FETCH)
    Optional<TransportPolicyPremiumEntity> findByTransportPolicyId(@NotNull String transportPolicyId);
}
