package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePersonInsuredObjectCoverage;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePersonInsuredObjectCoverageV2;

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
@Table(name = "person_insured_object_coverages")
public class PersonPolicyInsuredObjectCoverageEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "person_insured_object_coverage_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID personInsuredObjectCoverageId;

    @Column(name = "person_insured_object_id")
    private UUID personInsuredObjectId;

    @Column(name = "type")
    private String type;

    @Column(name = "feature")
    private String feature;

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

    @Column(name = "lmi_sublimit")
    private Boolean isLMISublimit;

    @Column(name = "term_start_date")
    private LocalDate termStartDate;

    @Column(name = "term_end_date")
    private LocalDate termEndDate;

    @Column(name = "main_coverage")
    private Boolean isMainCoverage;

    @Column(name = "trigger_event")
    private String triggerEvent;

    @Column(name = "financial_type")
    private String financialType;

    @Column(name = "benefit_payment_modality")
    private String benefitPaymentModality;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_insured_object_id", referencedColumnName = "person_insured_object_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PersonPolicyInsuredObjectEntity personInsuredObject;

    public InsurancePersonInsuredObjectCoverage mapDTO() {
        return new InsurancePersonInsuredObjectCoverage()
                .type(InsurancePersonInsuredObjectCoverage.TypeEnum.valueOf(this.getType()))
                .feature(InsurancePersonInsuredObjectCoverage.FeatureEnum.valueOf(this.getFeature()))
                .branch(this.getBranch())
                .code(InsurancePersonInsuredObjectCoverage.CodeEnum.valueOf(this.getCode()))
                .description(this.getDescription())
                .internalCode(this.getInternalCode())
                .susepProcessNumber(this.getSusepProcessNumber())
                .LMI(new AmountDetails()
                        .amount(this.getLmiAmount())
                        .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getLmiUnitType()))
                )
                .isLMISublimit(this.getIsLMISublimit())
                .termStartDate(this.getTermStartDate())
                .termEndDate(this.getTermEndDate())
                .isMainCoverage(this.getIsMainCoverage())
                .triggerEvent(InsurancePersonInsuredObjectCoverage.TriggerEventEnum.valueOf(this.getTriggerEvent()))
                .financialType(InsurancePersonInsuredObjectCoverage.FinancialTypeEnum.valueOf(this.getFinancialType()))
                .benefitPaymentModality(InsurancePersonInsuredObjectCoverage.BenefitPaymentModalityEnum.valueOf(this.getBenefitPaymentModality()));
    }

    public InsurancePersonInsuredObjectCoverageV2 mapDTOV2() {
        return new InsurancePersonInsuredObjectCoverageV2()
                .type(InsurancePersonInsuredObjectCoverageV2.TypeEnum.valueOf(this.getType()))
                .feature(InsurancePersonInsuredObjectCoverageV2.FeatureEnum.valueOf(this.getFeature()))
                .branch(this.getBranch())
                .code(InsurancePersonInsuredObjectCoverageV2.CodeEnum.valueOf(this.getCode()))
                .description(this.getDescription())
                .internalCode(this.getInternalCode())
                .susepProcessNumber(this.getSusepProcessNumber())
                .LMI(new AmountDetails()
                        .amount(this.getLmiAmount())
                        .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getLmiUnitType()))
                )
                .isLMISublimit(this.getIsLMISublimit())
                .termStartDate(this.getTermStartDate())
                .termEndDate(this.getTermEndDate())
                .isMainCoverage(this.getIsMainCoverage())
                .triggerEvent(InsurancePersonInsuredObjectCoverageV2.TriggerEventEnum.valueOf(this.getTriggerEvent()))
                .financialType(InsurancePersonInsuredObjectCoverageV2.FinancialTypeEnum.valueOf(this.getFinancialType()))
                .benefitPaymentModality(InsurancePersonInsuredObjectCoverageV2.BenefitPaymentModalityEnum.valueOf(this.getBenefitPaymentModality()));
    }
}
