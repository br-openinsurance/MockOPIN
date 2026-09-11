package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePensionPlanPortabilityPortabilityInfo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "pension_plan_contract_portabilities")
public class PensionPlanContractPortabilityInfoEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "pension_plan_contract_portability_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID portabilityId;

    @Column(name = "pension_plan_contract_id")
    private String pensionPlanContractId;

    @Column(name = "direction")
    private String direction;

    @Column(name = "type")
    private String type;

    @Column(name = "amount")
    private String amount;

    @Column(name = "amount_unit_type")
    private String amountUnitType;

    @Column(name = "amount_unit_type_others")
    private String amountUnitTypeOthers;

    @Column(name = "amount_unit_code")
    private String amountUnitCode;

    @Column(name = "amount_unit_description")
    private String amountUnitDescription;

    @Column(name = "amount_currency")
    private String amountCurrency;

    @Column(name = "request_date")
    private String requestDate;

    @Column(name = "liquidation_date")
    private String liquidationDate;

    @Column(name = "charging_value")
    private String chargingValue;

    @Column(name = "charging_value_unit_type")
    private String chargingValueUnitType;

    @Column(name = "charging_value_unit_type_others")
    private String chargingValueUnitTypeOthers;

    @Column(name = "charging_value_unit_code")
    private String chargingValueUnitCode;

    @Column(name = "charging_value_unit_description")
    private String chargingValueUnitDescription;

    @Column(name = "charging_value_currency")
    private String chargingValueCurrency;

    @Column(name = "source_entity")
    private String sourceEntity;

    @Column(name = "target_entity")
    private String targetEntity;

    @Column(name = "susep_process")
    private String susepProcess;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pension_plan_contract_id", referencedColumnName = "pension_plan_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PensionPlanContractEntity pensionPlanContract;

    public InsurancePensionPlanPortabilityPortabilityInfo mapDTO() {
        return new InsurancePensionPlanPortabilityPortabilityInfo()
                .direction(InsurancePensionPlanPortabilityPortabilityInfo.DirectionEnum.fromValue(this.getDirection()))
                .type(InsurancePensionPlanPortabilityPortabilityInfo.TypeEnum.fromValue(this.getType()))
                .amount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getAmountUnitType()))
                        .amount(this.getAmount()))
                .requestDate(OffsetDateTime.parse(this.getRequestDate()))
                .liquidationDate(OffsetDateTime.parse(this.getLiquidationDate()))
                .chargingValue(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getChargingValueUnitType()))
                        .amount(this.getChargingValue()))
                .sourceEntity(this.getSourceEntity())
                .targetEntity(this.getTargetEntity())
                .susepProcess(this.getSusepProcess());
    }
}
