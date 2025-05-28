package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "pension_plan_contracts")
public class PensionPlanContractEntity extends BaseEntity implements HasStatusInterface {
    @Id
    @Column(name = "pension_plan_contract_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private String pensionPlanContractId;

    @Column(name = "status")
    private String status;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    public ResponseInsurancePensionPlanBrandContracts mapContractDTO() {
        return new ResponseInsurancePensionPlanBrandContracts()
                .pensionIdentification(this.getPensionPlanContractId())
                .productName("Mock Insurer Pension Plan Contract");
    }

    public ResponseInsurancePensionPlanContractInfo mapContractInfoDTO() {
        return new ResponseInsurancePensionPlanContractInfo()
                .data(new InsurancePensionPlanContractInfo()
                        .pensionIdentification(this.getPensionPlanContractId())
                        .contractingType(InsurancePensionPlanContractInfo.ContractingTypeEnum.INDIVIDUAL)
                        .documents(List.of(new InsurancePensionPlanDocuments()
                                .certificateId("67")
                                .effectiveDateStart(LocalDate.of(2021, 5, 21))
                                .effectiveDateEnd(LocalDate.of(2023, 5, 21))
                                .proposalId("987")
                                .insureds(List.of(new InsurancePensionPlanDocumentsInsured()
                                        .documentType(InsurancePensionPlanDocumentsInsured.DocumentTypeEnum.CPF)
                                        .documentNumber("12345678910")
                                        .name("JOAO DA SILVA")
                                        .birthDate(LocalDate.of(2021, 5, 1))
                                        .gender(InsurancePensionPlanDocumentsInsured.GenderEnum.FEMININO)
                                        .postCode("17500001")
                                        .townName("Rio de Janeiro")
                                        .countrySubDivision(EnumCountrySubDivision.RJ)
                                        .countryCode(EnumCountryCode.BRA)
                                        .address("Av Naburo Ykesaki, 1270")))
                                .plans(new InsurancePensionPlanDocumentsPlans()
                                        .coverages(List.of(new InsurancePensionPlanDocumentsPlansCoverage()
                                                .coverageCode("1999")
                                                .susepProcessNumber("12345")
                                                .structureModality(InsurancePensionPlanDocumentsPlansCoverage.StructureModalityEnum.BENEFICIO_DEFINIDO)
                                                .benefitAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                                                .periodicity(InsurancePensionPlanDocumentsPlansCoverage.PeriodicityEnum.MENSAL)
                                                .coverageName("coverage")
                                                .lockedPlan(false)
                                                .termStartDate(LocalDate.of(2021, 5, 21))
                                                .termEndDate(LocalDate.of(2023, 5, 21))
                                                .financialRegime(InsurancePensionPlanDocumentsPlansCoverage.FinancialRegimeEnum.CAPITALIZACAO)
                                                .pricingMethod(InsurancePensionPlanDocumentsPlansCoverage.PricingMethodEnum.POR_IDADE)
                                                .updateIndex(InsurancePensionPlanDocumentsPlansCoverage.UpdateIndexEnum.IGPM_FGV)
                                                .updateIndexLagging(1)
                                                .contributionAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                                                .benefitPaymentAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                                                .benefitPaymentMethod(InsurancePensionPlanDocumentsPlansCoverage.BenefitPaymentMethodEnum.UNICO)
                                                .chargedAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))))))));
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getPensionPlanContractId());
    }
}
