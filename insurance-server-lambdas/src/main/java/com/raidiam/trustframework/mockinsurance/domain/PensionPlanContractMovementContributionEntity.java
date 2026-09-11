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

    @Column(name = "contribution_amount")
    private String contributionAmount;

    @Column(name = "contribution_unit_type")
    private String contributionUnitType;

    @Column(name = "contribution_unit_type_others")
    private String contributionUnitTypeOthers;

    @Column(name = "contribution_unit_code")
    private String contributionUnitCode;

    @Column(name = "contribution_unit_description")
    private String contributionUnitDescription;

    @Column(name = "contribution_currency")
    private String contributionCurrency;

    @Column(name = "charged_in_advance_amount")
    private String chargedInAdvanceAmount;

    @Column(name = "charged_in_advance_unit_type")
    private String chargedInAdvanceUnitType;

    @Column(name = "charged_in_advance_unit_type_others")
    private String chargedInAdvanceUnitTypeOthers;

    @Column(name = "charged_in_advance_unit_code")
    private String chargedInAdvanceUnitCode;

    @Column(name = "charged_in_advance_unit_description")
    private String chargedInAdvanceUnitDescription;

    @Column(name = "charged_in_advance_currency")
    private String chargedInAdvanceCurrency;

    @Column(name = "periodicity")
    private String periodicity;

    @Column(name = "contribution_expiration_date")
    private LocalDate contributionExpirationDate;

    @Column(name = "contribution_payment_date")
    private LocalDate contributionPaymentDate;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pension_plan_contract_id", referencedColumnName = "pension_plan_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PensionPlanContractEntity pensionPlanContract;

    public MovementContributions mapDTO() {
        return new MovementContributions()
                .contributionAmount(new AmountDetails()
                        .amount(this.getContributionAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getContributionUnitType()))
                )
                .contributionExpirationDate(this.getContributionExpirationDate())
                .chargedInAdvanceAmount(new AmountDetails()
                        .amount(this.getChargedInAdvanceAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getChargedInAdvanceUnitType())))
                .periodicity(MovementContributions.PeriodicityEnum.fromValue(this.getPeriodicity()))
                .contributionPaymentDate(this.getContributionPaymentDate());
    }
}
