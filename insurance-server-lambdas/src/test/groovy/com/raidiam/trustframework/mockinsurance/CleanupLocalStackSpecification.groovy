package com.raidiam.trustframework.mockinsurance

import spock.lang.Shared

import jakarta.inject.Inject

class CleanupLocalStackSpecification extends AbstractLocalStackSpec {

    @Shared
    boolean runSetup = true

    @Shared
    boolean runCleanup = false

    def cleanup() {
        if (runCleanup) {

            runCleanup = false
            runSetup = true
        }
    }
}
