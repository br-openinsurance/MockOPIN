package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceFinancialAssistanceMovements;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "financial_assistance_contract_movements")
public class FinancialAssistanceContractMovementEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "financial_assistance_contract_movement_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID financialAssistanceContractMovementId;

    @Column(name = "updated_debit_amount")
    private String updatedDebitAmount;

    @Column(name = "updated_debit_unit_type")
    private String updatedDebitUnitType;

    @Column(name = "updated_debit_unit_type_others")
    private String updatedDebitUnitTypeOthers;

    @Column(name = "updated_debit_unit_code")
    private String updatedDebitUnitCode;

    @Column(name = "updated_debit_unit_description")
    private String updatedDebitUnitDescription;

    @Column(name = "updated_debit_currency")
    private String updatedDebitCurrency;

    @Column(name = "remaining_counter_installments_quantity")
    private Integer remainingCounterInstallmentsQuantity;

    @Column(name = "remaining_unpaid_counter_installments_quantity")
    private Integer remainingUnpaidCounterInstallmentsQuantity;

    @Column(name = "life_pension_pm_bac_amount")
    private String lifePensionPmBacAmount;

    @Column(name = "life_pension_pm_bac_unit_type")
    private String lifePensionPmBacUnitType;

    @Column(name = "life_pension_pm_bac_unit_type_others")
    private String lifePensionPmBacUnitTypeOthers;

    @Column(name = "life_pension_pm_bac_unit_code")
    private String lifePensionPmBacUnitCode;

    @Column(name = "life_pension_pm_bac_unit_description")
    private String lifePensionPmBacUnitDescription;

    @Column(name = "life_pension_pm_bac_currency")
    private String lifePensionPmBacCurrency;

    @Column(name = "pension_plan_pm_bac_amount")
    private String pensionPlanPmBacAmount;

    @Column(name = "pension_plan_pm_bac_unit_type")
    private String pensionPlanPmBacUnitType;

    @Column(name = "pension_plan_pm_bac_unit_type_others")
    private String pensionPlanPmBacUnitTypeOthers;

    @Column(name = "pension_plan_pm_bac_unit_code")
    private String pensionPlanPmBacUnitCode;

    @Column(name = "pension_plan_pm_bac_unit_description")
    private String pensionPlanPmBacUnitDescription;

    @Column(name = "pension_plan_pm_bac_currency")
    private String pensionPlanPmBacCurrency;

    @Column(name = "financial_assistance_contract_id")
    private String financialAssistanceContractId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_assistance_contract_id", referencedColumnName = "financial_assistance_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private FinancialAssistanceContractEntity financialAssistanceContract;

    public InsuranceFinancialAssistanceMovements mapDto() {
        return new InsuranceFinancialAssistanceMovements()
                .updatedDebitAmount(new AmountDetails()
                        .amount(this.getUpdatedDebitAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getUpdatedDebitUnitType()))
                        .unitTypeOthers(this.getUpdatedDebitUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getUpdatedDebitUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getUpdatedDebitUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getUpdatedDebitCurrency())))
                .remainingCounterInstallmentsQuantity(this.getRemainingCounterInstallmentsQuantity())
                .remainingUnpaidCounterInstallmentsQuantity(this.getRemainingUnpaidCounterInstallmentsQuantity())
                .lifePensionPmBacAmount(new AmountDetails()
                        .amount(this.getLifePensionPmBacAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getLifePensionPmBacUnitType()))
                        .unitTypeOthers(this.getLifePensionPmBacUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getLifePensionPmBacUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getLifePensionPmBacUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getLifePensionPmBacCurrency())))
                .pensionPlanPmBacAmount(new AmountDetails()
                        .amount(this.getLifePensionPmBacAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getLifePensionPmBacUnitType()))
                        .unitTypeOthers(this.getLifePensionPmBacUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getLifePensionPmBacUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getLifePensionPmBacUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getLifePensionPmBacCurrency())));
    }
}
