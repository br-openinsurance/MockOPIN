package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.ClaimNotificationDamageEntity
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.transaction.TransactionDefinition
import io.micronaut.transaction.annotation.Transactional

@Singleton
@Replaces(ClaimNotificationDamageService)
@Transactional(propagation = TransactionDefinition.Propagation.NOT_SUPPORTED)
@Requires(property = "spec.name", value = "ClaimNotificationControllerSpec")
class StubClaimNotificationDamageService extends ClaimNotificationDamageService {
    @Override
    ClaimNotificationDamageEntity createClaimNotification(ClaimNotificationDamageEntity claim) {
        claim.claimId = TestEntityDataFactory.aClaimId()
        return claim
    }
}