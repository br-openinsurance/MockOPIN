package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.CapitalizationTitlePlanHolderEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Repository
public interface CapitalizationTitleHolderRepository extends PageableRepository<CapitalizationTitlePlanHolderEntity, UUID> {
    @Join(value="capitalizationTitleSubscriber", type = Join.Type.FETCH)
    Page<CapitalizationTitlePlanHolderEntity> findByCapitalizationTitleSubscriberId(@NotNull UUID capitalizationTitleSubscriberId, Pageable pageable);
}