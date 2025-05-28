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
@Table(name = "life_pension_contract_claims")
public class LifePensionContractClaimEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "life_pension_contract_claim_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID claimId;

    @Column(name = "life_pension_contract_id")
    private UUID lifePensionContractId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "life_pension_contract_id", referencedColumnName = "life_pension_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private LifePensionContractEntity lifePensionContract;

    public InsuranceLifePensionClaim mapDTO() {
        return new InsuranceLifePensionClaim()
                .eventInfo(new EventInfo()
                        .eventStatus(EventInfo.EventStatusEnum.ABERTO)
                        .eventAlertDate(LocalDate.of(2021, 5, 1))
                        .eventRegisterDate(LocalDate.of(2021, 5, 1))
                )
                .incomeInfo(new InsuranceLifePensionClaimIncomeInfo()
                        .beneficiaryDocument("12345678910")
                        .beneficiaryDocumentType(InsuranceLifePensionClaimIncomeInfo.BeneficiaryDocumentTypeEnum.CPF)
                        .beneficiaryName("NOME BENEFICIARIO")
                        .beneficiaryCategory(InsuranceLifePensionClaimIncomeInfo.BeneficiaryCategoryEnum.SEGURADO)
                        .beneficiaryBirthDate(LocalDate.of(1990, 1, 1))
                        .incomeType(InsuranceLifePensionClaimIncomeInfo.IncomeTypeEnum.PAGAMENTO_UNICO)
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
                        .monetaryUpdateIndex(InsuranceLifePensionClaimIncomeInfo.MonetaryUpdateIndexEnum.IPC_FGV)
                        .lastUpdateDate(LocalDate.of(2021, 5, 1))
                        .defermentDueDate(LocalDate.of(2025, 5, 1))
                );
    }
}
