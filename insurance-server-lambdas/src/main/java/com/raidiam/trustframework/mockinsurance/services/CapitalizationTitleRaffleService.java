package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.CapitalizationTitleRaffleEntity;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
@Transactional
public class CapitalizationTitleRaffleService extends BaseInsuranceService {

    public CapitalizationTitleRaffleEntity createRaffle(CapitalizationTitleRaffleEntity raffle) {
        return capitalizationTitleRaffleRepository.save(raffle);
    }

}
