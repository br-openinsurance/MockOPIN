package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "pension_plan_contract_claims")
public class PensionPlanContractClaimEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "pension_plan_contract_claim_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID claimId;

    @Column(name = "pension_plan_contract_id")
    private String pensionPlanContractId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pension_plan_contract_id", referencedColumnName = "pension_plan_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PensionPlanContractEntity pensionPlanContract;

    public InsurancePensionPlanClaim mapDTO() {
        return new InsurancePensionPlanClaim()
                .eventInfo(new EventInfo()
                        .eventStatus(EventInfo.EventStatusEnum.ABERTO)
                        .eventAlertDate(LocalDate.of(2021, 5, 1))
                        .eventRegisterDate(LocalDate.of(2021, 5, 1))
                )
                .incomeInfo(new InsurancePensionPlanClaimIncomeInfo()
                        .beneficiaryDocument("12345678910")
                        .beneficiaryDocumentType(InsurancePensionPlanClaimIncomeInfo.BeneficiaryDocumentTypeEnum.CPF)
                        .beneficiaryName("NOME BENEFICIARIO")
                        .beneficiaryCategory(InsurancePensionPlanClaimIncomeInfo.BeneficiaryCategoryEnum.SEGURADO)
                        .beneficiaryBirthDate(LocalDate.of(1990, 1, 1))
                        .incomeType(InsurancePensionPlanClaimIncomeInfo.IncomeTypeEnum.PAGAMENTO_UNICO)
                        .reversedIncome(false)
                        .incomeAmount(new AmountDetails()
                                .amount("10000.00")
                                .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                                .unit(new AmountDetailsUnit()
                                        .code("Br")
                                        .description(AmountDetailsUnit.DescriptionEnum.BRL)
                                )
                        )
                        .paymentTerms("PRAZO")
                        .benefitAmount(1000)
                        .grantedDate(LocalDate.of(2021, 5, 1))
                        .monetaryUpdateIndex(InsurancePensionPlanClaimIncomeInfo.MonetaryUpdateIndexEnum.IPC_FGV)
                        .lastUpdateDate(LocalDate.of(2021, 5, 1)));
    }

    public InsurancePensionPlanClaimV2 mapDTOV2() {
        return new InsurancePensionPlanClaimV2()
                .eventInfo(new EventInfo()
                        .eventStatus(EventInfo.EventStatusEnum.ABERTO)
                        .eventAlertDate(LocalDate.of(2021, 5, 1))
                        .eventRegisterDate(LocalDate.of(2021, 5, 1))
                )
                .incomeInfo(new InsurancePensionPlanClaimV2IncomeInfo()
                        .beneficiaryDocument("12345678910")
                        .beneficiaryDocumentType(InsurancePensionPlanClaimV2IncomeInfo.BeneficiaryDocumentTypeEnum.CPF)
                        .beneficiaryName("NOME BENEFICIARIO")
                        .beneficiaryCategory(InsurancePensionPlanClaimV2IncomeInfo.BeneficiaryCategoryEnum.SEGURADO)
                        .beneficiaryBirthDate(LocalDate.of(1990, 1, 1))
                        .incomeType(InsurancePensionPlanClaimV2IncomeInfo.IncomeTypeEnum.PAGAMENTO_UNICO)
                        .reversedIncome(false)
                        .incomeAmount(new AmountDetails()
                                .amount("10000.00")
                                .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                                .unit(new AmountDetailsUnit()
                                        .code("Br")
                                        .description(AmountDetailsUnit.DescriptionEnum.BRL)
                                )
                        )
                        .paymentTerms("PRAZO")
                        .benefitAmount(1000)
                        .grantedDate(LocalDate.of(2021, 5, 1))
                        .monetaryUpdateIndex(InsurancePensionPlanClaimV2IncomeInfo.MonetaryUpdateIndexEnum.IPC_FGV)
                        .lastUpdateDate(LocalDate.of(2021, 5, 1)));
    }
}
