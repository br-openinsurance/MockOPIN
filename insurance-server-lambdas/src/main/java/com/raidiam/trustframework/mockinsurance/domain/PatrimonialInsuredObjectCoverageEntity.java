package com.raidiam.trustframework.mockinsurance.domain;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePatrimonialCoverageCode;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePatrimonialInsuredObjectCoverage;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "patrimonial_insured_object_coverages")
public class PatrimonialInsuredObjectCoverageEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "patrimonial_insured_object_coverage_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID patrimonialInsuredObjectCoverageId;

    @Column(name = "patrimonial_insured_object_id")
    private UUID patrimonialInsuredObjectId;

    @Column(name = "branch")
    private String branch;

    @Column(name = "code")
    private String code;

    @Column(name = "susep_process_number")
    private String susepProcessNumber;

    @Column(name = "lmi_amount")
    private String lmiAmount;

    @Column(name = "lmi_unit_type")
    private String lmiUnitType;

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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patrimonial_insured_object_id", referencedColumnName = "patrimonial_insured_object_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PatrimonialInsuredObjectEntity patrimonialInsuredObject;

    public InsurancePatrimonialInsuredObjectCoverage mapDto() {
        return new InsurancePatrimonialInsuredObjectCoverage()
            .branch(this.getBranch())
            .code(InsurancePatrimonialCoverageCode.valueOf(this.getCode()))
            .susepProcessNumber(this.getSusepProcessNumber())
            .LMI(new AmountDetails()
                .amount(this.getLmiAmount())
                .currency(AmountDetails.CurrencyEnum.BRL)
            )
            .isLMISublimit(this.getIsLMISublimit())
            .termStartDate(this.getTermStartDate())
            .termEndDate(this.getTermEndDate())
            .isMainCoverage(this.getIsMainCoverage())
            .feature(InsurancePatrimonialInsuredObjectCoverage.FeatureEnum.valueOf(this.getFeature()))
            .type(InsurancePatrimonialInsuredObjectCoverage.TypeEnum.valueOf(this.getType()))
            .gracePeriod(this.getGracePeriod())
            .gracePeriodicity(InsurancePatrimonialInsuredObjectCoverage.GracePeriodicityEnum.valueOf(this.getGracePeriodicity()))
            .gracePeriodCountingMethod(InsurancePatrimonialInsuredObjectCoverage.GracePeriodCountingMethodEnum.fromValue(this.getGracePeriodCountingMethod()))
            .gracePeriodStartDate(this.getGracePeriodStartDate())
            .gracePeriodEndDate(this.getGracePeriodEndDate())
            .premiumPeriodicity(InsurancePatrimonialInsuredObjectCoverage.PremiumPeriodicityEnum.valueOf(this.getPremiumPeriodicity()));
    }
}
