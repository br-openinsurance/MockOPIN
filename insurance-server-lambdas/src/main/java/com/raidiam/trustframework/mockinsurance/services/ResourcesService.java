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

import static com.raidiam.trustframework.mockinsurance.models.generated.ResponseResourceListData.StatusEnum.*;

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

    public ResponseResourceList getResourceList(Pageable pageable, @NotNull String consentId) {
        LOG.info("Getting resources response for consent id {}", consentId);
        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        LOG.info("Checking permissions for consent id {}", consentId);

        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.RESOURCES_READ);

        var permissions = InsuranceLambdaUtils.getConsentPermissions(consentEntity);
        if (checkIfOnlyCustomersGroup(permissions)) {
            return new ResponseResourceList()
                    .data(new ArrayList<>())
                    .meta(InsuranceLambdaUtils.getMeta(null, false));
        }

        LOG.info("Adding resources to response");
        Map<ResponseResourceListData, String> responseMap = new HashMap<>();

        var capitalizationTitlePlans = consentEntity.getCapitalizationTitlePlans();
        LOG.info("Found {} capitalization title plans to include in resource response", capitalizationTitlePlans.size());
        addCapitalizationTitlePlansToResources(capitalizationTitlePlans, consentEntity, responseMap, permissions.contains(PERMISSION_MAP.get(ResourceType.CAPITALIZATION_TITLES)));
        
        var acceptanceAndBranchesAbroadPolicies = consentEntity.getAcceptanceAndBranchesAbroadPolicies();
        LOG.info("Found {} acceptance and branches abroad policies to include in resource response", acceptanceAndBranchesAbroadPolicies.size());
        addAcceptanceAndBranchesAbroadPoliciesToResources(acceptanceAndBranchesAbroadPolicies, consentEntity, responseMap, permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_ACCEPTANCE_AND_BRANCHES_ABROAD)));
        
        var patrimonialPolicies = consentEntity.getPatrimonialPolicies();
        LOG.info("Found {} patrimonial policies to include in resource response", patrimonialPolicies.size());
        addPatrimonialPoliciesToResources(patrimonialPolicies, consentEntity, responseMap, permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_PATRIMONIAL)));
        
        var ruralPolicies = consentEntity.getRuralPolicies();
        LOG.info("Found {} rural policies to include in resource response", ruralPolicies.size());
        addRuralPoliciesToResources(ruralPolicies, consentEntity, responseMap, permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_RURAL)));

        var financialRiskPolicies = consentEntity.getFinancialRiskPolicies();
        LOG.info("Found {} financial risk policies to include in resource response", financialRiskPolicies.size());
        addFinancialRiskPoliciesToResources(financialRiskPolicies, consentEntity, responseMap, permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_FINANCIAL_RISKS)));

        var housingPolicies = consentEntity.getHousingPolicies();
        LOG.info("Found {} housing policies to include in resource response", housingPolicies.size());
        addHousingPoliciesToResources(housingPolicies, consentEntity, responseMap, permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_HOUSING)));

        var responsibilityPolicies = consentEntity.getResponsibilityPolicies();
        LOG.info("Found {} responsibility policies to include in resource response", responsibilityPolicies.size());
        addResponsibilityPoliciesToResources(responsibilityPolicies, consentEntity, responseMap, permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_RESPONSIBILITY)));

        var personPolicies = consentEntity.getPersonPolicies();
        LOG.info("Found {} person policies to include in resource response", personPolicies.size());
        addPersonPoliciesToResources(personPolicies, consentEntity, responseMap, permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_PERSON)));

        var lifePensionContracts = consentEntity.getLifePensionContracts();
        LOG.info("Found {} life pension contracts to include in resource response", lifePensionContracts.size());
        addLifePensionContractsToResources(lifePensionContracts, consentEntity, responseMap, permissions.contains(PERMISSION_MAP.get(ResourceType.LIFE_PENSION)));

        var pensionPlanContracts = consentEntity.getPensionPlanContracts();
        LOG.info("Found {} pension plan contracts to include in resource response", pensionPlanContracts.size());
        addPensionPlanContractsToResources(pensionPlanContracts, consentEntity, responseMap, permissions.contains(PERMISSION_MAP.get(ResourceType.PENSION_PLAN)));

        var financialAssistanceContracts = consentEntity.getFinancialAssistanceContracts();
        LOG.info("Found {} financial assistance contracts to include in resource response", financialAssistanceContracts.size());
        addFinancialAssistanceContractsToResources(financialAssistanceContracts, consentEntity, responseMap, permissions.contains(PERMISSION_MAP.get(ResourceType.FINANCIAL_ASSISTANCE)));

        var autoPolicies = consentEntity.getAutoPolicies();
        LOG.info("Found {} auto policies to include in resource response", autoPolicies.size());
        addAutoPoliciesToResources(autoPolicies, consentEntity, responseMap, permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_AUTO)));

        var transportPolicies = consentEntity.getTransportPolicies();
        LOG.info("Found {} transport policies to include in resource response", transportPolicies.size());
        addTransportPoliciesToResources(transportPolicies, consentEntity, responseMap, permissions.contains(PERMISSION_MAP.get(ResourceType.DAMAGES_AND_PEOPLE_TRANSPORT)));

        var resourcePage = getPage(responseMap, pageable);
        LOG.info("Building response with resources");
        var response = new ResponseResourceList().data(resourcePage.getContent());
        response.setMeta(InsuranceLambdaUtils.getMeta(resourcePage, false));
        return response;
    }

    public void checkStatusAvailable(HasStatusInterface accountOrContract, ConsentEntity consent) {
        if(getStatus(accountOrContract, consent).equals(ResponseResourceListData.StatusEnum.PENDING_AUTHORISATION)) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, new ResponseErrorErrors()
                    .code("status_RESOURCE_PENDING_AUTHORISATION")
                    .title("Aguardando autorização de multiplas alçadas")
                    .detail("status_RESOURCE_PENDING_AUTHORISATION"));
        }
        if(getStatus(accountOrContract, consent).equals(ResponseResourceListData.StatusEnum.TEMPORARILY_UNAVAILABLE)) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, new ResponseErrorErrors()
                    .code("status_RESOURCE_TEMPORARILY_UNAVAILABLE")
                    .title("Recurso temporariamente indisponível")
                    .detail("status_RESOURCE_TEMPORARILY_UNAVAILABLE"));
        }
    }

    private ResponseResourceListData.StatusEnum getStatus(HasStatusInterface accountOrContract, ConsentEntity consent) {
        if (AVAILABLE.toString().equals(accountOrContract.getStatus())) {
            return switch (EnumConsentStatus.fromValue(consent.getStatus())) {
                case AUTHORISED -> AVAILABLE;
                case AWAITING_AUTHORISATION -> PENDING_AUTHORISATION;
                default -> ResponseResourceListData.StatusEnum.UNAVAILABLE;
            };
        } else if (PENDING_AUTHORISATION.toString().equals(accountOrContract.getStatus())) {
            return PENDING_AUTHORISATION;
        } else if (TEMPORARILY_UNAVAILABLE.toString().equals(accountOrContract.getStatus())) {
            return TEMPORARILY_UNAVAILABLE;
        } else {
            return ResponseResourceListData.StatusEnum.UNAVAILABLE;
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

    private static Page<ResponseResourceListData> getPage(Map<ResponseResourceListData, String> responseMap, Pageable pageable) {
        //Sort by create date and convert to list
        List<ResponseResourceListData> list = responseMap.entrySet().stream()
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

    private boolean checkIfOnlyCustomersGroup(Set<EnumConsentPermission> permissions) {
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
        ) {
            return false;
        }

        if ((permissions.contains(EnumConsentPermission.CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ)
                && permissions.contains(EnumConsentPermission.CUSTOMERS_PERSONAL_ADDITIONALINFO_READ)
                && permissions.contains(EnumConsentPermission.CUSTOMERS_PERSONAL_QUALIFICATION_READ))
                || (permissions.contains(EnumConsentPermission.CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ)
                && permissions.contains(EnumConsentPermission.CUSTOMERS_BUSINESS_ADDITIONALINFO_READ))
                && permissions.contains(EnumConsentPermission.CUSTOMERS_BUSINESS_QUALIFICATION_READ)) {
            return true;
        }

        throw new HttpStatusException(HttpStatus.NOT_FOUND, "Resource not found, no appropriate permissions attached to consent");
    }
}
