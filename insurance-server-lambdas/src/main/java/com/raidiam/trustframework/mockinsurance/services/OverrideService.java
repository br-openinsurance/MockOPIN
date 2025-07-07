package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.OverrideResponseEntity;
import com.raidiam.trustframework.mockinsurance.models.generated.OverridePayload;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
public class OverrideService extends BaseInsuranceService {
    private static final Logger LOG = LoggerFactory.getLogger(OverrideService.class);

    public OverridePayload override(OverridePayload req, String clientId) {
        var existingEntity = overrideResponseRepository.findByClientIdAndPathAndMethod(
                clientId, req.getData().getPath(), req.getData().getMethod().toString()
        );

        OverrideResponseEntity entity = existingEntity.orElseGet(OverrideResponseEntity::new);
        entity.updateFromRequest(req, clientId);
        if (existingEntity.isEmpty()) {
            return overrideResponseRepository.save(entity).toResponse();
        } else {
            return overrideResponseRepository.update(entity).toResponse();
        }
    }

    public Optional<OverrideResponseEntity> getOverride(String clientId, String path, String method) {
        var overrideEntityOp = overrideResponseRepository.findByClientIdAndPathAndMethod(clientId, path, method);
        if (overrideEntityOp.isEmpty()) {
            return Optional.empty();
        }

        var overrideEntity = overrideEntityOp.get();
        if (overrideEntity.isExpired()) {
            LOG.info("Response override found, but expired");
            return Optional.empty();
        }

        return overrideEntityOp;
    }
}
