package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.MovementContributions;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "pension_plan_contract_movement_contributions")
public class PensionPlanContractMovementContributionEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "pension_plan_contract_movement_contribution_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID contributionId;

    @Column(name = "pension_plan_contract_id")
    private String pensionPlanContractId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pension_plan_contract_id", referencedColumnName = "pension_plan_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PensionPlanContractEntity pensionPlanContract;

    public MovementContributions mapDTO() {
        return new MovementContributions()
                .contributionAmount(new AmountDetails()
                        .amount("95.90")
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                )
                .contributionExpirationDate(LocalDate.of(2022, 5, 1))
                .chargedInAdvanceAmount(new AmountDetails()
                        .amount("95.90")
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                .periodicity(MovementContributions.PeriodicityEnum.MENSAL)
                .contributionPaymentDate(LocalDate.of(2022, 5, 1));
    }
}
