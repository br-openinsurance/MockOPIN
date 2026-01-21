package com.raidiam.trustframework.mockinsurance.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails.UnitTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit.DescriptionEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralClaim;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralClaimV2;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralSpecificClaim;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralSpecificClaimV2;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralSpecificClaimV2SurveyAddress;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "rural_policy_claims")
public class RuralPolicyClaimEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "rural_policy_claim_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID ruralPolicyClaimId;

    @Column(name = "rural_policy_id")
    private UUID ruralPolicyId;

    @Column(name = "identification")
    private String identification;

    @Column(name = "documentation_delivery_date")
    private LocalDate documentationDeliveryDate;

    @Column(name = "status")
    private String status;

    @Column(name = "status_alteration_date")
    private LocalDate statusAlterationDate;

    @Column(name = "ocurrance_date")
    private LocalDate occurrenceDate;

    @Column(name = "warning_date")
    private LocalDate warningDate;

    @Column(name = "third_party_date")
    private LocalDate thirdPartyClaimDate;

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

    @Column(name = "denial_justification")
    private String denialJustification;

    @Column(name = "denial_justification_description")
    private String denialJustificationDescription;
        
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruralPolicyClaim")
    private List<RuralPolicyClaimCoverageEntity> coverages;

    @Column(name = "survey_date")
    private LocalDate surveyDate;

    @Column(name = "survey_address")
    private String surveyAddress;

    @Column(name = "survey_address_complementary_info")
    private String surveyAddressComplementaryInfo;

    @Column(name = "survey_country_sub_division")
    private String surveyCountrySubDivision;

    @Column(name = "survey_postcode")
    private String surveyPostCode;

    @Column(name = "survey_country_code")
    private String surveyCountryCode;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rural_policy_id", referencedColumnName = "rural_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private RuralPolicyEntity ruralPolicy;

    public InsuranceRuralClaim toResponse() {
        return new InsuranceRuralClaim()
            .identification(this.getIdentification())
            .documentationDeliveryDate(this.getDocumentationDeliveryDate())
            .status(InsuranceRuralClaim.StatusEnum.valueOf(this.getStatus()))
            .statusAlterationDate(this.getStatusAlterationDate())
            .occurrenceDate(this.getOccurrenceDate())
            .warningDate(this.getWarningDate())
            .thirdPartyClaimDate(this.getThirdPartyClaimDate())
            .amount(new AmountDetails()
                .amount(this.getAmount())
                .unitType(UnitTypeEnum.valueOf(this.getUnitType()))
                .unitTypeOthers(this.getUnitTypeOthers())
                .unit(new AmountDetailsUnit().code(this.getUnitCode()).description(DescriptionEnum.valueOf(this.getUnitDescription())))
            )
            .denialJustification(InsuranceRuralClaim.DenialJustificationEnum.valueOf(this.getDenialJustification()))
            .denialJustificationDescription(this.getDenialJustificationDescription())
            .coverages(this.getCoverages().stream().map(RuralPolicyClaimCoverageEntity::mapDto).toList())
            .branchInfo(new InsuranceRuralSpecificClaim()
                .surveyDate(this.getSurveyDate())
                .surveyAddress(this.getSurveyAddress())
                .surveyCountrySubDivision(InsuranceRuralSpecificClaim.SurveyCountrySubDivisionEnum.valueOf(this.getSurveyCountrySubDivision()))
                .surveyPostCode(this.getSurveyPostCode())
                .surveyCountryCode(InsuranceRuralSpecificClaim.SurveyCountryCodeEnum.valueOf(this.getSurveyCountryCode()))
            );
    }

    public InsuranceRuralClaimV2 toResponseV2() {
        return new InsuranceRuralClaimV2()
            .identification(this.getIdentification())
            .documentationDeliveryDate(this.getDocumentationDeliveryDate())
            .status(InsuranceRuralClaimV2.StatusEnum.valueOf(this.getStatus()))
            .statusAlterationDate(this.getStatusAlterationDate())
            .occurrenceDate(this.getOccurrenceDate())
            .warningDate(this.getWarningDate())
            .thirdPartyClaimDate(this.getThirdPartyClaimDate())
            .amount(new AmountDetails()
                .amount(this.getAmount())
                .unitType(UnitTypeEnum.valueOf(this.getUnitType()))
                .unitTypeOthers(this.getUnitTypeOthers())
                .unit(new AmountDetailsUnit().code(this.getUnitCode()).description(DescriptionEnum.valueOf(this.getUnitDescription())))
            )
            .denialJustification(InsuranceRuralClaimV2.DenialJustificationEnum.valueOf(this.getDenialJustification()))
            .denialJustificationDescription(this.getDenialJustificationDescription())
            .coverages(this.getCoverages().stream().map(RuralPolicyClaimCoverageEntity::mapDto).toList())
            .branchInfo(new InsuranceRuralSpecificClaimV2()
                .surveyDate(this.getSurveyDate())
                .surveyAddress(new InsuranceRuralSpecificClaimV2SurveyAddress()
                    .name(this.getSurveyAddress().split(" ", 2)[1].split(",")[0])
                    .number(this.getSurveyAddress().split(" ", 2)[1].split(",")[1].trim())
                    .type(InsuranceRuralSpecificClaimV2SurveyAddress.TypeEnum.valueOf(this.getSurveyAddress().split(" ")[0].toUpperCase()))
                    .addressComplementaryInfo(this.getSurveyAddressComplementaryInfo())
                )
                .surveyCountrySubDivision(InsuranceRuralSpecificClaimV2.SurveyCountrySubDivisionEnum.valueOf(this.getSurveyCountrySubDivision()))
                .surveyPostCode(this.getSurveyPostCode())
                .surveyCountryCode(InsuranceRuralSpecificClaimV2.SurveyCountryCodeEnum.valueOf(this.getSurveyCountryCode()))
            );
    }
}
