package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.CapitalizationTitleRaffleEntity;

import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentPermission;
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentStatus;
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentV3Permission;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
@Transactional
public class CapitalizationTitleRaffleService extends BaseInsuranceService {

    private final ConsentService consentService;

    public CapitalizationTitleRaffleService(ConsentService consentService) {
        this.consentService = consentService;
    }

    public CapitalizationTitleRaffleEntity createRaffle(CapitalizationTitleRaffleEntity raffle, String consentId) {
        ConsentEntity consent = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        if (raffle.getClientId() == null || !raffle.getClientId().equals(consent.getClientId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "NAO_INFORMADO: Requested a consent created with a different oauth client");
        }

        if (!consent.getStatus().equals(EnumConsentStatus.AUTHORISED.toString())) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "NAO_INFORMADO: consent is not authorised");
        }

        InsuranceLambdaUtils.checkConsentPermissions(consent,
                EnumConsentPermission.QUOTE_CAPITALIZATION_TITLE_RAFFLE_CREATE,
                EnumConsentV3Permission.CAPITALIZATION_TITLE_RAFFLE_CREATE);

        consentService.consumeConsent(consentId);
        return capitalizationTitleRaffleRepository.save(raffle);
    }

}
