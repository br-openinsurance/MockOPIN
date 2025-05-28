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
@Table(name = "financial_assistance_contracts")
public class FinancialAssistanceContractEntity extends BaseEntity implements HasStatusInterface {
    @Id
    @Column(name = "financial_assistance_contract_id", unique = true, nullable = false, updatable = false)
    private String financialAssistanceContractId;

    @Column(name = "status")
    private String status;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    public ResponseInsuranceFinancialAssistanceBrandContracts mapContractDto() {
        return new ResponseInsuranceFinancialAssistanceBrandContracts()
                .contractId(this.getFinancialAssistanceContractId());
    }

    public ResponseInsuranceFinancialAssistanceContractInfo mapContractInfoDto() {
        return new ResponseInsuranceFinancialAssistanceContractInfo()
                .data(new InsuranceFinancialAssistanceContractInfo()
                        .contractId(this.getFinancialAssistanceContractId())
                        .certificateId("42")
                        .susepProcessNumber("12345")
                        .insureds(List.of(new InsuranceFinancialAssistanceInsured()
                                .documentType(InsuranceFinancialAssistanceInsured.DocumentTypeEnum.CPF)
                                .documentNumber(this.getAccountHolder().getDocumentIdentification())
                                .name(this.getAccountHolder().getAccountHolderName())))
                        .conceivedCreditValue(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                        .creditedLiquidValue(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                        .counterInstallments(new InsuranceFinancialAssistanceCounterInstallments()
                                .value((new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO)))
                                .periodicity(InsuranceFinancialAssistanceCounterInstallments.PeriodicityEnum.MENSAL)
                                .quantity(4)
                                .firstDate(LocalDate.of(2025, 1, 1))
                                .lastDate(LocalDate.of(2025, 4, 1)))
                        .interestRate(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                        .effectiveCostRate(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                        .amortizationPeriod(4)
                        .taxesValue(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                        .administrativeFeesValue(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                        .interestValue(new AmountDetails().amount("01.00").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)));
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getFinancialAssistanceContractId());
    }
}
