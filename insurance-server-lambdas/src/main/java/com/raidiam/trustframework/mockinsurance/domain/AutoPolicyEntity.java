package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "auto_policies")
public class AutoPolicyEntity extends BaseEntity implements HasStatusInterface {

    @Id
    @Column(name = "auto_policy_id", unique = true, nullable = false, updatable = false)
    private String autoPolicyId;

    @Column(name = "status")
    private String status;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "proposal_id")
    private String proposalId;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    public BaseBrandAndCompanyDataPolicies mapPolicyDto() {
        return new BaseBrandAndCompanyDataPolicies()
                .policyId(this.getAutoPolicyId())
                .productName("Mock Insurer Auto Policy Plan");
    }

    public ResponseInsuranceAutoPolicyInfo mapInfoDto() {
        return new ResponseInsuranceAutoPolicyInfo()
                .data(new InsuranceAutoPolicyInfo()
                        .documentType(InsuranceAutoPolicyInfo.DocumentTypeEnum.fromValue(this.getDocumentType()))
                        .policyId(this.getAutoPolicyId())
                        .issuanceType(InsuranceAutoPolicyInfo.IssuanceTypeEnum.EMISSAO_PROPRIA)
                        .termStartDate(LocalDate.now().minusDays(10))
                        .termEndDate(LocalDate.now().plusDays(10))
                        .maxLMG(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                        .proposalId(this.getProposalId())
                        .issuanceDate(LocalDate.now())
                        .insureds(List.of(new PersonalInfo()
                                .identification(this.getAccountHolder().getDocumentIdentification())
                                .identificationType(PersonalInfo.IdentificationTypeEnum.CPF)
                                .name(this.getAccountHolder().getAccountHolderName())
                                .birthDate(LocalDate.of(2000, 1, 1))
                                .postCode("10000000")
                                .city("Sao Paulo")
                                .state(PersonalInfo.StateEnum.SP)
                                .country(PersonalInfo.CountryEnum.BRA)
                                .address("Av Naburo Ykesaki, 1270")))
                        .insuredObjects(List.of(new InsuranceAutoInsuredObject()
                                .identification(this.getAccountHolder().getDocumentIdentification())
                                .type(InsuranceAutoInsuredObject.TypeEnum.CONDUTOR)
                                .description("condutor")
                                .coverages(List.of(new InsuranceAutoInsuredObjectCoverage()
                                        .branch("0111")
                                        .code(InsuranceAutoInsuredObjectCoverage.CodeEnum.PEQUENOS_REPAROS)
                                        .susepProcessNumber("12345")
                                        .LMI(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                                        .termStartDate(LocalDate.now().minusDays(10))
                                        .termEndDate(LocalDate.now().plusDays(10))
                                        .isMainCoverage(true).feature(InsuranceAutoInsuredObjectCoverage.FeatureEnum.GRANDES_RISCOS)
                                        .type(InsuranceAutoInsuredObjectCoverage.TypeEnum.REGULAR_COMUM)
                                        .premiumAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                                        .premiumPeriodicity(InsuranceAutoInsuredObjectCoverage.PremiumPeriodicityEnum.MENSAL)))))
                        .repairNetwork(InsuranceAutoPolicyInfo.RepairNetworkEnum.LIVRE_ESCOLHA)
                        .repairedPartsUsageType(InsuranceAutoPolicyInfo.RepairedPartsUsageTypeEnum.NOVA)
                        .repairedPartsClassification(InsuranceAutoPolicyInfo.RepairedPartsClassificationEnum.ORIGINAL)
                        .repairedPartsNationality(InsuranceAutoPolicyInfo.RepairedPartsNationalityEnum.NACIONAL)
                        .validityType(InsuranceAutoPolicyInfo.ValidityTypeEnum.MENSAL));
    }

    public ResponseInsuranceAutoPremium mapPremiumDto() {
        return new ResponseInsuranceAutoPremium()
                .data(new InsuranceAutoPremium()
                        .paymentsQuantity("3")
                        .amount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                        .coverages(List.of(new InsuranceAutoPremiumCoverage()
                                .branch("0111")
                                .code(InsuranceAutoPremiumCoverage.CodeEnum.ACESSORIOS_E_EQUIPAMENTOS)
                                .premiumAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))))
                        .payments(List.of(new Payment()
                                .movementDate(LocalDate.now().minusDays(5))
                                .movementType(Payment.MovementTypeEnum.LIQUIDACAO_DE_PREMIO)
                                .movementPaymentsNumber(BigDecimal.valueOf(1))
                                .amount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                                .maturityDate(LocalDate.now()))));
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getAutoPolicyId());
    }
}
