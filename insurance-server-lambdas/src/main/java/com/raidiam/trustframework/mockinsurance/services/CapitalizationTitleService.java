package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.*;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@Singleton
@Transactional
public class CapitalizationTitleService extends BaseInsuranceService {

    @Inject
    ResourcesService resourcesService;

    private static final Logger LOG = LoggerFactory.getLogger(CapitalizationTitleService.class);

    public ResponseInsuranceCapitalizationTitle getPlans(String consentId, Pageable pageable) {
        LOG.info("Getting capitalization title plans response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.CAPITALIZATION_TITLE_READ, EnumConsentV3Permission.CAPITALIZATION_TITLE_READ);

        var consentPlans = consentCapitalizationTitlePlanRepository.findByConsentConsentIdOrderByCreatedAtAsc(consentId, pageable);
        this.checkConsentOwnerIsPlanOwner(consentPlans, consentEntity);

        var response = new ResponseInsuranceCapitalizationTitle()
                .data(List.of(new ResponseInsuranceCapitalizationTitleData()
                        .brand(new ResponseInsuranceCapitalizationTitleBrand()
                                .name("Mock")
                                .companies(List.of(new ResponseInsuranceCapitalizationTitleBrandCompanies()
                                        .companyName("Mock Insurer")
                                        .cnpjNumber("12345678901234")
                                        .products(consentPlans.getContent()
                                                .stream()
                                                .map(consentAccountEntity -> {
                                                    resourcesService.checkStatusAvailable(consentAccountEntity.getCapitalizationTitlePlan(), consentEntity);
                                                    return consentAccountEntity.getCapitalizationTitlePlan();
                                                })
                                                .map(CapitalizationTitlePlanEntity::mapProductDto)
                                                .toList()))))));
        response.setMeta(InsuranceLambdaUtils.getMeta(consentPlans, false));
        return response;
    }

    public ResponseInsuranceCapitalizationTitleV2 getPlansV2(String consentId, Pageable pageable) {
        LOG.info("Getting capitalization title plans response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.CAPITALIZATION_TITLE_READ, EnumConsentV3Permission.CAPITALIZATION_TITLE_READ);

        var consentPlans = consentCapitalizationTitlePlanRepository.findByConsentConsentIdOrderByCreatedAtAsc(consentId, pageable);
        this.checkConsentOwnerIsPlanOwner(consentPlans, consentEntity);

        var response = new ResponseInsuranceCapitalizationTitleV2()
                .data(List.of(new ResponseInsuranceCapitalizationTitleV2Data()
                        .brand(new ResponseInsuranceCapitalizationTitleV2Brand()
                                .name("Mock")
                                .companies(List.of(new ResponseInsuranceCapitalizationTitleV2BrandCompanies()
                                        .companyName("Mock Insurer")
                                        .cnpjNumber("12345678901234")
                                        .products(consentPlans.getContent()
                                                .stream()
                                                .map(consentAccountEntity -> {
                                                    resourcesService.checkStatusAvailable(consentAccountEntity.getCapitalizationTitlePlan(), consentEntity);
                                                    return consentAccountEntity.getCapitalizationTitlePlan();
                                                })
                                                .map(CapitalizationTitlePlanEntity::mapProductDto)
                                                .toList()))))));
        response.setMeta(InsuranceLambdaUtils.getMeta(consentPlans, false));
        return response;
    }

    public ResponseInsuranceCapitalizationTitlePlanInfo getPlanInfo(UUID planId, String consentId) {
        LOG.info("Getting capitalization title plan info response for consent id {}", consentId);
        return getPlan(planId, consentId, EnumConsentPermission.CAPITALIZATION_TITLE_PLANINFO_READ, EnumConsentV3Permission.CAPITALIZATION_TITLE_PLANINFO_READ).mapPlanInfoDto();
    }

    public ResponseInsuranceCapitalizationTitlePlanInfoV2 getPlanInfoV2(UUID planId, String consentId) {
        LOG.info("Getting capitalization title plan info response for consent id {}", consentId);
        return getPlan(planId, consentId, EnumConsentPermission.CAPITALIZATION_TITLE_PLANINFO_READ, EnumConsentV3Permission.CAPITALIZATION_TITLE_PLANINFO_READ).mapPlanInfoDtoV2();
    }

    public ResponseInsuranceCapitalizationTitleEvent getPlanEvents(UUID planId, String consentId, Pageable pageable) {
        LOG.info("Getting capitalization title plan events response for consent id {}", consentId);
        getPlan(planId, consentId, EnumConsentPermission.CAPITALIZATION_TITLE_EVENTS_READ, EnumConsentV3Permission.CAPITALIZATION_TITLE_EVENTS_READ);

        var events = capitalizationTitlePlanEventRepository.findByCapitalizationTitlePlanId(planId, pageable);
        var resp = new ResponseInsuranceCapitalizationTitleEvent()
                .data(events.getContent().stream().map(CapitalizationTitlePlanEventEntity::mapDto).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(events, false));
        return resp;
    }

    public ResponseInsuranceCapitalizationTitleSettlement getPlanSettlements(UUID planId, String consentId, Pageable pageable) {
        LOG.info("Getting capitalization title plan settlements response for consent id {}", consentId);
        getPlan(planId, consentId, EnumConsentPermission.CAPITALIZATION_TITLE_SETTLEMENTS_READ, EnumConsentV3Permission.CAPITALIZATION_TITLE_SETTLEMENTS_READ);

        var settlements = capitalizationTitlePlanSettlementRepository.findByCapitalizationTitlePlanId(planId, pageable);
        var resp = new ResponseInsuranceCapitalizationTitleSettlement()
                .data(settlements.getContent().stream().map(CapitalizationTitlePlanSettlementEntity::mapDto).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(settlements, false));
        return resp;
    }

    private CapitalizationTitlePlanEntity getPlan(UUID planId, String consentId, EnumConsentPermission permission, EnumConsentV3Permission permissionV3) {
        LOG.info("Getting capitalization title plan for plan id {} and consent id {}", planId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var plan = capitalizationTitleRepository.findByCapitalizationTitlePlanId(planId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Plan id " + planId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission, permissionV3);
        this.checkConsentCoversPlan(consentEntity, plan);
        this.checkConsentOwnerIsPlanOwner(consentEntity, plan);

        return plan;
    }

    public void checkConsentCoversPlan(ConsentEntity consentEntity, CapitalizationTitlePlanEntity plan) {
        var planFromConsent = consentEntity.getCapitalizationTitlePlans()
                .stream()
                .filter(p -> plan.getCapitalizationTitleId().equals(p.getCapitalizationTitleId()))
                .findFirst();
        if (planFromConsent.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad request, consent does not cover this capitalization title!");
        }
    }

    public void checkConsentOwnerIsPlanOwner(ConsentEntity consentEntity, CapitalizationTitlePlanEntity plan) {
        if (!consentEntity.getAccountHolderId().equals(plan.getAccountHolderId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match account owner!");
        }
    }

    public void checkConsentOwnerIsPlanOwner(Page<ConsentCapitalizationTitlePlanEntity> consentPlan, ConsentEntity consentEntity) {
        if(consentPlan.getContent()
                .stream()
                .map(ConsentCapitalizationTitlePlanEntity::getCapitalizationTitlePlan)
                .map(CapitalizationTitlePlanEntity::getAccountHolderId)
                .anyMatch(accountHolderId -> !accountHolderId.equals(consentEntity.getAccountHolderId()))) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match plan owner!");
        }
    }
}
