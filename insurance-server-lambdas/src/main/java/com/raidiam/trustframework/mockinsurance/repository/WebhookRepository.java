package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.WebhookEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

@Repository
public interface WebhookRepository  extends PageableRepository<WebhookEntity, String> {
    Optional<WebhookEntity> findByClientId(@NotNull String clientId);
}
