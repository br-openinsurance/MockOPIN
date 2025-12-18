package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

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

    @Column(name = "certificate_id")
    private String certificateId;
    
    @Column(name = "group_contract_id")
    private String groupContractId;
    
    @Column(name = "susep_process_number")
    private String susepProcessNumber;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "financialAssistanceContract")
    private List<FinancialAssistanceContractInsuredEntity> insureds;
    
    @Column(name = "conceived_credit_value_amount")
    private String conceivedCreditValueAmount;

    @Column(name = "conceived_credit_value_unit_type")
    private String conceivedCreditValueUnitType;

    @Column(name = "conceived_credit_value_unit_type_others")
    private String conceivedCreditValueUnitTypeOthers;

    @Column(name = "conceived_credit_value_unit_code")
    private String conceivedCreditValueUnitCode;

    @Column(name = "conceived_credit_value_unit_description")
    private String conceivedCreditValueUnitDescription;

    @Column(name = "conceived_credit_value_currency")
    private String conceivedCreditValueCurrency;
    
    @Column(name = "credited_liquid_value_amount")
    private String creditedLiquidValueAmount;

    @Column(name = "credited_liquid_value_unit_type")
    private String creditedLiquidValueUnitType;

    @Column(name = "credited_liquid_value_unit_type_others")
    private String creditedLiquidValueUnitTypeOthers;

    @Column(name = "credited_liquid_value_unit_code")
    private String creditedLiquidValueUnitCode;

    @Column(name = "credited_liquid_value_unit_description")
    private String creditedLiquidValueUnitDescription;

    @Column(name = "credited_liquid_value_currency")
    private String creditedLiquidValueCurrency;

    @Column(name = "counter_installment_amount")
    private String counterInstallmentAmount;
    
    @Column(name = "counter_installment_unit_type")
    private String counterInstallmentUnitType;
    
    @Column(name = "counter_installment_unit_type_others")
    private String counterInstallmentUnitTypeOthers;

    @Column(name = "counter_installment_unit_code")
    private String counterInstallmentUnitCode;

    @Column(name = "counter_installment_unit_description")
    private String counterInstallmentUnitDescription;

    @Column(name = "counter_installment_currency")
    private String counterInstallmentCurrency;
    
    @Column(name = "counter_installment_periodicity")
    private String counterInstallmentPeriodicity;
    
    @Column(name = "counter_installment_quantity")
    private Integer counterInstallmentQuantity;
    
    @Column(name = "counter_installment_first_date")
    private LocalDate counterInstallmentFirstDate;
    
    @Column(name = "counter_installment_last_date")
    private LocalDate counterInstallmentLastDate;
    
    @Column(name = "interest_rate_amount")
    private String interestRateAmount;

    @Column(name = "interest_rate_unit_type")
    private String interestRateUnitType;

    @Column(name = "interest_rate_unit_type_others")
    private String interestRateUnitTypeOthers;

    @Column(name = "interest_rate_unit_code")
    private String interestRateUnitCode;

    @Column(name = "interest_rate_unit_description")
    private String interestRateUnitDescription;

    @Column(name = "interest_rate_currency")
    private String interestRateCurrency;
    
    @Column(name = "effective_cost_rate_amount")
    private String effectiveCostRateAmount;

    @Column(name = "effective_cost_rate_unit_type")
    private String effectiveCostRateUnitType;

    @Column(name = "effective_cost_rate_unit_type_others")
    private String effectiveCostRateUnitTypeOthers;

    @Column(name = "effective_cost_rate_unit_code")
    private String effectiveCostRateUnitCode;

    @Column(name = "effective_cost_rate_unit_description")
    private String effectiveCostRateUnitDescription;

    @Column(name = "effective_cost_rate_currency")
    private String effectiveCostRateCurrency;

    @Column(name = "amortization_period")
    private Integer amortizationPeriod;

    @Column(name = "acquittance_value_amount")
    private String acquittanceValueAmount;

    @Column(name = "acquittance_value_unit_type")
    private String acquittanceValueUnitType;

    @Column(name = "acquittance_value_unit_type_others")
    private String acquittanceValueUnitTypeOthers;

    @Column(name = "acquittance_value_unit_code")
    private String acquittanceValueUnitCode;

    @Column(name = "acquittance_value_unit_description")
    private String acquittanceValueUnitDescription;

    @Column(name = "acquittance_value_currency")
    private String acquittanceValueCurrency;

    @Column(name = "acquittance_date")
    private LocalDate acquittanceDate;

    @Column(name = "taxes_value_amount")
    private String taxesValueAmount;

    @Column(name = "taxes_value_unit_type")
    private String taxesValueUnitType;

    @Column(name = "taxes_value_unit_type_others")
    private String taxesValueUnitTypeOthers;

    @Column(name = "taxes_value_unit_code")
    private String taxesValueUnitCode;

    @Column(name = "taxes_value_unit_description")
    private String taxesValueUnitDescription;

    @Column(name = "taxes_value_currency")
    private String taxesValueCurrency;

    @Column(name = "expenses_value_amount")
    private String expensesValueAmount;

    @Column(name = "expenses_value_unit_type")
    private String expensesValueUnitType;

    @Column(name = "expenses_value_unit_type_others")
    private String expensesValueUnitTypeOthers;

    @Column(name = "expenses_value_unit_code")
    private String expensesValueUnitCode;

    @Column(name = "expenses_value_unit_description")
    private String expensesValueUnitDescription;

    @Column(name = "expenses_value_currency")
    private String expensesValueCurrency;

    @Column(name = "fines_value_amount")
    private String finesValueAmount;

    @Column(name = "fines_value_unit_type")
    private String finesValueUnitType;

    @Column(name = "fines_value_unit_type_others")
    private String finesValueUnitTypeOthers;

    @Column(name = "fines_value_unit_code")
    private String finesValueUnitCode;

    @Column(name = "fines_value_unit_description")
    private String finesValueUnitDescription;

    @Column(name = "fines_value_currency")
    private String finesValueCurrency;

    @Column(name = "monetary_updates_value_amount")
    private String monetaryUpdatesValueAmount;

    @Column(name = "monetary_updates_value_unit_type")
    private String monetaryUpdatesValueUnitType;

    @Column(name = "monetary_updates_value_unit_type_others")
    private String monetaryUpdatesValueUnitTypeOthers;

    @Column(name = "monetary_updates_value_unit_code")
    private String monetaryUpdatesValueUnitCode;

    @Column(name = "monetary_updates_value_unit_description")
    private String monetaryUpdatesValueUnitDescription;

    @Column(name = "monetary_updates_value_currency")
    private String monetaryUpdatesValueCurrency;

    @Column(name = "administrative_fees_value_amount")
    private String administrativeFeesValueAmount;

    @Column(name = "administrative_fees_value_unit_type")
    private String administrativeFeesValueUnitType;

    @Column(name = "administrative_fees_value_unit_type_others")
    private String administrativeFeesValueUnitTypeOthers;

    @Column(name = "administrative_fees_value_unit_code")
    private String administrativeFeesValueUnitCode;

    @Column(name = "administrative_fees_value_unit_description")
    private String administrativeFeesValueUnitDescription;

    @Column(name = "administrative_fees_value_currency")
    private String administrativeFeesValueCurrency;

    @Column(name = "interest_value_amount")
    private String interestValueAmount;

    @Column(name = "interest_value_unit_type")
    private String interestValueUnitType;

    @Column(name = "interest_value_unit_type_others")
    private String interestValueUnitTypeOthers;

    @Column(name = "interest_value_unit_code")
    private String interestValueUnitCode;

    @Column(name = "interest_value_unit_description")
    private String interestValueUnitDescription;

    @Column(name = "interest_value_currency")
    private String interestValueCurrency;

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
                        .certificateId(this.getCertificateId())
                        .groupContractId(this.getGroupContractId())
                        .susepProcessNumber(this.getSusepProcessNumber())
                        .insureds(this.getInsureds().stream().map(FinancialAssistanceContractInsuredEntity::mapDto).toList())
                        .conceivedCreditValue(new AmountDetails()
                            .amount(this.getConceivedCreditValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getConceivedCreditValueUnitType()))
                            .unitTypeOthers(this.getConceivedCreditValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getConceivedCreditValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getConceivedCreditValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getConceivedCreditValueCurrency())))
                        .creditedLiquidValue(new AmountDetails()
                                .amount(this.getCreditedLiquidValueAmount())
                                .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getCreditedLiquidValueUnitType()))
                                .unitTypeOthers(this.getCreditedLiquidValueUnitTypeOthers())
                                .unit(new AmountDetailsUnit()
                                        .code(this.getCreditedLiquidValueUnitCode())
                                        .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getCreditedLiquidValueUnitDescription())))
                                .currency(AmountDetails.CurrencyEnum.fromValue(this.getCreditedLiquidValueCurrency())))
                        .counterInstallments(new InsuranceFinancialAssistanceCounterInstallments()
                                .value(new AmountDetails()
                                        .amount(this.getCounterInstallmentAmount())
                                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getCounterInstallmentUnitType()))
                                        .unitTypeOthers(this.getCounterInstallmentUnitTypeOthers())
                                        .unit(new AmountDetailsUnit()
                                                .code(this.getCounterInstallmentUnitCode())
                                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getCounterInstallmentUnitDescription())))
                                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getCounterInstallmentCurrency())))
                                .firstDate(this.getCounterInstallmentFirstDate())
                                .lastDate(this.getCounterInstallmentLastDate())
                                .periodicity(InsuranceFinancialAssistanceCounterInstallments.PeriodicityEnum.fromValue(this.getCounterInstallmentPeriodicity()))
                                .quantity(this.getCounterInstallmentQuantity()))
                        .interestRate(new AmountDetails()
                            .amount(this.getInterestRateAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getInterestRateUnitType()))
                            .unitTypeOthers(this.getInterestRateUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getInterestRateUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getInterestRateUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getInterestRateCurrency())))
                        .effectiveCostRate(new AmountDetails()
                            .amount(this.getEffectiveCostRateAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getEffectiveCostRateUnitType()))
                            .unitTypeOthers(this.getEffectiveCostRateUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getEffectiveCostRateUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getEffectiveCostRateUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getEffectiveCostRateCurrency())))
                        .amortizationPeriod(this.getAmortizationPeriod())
                        .acquittanceValue(new AmountDetails()
                            .amount(this.getAcquittanceValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getAcquittanceValueUnitType()))
                            .unitTypeOthers(this.getAcquittanceValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getAcquittanceValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getAcquittanceValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getAcquittanceValueCurrency())))
                        .acquittanceDate(this.getAcquittanceDate())
                        .taxesValue(new AmountDetails()
                            .amount(this.getTaxesValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getTaxesValueUnitType()))
                            .unitTypeOthers(this.getTaxesValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getTaxesValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getTaxesValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getTaxesValueCurrency())))
                        .expensesValue(new AmountDetails()
                            .amount(this.getExpensesValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getExpensesValueUnitType()))
                            .unitTypeOthers(this.getExpensesValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getExpensesValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getExpensesValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getExpensesValueCurrency())))
                        .finesValue(new AmountDetails()
                            .amount(this.getFinesValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getFinesValueUnitType()))
                            .unitTypeOthers(this.getFinesValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getFinesValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getFinesValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getFinesValueCurrency())))
                        .monetaryUpdatesValue(new AmountDetails()
                            .amount(this.getMonetaryUpdatesValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getMonetaryUpdatesValueUnitType()))
                            .unitTypeOthers(this.getMonetaryUpdatesValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getMonetaryUpdatesValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getMonetaryUpdatesValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getMonetaryUpdatesValueCurrency())))
                        .administrativeFeesValue(new AmountDetails()
                            .amount(this.getAdministrativeFeesValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getAdministrativeFeesValueUnitType()))
                            .unitTypeOthers(this.getAdministrativeFeesValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getAdministrativeFeesValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getAdministrativeFeesValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getAdministrativeFeesValueCurrency())))
                        .interestValue(new AmountDetails()
                            .amount(this.getInterestValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getInterestValueUnitType()))
                            .unitTypeOthers(this.getInterestValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getInterestValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getInterestValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getInterestValueCurrency())))
                );
    }

    public ResponseInsuranceFinancialAssistanceContractInfoV2 mapContractInfoDtoV2() {
        return new ResponseInsuranceFinancialAssistanceContractInfoV2()
                .data(new InsuranceFinancialAssistanceContractInfoV2()
                        .contractId(this.getFinancialAssistanceContractId())
                        .certificateId(this.getCertificateId())
                        .groupContractId(this.getGroupContractId())
                        .susepProcessNumber(this.getSusepProcessNumber())
                        .insureds(this.getInsureds().stream().map(FinancialAssistanceContractInsuredEntity::mapDtoV2).toList())
                        .conceivedCreditValue(new AmountDetails()
                            .amount(this.getConceivedCreditValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getConceivedCreditValueUnitType()))
                            .unitTypeOthers(this.getConceivedCreditValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getConceivedCreditValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getConceivedCreditValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getConceivedCreditValueCurrency())))
                        .creditedLiquidValue(new AmountDetails()
                                .amount(this.getCreditedLiquidValueAmount())
                                .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getCreditedLiquidValueUnitType()))
                                .unitTypeOthers(this.getCreditedLiquidValueUnitTypeOthers())
                                .unit(new AmountDetailsUnit()
                                        .code(this.getCreditedLiquidValueUnitCode())
                                        .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getCreditedLiquidValueUnitDescription())))
                                .currency(AmountDetails.CurrencyEnum.fromValue(this.getCreditedLiquidValueCurrency())))
                        .counterInstallments(new InsuranceFinancialAssistanceCounterInstallments()
                                .value(new AmountDetails()
                                        .amount(this.getCounterInstallmentAmount())
                                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getCounterInstallmentUnitType()))
                                        .unitTypeOthers(this.getCounterInstallmentUnitTypeOthers())
                                        .unit(new AmountDetailsUnit()
                                                .code(this.getCounterInstallmentUnitCode())
                                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getCounterInstallmentUnitDescription())))
                                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getCounterInstallmentCurrency())))
                                .firstDate(this.getCounterInstallmentFirstDate())
                                .lastDate(this.getCounterInstallmentLastDate())
                                .periodicity(InsuranceFinancialAssistanceCounterInstallments.PeriodicityEnum.fromValue(this.getCounterInstallmentPeriodicity()))
                                .quantity(this.getCounterInstallmentQuantity()))
                        .interestRate(new AmountDetails()
                            .amount(this.getInterestRateAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getInterestRateUnitType()))
                            .unitTypeOthers(this.getInterestRateUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getInterestRateUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getInterestRateUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getInterestRateCurrency())))
                        .effectiveCostRate(new AmountDetails()
                            .amount(this.getEffectiveCostRateAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getEffectiveCostRateUnitType()))
                            .unitTypeOthers(this.getEffectiveCostRateUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getEffectiveCostRateUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getEffectiveCostRateUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getEffectiveCostRateCurrency())))
                        .amortizationPeriod(this.getAmortizationPeriod())
                        .acquittanceValue(new AmountDetails()
                            .amount(this.getAcquittanceValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getAcquittanceValueUnitType()))
                            .unitTypeOthers(this.getAcquittanceValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getAcquittanceValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getAcquittanceValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getAcquittanceValueCurrency())))
                        .acquittanceDate(this.getAcquittanceDate())
                        .taxesValue(new AmountDetails()
                            .amount(this.getTaxesValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getTaxesValueUnitType()))
                            .unitTypeOthers(this.getTaxesValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getTaxesValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getTaxesValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getTaxesValueCurrency())))
                        .expensesValue(new AmountDetails()
                            .amount(this.getExpensesValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getExpensesValueUnitType()))
                            .unitTypeOthers(this.getExpensesValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getExpensesValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getExpensesValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getExpensesValueCurrency())))
                        .finesValue(new AmountDetails()
                            .amount(this.getFinesValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getFinesValueUnitType()))
                            .unitTypeOthers(this.getFinesValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getFinesValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getFinesValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getFinesValueCurrency())))
                        .monetaryUpdatesValue(new AmountDetails()
                            .amount(this.getMonetaryUpdatesValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getMonetaryUpdatesValueUnitType()))
                            .unitTypeOthers(this.getMonetaryUpdatesValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getMonetaryUpdatesValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getMonetaryUpdatesValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getMonetaryUpdatesValueCurrency())))
                        .administrativeFeesValue(new AmountDetails()
                            .amount(this.getAdministrativeFeesValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getAdministrativeFeesValueUnitType()))
                            .unitTypeOthers(this.getAdministrativeFeesValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getAdministrativeFeesValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getAdministrativeFeesValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getAdministrativeFeesValueCurrency())))
                        .interestValue(new AmountDetails()
                            .amount(this.getInterestValueAmount())
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getInterestValueUnitType()))
                            .unitTypeOthers(this.getInterestValueUnitTypeOthers())
                            .unit(new AmountDetailsUnit()
                                    .code(this.getInterestValueUnitCode())
                                    .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getInterestValueUnitDescription())))
                            .currency(AmountDetails.CurrencyEnum.fromValue(this.getInterestValueCurrency())))
                );
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getFinancialAssistanceContractId());
    }
}
