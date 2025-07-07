package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
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
@Table(name = "capitalization_title_plan_events")
public class CapitalizationTitlePlanEventEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "capitalization_title_plan_event_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID eventId;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "raffle_amount")
    private String raffleAmount;

    @Column(name = "raffle_unit_type")
    private String raffleUnitType;

    @Column(name = "raffle_unit_type_others")
    private String raffleUnitTypeOthers;

    @Column(name = "raffle_unit_code")
    private String raffleUnitCode;

    @Column(name = "raffle_unit_description")
    private String raffleUnitDescription;

    @Column(name = "raffle_currency")
    private String raffleCurrency;

    @Column(name = "raffle_date")
    private LocalDate raffleDate;

    @Column(name = "raffle_settlement_date")
    private LocalDate raffleSettlementDate;

    @Column(name = "redemption_type")
    private String redemptionType;

    @Column(name = "redemption_amount")
    private String redemptionAmount;

    @Column(name = "redemption_unit_type")
    private String redemptionUnitType;

    @Column(name = "redemption_unit_type_others")
    private String redemptionUnitTypeOthers;

    @Column(name = "redemption_unit_code")
    private String redemptionUnitCode;

    @Column(name = "redemption_unit_description")
    private String redemptionUnitDescription;

    @Column(name = "redemption_currency")
    private String redemptionCurrency;

    @Column(name = "redemption_bonus_amount")
    private String redemptionBonusAmount;

    @Column(name = "redemption_bonus_unit_type")
    private String redemptionBonusUnitType;

    @Column(name = "redemption_bonus_unit_type_others")
    private String redemptionBonusUnitTypeOthers;

    @Column(name = "redemption_bonus_unit_code")
    private String redemptionBonusUnitCode;

    @Column(name = "redemption_bonus_unit_description")
    private String redemptionBonusUnitDescription;

    @Column(name = "redemption_bonus_currency")
    private String redemptionBonusCurrency;

    @Column(name = "redemption_request_date")
    private LocalDate redemptionRequestDate;

    @Column(name = "redemption_settlement_date")
    private LocalDate redemptionSettlementDate;

    @Column(name = "capitalization_title_plan_id")
    private UUID capitalizationTitlePlanId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capitalization_title_plan_id", referencedColumnName = "capitalization_title_plan_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private CapitalizationTitlePlanEntity capitalizationTitlePlan;

    public ResponseInsuranceCapitalizationTitleEventData mapDto() {
        return new ResponseInsuranceCapitalizationTitleEventData()
                .titleId(this.getCapitalizationTitlePlan().getCapitalizationTitleId())
                .eventType(ResponseInsuranceCapitalizationTitleEventData.EventTypeEnum.fromValue(this.getEventType()))
                .event(ResponseInsuranceCapitalizationTitleEventData.EventTypeEnum.SORTEIO.toString().equals(eventType)
                                ? new ResponseInsuranceCapitalizationTitleEventEvent()
                                .raffle(new ResponseInsuranceCapitalizationTitleEventEventRaffle()
                                                .raffleAmount(new AmountDetails()
                                                        .amount(this.getRaffleAmount())
                                                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getRaffleUnitType()))
                                                        .unitTypeOthers(this.getRaffleUnitTypeOthers())
                                                        .unit(new AmountDetailsUnit()
                                                                .code(this.getRaffleUnitCode())
                                                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getRaffleUnitDescription())))
                                                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getRaffleCurrency())))
                                                .raffleDate(this.getRaffleDate())
                                                .raffleSettlementDate(this.getRaffleSettlementDate()))
                                : new ResponseInsuranceCapitalizationTitleEventEvent()
                                    .redemption(new ResponseInsuranceCapitalizationTitleEventEventRedemption()
                                            .redemptionType(ResponseInsuranceCapitalizationTitleEventEventRedemption.RedemptionTypeEnum.fromValue(this.redemptionType))
                                            .redemptionAmount(new AmountDetails()
                                                    .amount(this.getRedemptionAmount())
                                                    .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getRedemptionUnitType()))
                                                    .unitTypeOthers(this.getRedemptionUnitTypeOthers())
                                                    .unit(new AmountDetailsUnit()
                                                            .code(this.getRedemptionUnitCode())
                                                            .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getRedemptionUnitDescription())))
                                                    .currency(AmountDetails.CurrencyEnum.fromValue(this.getRedemptionCurrency())))
                                            .redemptionBonusAmount(new AmountDetails()
                                                    .amount(this.getRedemptionBonusAmount())
                                                    .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getRedemptionBonusUnitType()))
                                                    .unitTypeOthers(this.getRedemptionBonusUnitTypeOthers())
                                                    .unit(new AmountDetailsUnit()
                                                            .code(this.getRedemptionBonusUnitCode())
                                                            .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getRedemptionBonusUnitDescription())))
                                                    .currency(AmountDetails.CurrencyEnum.fromValue(this.getRedemptionBonusCurrency())))
                                            .redemptionRequestDate(this.getRedemptionRequestDate())
                                            .redemptionSettlementDate(this.getRedemptionSettlementDate())));
    }
}
