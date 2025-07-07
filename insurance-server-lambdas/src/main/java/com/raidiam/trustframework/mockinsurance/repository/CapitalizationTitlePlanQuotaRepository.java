package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.CapitalizationTitlePlanQuotaEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Repository
public interface CapitalizationTitlePlanQuotaRepository extends PageableRepository<CapitalizationTitlePlanQuotaEntity, UUID> {
    @Join(value="capitalizationTitlePlanSeries", type = Join.Type.FETCH)
    Page<CapitalizationTitlePlanQuotaEntity> findByCapitalizationTitlePlanSeriesId(@NotNull UUID capitalizationTitlePlanSeriesId, Pageable pageable);
}