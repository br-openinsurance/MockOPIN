package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.AccountHolderEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountHolderRepository extends PageableRepository<AccountHolderEntity, Integer> {
    Optional<AccountHolderEntity> findByDocumentIdentificationAndDocumentRel(@NotNull String documentId, @NotNull String documentRel);

    Optional<AccountHolderEntity> findByAccountHolderId(@NotNull UUID accountHolderId);
}
