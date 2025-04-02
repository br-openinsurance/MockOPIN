package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

@Repository
public interface ConsentRepository extends PageableRepository<ConsentEntity, Integer> {
    Optional<ConsentEntity> findByConsentId(@NotNull String id);
}