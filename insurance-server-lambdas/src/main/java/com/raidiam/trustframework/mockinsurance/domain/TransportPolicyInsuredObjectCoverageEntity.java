package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceTransportInsuredObjectCoverage;
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

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "transport_policy_insured_object_coverages")
public class TransportPolicyInsuredObjectCoverageEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "transport_policy_insured_object_coverage_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID insuredObjectCoverageId;

    @Column(name = "transport_policy_insured_object_id")
    private UUID transportInsuredObjectId;

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

    @Column(name = "lmi_currency")
    private String lmiCurrency;

    @Column(name = "is_lmi_sublimit")
    private Boolean isLMISublimit;

    @Column(name = "term_start_date")
    private LocalDate termStartDate;

    @Column(name = "term_end_date")
    private LocalDate termEndDate;

    @Column(name = "is_main_coverage")
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
    @JoinColumn(name = "transport_policy_insured_object_id", referencedColumnName = "transport_policy_insured_object_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private TransportPolicyInsuredObjectEntity transportInsuredObject;

    public InsuranceTransportInsuredObjectCoverage mapDto() {
        return new InsuranceTransportInsuredObjectCoverage()
                .branch(this.getBranch())
                .code(InsuranceTransportInsuredObjectCoverage.CodeEnum.fromValue(this.getCode()))
                .description(this.getDescription())
                .internalCode(this.getInternalCode())
                .susepProcessNumber(this.getSusepProcessNumber())
                .LMI(new AmountDetails()
                        .amount(this.getLmiAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getLmiUnitType()))
                        .unitTypeOthers(this.getLmiUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getLmiUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getLmiUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getLmiCurrency())))
                .isLMISublimit(this.getIsLMISublimit())
                .termStartDate(this.getTermStartDate())
                .termEndDate(this.getTermEndDate())
                .isMainCoverage(this.getIsMainCoverage())
                .feature(InsuranceTransportInsuredObjectCoverage.FeatureEnum.fromValue(this.getFeature()))
                .type(InsuranceTransportInsuredObjectCoverage.TypeEnum.fromValue(this.getType()))
                .gracePeriod(this.getGracePeriod())
                .gracePeriodicity(InsuranceTransportInsuredObjectCoverage.GracePeriodicityEnum.fromValue(this.getGracePeriodicity()))
                .gracePeriodCountingMethod(InsuranceTransportInsuredObjectCoverage.GracePeriodCountingMethodEnum.fromValue(this.getGracePeriodCountingMethod()))
                .gracePeriodStartDate(this.getGracePeriodStartDate())
                .gracePeriodEndDate(this.getGracePeriodEndDate())
                .premiumPeriodicity(InsuranceTransportInsuredObjectCoverage.PremiumPeriodicityEnum.fromValue(this.getPremiumPeriodicity()))
                .premiumPeriodicityOthers(this.getPremiumPeriodicityOthers());
    }
}
