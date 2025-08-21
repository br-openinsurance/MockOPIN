package com.raidiam.trustframework.mockinsurance.domain;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails.UnitTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit.DescriptionEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralInsuredObjectCoverage;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "rural_policy_insured_object_coverages")
public class RuralPolicyInsuredObjectCoverageEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "rural_policy_insured_object_coverage_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID ruralPolicyInsuredObjectCoverageId;

    @Column(name = "rural_policy_insured_object_id")
    private UUID ruralPolicyInsuredObjectId;

    @Column(name = "branch")
    private String branch;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "internal_code")
    private String internalCode;

    @Column(name = "susep_process_number")
    private String susepProcessNumber;

    @Column(name = "lmi_amount")
    private String lmiAmount;

    @Column(name = "lmi_unit_type")
    private String lmiUnitType;

    @Column(name = "lmi_unit_type_others")
    private String lmiUnitTypeOthers;

    @Column(name = "lmi_unit_code")
    private String lmiUnitCode;

    @Column(name = "lmi_unit_description")
    private String lmiUnitDescription;

    @Column(name = "lmi_sublimit")
    private Boolean isLMISublimit;
    
    @Column(name = "term_start_date")
    private LocalDate termStartDate;
    
    @Column(name = "term_end_date")
    private LocalDate termEndDate;

    @Column(name = "main_coverage")
    private Boolean isMainCoverage;

    @Column(name = "feature")
    private String feature;

    @Column(name = "type")
    private String type;

    @Column(name = "grace_period")
    private Integer gracePeriod;

    @Column(name = "grace_periodicity")
    private String gracePeriodicity;

    @Column(name = "grace_period_counting_method")
    private String gracePeriodCountingMethod;

    @Column(name = "grace_period_start_date")
    private LocalDate gracePeriodStartDate;

    @Column(name = "grace_period_end_date")
    private LocalDate gracePeriodEndDate;

    @Column(name = "premium_periodicity")
    private String premiumPeriodicity;

    @Column(name = "premium_periodicity_others")
    private String premiumPeriodicityOthers;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rural_policy_insured_object_id", referencedColumnName = "rural_policy_insured_object_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private RuralPolicyInsuredObjectEntity ruralPolicyInsuredObject;

    public InsuranceRuralInsuredObjectCoverage mapDto() {
        return new InsuranceRuralInsuredObjectCoverage()
            .branch(this.getBranch())
            .code(InsuranceRuralInsuredObjectCoverage.CodeEnum.valueOf(this.getCode()))
            .description(this.getDescription())
            .internalCode(this.getInternalCode())
            .susepProcessNumber(this.getSusepProcessNumber())
            .LMI(new AmountDetails()
                .amount(this.getLmiAmount())
                .unitType(UnitTypeEnum.valueOf(this.getLmiUnitType()))
                .unitTypeOthers(this.getLmiUnitTypeOthers())
                .unit(new AmountDetailsUnit().code(this.getLmiUnitCode()).description(DescriptionEnum.valueOf(this.getLmiUnitDescription())))
            )
            .isLMISublimit(this.getIsLMISublimit())
            .termStartDate(this.getTermStartDate())
            .termEndDate(this.getTermEndDate())
            .isMainCoverage(this.getIsMainCoverage())
            .feature(InsuranceRuralInsuredObjectCoverage.FeatureEnum.valueOf(this.getFeature()))
            .type(InsuranceRuralInsuredObjectCoverage.TypeEnum.valueOf(this.getType()))
            .gracePeriod(this.getGracePeriod())
            .gracePeriodicity(InsuranceRuralInsuredObjectCoverage.GracePeriodicityEnum.valueOf(this.getGracePeriodicity()))
            .gracePeriodCountingMethod(InsuranceRuralInsuredObjectCoverage.GracePeriodCountingMethodEnum.valueOf(this.getGracePeriodCountingMethod()))
            .gracePeriodStartDate(this.getGracePeriodStartDate())
            .gracePeriodEndDate(this.getGracePeriodEndDate())
            .premiumPeriodicity(InsuranceRuralInsuredObjectCoverage.PremiumPeriodicityEnum.valueOf(this.getPremiumPeriodicity()))
            .premiumPeriodicityOthers(this.getPremiumPeriodicityOthers());
    }
}
