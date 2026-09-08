package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
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
@Table(name = "pension_plan_contract_document_coverages")
public class PensionPlanContractDocumentCoverageEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "pension_plan_contract_document_coverage_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID coverageId;

    @Column(name = "pension_plan_contract_document_id")
    private UUID pensionPlanContractDocumentId;

    @Column(name = "coverage_code")
    private String coverageCode;

    @Column(name = "susep_process_number")
    private String susepProcessNumber;

    @Column(name = "structure_modality")
    private String structureModality;

    @Column(name = "benefit_amount")
    private String benefitAmount;

    @Column(name = "benefit_amount_unit_type")
    private String benefitAmountUnitType;

    @Column(name = "periodicity")
    private String periodicity;

    @Column(name = "coverage_name")
    private String coverageName;

    @Column(name = "locked_plan")
    private Boolean lockedPlan;

    @Column(name = "term_start_date")
    private LocalDate termStartDate;

    @Column(name = "term_end_date")
    private LocalDate termEndDate;

    @Column(name = "financial_regime")
    private String financialRegime;

    @Column(name = "pricing_method")
    private String pricingMethod;

    @Column(name = "update_index")
    private String updateIndex;

    @Column(name = "update_index_lagging")
    private Integer updateIndexLagging;

    @Column(name = "contribution_amount")
    private String contributionAmount;

    @Column(name = "contribution_amount_unit_type")
    private String contributionAmountUnitType;

    @Column(name = "benefit_payment_amount")
    private String benefitPaymentAmount;

    @Column(name = "benefit_payment_amount_unit_type")
    private String benefitPaymentAmountUnitType;

    @Column(name = "benefit_payment_method")
    private String benefitPaymentMethod;

    @Column(name = "charged_amount")
    private String chargedAmount;

    @Column(name = "charged_amount_unit_type")
    private String chargedAmountUnitType;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pension_plan_contract_document_id", referencedColumnName = "pension_plan_contract_document_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PensionPlanContractDocumentEntity pensionPlanContractDocument;

    public InsurancePensionPlanDocumentsPlansCoverage getDTO() {
        return new InsurancePensionPlanDocumentsPlansCoverage()
                .coverageCode(this.getCoverageCode())
                .susepProcessNumber(this.getSusepProcessNumber())
                .structureModality(InsurancePensionPlanDocumentsPlansCoverage.StructureModalityEnum.fromValue(this.getStructureModality()))
                .benefitAmount(new AmountDetails().amount(this.getBenefitAmount()).unitType(AmountDetails.UnitTypeEnum.fromValue(this.getBenefitAmountUnitType())))
                .periodicity(InsurancePensionPlanDocumentsPlansCoverage.PeriodicityEnum.fromValue(this.getPeriodicity()))
                .coverageName(this.getCoverageName())
                .lockedPlan(this.getLockedPlan())
                .termStartDate(this.getTermStartDate())
                .termEndDate(this.getTermEndDate())
                .financialRegime(InsurancePensionPlanDocumentsPlansCoverage.FinancialRegimeEnum.fromValue(this.getFinancialRegime()))
                .pricingMethod(InsurancePensionPlanDocumentsPlansCoverage.PricingMethodEnum.fromValue(this.getPricingMethod()))
                .updateIndex(InsurancePensionPlanDocumentsPlansCoverage.UpdateIndexEnum.fromValue(this.getUpdateIndex()))
                .updateIndexLagging(this.getUpdateIndexLagging())
                .contributionAmount(new AmountDetails().amount(this.getContributionAmount()).unitType(AmountDetails.UnitTypeEnum.fromValue(this.getContributionAmountUnitType())))
                .benefitPaymentAmount(new AmountDetails().amount(this.getBenefitPaymentAmount()).unitType(AmountDetails.UnitTypeEnum.fromValue(this.getBenefitPaymentAmountUnitType())))
                .benefitPaymentMethod(InsurancePensionPlanDocumentsPlansCoverage.BenefitPaymentMethodEnum.fromValue(this.getBenefitPaymentMethod()))
                .chargedAmount(new AmountDetails().amount(this.getChargedAmount()).unitType(AmountDetails.UnitTypeEnum.fromValue(this.getChargedAmountUnitType())));
    }

    public InsurancePensionPlanDocumentsPlansCoverageV2 getDTOV2() {
        return new InsurancePensionPlanDocumentsPlansCoverageV2()
                .coverageCode(this.getCoverageCode())
                .susepProcessNumber(this.getSusepProcessNumber())
                .structureModality(InsurancePensionPlanDocumentsPlansCoverageV2.StructureModalityEnum.fromValue(this.getStructureModality()))
                .benefitAmount(new AmountDetails().amount(this.getBenefitAmount()).unitType(AmountDetails.UnitTypeEnum.fromValue(this.getBenefitAmountUnitType())))
                .periodicity(InsurancePensionPlanDocumentsPlansCoverageV2.PeriodicityEnum.fromValue(this.getPeriodicity()))
                .coverageName(this.getCoverageName())
                .lockedPlan(this.getLockedPlan())
                .termStartDate(this.getTermStartDate())
                .termEndDate(this.getTermEndDate())
                .financialRegime(InsurancePensionPlanDocumentsPlansCoverageV2.FinancialRegimeEnum.fromValue(this.getFinancialRegime()))
                .pricingMethod(InsurancePensionPlanDocumentsPlansCoverageV2.PricingMethodEnum.fromValue(this.getPricingMethod()))
                .updateIndex(InsurancePensionPlanDocumentsPlansCoverageV2.UpdateIndexEnum.fromValue(this.getUpdateIndex()))
                .updateIndexLagging(this.getUpdateIndexLagging())
                .contributionAmount(new AmountDetails().amount(this.getContributionAmount()).unitType(AmountDetails.UnitTypeEnum.fromValue(this.getContributionAmountUnitType())))
                .benefitPaymentAmount(new AmountDetails().amount(this.getBenefitPaymentAmount()).unitType(AmountDetails.UnitTypeEnum.fromValue(this.getBenefitPaymentAmountUnitType())))
                .benefitPaymentMethod(InsurancePensionPlanDocumentsPlansCoverageV2.BenefitPaymentMethodEnum.fromValue(this.getBenefitPaymentMethod()))
                .chargedAmount(new AmountDetails().amount(this.getChargedAmount()).unitType(AmountDetails.UnitTypeEnum.fromValue(this.getChargedAmountUnitType())));
    }
}
