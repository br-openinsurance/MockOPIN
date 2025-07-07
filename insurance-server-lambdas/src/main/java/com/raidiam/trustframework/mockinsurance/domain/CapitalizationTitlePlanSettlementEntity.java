package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceCapitalizationTitleSettlementData;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "capitalization_title_plan_settlements")
public class CapitalizationTitlePlanSettlementEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "capitalization_title_plan_settlement_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID settlementId;

    @Column(name = "settlement_financial_amount")
    private String settlementFinancialAmount;

    @Column(name = "settlement_financial_unit_type")
    private String settlementFinancialUnitType;

    @Column(name = "settlement_financial_unit_type_others")
    private String settlementFinancialUnitTypeOthers;

    @Column(name = "settlement_financial_unit_code")
    private String settlementFinancialUnitCode;

    @Column(name = "settlement_financial_unit_description")
    private String settlementFinancialUnitDescription;

    @Column(name = "settlement_financial_currency")
    private String settlementFinancialCurrency;

    @Column(name = "settlement_payment_date")
    private LocalDate settlementPaymentDate;

    @Column(name = "settlement_due_date")
    private LocalDate settlementDueDate;

    @Column(name = "capitalization_title_plan_id")
    private UUID capitalizationTitlePlanId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capitalization_title_plan_id", referencedColumnName = "capitalization_title_plan_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private CapitalizationTitlePlanEntity capitalizationTitlePlan;

    public ResponseInsuranceCapitalizationTitleSettlementData mapDto() {
        return new ResponseInsuranceCapitalizationTitleSettlementData()
                .settlementId(this.getSettlementId().toString())
                .settlementFinancialAmount(new AmountDetails()
                        .amount(this.getSettlementFinancialAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getSettlementFinancialUnitType()))
                        .unitTypeOthers(this.getSettlementFinancialUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getSettlementFinancialUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getSettlementFinancialUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getSettlementFinancialCurrency())))
                .settlementDueDate(this.getSettlementDueDate())
                .settlementPaymentDate(this.getSettlementPaymentDate());
    }
}
