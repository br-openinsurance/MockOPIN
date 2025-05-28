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

    public ResponseInsuranceFinancialAssistance getContracts(Pageable pageable, String consentId) {
        LOG.info("Getting financial assistance contracts response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.FINANCIAL_ASSISTANCE_READ);

        var contracts = financialAssistanceContractRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId(), pageable).getContent();
        return new ResponseInsuranceFinancialAssistance()
                .data(List.of(new ResponseInsuranceFinancialAssistanceData()
                        .brand(new ResponseInsuranceFinancialAssistanceBrand()
                                .name("Mock")
                                .companies(List.of(new ResponseInsuranceFinancialAssistanceBrandCompanies()
                                        .companyName("Mock Insurer")
                                        .cnpjNumber("12345678901234")
                                        .contracts(contracts.stream().map(FinancialAssistanceContractEntity::mapContractDto).toList()))))));
    }

    public ResponseInsuranceFinancialAssistanceContractInfo getContractInfo(String contractId, String consentId) {
        return this.getContract(contractId, consentId, EnumConsentPermission.FINANCIAL_ASSISTANCE_CONTRACTINFO_READ).mapContractInfoDto();
    }

    public ResponseInsuranceFinancialAssistanceMovements getContractMovements(String contractId, String consentId, Pageable pageable) {
        LOG.info("Getting financial assistance contract movement response for consent id {}", consentId);
        getContract(contractId, consentId, EnumConsentPermission.FINANCIAL_ASSISTANCE_MOVEMENTS_READ);

        var movements = financialAssistanceContractMovementRepository.findByFinancialAssistanceContractId(contractId, pageable);
        var resp = new ResponseInsuranceFinancialAssistanceMovements()
                .data(movements.getContent().stream().map(FinancialAssistanceContractMovementEntity::mapDto).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(movements, false));
        return resp;
    }

    private FinancialAssistanceContractEntity getContract(String contractId, String consentId, EnumConsentPermission permission) {
        LOG.info("Getting financial assistance contract for contract id {} and consent id {}", contractId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var contract = financialAssistanceContractRepository.findByFinancialAssistanceContractId(contractId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Contract id " + contractId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission);
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
