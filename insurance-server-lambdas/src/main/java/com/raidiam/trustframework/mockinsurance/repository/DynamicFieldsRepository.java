package com.raidiam.trustframework.mockinsurance.repository;

import com.raidiam.trustframework.mockinsurance.domain.DynamicFieldsEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DynamicFieldsRepository extends PageableRepository<DynamicFieldsEntity, UUID> {

    List<DynamicFieldsEntity> findByApi(String api);

}
