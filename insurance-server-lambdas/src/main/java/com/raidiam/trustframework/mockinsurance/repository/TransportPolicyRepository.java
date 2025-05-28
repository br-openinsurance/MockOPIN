package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.TransportPolicyEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportPolicyRepository extends PageableRepository<TransportPolicyEntity, String> {
    Optional<TransportPolicyEntity> findByTransportPolicyId(String transportPolicyId);

    @Join(value="accountHolder", type = Join.Type.FETCH)
    List<TransportPolicyEntity> findByAccountHolderUserId(@NotNull String userId);
}
