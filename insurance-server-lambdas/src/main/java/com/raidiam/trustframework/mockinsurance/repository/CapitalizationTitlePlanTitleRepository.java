package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.CapitalizationTitlePlanTitleEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Repository
public interface CapitalizationTitlePlanTitleRepository extends PageableRepository<CapitalizationTitlePlanTitleEntity, UUID> {
    @Join(value="capitalizationTitlePlanSeries", type = Join.Type.FETCH)
    Page<CapitalizationTitlePlanTitleEntity> findByCapitalizationTitlePlanSeriesId(@NotNull UUID capitalizationTitlePlanSeriesId, Pageable pageable);
}