package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.*;
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
public class PensionPlanService extends BaseInsuranceService {

    private static final Logger LOG = LoggerFactory.getLogger(PensionPlanService.class);

    private List<PensionPlanContractEntity> getPensionPlanContractEntities(Pageable pageable, String consentId) {
        LOG.info("Getting Pension Plan Contracts response for consent id {}", consentId);
    
        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
    
        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, EnumConsentPermission.PENSION_PLAN_READ, EnumConsentV3Permission.PENSION_PLAN_READ);
    
        return pensionPlanContractRepository.findByAccountHolderAccountHolderId(consentEntity.getAccountHolderId(), pageable).getContent();
    }

    public ResponseInsurancePensionPlan getContracts(Pageable pageable, String consentId) {
        var contracts = getPensionPlanContractEntities(pageable, consentId);
        return new ResponseInsurancePensionPlan()
                .data(List.of(new ResponseInsurancePensionPlanData()
                        .brand(new ResponseInsurancePensionPlanBrand()
                                .name("Mock")
                                .companies(List.of(new ResponseInsurancePensionPlanBrandCompanies()
                                        .companyName("Mock Insurer")
                                        .cnpjNumber("12345678901234")
                                        .contracts(contracts.stream().map(PensionPlanContractEntity::mapContractDTO).toList()))))));
    }

    public ResponseInsurancePensionPlanV2 getContractsV2(Pageable pageable, String consentId) {
        var contracts = getPensionPlanContractEntities(pageable, consentId);
        return new ResponseInsurancePensionPlanV2()
                .data(List.of(new ResponseInsurancePensionPlanV2Data()
                        .brand(new ResponseInsurancePensionPlanV2Brand()
                                .name("Mock")
                                .companies(List.of(new ResponseInsurancePensionPlanV2BrandCompanies()
                                        .companyName("Mock Insurer")
                                        .cnpjNumber("12345678901234")
                                        .contracts(contracts.stream().map(PensionPlanContractEntity::mapContractDTO).toList()))))));
    }

    private PensionPlanContractEntity getContract(String contractId, String consentId, EnumConsentPermission permission, EnumConsentV3Permission permissionV3) {
        LOG.info("Getting pension plan contract for contract id {} and consent id {}", contractId, consentId);

        var consentEntity = InsuranceLambdaUtils.getConsent(consentId, consentRepository);
        var contract = pensionPlanContractRepository.findByPensionPlanContractId(contractId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Certificate id " + contractId + " not found"));

        InsuranceLambdaUtils.checkAuthorisationStatus(consentEntity);
        InsuranceLambdaUtils.checkConsentPermissions(consentEntity, permission, permissionV3);
        this.checkConsentCoversContract(consentEntity, contract);
        this.checkConsentOwnerIsContractOwner(consentEntity, contract);

        return contract;
    }

    public ResponseInsurancePensionPlanContractInfo getContractInfo(String contractId, String consentId) {
        LOG.info("Getting pension plan contract info response for consent id {}", consentId);
        return getContract(contractId, consentId, EnumConsentPermission.PENSION_PLAN_CONTRACTINFO_READ, EnumConsentV3Permission.PENSION_PLAN_CONTRACTINFO_READ).mapContractInfoDTO();
    }

    public ResponseInsurancePensionPlanContractInfoV2 getContractInfoV2(String contractId, String consentId) {
        LOG.info("Getting pension plan contract info response for consent id {}", consentId);
        return getContract(contractId, consentId, EnumConsentPermission.PENSION_PLAN_CONTRACTINFO_READ, EnumConsentV3Permission.PENSION_PLAN_CONTRACTINFO_READ).mapContractInfoDTOV2();
    }

    public ResponseInsurancePensionPlanWithdrawals getContractWithdrawals(String contractId, String consentId, Pageable pageable) {
        LOG.info("Getting pension plan contract withdrawals response for consent id {}", consentId);
        getContract(contractId, consentId, EnumConsentPermission.PENSION_PLAN_WITHDRAWALS_READ, EnumConsentV3Permission.PENSION_PLAN_WITHDRAWALS_READ);

        var withdrawals = pensionPlanContractWithdrawalRepository.findByPensionPlanContractId(contractId, pageable);
        var resp = new ResponseInsurancePensionPlanWithdrawals()
                .data(withdrawals.getContent().stream().map(PensionPlanContractWithdrawalEntity::mapDTO).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(withdrawals, false));
        return resp;
    }

    public ResponseInsurancePensionPlanWithdrawalsV2 getContractWithdrawalsV2(String contractId, String consentId, Pageable pageable) {
        LOG.info("Getting pension plan contract withdrawals response for consent id {}", consentId);
        getContract(contractId, consentId, EnumConsentPermission.PENSION_PLAN_WITHDRAWALS_READ, EnumConsentV3Permission.PENSION_PLAN_WITHDRAWALS_READ);

        var withdrawals = pensionPlanContractWithdrawalRepository.findByPensionPlanContractId(contractId, pageable);
        var resp = new ResponseInsurancePensionPlanWithdrawalsV2()
                .data(withdrawals.getContent().stream().map(PensionPlanContractWithdrawalEntity::mapDTOV2).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(withdrawals, false));
        return resp;
    }

    public ResponseInsurancePensionPlanClaim getContractClaims(String contractId, String consentId, Pageable pageable) {
        LOG.info("Getting pension plan contract claims response for consent id {}", consentId);
        getContract(contractId, consentId, EnumConsentPermission.PENSION_PLAN_CLAIM, EnumConsentV3Permission.PENSION_PLAN_CLAIM_READ);

        var withdrawals = pensionPlanContractClaimRepository.findByPensionPlanContractId(contractId, pageable);
        var resp = new ResponseInsurancePensionPlanClaim()
                .data(withdrawals.getContent().stream().map(PensionPlanContractClaimEntity::mapDTO).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(withdrawals, false));
        return resp;
    }

    public ResponseInsurancePensionPlanClaimV2 getContractClaimsV2(String contractId, String consentId, Pageable pageable) {
        LOG.info("Getting pension plan contract claims response for consent id {}", consentId);
        getContract(contractId, consentId, EnumConsentPermission.PENSION_PLAN_CLAIM, EnumConsentV3Permission.PENSION_PLAN_CLAIM_READ);

        var withdrawals = pensionPlanContractClaimRepository.findByPensionPlanContractId(contractId, pageable);
        var resp = new ResponseInsurancePensionPlanClaimV2()
                .data(withdrawals.getContent().stream().map(PensionPlanContractClaimEntity::mapDTOV2).toList());
        resp.setMeta(InsuranceLambdaUtils.getMeta(withdrawals, false));
        return resp;
    }

    public ResponseInsurancePensionPlanPortabilities getContractPortabilities(String contractId, String consentId,
                                                                              Pageable pageable) {
        LOG.info("Getting pension plan contract portability response for consent id {}", consentId);
        getContract(contractId, consentId, EnumConsentPermission.PENSION_PLAN_PORTABILITIES_READ, EnumConsentV3Permission.PENSION_PLAN_PORTABILITIES_READ);

        var portability = pensionPlanContractPortabilityRepository.findByPensionPlanContractId(contractId, pageable);
        var resp = new ResponseInsurancePensionPlanPortabilities()
                .data(new InsurancePensionPlanPortability()
                        .portabilityInfo(portability.getContent().stream().map(PensionPlanContractPortabilityInfoEntity::mapDTO).toList())
                        .hasOccurredPortability(!portability.isEmpty())
                );
        resp.setMeta(InsuranceLambdaUtils.getMeta(portability, false));
        return resp;
    }

    public ResponseInsurancePensionPlanMovements getContractMovements(String contractId, String consentId, Pageable pageable) {
        LOG.info("Getting pension plan contract movement response for consent id {}", consentId);
        getContract(contractId, consentId, EnumConsentPermission.PENSION_PLAN_MOVEMENTS_READ, EnumConsentV3Permission.PENSION_PLAN_MOVEMENTS_READ);

        var benefits = pensionPlanContractMovementBenefitRepository.findByPensionPlanContractId(contractId, pageable);
        var contributions = pensionPlanContractMovementContributionRepository.findByPensionPlanContractId(contractId, pageable);
        var resp = new ResponseInsurancePensionPlanMovements()
                .data(new InsurancePensionPlanMovements()
                        .movementContributions(contributions.getContent().stream().map(PensionPlanContractMovementContributionEntity::mapDTO).toList())
                        .movementBenefits(benefits.getContent().stream().map(PensionPlanContractMovementBenefitEntity::mapDTO).toList())
                );
        resp.setMeta(InsuranceLambdaUtils.getMeta(benefits, false));
        return resp;
    }

    public void checkConsentCoversContract(ConsentEntity consentEntity, PensionPlanContractEntity contract) {
        var contractFromConsent = consentEntity.getPensionPlanContracts()
                .stream()
                .filter(p -> contract.getPensionPlanContractId().equals(p.getPensionPlanContractId()))
                .findFirst();
        if (contractFromConsent.isEmpty()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Bad request, consent does not cover this pension plan!");
        }
    }

    public void checkConsentOwnerIsContractOwner(ConsentEntity consentEntity, PensionPlanContractEntity contract) {
        if (!consentEntity.getAccountHolderId().equals(contract.getAccountHolderId())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Forbidden, consent owner does not match account owner!");
        }
    }
}
