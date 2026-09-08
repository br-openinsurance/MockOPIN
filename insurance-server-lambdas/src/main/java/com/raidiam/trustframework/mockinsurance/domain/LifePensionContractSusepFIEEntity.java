package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "life_pension_contract_susep_fies")
public class LifePensionContractSusepFIEEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "life_pension_contract_susep_fie_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID susepFieId;

    @Column(name = "fie_cnpj")
    private String fieCnpj;

    @Column(name = "fie_name")
    private String fieName;

    @Column(name = "fie_trade_name")
    private String fieTradeName;

    @Column(name = "pmbac_amount")
    private String pmbacAmount;

    @Column(name = "pmbac_unit_type")
    private String pmbacUnitType;

    @Column(name = "pmbac_unit_type_others")
    private String pmbacUnitTypeOthers;

    @Column(name = "pmbac_unit_code")
    private String pmbacUnitCode;

    @Column(name = "pmbac_unit_description")
    private String pmbacUnitDescription;

    @Column(name = "pmbac_currency")
    private String pmbacCurrency;

    @Column(name = "provision_surplus_amount")
    private String provisionSurplusAmount;

    @Column(name = "provision_surplus_unit_type")
    private String provisionSurplusUnitType;

    @Column(name = "provision_surplus_unit_type_others")
    private String provisionSurplusUnitTypeOthers;

    @Column(name = "provision_surplus_unit_code")
    private String provisionSurplusUnitCode;

    @Column(name = "provision_surplus_unit_description")
    private String provisionSurplusUnitDescription;

    @Column(name = "provision_surplus_currency")
    private String provisionSurplusCurrency;

    @Column(name = "life_pension_contract_susep_id")
    private UUID lifePensionContractSusepId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "life_pension_contract_susep_id", referencedColumnName = "life_pension_contract_susep_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private LifePensionContractSusepEntity lifePensionContractSusep;

    public SusepsFIE getDTO() {
        return new SusepsFIE()
                .FIECNPJ(this.getFieCnpj())
                .fiEName(this.getFieName())
                .fiETradeName(this.getFieTradeName())
                .pmbacAmount(new AmountDetails()
                        .amount(this.getPmbacAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPmbacUnitType())))
                .provisionSurplusAmount(new AmountDetails()
                        .amount(this.getProvisionSurplusAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getProvisionSurplusUnitType())));
    }

    public SusepsFIEV2 getDTOV2() {
        return new SusepsFIEV2()
                .FIECNPJ(this.getFieCnpj())
                .fiEName(this.getFieName())
                .fiETradeName(this.getFieTradeName())
                .pmbacAmount(new AmountDetails()
                        .amount(this.getPmbacAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPmbacUnitType())))
                .provisionSurplusAmount(new AmountDetails()
                        .amount(this.getProvisionSurplusAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getProvisionSurplusUnitType())));
    }
}
