package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceTransportSpecificPolicyInfoEndorsements;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "transport_policy_endorsements")
public class TransportPolicyEndorsementEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "transport_policy_endorsement_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID endorsementId;

    @Column(name = "transport_policy_id")
    private String transportPolicyId;

    @Column(name = "travel_type")
    private String travelType;

    @Column(name = "transport_type")
    private String transportType;

    @Column(name = "shipments_number")
    private Integer shipmentsNumber;

    @Column(name = "branch")
    private String branch;

    @Column(name = "shipments_premium_amount")
    private String shipmentsPremiumAmount;

    @Column(name = "shipments_premium_unit_type")
    private String shipmentsPremiumUnitType;

    @Column(name = "shipments_premium_unit_type_others")
    private String shipmentsPremiumUnitTypeOthers;

    @Column(name = "shipments_premium_unit_code")
    private String shipmentsPremiumUnitCode;

    @Column(name = "shipments_premium_unit_description")
    private String shipmentsPremiumUnitDescription;

    @Column(name = "shipments_premium_currency")
    private String shipmentsPremiumCurrency;

    @Column(name = "shipments_premium_brl")
    private String shipmentsPremiumBRL;

    @Column(name = "shipments_insureds_amount")
    private String shipmentsInsuredsAmount;

    @Column(name = "shipments_insureds_unit_type")
    private String shipmentsInsuredsUnitType;

    @Column(name = "shipments_insureds_unit_type_others")
    private String shipmentsInsuredsUnitTypeOthers;

    @Column(name = "shipments_insureds_unit_code")
    private String shipmentsInsuredsUnitCode;

    @Column(name = "shipments_insureds_unit_description")
    private String shipmentsInsuredsUnitDescription;

    @Column(name = "shipments_insureds_currency")
    private String shipmentsInsuredsCurrency;

    @Column(name = "min_insured_amount")
    private String minInsuredAmount;

    @Column(name = "min_insured_unit_type")
    private String minInsuredUnitType;

    @Column(name = "min_insured_unit_type_others")
    private String minInsuredUnitTypeOthers;

    @Column(name = "min_insured_unit_code")
    private String minInsuredUnitCode;

    @Column(name = "min_insured_unit_description")
    private String minInsuredUnitDescription;

    @Column(name = "min_insured_currency")
    private String minInsuredCurrency;

    @Column(name = "max_insured_amount")
    private String maxInsuredAmount;

    @Column(name = "max_insured_unit_type")
    private String maxInsuredUnitType;

    @Column(name = "max_insured_unit_type_others")
    private String maxInsuredUnitTypeOthers;

    @Column(name = "max_insured_unit_code")
    private String maxInsuredUnitCode;

    @Column(name = "max_insured_unit_description")
    private String maxInsuredUnitDescription;

    @Column(name = "max_insured_currency")
    private String maxInsuredCurrency;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_policy_id", referencedColumnName = "transport_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private TransportPolicyEntity transportPolicy;

    public InsuranceTransportSpecificPolicyInfoEndorsements mapDto() {
        return new InsuranceTransportSpecificPolicyInfoEndorsements()
                .travelType(InsuranceTransportSpecificPolicyInfoEndorsements.TravelTypeEnum.fromValue(this.getTravelType()))
                .transportType(InsuranceTransportSpecificPolicyInfoEndorsements.TransportTypeEnum.fromValue(this.getTransportType()))
                .shipmentsNumber(this.getShipmentsNumber())
                .branch(this.getBranch())
                .shipmentsPremium(new AmountDetails()
                        .amount(this.getShipmentsPremiumAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getShipmentsPremiumUnitType()))
                        .unitTypeOthers(this.getShipmentsPremiumUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getShipmentsPremiumUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getShipmentsPremiumUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getShipmentsPremiumCurrency())))
                .shipmentsPremiumBRL(this.getShipmentsPremiumBRL())
                .shipmentsInsuredsAmount(new AmountDetails()
                        .amount(this.getShipmentsInsuredsAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getShipmentsInsuredsUnitType()))
                        .unitTypeOthers(this.getShipmentsInsuredsUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getShipmentsInsuredsUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getShipmentsInsuredsUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getShipmentsInsuredsCurrency())))
                .minInsuredAmount(new AmountDetails()
                        .amount(this.getMinInsuredAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getMinInsuredUnitType()))
                        .unitTypeOthers(this.getMinInsuredUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getMinInsuredUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getMinInsuredUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getMinInsuredCurrency())))
                .maxInsuredAmount(new AmountDetails()
                        .amount(this.getMaxInsuredAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getMaxInsuredUnitType()))
                        .unitTypeOthers(this.getMaxInsuredUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getMaxInsuredUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getMaxInsuredUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getMaxInsuredCurrency())));
    }
}
