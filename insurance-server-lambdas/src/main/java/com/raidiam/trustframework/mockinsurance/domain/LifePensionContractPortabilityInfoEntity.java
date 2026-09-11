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

import java.time.OffsetDateTime;
import java.util.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "life_pension_contract_portabilities")
public class LifePensionContractPortabilityInfoEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "life_pension_contract_portability_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID portabilityId;

    @Column(name = "life_pension_contract_id")
    private UUID lifePensionContractId;

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

    @Column(name = "posted_charged_amount")
    private String postedChargedAmount;

    @Column(name = "posted_charged_unit_type")
    private String postedChargedUnitType;

    @Column(name = "posted_charged_unit_type_others")
    private String postedChargedUnitTypeOthers;

    @Column(name = "posted_charged_unit_code")
    private String postedChargedUnitCode;

    @Column(name = "posted_charged_unit_description")
    private String postedChargedUnitDescription;

    @Column(name = "posted_charged_currency")
    private String postedChargedCurrency;

    @Column(name = "source_entity")
    private String sourceEntity;

    @Column(name = "target_entity")
    private String targetEntity;

    @Column(name = "susep_process")
    private String susepProcess;

    @Column(name = "tax_regime")
    private String taxRegime;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lifePensionContractPortability")
    private List<LifePensionContractPortabilityFIEEntity> fies = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "life_pension_contract_id", referencedColumnName = "life_pension_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private LifePensionContractEntity lifePensionContract;

    public InsuranceLifePensionPortabilityPortabilityInfo mapDTO() {
        return new InsuranceLifePensionPortabilityPortabilityInfo()
                .direction(InsuranceLifePensionPortabilityPortabilityInfo.DirectionEnum.fromValue(this.getDirection()))
                .type(InsuranceLifePensionPortabilityPortabilityInfo.TypeEnum.fromValue(this.getType()))
                .amount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getAmountUnitType()))
                        .amount(this.getAmount())
                )
                .requestDate(OffsetDateTime.parse(this.getRequestDate()))
                .liquidationDate(OffsetDateTime.parse(this.getLiquidationDate()))
                .postedChargedAmount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPostedChargedUnitType()))
                        .amount(this.getPostedChargedAmount())
                )
                .sourceEntity(this.getSourceEntity())
                .targetEntity(this.getTargetEntity())
                .susepProcess(this.getSusepProcess())
                .taxRegime(InsuranceLifePensionPortabilityPortabilityInfo.TaxRegimeEnum.fromValue(this.getTaxRegime()))
                .FIE(this.getFies().stream().map(LifePensionContractPortabilityFIEEntity::getDTO).toList());
    }

    public InsuranceLifePensionPortabilityV2PortabilityInfo mapDTOV2() {
        return new InsuranceLifePensionPortabilityV2PortabilityInfo()
                .direction(InsuranceLifePensionPortabilityV2PortabilityInfo.DirectionEnum.fromValue(this.getDirection()))
                .type(InsuranceLifePensionPortabilityV2PortabilityInfo.TypeEnum.fromValue(this.getType()))
                .amount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getAmountUnitType()))
                        .amount(this.getAmount())
                )
                .requestDate(OffsetDateTime.parse(this.getRequestDate()))
                .liquidationDate(OffsetDateTime.parse(this.getLiquidationDate()))
                .postedChargedAmount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPostedChargedUnitType()))
                        .amount(this.getPostedChargedAmount())
                )
                .sourceEntity(this.getSourceEntity())
                .targetEntity(this.getTargetEntity())
                .susepProcess(this.getSusepProcess())
                .taxRegime(InsuranceLifePensionPortabilityV2PortabilityInfo.TaxRegimeEnum.fromValue(this.getTaxRegime()))
                .FIE(this.getFies().stream().map(LifePensionContractPortabilityFIEEntity::getDTOV2).toList());
    }
}
