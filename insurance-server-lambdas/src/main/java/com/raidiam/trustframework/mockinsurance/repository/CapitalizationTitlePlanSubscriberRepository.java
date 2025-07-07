package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.CapitalizationTitlePlanSubscriberEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Repository
public interface CapitalizationTitlePlanSubscriberRepository extends PageableRepository<CapitalizationTitlePlanSubscriberEntity, UUID> {
    @Join(value="capitalizationTitleTitle", type = Join.Type.FETCH)
    Page<CapitalizationTitlePlanSubscriberEntity> findBycapitalizationTitlePlanTitleId(@NotNull UUID capitalizationTitlePlanTitleId, Pageable pageable);
}