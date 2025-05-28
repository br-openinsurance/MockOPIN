package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.CapitalizationTitlePlanEntity;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CapitalizationTitlePlanRepository extends PageableRepository<CapitalizationTitlePlanEntity, UUID> {
    Optional<CapitalizationTitlePlanEntity> findByCapitalizationTitlePlanId(UUID capitalizationTitlePlanId);

    @Join(value="accountHolder", type = Join.Type.FETCH)
    List<CapitalizationTitlePlanEntity> findByAccountHolderUserId(@NotNull String userId);
}
