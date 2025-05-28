package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceCapitalizationTitleSettlementData;
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
@Table(name = "capitalization_title_plan_settlements")
public class CapitalizationTitlePlanSettlementEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "capitalization_title_plan_settlement_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID capitalizationTitlePlanSettlementId;

    @Column(name = "capitalization_title_plan_id")
    private UUID capitalizationTitlePlanId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capitalization_title_plan_id", referencedColumnName = "capitalization_title_plan_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private CapitalizationTitlePlanEntity capitalizationTitlePlan;

    public ResponseInsuranceCapitalizationTitleSettlementData mapDto() {
        return new ResponseInsuranceCapitalizationTitleSettlementData()
                .settlementId(this.getCapitalizationTitlePlanSettlementId().toString())
                .settlementFinancialAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                .settlementDueDate(LocalDate.now())
                .settlementPaymentDate(LocalDate.now());
    }
}
