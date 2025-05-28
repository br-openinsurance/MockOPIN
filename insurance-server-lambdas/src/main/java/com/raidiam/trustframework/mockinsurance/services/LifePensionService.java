package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.*;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.data.model.Pageable;

import java.util.List;
import java.util.UUID;

@Singleton
@Transactional
public class LifePensionService extends BaseInsuranceService {

    private static final Logger LOG = LoggerFactory.getLogger(LifePensionService.class);

    public ResponseInsuranceLifePension getContracts(Pageable pageable, String consentId) {
        LOG.info("Getting Personal Customers Identification response for consent id {}", consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.LIFE_PENSION_READ);

        var contracts = lifePensionContractRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId(), pageable).getContent();
        return new ResponseInsuranceLifePension()
                .data(List.of(new ResponseInsuranceLifePensionData()
                        .brand(new ResponseInsuranceLifePensionBrand()
                                .name("Mock")
                                .companies(List.of(new ResponseInsuranceLifePensionBrandCompanies()
                                        .companyName("Mock Insurer")
                                        .cnpjNumber("12345678901234")
                                        .contracts(contracts.stream().map(LifePensionContractEntity::mapContractDTO).toList()))))));
    }

    private LifePensionContractEntity getContract(UUID certificateId, String consentId, EnumConsentPermission permission) {
        LOG.info("Getting life pension contract for contract id {} and consent id {}", certificateId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var contract = lifePensionContractRepository.findByLifePensionContractId(certificateId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Certificate id " + certificateId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission);
        this.checkConsentCoversContract(consentEntity, contract);
        this.checkConsentOwnerIsContractOwner(consentEntity, contract);

        return contract;
    }

    public ResponseInsuranceLifePensionContractInfo getContractInfo(UUID certificateId, String consentId) {
        LOG.info("Getting life pension contract info response for consent id {}", consentId);
        return getContract(certificateId, consentId, EnumConsentPermission.LIFE_PENSION_CONTRACTINFO_READ).mapContractInfoDTO();
    }

    public ResponseInsuranceLifePensionWithdrawal getContractWithdrawals(UUID certificateId, String consentId, Pageable pageable) {
        LOG.info("Getting life pension contract withdrawals response for consent id {}", consentId);
        getContract(certificateId, consentId, EnumConsentPermission.LIFE_PENSION_WITHDRAWALS_READ);

        var withdrawals = lifePensionContractWithdrawalRepository.findByLifePensionContractId(certificateId, pageable);
        var resp = new ResponseInsuranceLifePensionWithdrawal()
                .data(withdrawals.getContent().stream().map(LifePensionContractWithdrawalEntity::mapDTO).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(withdrawals, false));
        return resp;
    }

    public ResponseInsuranceLifePensionClaim getContractClaims(UUID certificateId, String consentId, Pageable pageable) {
        LOG.info("Getting life pension contract claims response for consent id {}", consentId);
        getContract(certificateId, consentId, EnumConsentPermission.LIFE_PENSION_CLAIM);

        var withdrawals = lifePensionContractClaimRepository.findByLifePensionContractId(certificateId, pageable);
        var resp = new ResponseInsuranceLifePensionClaim()
                .data(withdrawals.getContent().stream().map(LifePensionContractClaimEntity::mapDTO).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(withdrawals, false));
        return resp;
    }

    public ResponseInsuranceLifePensionPortabilities getContractPortabilities(UUID certificateId, String consentId,
                                                                              Pageable pageable) {
        LOG.info("Getting life pension contract portability response for consent id {}", consentId);
        getContract(certificateId, consentId, EnumConsentPermission.LIFE_PENSION_PORTABILITIES_READ);

        var portability = lifePensionContractPortabilityRepository.findByLifePensionContractId(certificateId, pageable);
        var resp = new ResponseInsuranceLifePensionPortabilities()
                .data(new InsuranceLifePensionPortability()
                        .portabilityInfo(portability.getContent().stream().map(LifePensionContractPortabilityInfoEntity::mapDTO).toList())
                        .hasOccurredPortability(!portability.isEmpty())
                );
        resp.setMeta(InsuranceLambdaUtils.getMeta(portability, false));
        return resp;
    }

    public ResponseInsuranceLifePensionMovements getContractMovements(UUID certificateId, String consentId, Pageable pageable) {
        LOG.info("Getting life pension contract movement response for consent id {}", consentId);
        getContract(certificateId, consentId, EnumConsentPermission.LIFE_PENSION_MOVEMENTS_READ);

        var benefits = lifePensionContractMovementBenefitRepository.findByLifePensionContractId(certificateId, pageable);
        var contributions = lifePensionContractMovementContributionRepository.findByLifePensionContractId(certificateId, pageable);
        var resp = new ResponseInsuranceLifePensionMovements()
                .data(new InsuranceLifePensionMovements()
                        .movementContributions(contributions.getContent().stream().map(LifePensionContractMovementContributionEntity::mapDTO).toList())
                        .movementBenefits(benefits.getContent().stream().map(LifePensionContractMovementBenefitEntity::mapDTO).toList())
                );
        resp.setMeta(InsuranceLambdaUtils.getMeta(benefits, false));
        return resp;
    }

    public void checkConsentCoversContract(ConsentEntity consentEntity, LifePensionContractEntity contract) {
        var contractFromConsent = consentEntity.getLifePensionContracts()
                .stream()
                .filter(p -> contract.getLifePensionId().equals(p.getLifePensionId()))
                .findFirst();
        if (contractFromConsent.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad request, consent does not cover this life pension!");
        }
    }

    public void checkConsentOwnerIsContractOwner(ConsentEntity consentEntity, LifePensionContractEntity contract) {
        if (!consentEntity.getAccountHolderId().equals(contract.getAccountHolderId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match account owner!");
        }
    }
}
