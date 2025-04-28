package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.ClaimNotificationDamageEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
@Transactional
public class ClaimNotificationDamageService extends ClaimNotificationService<ClaimNotificationDamageEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(ClaimNotificationDamageService.class);

    @Override
    protected ClaimNotificationDamageEntity saveClaimNotification(ClaimNotificationDamageEntity claim) {
        return claimNotificationDamageRepository.save(claim);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
