package com.raidiam.trustframework.mockinsurance.domain;


import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails.UnitTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit.DescriptionEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralSpecificInsuredObject;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralSpecificInsuredObject.AnimalTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralSpecificInsuredObject.CoveredAnimalDestinationEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralSpecificInsuredObject.FlockCodeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralSpecificInsuredObject.ForestCodeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralSpecificInsuredObject.ModelTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralSpecificInsuredObject.SurveyCountryCodeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralSpecificInsuredObject.SurveyCountrySubDivisionEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralSpecificInsuredObject.SurveyorIdTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralSpecificInsuredObject.UnitMeasureEnum;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "rural_policy_branch_insured_objects")
public class RuralPolicyBranchInsuredObjectEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "rural_policy_branch_insured_object_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID branchInsuredObjectId;

    @Column(name = "rural_policy_id")
    private UUID ruralPolicyId;

    @Column(name = "identification")
    private String identification;

    @Column(name = "fesr_participant")
    private Boolean isFESRParticipant;

    @Column(name = "amount")
    private String amount;

    @Column(name = "unit_type")
    private String unitType;

    @Column(name = "unit_type_others")
    private String unitTypeOthers;

    @Column(name = "unit_code")
    private String unitCode;

    @Column(name = "unit_description")
    private String unitDescription;

    @Column(name = "subvention_type")
    private String subventionType;

    @Column(name = "safe_area")
    private String safeArea;

    @Column(name = "unit_measure")
    private String unitMeasure;

    @Column(name = "unit_measure_others")
    private String unitMeasureOthers;

    @Column(name = "culture_code")
    private String cultureCode;

    @Column(name = "flock_code")
    private String flockCode;

    @Column(name = "flock_code_others")
    private String flockCodeOthers;

    @Column(name = "forest_code")
    private String forestCode;

    @Column(name = "forest_code_others")
    private String forestCodeOthers;

    @Column(name = "survey_date")
    private LocalDate surveyDate;

    @Column(name = "survey_address")
    private String surveyAddress;

    @Column(name = "survey_country_sub_division")
    private String surveyCountrySubDivision;

    @Column(name = "survey_postcode")
    private String surveyPostCode;

    @Column(name = "survey_country_code")
    private String surveyCountryCode;

    @Column(name = "surveyor_id_type")
    private String surveyorIdType;

    @Column(name = "surveyor_id_others")
    private String surveyorIdOthers;

    @Column(name = "surveyor_id")
    private String surveyorId;

    @Column(name = "surveyor_name")
    private String surveyorName;

    @Column(name = "model_type")
    private String modelType;

    @Column(name = "model_type_others")
    private String modelTypeOthers;

    @Column(name = "assets_covered")
    private Boolean areAssetsCovered;

    @Column(name = "covered_animal_destination")
    private String coveredAnimalDestination;

    @Column(name = "animal_type")
    private String animalType;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rural_policy_id", referencedColumnName = "rural_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private RuralPolicyEntity ruralPolicy;

    public InsuranceRuralSpecificInsuredObject mapDto() {
        return new InsuranceRuralSpecificInsuredObject()
            .identification(this.getIdentification())
            .isFESRParticipant(this.getIsFESRParticipant())
            .subventionAmount(new AmountDetails()
                .amount(this.getAmount())
                .unitType(UnitTypeEnum.valueOf(this.getUnitType()))
                .unitTypeOthers(this.getUnitTypeOthers())
                .unit(new AmountDetailsUnit().code(this.getUnitCode()).description(DescriptionEnum.valueOf(this.getUnitDescription())))
            )
            .subventionType(InsuranceRuralSpecificInsuredObject.SubventionTypeEnum.valueOf(this.getSubventionType()))
            .safeArea(this.getSafeArea())
            .unitMeasure(UnitMeasureEnum.valueOf(this.getUnitMeasure()))
            .unitMeasureOthers(this.getUnitMeasureOthers())
            .cultureCode(this.getCultureCode())
            .flockCode(FlockCodeEnum.valueOf(this.getFlockCode()))
            .flockCodeOthers(this.getFlockCodeOthers())
            .forestCode(ForestCodeEnum.valueOf(this.getForestCode()))
            .forestCodeOthers(this.getForestCodeOthers())
            .surveyDate(this.getSurveyDate())
            .surveyAddress(this.getSurveyAddress())
            .surveyCountrySubDivision(SurveyCountrySubDivisionEnum.valueOf(this.getSurveyCountrySubDivision()))
            .surveyPostCode(this.getSurveyPostCode())
            .surveyCountryCode(SurveyCountryCodeEnum.valueOf(this.getSurveyCountryCode()))
            .surveyorId(this.getSurveyorId())
            .surveyorIdOthers(this.getSurveyorIdOthers())
            .surveyorIdType(SurveyorIdTypeEnum.valueOf(this.getSurveyorIdType()))
            .surveyorIdOthers(this.getSurveyorIdOthers())
            .surveyorId(this.getSurveyorId())
            .surveyorName(this.getSurveyorName())
            .modelType(ModelTypeEnum.valueOf(this.getModelType()))
            .modelTypeOthers(this.getModelTypeOthers())
            .areAssetsCovered(this.getAreAssetsCovered())
            .coveredAnimalDestination(CoveredAnimalDestinationEnum.valueOf(this.getCoveredAnimalDestination()))
            .animalType(AnimalTypeEnum.valueOf(this.getAnimalType()));
    }
}
