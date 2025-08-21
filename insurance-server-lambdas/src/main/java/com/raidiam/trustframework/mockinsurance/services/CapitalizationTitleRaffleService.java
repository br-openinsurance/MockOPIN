package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.CapitalizationTitleRaffleEntity;

import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentStatus;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.Date;

@Singleton
@Transactional
public class CapitalizationTitleRaffleService extends BaseInsuranceService {

    public CapitalizationTitleRaffleEntity createRaffle(CapitalizationTitleRaffleEntity raffle, String consentId) {
        ConsentEntity consent = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        consent.setStatus(EnumConsentStatus.CONSUMED.toString());
        consent.setStatusUpdateDateTime(Date.from(Instant.now()));
        consentRepository.update(consent);
        return capitalizationTitleRaffleRepository.save(raffle);
    }

}
