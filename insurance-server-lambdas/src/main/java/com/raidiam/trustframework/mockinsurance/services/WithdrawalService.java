package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.domain.WithdrawalCapitalizationTitleEntity;
import com.raidiam.trustframework.mockinsurance.domain.WithdrawalPensionEntity;
import com.raidiam.trustframework.mockinsurance.domain.WithdrawalPensionLeadEntity;
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentPermission;
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentStatus;
import com.raidiam.trustframework.mockinsurance.models.generated.EnumConsentV3Permission;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
@Transactional(dontRollbackOn = {HttpStatusException.class})
public class WithdrawalService extends BaseInsuranceService {

    private static final Logger LOG = LoggerFactory.getLogger(WithdrawalService.class);

    @Inject
    ConsentService consentService;

    public WithdrawalPensionLeadEntity createPensionWithdrawalLead(WithdrawalPensionLeadEntity withdrawal) {
        ConsentEntity consent = validateConsent(withdrawal.getConsentId(), withdrawal.getClientId(),
                EnumConsentPermission.PENSION_WITHDRAWAL_LEAD_CREATE, EnumConsentV3Permission.PENSION_WITHDRAWAL_LEAD_CREATE);
        var data = withdrawal.getData();
        validateWithdrawalLifePensionInformation(consent,
                data.getCertificateId(), data.getProductName(), data.getWithdrawalType(), data.getWithdrawalReason());

        LOG.info("Consuming consent");
        consentService.consumeConsent(withdrawal.getConsentId());

        LOG.info("Creating pension withdrawal lead");
        return withdrawalPensionLeadRepository.save(withdrawal);
    }

    public WithdrawalPensionEntity createPensionWithdrawal(WithdrawalPensionEntity withdrawal) {
        ConsentEntity consent = validateConsent(withdrawal.getConsentId(), withdrawal.getClientId(),
                EnumConsentPermission.PENSION_WITHDRAWAL_CREATE, EnumConsentV3Permission.PENSION_WITHDRAWAL_CREATE);
        var data = withdrawal.getData();
        validateWithdrawalLifePensionInformation(consent,
                data.getCertificateId(), data.getProductName(), data.getWithdrawalType(), data.getWithdrawalReason());

        LOG.info("Consuming consent");
        consentService.consumeConsent(withdrawal.getConsentId());

        LOG.info("Creating pension withdrawal");
        return withdrawalPensionRepository.save(withdrawal);
    }

    public WithdrawalCapitalizationTitleEntity createCapitalizationTitleWithdrawal(WithdrawalCapitalizationTitleEntity withdrawal) {
        ConsentEntity consent = validateConsent(withdrawal.getConsentId(), withdrawal.getClientId(),
                EnumConsentPermission.CAPITALIZATION_TITLE_WITHDRAWAL_CREATE, EnumConsentV3Permission.CAPITALIZATION_TITLE_WITHDRAWAL_CREATE);
        var data = withdrawal.getData();
        validateWithdrawalCaptalizationInformation(consent,
                data.getCapitalizationTitleName(), data.getPlanId(), data.getTitleId(),
                data.getSeriesId(), data.getTermEndDate(), data.getWithdrawalReason());

        LOG.info("Consuming consent");
        consentService.consumeConsent(withdrawal.getConsentId());

        LOG.info("Creating capitalization title withdrawal");
        return withdrawalCapitalizationTitleRepository.save(withdrawal);
    }

    private ConsentEntity validateConsent(String consentId, String clientId,
                                          EnumConsentPermission permission, EnumConsentV3Permission permissionV3) {
        LOG.info("Validating withdrawal");

        if (StringUtils.isBlank(consentId)) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: consent id was not informed");
        }

        ConsentEntity consent = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        if (!clientId.equals(consent.getClientId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "NAO_INFORMADO: Requested a consent created with a different oauth client");
        }

        if (!consent.getStatus().equals(EnumConsentStatus.AUTHORISED.toString())) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "NAO_INFORMADO: consent is not authorised");
        }

        InsuranceLambdaUtils.checkConsentPermissions(consent, permission, permissionV3);

        return consent;
    }

    private void validateWithdrawalLifePensionInformation(ConsentEntity consent,
                                                          String certificateId, String productName,
                                                          String withdrawalType, String withdrawalReason) {
        var info = consent.getWithdrawalLifePensionInformation();

        if (info == null) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: consent does not have withdrawal life pension information");
        }

        assertFieldMatch(consent, certificateId, info.getCertificateId(), "certificateId");
        assertFieldMatch(consent, productName, info.getProductName(), "productName");

        if (withdrawalType != null && info.getWithdrawalType() != null
                && !stripVersionedEnumPrefix(withdrawalType)
                        .equals(stripVersionedEnumPrefix(info.getWithdrawalType().toString()))) {
            consumeAndThrow(consent, "NAO_INFORMADO: withdrawalType does not match");
        }

        if (withdrawalReason != null && info.getWithdrawalReason() != null
                && !stripVersionedEnumPrefix(withdrawalReason)
                        .equals(stripVersionedEnumPrefix(info.getWithdrawalReason().toString()))) {
            consumeAndThrow(consent, "NAO_INFORMADO: withdrawalReason does not match");
        }

        assertEntityExists(consent, certificateId,
                lifePensionContractRepository::findByLifePensionContractId,
                "NAO_INFORMADO: certificateId does not exist in life pension contracts");
    }

    private void validateWithdrawalCaptalizationInformation(ConsentEntity consent,
                                                            String capitalizationTitleName, String planId,
                                                            String titleId, String seriesId,
                                                            Object termEndDate, String withdrawalReason) {
        var info = consent.getWithdrawalCaptalizationInformation();

        if (info == null) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "NAO_INFORMADO: consent does not have withdrawal capitalization information");
        }

        assertFieldMatch(consent, capitalizationTitleName, info.getCapitalizationTitleName(), "capitalizationTitleName");
        assertFieldMatch(consent, planId, info.getPlanId(), "planId");
        assertFieldMatch(consent, titleId, info.getTitleId(), "titleId");
        assertFieldMatch(consent, seriesId, info.getSeriesId(), "seriesId");
        assertFieldMatch(consent, termEndDate, info.getTermEndDate(), "termEndDate");

        assertEntityExists(consent, planId,
                capitalizationTitleRepository::findByCapitalizationTitlePlanId,
                "NAO_INFORMADO: planId does not exist in capitalization title plans");

        if (withdrawalReason != null && info.getWithdrawalReason() != null
                && !withdrawalReason.equals(info.getWithdrawalReason().toString())) {
            consumeAndThrow(consent, "NAO_INFORMADO: withdrawalReason does not match");
        }
    }

    private void assertFieldMatch(ConsentEntity consent, Object requestValue, Object consentValue, String fieldName) {
        if (!requestValue.equals(consentValue)) {
            consumeAndThrow(consent, "NAO_INFORMADO: " + fieldName + " does not match");
        }
    }

    private void assertEntityExists(ConsentEntity consent, String id, Function<UUID, Optional<?>> finder, String message) {
        try {
            UUID entityId = UUID.fromString(id);
            if (finder.apply(entityId).isEmpty()) {
                consumeAndThrow(consent, message);
            }
        } catch (IllegalArgumentException e) {
            consumeAndThrow(consent, message);
        }
    }

    private void consumeAndThrow(ConsentEntity consent, String message) {
        consent.setStatus(EnumConsentStatus.CONSUMED.toString());
        consent.setStatusUpdateDateTime(Date.from(Instant.now()));
        consentRepository.update(consent);
        throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
    
    private static final Pattern NUMERIC_VERSION_PREFIX_PATTERN = Pattern.compile("^\\d+_");

    static String stripVersionedEnumPrefix(String value) {
        if (value == null) {
            return null;
        }
        Matcher matcher = NUMERIC_VERSION_PREFIX_PATTERN.matcher(value);
        if (matcher.find()) {
            return value.substring(matcher.end());
        }
        return value;
    }
}
