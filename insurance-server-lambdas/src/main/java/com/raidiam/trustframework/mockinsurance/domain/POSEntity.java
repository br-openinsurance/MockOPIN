package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.POS;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "pos")
public class POSEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "pos_id", unique = true, nullable = false, updatable = false, insertable = false)
    private UUID posId;

    @Column(name = "application_type")
    private String applicationType;

    @Column(name = "description")
    private String description;

    @Column(name = "min_value_amount")
    private String minValueAmount;

    @Column(name = "min_value_unit_type")
    private String minValueUnitType;

    @Column(name = "min_value_unit_type_others")
    private String minValueUnitTypeOthers;

    @Column(name = "min_value_unit_code")
    private String minValueUnitCode;

    @Column(name = "min_value_unit_description")
    private String minValueUnitDescription;

    @Column(name = "min_value_currency")
    private String minValueCurrency;

    @Column(name = "max_value_amount")
    private String maxValueAmount;

    @Column(name = "max_value_unit_type")
    private String maxValueUnitType;

    @Column(name = "max_value_unit_type_others")
    private String maxValueUnitTypeOthers;

    @Column(name = "max_value_unit_code")
    private String maxValueUnitCode;

    @Column(name = "max_value_unit_description")
    private String maxValueUnitDescription;

    @Column(name = "max_value_currency")
    private String maxValueCurrency;

    @Column(name = "percentage_amount")
    private String percentageAmount;

    @Column(name = "percentage_unit_type")
    private String percentageUnitType;

    @Column(name = "percentage_unit_type_others")
    private String percentageUnitTypeOthers;

    @Column(name = "percentage_unit_code")
    private String percentageUnitCode;

    @Column(name = "percentage_unit_description")
    private String percentageUnitDescription;

    @Column(name = "percentage_currency")
    private String percentageCurrency;

    @Column(name = "value_others_amount")
    private String valueOthersAmount;

    @Column(name = "value_others_unit_type")
    private String valueOthersUnitType;

    @Column(name = "value_others_unit_type_others")
    private String valueOthersUnitTypeOthers;

    @Column(name = "value_others_unit_code")
    private String valueOthersUnitCode;

    @Column(name = "value_others_unit_description")
    private String valueOthersUnitDescription;

    @Column(name = "value_others_currency")
    private String valueOthersCurrency;

    public POS mapDTO() {
        return new POS()
                .applicationType(POS.ApplicationTypeEnum.fromValue(this.getApplicationType()))
                .description(this.getDescription())
                .minValue(new AmountDetails()
                        .amount(this.getMinValueAmount())
                        .unit(new AmountDetailsUnit()
                                .code(this.getMinValueUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getMinValueUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getMinValueCurrency()))
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getMinValueUnitType()))
                        .unitTypeOthers(this.getMinValueUnitTypeOthers()))
                .maxValue(new AmountDetails()
                        .amount(this.getMaxValueAmount())
                        .unit(new AmountDetailsUnit()
                                .code(this.getMaxValueUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getMaxValueUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getMaxValueCurrency()))
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getMaxValueUnitType()))
                        .unitTypeOthers(this.getMaxValueUnitTypeOthers()))
                .percentage(new AmountDetails()
                        .amount(this.getPercentageAmount())
                        .unit(new AmountDetailsUnit()
                                .code(this.getPercentageUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getPercentageUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getPercentageCurrency()))
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPercentageUnitType()))
                        .unitTypeOthers(this.getPercentageUnitTypeOthers()))
                .valueOthers(new AmountDetails()
                        .amount(this.getValueOthersAmount())
                        .unit(new AmountDetailsUnit()
                                .code(this.getValueOthersUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getValueOthersUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getValueOthersCurrency()))
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getValueOthersUnitType()))
                        .unitTypeOthers(this.getValueOthersUnitTypeOthers()));
    }
}
