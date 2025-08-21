package com.raidiam.trustframework.mockinsurance.domain;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.Generated;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceHousingInsuredObjectCoverage;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "housing_policy_insured_object_coverages")
public class HousingPolicyInsuredObjectCoverageEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated()
    @Column(name = "housing_policy_insured_object_coverage_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID housingPolicyInsuredObjectCoverageId;

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
    private String LMIAmount;

    @Column(name = "lmi_unit_type")
    private String LMIUnitType;

    @Column(name = "lmi_sub_limit")
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

    @Column(name = "premium_periodicity")
    private String premiumPeriodicity;

    @Column(name = "housing_policy_insured_object_id")
    private UUID housingPolicyInsuredObjectId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_policy_insured_object_id", referencedColumnName = "housing_policy_insured_object_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private HousingPolicyInsuredObjectEntity housingPolicyInsuredObject;

    public InsuranceHousingInsuredObjectCoverage mapDto() {
        return new InsuranceHousingInsuredObjectCoverage()
            .branch(this.getBranch())
            .code(InsuranceHousingInsuredObjectCoverage.CodeEnum.valueOf(this.getCode()))
            .description(this.getDescription())
            .internalCode(this.getInternalCode())
            .susepProcessNumber(this.getSusepProcessNumber())
            .LMI(new AmountDetails()
                    .amount(this.getLMIAmount())
                    .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getLMIUnitType()))
            )
            .isLMISublimit(this.getIsLMISublimit())
            .termStartDate(this.getTermStartDate())
            .termEndDate(this.getTermEndDate())
            .isMainCoverage(this.getIsMainCoverage())
            .feature(InsuranceHousingInsuredObjectCoverage.FeatureEnum.valueOf(this.getFeature()))
            .type(InsuranceHousingInsuredObjectCoverage.TypeEnum.valueOf(this.getType()))
            .gracePeriod(this.getGracePeriod())
            .gracePeriodicity(InsuranceHousingInsuredObjectCoverage.GracePeriodicityEnum.valueOf(this.getGracePeriodicity()))
            .gracePeriodCountingMethod(InsuranceHousingInsuredObjectCoverage.GracePeriodCountingMethodEnum.valueOf(this.getGracePeriodCountingMethod()))
            .premiumPeriodicity(InsuranceHousingInsuredObjectCoverage.PremiumPeriodicityEnum.valueOf(this.getPremiumPeriodicity()));
    }
}