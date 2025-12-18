package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.domain.FinancialAssistanceContractEntity;
import com.raidiam.trustframework.mockinsurance.domain.FinancialAssistanceContractMovementEntity;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Singleton
@Transactional
public class FinancialAssistanceService extends BaseInsuranceService {
    private static final Logger LOG = LoggerFactory.getLogger(FinancialAssistanceService.class);

    private List<FinancialAssistanceContractEntity> getFinancialAssistanceContractEntities(Pageable pageable, String consentId) {
        LOG.info("Getting financial assistance contracts response for consent id {}", consentId);
    
        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
    
        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.FINANCIAL_ASSISTANCE_READ, EnumConsentV3Permission.FINANCIAL_ASSISTANCE_READ);
    
        return financialAssistanceContractRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId(), pageable).getContent();
    }

    public ResponseInsuranceFinancialAssistance getContracts(Pageable pageable, String consentId) {
        var contracts = getFinancialAssistanceContractEntities(pageable, consentId);
        return new ResponseInsuranceFinancialAssistance()
                .data(List.of(new ResponseInsuranceFinancialAssistanceData()
                        .brand(new ResponseInsuranceFinancialAssistanceBrand()
                                .name("Mock")
                                .companies(List.of(new ResponseInsuranceFinancialAssistanceBrandCompanies()
                                        .companyName("Mock Insurer")
                                        .cnpjNumber("12345678901234")
                                        .contracts(contracts.stream().map(FinancialAssistanceContractEntity::mapContractDto).toList()))))));
    }

    public ResponseInsuranceFinancialAssistanceV2 getContractsV2(Pageable pageable, String consentId) {
        var contracts = getFinancialAssistanceContractEntities(pageable, consentId);
        return new ResponseInsuranceFinancialAssistanceV2()
                .data(List.of(new ResponseInsuranceFinancialAssistanceV2Data()
                        .brand(new ResponseInsuranceFinancialAssistanceV2Brand()
                                .name("Mock")
                                .companies(List.of(new ResponseInsuranceFinancialAssistanceV2BrandCompanies()
                                        .companyName("Mock Insurer")
                                        .cnpjNumber("12345678901234")
                                        .contracts(contracts.stream().map(FinancialAssistanceContractEntity::mapContractDto).toList()))))));
    }

    public ResponseInsuranceFinancialAssistanceContractInfo getContractInfo(String contractId, String consentId) {
        return this.getContract(contractId, consentId, EnumConsentPermission.FINANCIAL_ASSISTANCE_CONTRACTINFO_READ, EnumConsentV3Permission.FINANCIAL_ASSISTANCE_CONTRACTINFO_READ).mapContractInfoDto();
    }

    public ResponseInsuranceFinancialAssistanceContractInfoV2 getContractInfoV2(String contractId, String consentId) {
        return this.getContract(contractId, consentId, EnumConsentPermission.FINANCIAL_ASSISTANCE_CONTRACTINFO_READ, EnumConsentV3Permission.FINANCIAL_ASSISTANCE_CONTRACTINFO_READ).mapContractInfoDtoV2();
    }

    public ResponseInsuranceFinancialAssistanceMovements getContractMovements(String contractId, String consentId, Pageable pageable) {
        LOG.info("Getting financial assistance contract movement response for consent id {}", consentId);
        getContract(contractId, consentId, EnumConsentPermission.FINANCIAL_ASSISTANCE_MOVEMENTS_READ, EnumConsentV3Permission.FINANCIAL_ASSISTANCE_MOVEMENTS_READ);

        var movements = financialAssistanceContractMovementRepository.findByFinancialAssistanceContractId(contractId, pageable);
        var resp = new ResponseInsuranceFinancialAssistanceMovements()
                .data(movements.getContent().stream().map(FinancialAssistanceContractMovementEntity::mapDto).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(movements, false));
        return resp;
    }

    private FinancialAssistanceContractEntity getContract(String contractId, String consentId, EnumConsentPermission permission, EnumConsentV3Permission permissionV3) {
        LOG.info("Getting financial assistance contract for contract id {} and consent id {}", contractId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var contract = financialAssistanceContractRepository.findByFinancialAssistanceContractId(contractId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Contract id " + contractId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission, permissionV3);
        this.checkConsentCoversContract(consentEntity, contract);
        this.checkConsentOwnerIsContractOwner(consentEntity, contract);

        return contract;
    }

    public void checkConsentCoversContract(ConsentEntity consentEntity, FinancialAssistanceContractEntity contract) {
        var contractFromConsent = consentEntity.getFinancialAssistanceContracts()
                .stream()
                .filter(c -> contract.getFinancialAssistanceContractId().equals(c.getFinancialAssistanceContractId()))
                .findFirst();
        if (contractFromConsent.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad request, consent does not cover this financial assistance contract!");
        }
    }

    public void checkConsentOwnerIsContractOwner(ConsentEntity consentEntity, FinancialAssistanceContractEntity contract) {
        if (!consentEntity.getAccountHolderId().equals(contract.getAccountHolderId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match account owner!");
        }
    }


}
