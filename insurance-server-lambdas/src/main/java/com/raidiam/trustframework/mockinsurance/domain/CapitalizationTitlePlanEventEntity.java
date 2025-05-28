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
    private UUID capitalizationTitlePlanEventId;

    @Column(name = "event_type")
    private String eventType;

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
                                                .raffleAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                                                .raffleDate(LocalDate.now())
                                                .raffleSettlementDate(LocalDate.now()))
                                : new ResponseInsuranceCapitalizationTitleEventEvent()
                                    .redemption(new ResponseInsuranceCapitalizationTitleEventEventRedemption()
                                            .redemptionType(ResponseInsuranceCapitalizationTitleEventEventRedemption.RedemptionTypeEnum.ANTECIPADO_TOTAL)
                                            .redemptionAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                                            .redemptionBonusAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                                            .redemptionRequestDate(LocalDate.now())
                                            .redemptionSettlementDate(LocalDate.now())));
    }
}
