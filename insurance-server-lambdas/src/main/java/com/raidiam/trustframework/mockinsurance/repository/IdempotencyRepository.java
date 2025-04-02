package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.IdempotencyEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

@Repository
public interface IdempotencyRepository extends PageableRepository<IdempotencyEntity, String> {
    Optional<IdempotencyEntity> findByIdempotencyId(@NotNull String id);
}