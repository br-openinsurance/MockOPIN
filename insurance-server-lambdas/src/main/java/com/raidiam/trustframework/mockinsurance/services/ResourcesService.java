package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.*;
import com.raidiam.trustframework.mockinsurance.enums.ResourceType;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
@Transactional
public class ResourcesService extends BaseInsuranceService {

    private static final Logger LOG = LoggerFactory.getLogger(ResourcesService.class);

    private static final Map<ResourceType, ResponseResourceListData.TypeEnum> TYPE_MAP = Map.ofEntries(
            Map.entry(ResourceType.CAPITALIZATION_TITLES, ResponseResourceListData.TypeEnum.CAPITALIZATION_TITLES),
            Map.entry(ResourceType.PENSION_PLAN, ResponseResourceListData.TypeEnum.PENSION_PLAN),
            Map.entry(ResourceType.LIFE_PENSION, ResponseResourceListData.TypeEnum.LIFE_PENSION),
            Map.entry(ResourceType.FINANCIAL_ASSISTANCE, ResponseResourceListData.TypeEnum.FINANCIAL_ASSISTANCE),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_PATRIMONIAL, ResponseResourceListData.TypeEnum.DAMAGES_AND_PEOPLE_PATRIMONIAL),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_RESPONSIBILITY, ResponseResourceListData.TypeEnum.DAMAGES_AND_PEOPLE_RESPONSIBILITY),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_TRANSPORT, ResponseResourceListData.TypeEnum.DAMAGES_AND_PEOPLE_TRANSPORT),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS, ResponseResourceListData.TypeEnum.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_RURAL, ResponseResourceListData.TypeEnum.DAMAGES_AND_PEOPLE_RURAL),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_AUTO, ResponseResourceListData.TypeEnum.DAMAGES_AND_PEOPLE_AUTO),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_HOUSING, ResponseResourceListData.TypeEnum.DAMAGES_AND_PEOPLE_HOUSING),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_PERSON, ResponseResourceListData.TypeEnum.DAMAGES_AND_PEOPLE_PERSON),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD, ResponseResourceListData.TypeEnum.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD)
    );

    private static final Map<ResourceType, ResponseResourceListV3Data.TypeEnum> TYPE_MAP_V3 = Map.ofEntries(
            Map.entry(ResourceType.CAPITALIZATION_TITLES, ResponseResourceListV3Data.TypeEnum.CAPITALIZATION_TITLES),
            Map.entry(ResourceType.PENSION_PLAN, ResponseResourceListV3Data.TypeEnum.PENSION_PLAN),
            Map.entry(ResourceType.LIFE_PENSION, ResponseResourceListV3Data.TypeEnum.LIFE_PENSION),
            Map.entry(ResourceType.FINANCIAL_ASSISTANCE, ResponseResourceListV3Data.TypeEnum.FINANCIAL_ASSISTANCE),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_PATRIMONIAL, ResponseResourceListV3Data.TypeEnum.DAMAGES_AND_PEOPLE_PATRIMONIAL),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_RESPONSIBILITY, ResponseResourceListV3Data.TypeEnum.DAMAGES_AND_PEOPLE_RESPONSIBILITY),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_TRANSPORT, ResponseResourceListV3Data.TypeEnum.DAMAGES_AND_PEOPLE_TRANSPORT),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS, ResponseResourceListV3Data.TypeEnum.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_RURAL, ResponseResourceListV3Data.TypeEnum.DAMAGES_AND_PEOPLE_RURAL),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_AUTO, ResponseResourceListV3Data.TypeEnum.DAMAGES_AND_PEOPLE_AUTO),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_HOUSING, ResponseResourceListV3Data.TypeEnum.DAMAGES_AND_PEOPLE_HOUSING),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_PERSON, ResponseResourceListV3Data.TypeEnum.DAMAGES_AND_PEOPLE_PERSON),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD, ResponseResourceListV3Data.TypeEnum.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD)
    );

    private static final Map<ResourceType, EnumConsentPermission> PERMISSION_MAP = Map.ofEntries(
            Map.entry(ResourceType.CAPITALIZATION_TITLES, EnumConsentPermission.CAPITALIZATION_TITLE_READ),
            Map.entry(ResourceType.PENSION_PLAN, EnumConsentPermission.PENSION_PLAN_READ),
            Map.entry(ResourceType.LIFE_PENSION, EnumConsentPermission.LIFE_PENSION_READ),
            Map.entry(ResourceType.FINANCIAL_ASSISTANCE, EnumConsentPermission.FINANCIAL_ASSISTANCE_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_PATRIMONIAL, EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_RESPONSIBILITY, EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_TRANSPORT, EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS, EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_RURAL, EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_AUTO, EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_HOUSING, EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_PERSON, EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD, EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_READ)
    );

    private static final Map<ResourceType, EnumConsentV3Permission> PERMISSION_MAP_V3 = Map.ofEntries(
            Map.entry(ResourceType.CAPITALIZATION_TITLES, EnumConsentV3Permission.CAPITALIZATION_TITLE_READ),
            Map.entry(ResourceType.PENSION_PLAN, EnumConsentV3Permission.PENSION_PLAN_READ),
            Map.entry(ResourceType.LIFE_PENSION, EnumConsentV3Permission.LIFE_PENSION_READ),
            Map.entry(ResourceType.FINANCIAL_ASSISTANCE, EnumConsentV3Permission.FINANCIAL_ASSISTANCE_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_PATRIMONIAL, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PATRIMONIAL_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_RESPONSIBILITY, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_TRANSPORT, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_TRANSPORT_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_RURAL, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_RURAL_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_AUTO, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_AUTO_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_HOUSING, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_HOUSING_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_PERSON, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PERSON_READ),
            Map.entry(ResourceType.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD, EnumConsentV3Permission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_READ)
    );

    public ResponseResourceList getResourceList(Pageable pageable, @NotNull String consentId) {
        LOG.info("Getting resources response for consent id {}", consentId);
        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        LOG.info("Checking permissions for consent id {}", consentId);

        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.RESOURCES_READ, EnumConsentV3Permission.RESOURCES_READ);

        var permissions = InsuranceLambdaUtils.getConsentPermissions(consentEntity);
        var permissionsV3 = InsuranceLambdaUtils.getConsentV3Permissions(consentEntity);

        if (checkIfOnlyCustomersGroup(permissions, permissionsV3)) {
            return new ResponseResourceList()
                    .data(new ArrayList<>())
                    .meta(InsuranceLambdaUtils.getMeta(null, false));
        }

        LOG.info("Adding resources to response");
        Map<ResponseResourceListData, String> responseMap = new HashMap<>();

        var capitalizationTitlePlans = consentEntity.getCapitalizationTitlePlans();
        LOG.info("Found {} capitalization title plans to include in resource response", capitalizationTitlePlans.size());
        addCapitalizationTitlePlansToResources(capitalizationTitlePlans, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.CAPITALIZATION_TITLES)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.CAPITALIZATION_TITLES))));
        
        var acceptanceAndBranchesAbroadPolicies = consentEntity.getAcceptanceAndBranchesAbroadPolicies();
        LOG.info("Found {} acceptance and branches abroad policies to include in resource response", acceptanceAndBranchesAbroadPolicies.size());
        addAcceptanceAndBranchesAbroadPoliciesToResources(acceptanceAndBranchesAbroadPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD))));
        
        var patrimonialPolicies = consentEntity.getPatrimonialPolicies();
        LOG.info("Found {} patrimonial policies to include in resource response", patrimonialPolicies.size());
        addPatrimonialPoliciesToResources(patrimonialPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_PATRIMONIAL)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_PATRIMONIAL))));
        
        var ruralPolicies = consentEntity.getRuralPolicies();
        LOG.info("Found {} rural policies to include in resource response", ruralPolicies.size());
        addRuralPoliciesToResources(ruralPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_RURAL)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_RURAL))));

        var financialRiskPolicies = consentEntity.getFinancialRiskPolicies();
        LOG.info("Found {} financial risk policies to include in resource response", financialRiskPolicies.size());
        addFinancialRiskPoliciesToResources(financialRiskPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS))));

        var housingPolicies = consentEntity.getHousingPolicies();
        LOG.info("Found {} housing policies to include in resource response", housingPolicies.size());
        addHousingPoliciesToResources(housingPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_HOUSING)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_HOUSING))));

        var responsibilityPolicies = consentEntity.getResponsibilityPolicies();
        LOG.info("Found {} responsibility policies to include in resource response", responsibilityPolicies.size());
        addResponsibilityPoliciesToResources(responsibilityPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_RESPONSIBILITY)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_RESPONSIBILITY))));

        var personPolicies = consentEntity.getPersonPolicies();
        LOG.info("Found {} person policies to include in resource response", personPolicies.size());
        addPersonPoliciesToResources(personPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_PERSON)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_PERSON))));

        var lifePensionContracts = consentEntity.getLifePensionContracts();
        LOG.info("Found {} life pension contracts to include in resource response", lifePensionContracts.size());
        addLifePensionContractsToResources(lifePensionContracts, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.LIFE_PENSION)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.LIFE_PENSION))));

        var pensionPlanContracts = consentEntity.getPensionPlanContracts();
        LOG.info("Found {} pension plan contracts to include in resource response", pensionPlanContracts.size());
        addPensionPlanContractsToResources(pensionPlanContracts, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.PENSION_PLAN)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.PENSION_PLAN))));

        var financialAssistanceContracts = consentEntity.getFinancialAssistanceContracts();
        LOG.info("Found {} financial assistance contracts to include in resource response", financialAssistanceContracts.size());
        addFinancialAssistanceContractsToResources(financialAssistanceContracts, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.FINANCIAL_ASSISTANCE)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.FINANCIAL_ASSISTANCE))));

        var autoPolicies = consentEntity.getAutoPolicies();
        LOG.info("Found {} auto policies to include in resource response", autoPolicies.size());
        addAutoPoliciesToResources(autoPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_AUTO)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_AUTO))));

        var transportPolicies = consentEntity.getTransportPolicies();
        LOG.info("Found {} transport policies to include in resource response", transportPolicies.size());
        addTransportPoliciesToResources(transportPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_TRANSPORT)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_TRANSPORT))));

        var resourcePage = getPage(responseMap, pageable);
        LOG.info("Building response with resources");
        var response = new ResponseResourceList().data(resourcePage.getContent());
        response.setMeta(InsuranceLambdaUtils.getMeta(resourcePage, false));
        return response;
    }

    public ResponseResourceListV3 getResourceListV3(Pageable pageable, @NotNull String consentId) {
        LOG.info("Getting resources response for consent id {}", consentId);
        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        LOG.info("Checking permissions for consent id {}", consentId);

        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.RESOURCES_READ, EnumConsentV3Permission.RESOURCES_READ);

        var permissions = InsuranceLambdaUtils.getConsentPermissions(consentEntity);
        var permissionsV3 = InsuranceLambdaUtils.getConsentV3Permissions(consentEntity);

        if (checkIfOnlyCustomersGroup(permissions, permissionsV3)) {
            return new ResponseResourceListV3()
                    .data(new ArrayList<>())
                    .meta(InsuranceLambdaUtils.getMeta(null, false));
        }

        LOG.info("Adding resources to response");
        Map<ResponseResourceListV3Data, String> responseMap = new HashMap<>();

        var capitalizationTitlePlans = consentEntity.getCapitalizationTitlePlans();
        LOG.info("Found {} capitalization title plans to include in resource response", capitalizationTitlePlans.size());
        addCapitalizationTitlePlansToResourcesV3(capitalizationTitlePlans, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.CAPITALIZATION_TITLES)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.CAPITALIZATION_TITLES))));
        
        var acceptanceAndBranchesAbroadPolicies = consentEntity.getAcceptanceAndBranchesAbroadPolicies();
        LOG.info("Found {} acceptance and branches abroad policies to include in resource response", acceptanceAndBranchesAbroadPolicies.size());
        addAcceptanceAndBranchesAbroadPoliciesToResourcesV3(acceptanceAndBranchesAbroadPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD))));
        
        var patrimonialPolicies = consentEntity.getPatrimonialPolicies();
        LOG.info("Found {} patrimonial policies to include in resource response", patrimonialPolicies.size());
        addPatrimonialPoliciesToResourcesV3(patrimonialPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_PATRIMONIAL)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_PATRIMONIAL))));
        
        var ruralPolicies = consentEntity.getRuralPolicies();
        LOG.info("Found {} rural policies to include in resource response", ruralPolicies.size());
        addRuralPoliciesToResourcesV3(ruralPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_RURAL)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_RURAL))));

        var financialRiskPolicies = consentEntity.getFinancialRiskPolicies();
        LOG.info("Found {} financial risk policies to include in resource response", financialRiskPolicies.size());
        addFinancialRiskPoliciesToResourcesV3(financialRiskPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS))));

        var housingPolicies = consentEntity.getHousingPolicies();
        LOG.info("Found {} housing policies to include in resource response", housingPolicies.size());
        addHousingPoliciesToResourcesV3(housingPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_HOUSING)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_HOUSING))));

        var responsibilityPolicies = consentEntity.getResponsibilityPolicies();
        LOG.info("Found {} responsibility policies to include in resource response", responsibilityPolicies.size());
        addResponsibilityPoliciesToResourcesV3(responsibilityPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_RESPONSIBILITY)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_RESPONSIBILITY))));

        var personPolicies = consentEntity.getPersonPolicies();
        LOG.info("Found {} person policies to include in resource response", personPolicies.size());
        addPersonPoliciesToResourcesV3(personPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_PERSON)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_PERSON))));

        var lifePensionContracts = consentEntity.getLifePensionContracts();
        LOG.info("Found {} life pension contracts to include in resource response", lifePensionContracts.size());
        addLifePensionContractsToResourcesV3(lifePensionContracts, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.LIFE_PENSION)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.LIFE_PENSION))));

        var pensionPlanContracts = consentEntity.getPensionPlanContracts();
        LOG.info("Found {} pension plan contracts to include in resource response", pensionPlanContracts.size());
        addPensionPlanContractsToResourcesV3(pensionPlanContracts, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.PENSION_PLAN)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.PENSION_PLAN))));

        var financialAssistanceContracts = consentEntity.getFinancialAssistanceContracts();
        LOG.info("Found {} financial assistance contracts to include in resource response", financialAssistanceContracts.size());
        addFinancialAssistanceContractsToResourcesV3(financialAssistanceContracts, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.FINANCIAL_ASSISTANCE)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.FINANCIAL_ASSISTANCE))));

        var autoPolicies = consentEntity.getAutoPolicies();
        LOG.info("Found {} auto policies to include in resource response", autoPolicies.size());
        addAutoPoliciesToResourcesV3(autoPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_AUTO)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_AUTO))));

        var transportPolicies = consentEntity.getTransportPolicies();
        LOG.info("Found {} transport policies to include in resource response", transportPolicies.size());
        addTransportPoliciesToResourcesV3(transportPolicies, consentEntity, responseMap, (permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_TRANSPORT)) || permissionsV3.contains(PERMISSION_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_TRANSPORT))));

        var resourcePage = getPage(responseMap, pageable);
        LOG.info("Building response with resources");
        var response = new ResponseResourceListV3().data(resourcePage.getContent());
        response.setMeta(InsuranceLambdaUtils.getMeta(resourcePage, false));
        return response;
    }

    public void checkStatusAvailable(HasStatusInterface accountOrContract, ConsentEntity consent) {
        var status = getStatus(accountOrContract, consent);
        var statusV3 = getStatusV3(accountOrContract, consent);
        if(status.equals(ResponseResourceListData.StatusEnum.PENDING_AUTHORISATION) ||
            statusV3.equals(ResponseResourceListV3Data.StatusEnum.PENDING_AUTHORISATION) ) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, new ResponseErrorErrors()
                    .code("status_RESOURCE_PENDING_AUTHORISATION")
                    .title("Aguardando autorização de multiplas alçadas")
                    .detail("status_RESOURCE_PENDING_AUTHORISATION"));
        }
        if(status.equals(ResponseResourceListData.StatusEnum.TEMPORARILY_UNAVAILABLE) ||
            statusV3.equals(ResponseResourceListV3Data.StatusEnum.TEMPORARILY_UNAVAILABLE) ) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, new ResponseErrorErrors()
                    .code("status_RESOURCE_TEMPORARILY_UNAVAILABLE")
                    .title("Recurso temporariamente indisponível")
                    .detail("status_RESOURCE_TEMPORARILY_UNAVAILABLE"));
        }
    }

    private ResponseResourceListData.StatusEnum getStatus(HasStatusInterface accountOrContract, ConsentEntity consent) {
        if (ResponseResourceListData.StatusEnum.AVAILABLE.toString().equals(accountOrContract.getStatus())) {
            return switch (EnumConsentStatus.fromValue(consent.getStatus())) {
                case AUTHORISED -> ResponseResourceListData.StatusEnum.AVAILABLE;
                case AWAITING_AUTHORISATION -> ResponseResourceListData.StatusEnum.PENDING_AUTHORISATION;
                default -> ResponseResourceListData.StatusEnum.UNAVAILABLE;
            };
        } else if (ResponseResourceListData.StatusEnum.PENDING_AUTHORISATION.toString().equals(accountOrContract.getStatus())) {
            return ResponseResourceListData.StatusEnum.PENDING_AUTHORISATION;
        } else if (ResponseResourceListData.StatusEnum.TEMPORARILY_UNAVAILABLE.toString().equals(accountOrContract.getStatus())) {
            return ResponseResourceListData.StatusEnum.TEMPORARILY_UNAVAILABLE;
        } else {
            return ResponseResourceListData.StatusEnum.UNAVAILABLE;
        }
    }

    private ResponseResourceListV3Data.StatusEnum getStatusV3(HasStatusInterface accountOrContract, ConsentEntity consent) {
        if (ResponseResourceListV3Data.StatusEnum.AVAILABLE.toString().equals(accountOrContract.getStatus())) {
            return switch (EnumConsentStatus.fromValue(consent.getStatus())) {
                case AUTHORISED -> ResponseResourceListV3Data.StatusEnum.AVAILABLE;
                case AWAITING_AUTHORISATION -> ResponseResourceListV3Data.StatusEnum.PENDING_AUTHORISATION;
                default -> ResponseResourceListV3Data.StatusEnum.UNAVAILABLE;
            };
        } else if (ResponseResourceListV3Data.StatusEnum.PENDING_AUTHORISATION.toString().equals(accountOrContract.getStatus())) {
            return ResponseResourceListV3Data.StatusEnum.PENDING_AUTHORISATION;
        } else if (ResponseResourceListV3Data.StatusEnum.TEMPORARILY_UNAVAILABLE.toString().equals(accountOrContract.getStatus())) {
            return ResponseResourceListV3Data.StatusEnum.TEMPORARILY_UNAVAILABLE;
        } else {
            return ResponseResourceListV3Data.StatusEnum.UNAVAILABLE;
        }
    }

    private void addCapitalizationTitlePlansToResources(Set<CapitalizationTitlePlanEntity> plans, ConsentEntity consent, Map<ResponseResourceListData, String> responseMap, boolean permitted) {
        for (var plan : plans) {
            var resourceType = TYPE_MAP.get(ResourceType.CAPITALIZATION_TITLES);
            var data = new ResponseResourceListData();
            String resourceId = plan.getCapitalizationTitlePlanId().toString();
            if (permitted) {
                var resourceStatus = getStatus(plan, consent);
                LOG.info("Adding capitalization title plan {}, status {}, resourceId {}, type {}", plan.getCapitalizationTitlePlanId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, resourceId);
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addCapitalizationTitlePlansToResourcesV3(Set<CapitalizationTitlePlanEntity> plans, ConsentEntity consent, Map<ResponseResourceListV3Data, String> responseMap, boolean permitted) {
        for (var plan : plans) {
            var resourceType = TYPE_MAP_V3.get(ResourceType.CAPITALIZATION_TITLES);
            var data = new ResponseResourceListV3Data();
            String resourceId = plan.getCapitalizationTitlePlanId().toString();
            if (permitted) {
                var resourceStatus = getStatusV3(plan, consent);
                LOG.info("Adding capitalization title plan {}, status {}, resourceId {}, type {}", plan.getCapitalizationTitlePlanId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, resourceId);
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addFinancialRiskPoliciesToResources(Set<FinancialRiskPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListData, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS);
            var data = new ResponseResourceListData();
            String resourceId = policy.getFinancialRiskPolicyId().toString();
            if (permitted) {
                var resourceStatus = getStatus(policy, consent);
                LOG.info("Adding financial risk policy {}, status {}, resourceId {}, type {}", policy.getFinancialRiskPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, policy.getFinancialRiskPolicyId().toString());
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addFinancialRiskPoliciesToResourcesV3(Set<FinancialRiskPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListV3Data, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS);
            var data = new ResponseResourceListV3Data();
            String resourceId = policy.getFinancialRiskPolicyId().toString();
            if (permitted) {
                var resourceStatus = getStatusV3(policy, consent);
                LOG.info("Adding financial risk policy {}, status {}, resourceId {}, type {}", policy.getFinancialRiskPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, policy.getFinancialRiskPolicyId().toString());
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addHousingPoliciesToResources(Set<HousingPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListData, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_HOUSING);
            var data = new ResponseResourceListData();
            String resourceId = policy.getHousingPolicyId().toString();
            if (permitted) {
                var resourceStatus = getStatus(policy, consent);
                LOG.info("Adding housing policy {}, status {}, resourceId {}, type {}", policy.getHousingPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, policy.getHousingPolicyId().toString());
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addHousingPoliciesToResourcesV3(Set<HousingPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListV3Data, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_HOUSING);
            var data = new ResponseResourceListV3Data();
            String resourceId = policy.getHousingPolicyId().toString();
            if (permitted) {
                var resourceStatus = getStatusV3(policy, consent);
                LOG.info("Adding housing policy {}, status {}, resourceId {}, type {}", policy.getHousingPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, policy.getHousingPolicyId().toString());
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addResponsibilityPoliciesToResources(Set<ResponsibilityPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListData, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_RESPONSIBILITY);
            var data = new ResponseResourceListData();
            String resourceId = policy.getResponsibilityPolicyId().toString();
            if (permitted) {
                var resourceStatus = getStatus(policy, consent);
                LOG.info("Adding responsibility policy {}, status {}, resourceId {}, type {}", policy.getResponsibilityPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, policy.getResponsibilityPolicyId().toString());
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addResponsibilityPoliciesToResourcesV3(Set<ResponsibilityPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListV3Data, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_RESPONSIBILITY);
            var data = new ResponseResourceListV3Data();
            String resourceId = policy.getResponsibilityPolicyId().toString();
            if (permitted) {
                var resourceStatus = getStatusV3(policy, consent);
                LOG.info("Adding responsibility policy {}, status {}, resourceId {}, type {}", policy.getResponsibilityPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, policy.getResponsibilityPolicyId().toString());
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addPersonPoliciesToResources(Set<PersonPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListData, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_PERSON);
            var data = new ResponseResourceListData();
            String resourceId = policy.getPersonPolicyId().toString();
            if (permitted) {
                var resourceStatus = getStatus(policy, consent);
                LOG.info("Adding person policy {}, status {}, resourceId {}, type {}", policy.getPersonPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, policy.getPersonPolicyId().toString());
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addPersonPoliciesToResourcesV3(Set<PersonPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListV3Data, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_PERSON);
            var data = new ResponseResourceListV3Data();
            String resourceId = policy.getPersonPolicyId().toString();
            if (permitted) {
                var resourceStatus = getStatusV3(policy, consent);
                LOG.info("Adding person policy {}, status {}, resourceId {}, type {}", policy.getPersonPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, policy.getPersonPolicyId().toString());
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addLifePensionContractsToResources(Set<LifePensionContractEntity> contracts, ConsentEntity consent, Map<ResponseResourceListData, String> responseMap, boolean permitted) {
        for (var contract : contracts) {
            var resourceType = TYPE_MAP.get(ResourceType.LIFE_PENSION);
            var data = new ResponseResourceListData();
            String resourceId = contract.getLifePensionContractId().toString();
            if (permitted) {
                var resourceStatus = getStatus(contract, consent);
                LOG.info("Adding life pension contract {}, status {}, resourceId {}, type {}", contract.getLifePensionContractId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, resourceId);
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addLifePensionContractsToResourcesV3(Set<LifePensionContractEntity> contracts, ConsentEntity consent, Map<ResponseResourceListV3Data, String> responseMap, boolean permitted) {
        for (var contract : contracts) {
            var resourceType = TYPE_MAP_V3.get(ResourceType.LIFE_PENSION);
            var data = new ResponseResourceListV3Data();
            String resourceId = contract.getLifePensionContractId().toString();
            if (permitted) {
                var resourceStatus = getStatusV3(contract, consent);
                LOG.info("Adding life pension contract {}, status {}, resourceId {}, type {}", contract.getLifePensionContractId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, resourceId);
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addPensionPlanContractsToResources(Set<PensionPlanContractEntity> contracts, ConsentEntity consent, Map<ResponseResourceListData, String> responseMap, boolean permitted) {
        for (var contract : contracts) {
            var resourceType = TYPE_MAP.get(ResourceType.PENSION_PLAN);
            var data = new ResponseResourceListData();
            String resourceId = contract.getPensionPlanContractId();
            if (permitted) {
                var resourceStatus = getStatus(contract, consent);
                LOG.info("Adding pension plan contract {}, status {}, resourceId {}, type {}", contract.getPensionPlanContractId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, resourceId);
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addPensionPlanContractsToResourcesV3(Set<PensionPlanContractEntity> contracts, ConsentEntity consent, Map<ResponseResourceListV3Data, String> responseMap, boolean permitted) {
        for (var contract : contracts) {
            var resourceType = TYPE_MAP_V3.get(ResourceType.PENSION_PLAN);
            var data = new ResponseResourceListV3Data();
            String resourceId = contract.getPensionPlanContractId();
            if (permitted) {
                var resourceStatus = getStatusV3(contract, consent);
                LOG.info("Adding pension plan contract {}, status {}, resourceId {}, type {}", contract.getPensionPlanContractId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, resourceId);
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addFinancialAssistanceContractsToResources(Set<FinancialAssistanceContractEntity> contracts, ConsentEntity consent, Map<ResponseResourceListData, String> responseMap, boolean permitted) {
        for (var contract : contracts) {
            var resourceType = TYPE_MAP.get(ResourceType.FINANCIAL_ASSISTANCE);
            var data = new ResponseResourceListData();
            String resourceId = contract.getFinancialAssistanceContractId();
            if (permitted) {
                var resourceStatus = getStatus(contract, consent);
                LOG.info("Adding financial assistance contract {}, status {}, resourceId {}, type {}", contract.getFinancialAssistanceContractId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, contract.getFinancialAssistanceContractId());
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addFinancialAssistanceContractsToResourcesV3(Set<FinancialAssistanceContractEntity> contracts, ConsentEntity consent, Map<ResponseResourceListV3Data, String> responseMap, boolean permitted) {
        for (var contract : contracts) {
            var resourceType = TYPE_MAP_V3.get(ResourceType.FINANCIAL_ASSISTANCE);
            var data = new ResponseResourceListV3Data();
            String resourceId = contract.getFinancialAssistanceContractId();
            if (permitted) {
                var resourceStatus = getStatusV3(contract, consent);
                LOG.info("Adding financial assistance contract {}, status {}, resourceId {}, type {}", contract.getFinancialAssistanceContractId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, contract.getFinancialAssistanceContractId());
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addAcceptanceAndBranchesAbroadPoliciesToResources(Set<AcceptanceAndBranchesAbroadPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListData, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD);
            var data = new ResponseResourceListData();
            String resourceId = policy.getPolicyId().toString();
            if (permitted) {
                var resourceStatus = getStatus(policy, consent);
                LOG.info("Adding acceptance and branches abroad policy {}, status {}, resourceId {}, type {}", policy.getPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, resourceId);
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addAcceptanceAndBranchesAbroadPoliciesToResourcesV3(Set<AcceptanceAndBranchesAbroadPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListV3Data, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD);
            var data = new ResponseResourceListV3Data();
            String resourceId = policy.getPolicyId().toString();
            if (permitted) {
                var resourceStatus = getStatusV3(policy, consent);
                LOG.info("Adding acceptance and branches abroad policy {}, status {}, resourceId {}, type {}", policy.getPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, resourceId);
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addPatrimonialPoliciesToResources(Set<PatrimonialPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListData, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_PATRIMONIAL);
            var data = new ResponseResourceListData();
            String resourceId = policy.getPolicyId().toString();
            if (permitted) {
                var resourceStatus = getStatus(policy, consent);
                LOG.info("Adding patrimonial policy {}, status {}, resourceId {}, type {}", policy.getPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, resourceId);
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addPatrimonialPoliciesToResourcesV3(Set<PatrimonialPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListV3Data, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_PATRIMONIAL);
            var data = new ResponseResourceListV3Data();
            String resourceId = policy.getPolicyId().toString();
            if (permitted) {
                var resourceStatus = getStatusV3(policy, consent);
                LOG.info("Adding patrimonial policy {}, status {}, resourceId {}, type {}", policy.getPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, resourceId);
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addRuralPoliciesToResources(Set<RuralPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListData, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_RURAL);
            var data = new ResponseResourceListData();
            String resourceId = policy.getRuralPolicyId().toString();
            if (permitted) {
                var resourceStatus = getStatus(policy, consent);
                LOG.info("Adding rural policy {}, status {}, resourceId {}, type {}", policy.getRuralPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, resourceId);
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addRuralPoliciesToResourcesV3(Set<RuralPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListV3Data, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_RURAL);
            var data = new ResponseResourceListV3Data();
            String resourceId = policy.getRuralPolicyId().toString();
            if (permitted) {
                var resourceStatus = getStatusV3(policy, consent);
                LOG.info("Adding rural policy {}, status {}, resourceId {}, type {}", policy.getRuralPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, resourceId);
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addAutoPoliciesToResources(Set<AutoPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListData, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_AUTO);
            var data = new ResponseResourceListData();
            String resourceId = policy.getAutoPolicyId();
            if (permitted) {
                var resourceStatus = getStatus(policy, consent);
                LOG.info("Adding auto policy {}, status {}, resourceId {}, type {}", policy.getAutoPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, policy.getAutoPolicyId());
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addAutoPoliciesToResourcesV3(Set<AutoPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListV3Data, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_AUTO);
            var data = new ResponseResourceListV3Data();
            String resourceId = policy.getAutoPolicyId();
            if (permitted) {
                var resourceStatus = getStatusV3(policy, consent);
                LOG.info("Adding auto policy {}, status {}, resourceId {}, type {}", policy.getAutoPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, policy.getAutoPolicyId());
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addTransportPoliciesToResources(Set<TransportPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListData, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_TRANSPORT);
            var data = new ResponseResourceListData();
            String resourceId = policy.getTransportPolicyId();
            if (permitted) {
                var resourceStatus = getStatus(policy, consent);
                LOG.info("Adding transport policy {}, status {}, resourceId {}, type {}", policy.getTransportPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, policy.getTransportPolicyId());
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private void addTransportPoliciesToResourcesV3(Set<TransportPolicyEntity> policies, ConsentEntity consent, Map<ResponseResourceListV3Data, String> responseMap, boolean permitted) {
        for (var policy : policies) {
            var resourceType = TYPE_MAP_V3.get(ResourceType.DAMAGES_AND_PEOPLE_TRANSPORT);
            var data = new ResponseResourceListV3Data();
            String resourceId = policy.getTransportPolicyId();
            if (permitted) {
                var resourceStatus = getStatusV3(policy, consent);
                LOG.info("Adding transport policy {}, status {}, resourceId {}, type {}", policy.getTransportPolicyId(), resourceStatus, resourceId, resourceType);
                data.status(resourceStatus).resourceId(resourceId).type(resourceType);
                responseMap.put(data, policy.getTransportPolicyId());
            } else {
                LOG.error("Account read permissions not found in consent id {}", consent.getConsentId());
            }
        }
    }

    private static <T> Page<T> getPage(Map<T, String> responseMap, Pageable pageable) {
        //Sort by create date and convert to list
        List<T> list = responseMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (Pageable.unpaged().equals(pageable)) return Page.of(list, pageable, (long) list.size());

        int page = pageable.getNumber();
        int pageSize = pageable.getSize();

        if (pageSize <= 0 || page < 0) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad Request page size: " + pageSize +
                    " and page: " + page);
        }

        int fromIndex = page * pageSize;
        if (list.isEmpty() || list.size() < fromIndex) {
            return Page.of(Collections.emptyList(), pageable, 0L);
        }

        var slice = list.subList(fromIndex, Math.min(fromIndex + pageSize, list.size()));
        return Page.of(slice, pageable, (long)list.size());
    }

    private boolean checkIfOnlyCustomersGroup(Set<EnumConsentPermission> permissions, Set<EnumConsentV3Permission> permissionsV3) {
        if (permissions.contains(EnumConsentPermission.CAPITALIZATION_TITLE_READ)
                || permissions.contains(EnumConsentPermission.PENSION_PLAN_READ)
                || permissions.contains(EnumConsentPermission.LIFE_PENSION_READ)
                || permissions.contains(EnumConsentPermission.FINANCIAL_ASSISTANCE_READ)
                || permissions.contains(EnumConsentPermission.DAMAGES_AND_PEOPLE_PATRIMONIAL_READ)
                || permissions.contains(EnumConsentPermission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_READ)
                || permissions.contains(EnumConsentPermission.DAMAGES_AND_PEOPLE_TRANSPORT_READ)
                || permissions.contains(EnumConsentPermission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_READ)
                || permissions.contains(EnumConsentPermission.DAMAGES_AND_PEOPLE_RURAL_READ)
                || permissions.contains(EnumConsentPermission.DAMAGES_AND_PEOPLE_AUTO_READ)
                || permissions.contains(EnumConsentPermission.DAMAGES_AND_PEOPLE_HOUSING_READ)
                || permissions.contains(EnumConsentPermission.DAMAGES_AND_PEOPLE_PERSON_READ)
                || permissions.contains(EnumConsentPermission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_READ)
                || permissionsV3.contains(EnumConsentV3Permission.CAPITALIZATION_TITLE_READ)
                || permissionsV3.contains(EnumConsentV3Permission.PENSION_PLAN_READ)
                || permissionsV3.contains(EnumConsentV3Permission.LIFE_PENSION_READ)
                || permissionsV3.contains(EnumConsentV3Permission.FINANCIAL_ASSISTANCE_READ)
                || permissionsV3.contains(EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PATRIMONIAL_READ)
                || permissionsV3.contains(EnumConsentV3Permission.DAMAGES_AND_PEOPLE_RESPONSIBILITY_READ)
                || permissionsV3.contains(EnumConsentV3Permission.DAMAGES_AND_PEOPLE_TRANSPORT_READ)
                || permissionsV3.contains(EnumConsentV3Permission.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS_READ)
                || permissionsV3.contains(EnumConsentV3Permission.DAMAGES_AND_PEOPLE_RURAL_READ)
                || permissionsV3.contains(EnumConsentV3Permission.DAMAGES_AND_PEOPLE_AUTO_READ)
                || permissionsV3.contains(EnumConsentV3Permission.DAMAGES_AND_PEOPLE_HOUSING_READ)
                || permissionsV3.contains(EnumConsentV3Permission.DAMAGES_AND_PEOPLE_PERSON_READ)
                || permissionsV3.contains(EnumConsentV3Permission.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD_READ)
        ) {
            return false;
        }

        var containsPersonalIdentifications = permissions.contains(EnumConsentPermission.CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ) || permissionsV3.contains(EnumConsentV3Permission.CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ);
        var containsPersonalAdditionalInfo = permissions.contains(EnumConsentPermission.CUSTOMERS_PERSONAL_ADDITIONALINFO_READ) || permissionsV3.contains(EnumConsentV3Permission.CUSTOMERS_PERSONAL_ADDITIONALINFO_READ);
        var containsPersonalQualifications = permissions.contains(EnumConsentPermission.CUSTOMERS_PERSONAL_QUALIFICATION_READ) || permissionsV3.contains(EnumConsentV3Permission.CUSTOMERS_PERSONAL_QUALIFICATION_READ);
        var containsBusinessIdentifications = permissions.contains(EnumConsentPermission.CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ) || permissionsV3.contains(EnumConsentV3Permission.CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ);
        var containsBusinessAdditionalInfo = permissions.contains(EnumConsentPermission.CUSTOMERS_BUSINESS_ADDITIONALINFO_READ) || permissionsV3.contains(EnumConsentV3Permission.CUSTOMERS_BUSINESS_ADDITIONALINFO_READ);
        var containsBusinessQualifications = permissions.contains(EnumConsentPermission.CUSTOMERS_BUSINESS_QUALIFICATION_READ) || permissionsV3.contains(EnumConsentV3Permission.CUSTOMERS_BUSINESS_QUALIFICATION_READ);

        if ((containsPersonalIdentifications && containsPersonalAdditionalInfo && containsPersonalQualifications)
                || (containsBusinessIdentifications && containsBusinessAdditionalInfo && containsBusinessQualifications)) {
            return true;
        }

        throw new HttpStatusException(HttpStatus.NOT_FOUND, "Resource not found, no appropriate permissions attached to consent");
    }
}
