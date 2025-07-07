package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.CapitalizationTitlePlanSeriesEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Repository
public interface CapitalizationTitlePlanPlanSeriesRepository extends PageableRepository<CapitalizationTitlePlanSeriesEntity, UUID> {
    @Join(value="capitalizationTitlePlan", type = Join.Type.FETCH)
    Page<CapitalizationTitlePlanSeriesEntity> findByCapitalizationTitlePlanId(@NotNull UUID capitalizationTitlePlanId, Pageable pageable);
}