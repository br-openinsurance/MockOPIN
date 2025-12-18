package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.Deductible;
import com.raidiam.trustframework.mockinsurance.models.generated.DeductibleV2;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "deductibles")
public class DeductibleEntity extends BaseIdEntity {

    @Column(name = "type")
    private String type;

    @Column(name = "type_additional_info")
    private String typeAdditionalInfo;

    @Column(name = "amount")
    private String amount;

    @Column(name = "unit_type")
    private String unitType;

    @Column(name = "unit_type_others")
    private String unitTypeOthers;

    @Column(name = "unit_code")
    private String unitCode;

    @Column(name = "unit_description")
    private String unitDescription;

    @Column(name = "currency")
    private String currency;

    @Column(name = "period")
    private Integer period;

    @Column(name = "periodicity")
    private String periodicity;

    @Column(name = "period_counting_method")
    private String periodCountingMethod;

    @Column(name = "period_start_date")
    private LocalDate periodStartDate;

    @Column(name = "period_end_date")
    private LocalDate periodEndDate;

    @Column(name = "description")
    private String description;

    public Deductible mapDTO() {
        return new Deductible()
                .type(Deductible.TypeEnum.fromValue(this.getType()))
                .typeAdditionalInfo(this.getTypeAdditionalInfo())
                .amount(new AmountDetails()
                        .amount(this.getAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getUnitType()))
                        .unitTypeOthers(this.getUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getCurrency())))
                .period(this.getPeriod())
                .periodicity(Deductible.PeriodicityEnum.fromValue(this.getPeriodicity()))
                .periodCountingMethod(Deductible.PeriodCountingMethodEnum.valueOf(this.getPeriodCountingMethod()))
                .periodStartDate(this.getPeriodStartDate())
                .periodEndDate(this.getPeriodEndDate())
                .description(this.getDescription());
    }

    public DeductibleV2 mapDTOV2() {
        return new DeductibleV2()
                .type(DeductibleV2.TypeEnum.fromValue(this.getType()))
                .typeAdditionalInfo(this.getTypeAdditionalInfo())
                .amount(new AmountDetails()
                        .amount(this.getAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getUnitType()))
                        .unitTypeOthers(this.getUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getCurrency())))
                .period(this.getPeriod())
                .periodicity(DeductibleV2.PeriodicityEnum.fromValue(this.getPeriodicity()))
                .periodCountingMethod(DeductibleV2.PeriodCountingMethodEnum.valueOf(this.getPeriodCountingMethod()))
                .periodStartDate(this.getPeriodStartDate())
                .periodEndDate(this.getPeriodEndDate())
                .description(this.getDescription());
    }
}
