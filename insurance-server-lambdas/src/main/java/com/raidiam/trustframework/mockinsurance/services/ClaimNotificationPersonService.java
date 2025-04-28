package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.ClaimNotificationPersonEntity;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
@Transactional
public class ClaimNotificationPersonService extends ClaimNotificationService<ClaimNotificationPersonEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(ClaimNotificationPersonService.class);

    @Override
    protected ClaimNotificationPersonEntity saveClaimNotification(ClaimNotificationPersonEntity claim) {
        return claimNotificationPersonRepository.save(claim);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
