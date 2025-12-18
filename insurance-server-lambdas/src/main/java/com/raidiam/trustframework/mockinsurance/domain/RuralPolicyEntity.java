package com.raidiam.trustframework.mockinsurance.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails.UnitTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit.DescriptionEnum;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "rural_policies")
public class RuralPolicyEntity extends BaseEntity implements HasStatusInterface {

    @Id
    @GeneratedValue
    @Column(name = "rural_policy_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID ruralPolicyId;

    @Column(name = "insurance_id")
    private String insuranceId;

    @Column(name = "status")
    private String status;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "policy_id")
    private String policyId;

    @Column(name = "susep_process_number")
    private String susepProcessNumber;

    @Column(name = "group_certificate_id")
    private String groupCertificateId;

    @Column(name = "issuance_type")
    private String issuanceType;

    @Column(name = "issuance_date")
    private LocalDate issuanceDate;

    @Column(name = "term_start_date")
    private LocalDate termStartDate;

    @Column(name = "term_end_date")
    private LocalDate termEndDate;

    @Column(name = "lead_insurer_code")
    private String leadInsurerCode;

    @Column(name = "lead_insurer_policy_id")
    private String leadInsurerPolicyId;

    @Column(name = "max_lmg_amount")
    private String maxLMGAmount;

    @Column(name = "max_lmg_unit_type")
    private String maxLMGUnitType;

    @Column(name = "max_lmg_unit_type_others")
    private String maxLMGUnitTypeOthers;

    @Column(name = "max_lmg_unit_code")
    private String maxLMGUnitCode;

    @Column(name = "max_lmg_unit_description")
    private String maxLMGUnitDescription;

    @Column(name = "proposal_id")
    private String proposalId;

    @ElementCollection
    @CollectionTable(name = "personal_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "rural_policy_id"))
    @Column(name = "personal_id")
    private List<UUID> insuredIds;

    @ElementCollection
    @CollectionTable(name = "beneficiary_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "rural_policy_id"))
    @Column(name = "beneficiary_id")
    private List<UUID> beneficiaryIds;

    @ElementCollection
    @CollectionTable(name = "principal_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "rural_policy_id"))
    @Column(name = "principal_id")
    private List<UUID> principalIds;

    @ElementCollection
    @CollectionTable(name = "intermediary_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "rural_policy_id"))
    @Column(name = "intermediary_id")
    private List<UUID> intermediaryIds;
    
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruralPolicy")
    private List<RuralPolicyInsuredObjectEntity> insuredObjects;
    
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruralPolicy")
    private List<RuralPolicyCoverageEntity> coverages;

    @Column(name = "coinsurance_retained_percentage")
    private String coinsuranceRetainedPercentage;

    @ElementCollection
    @CollectionTable(name = "coinsurer_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "rural_policy_id"))
    @Column(name = "coinsurer_id")
    private List<UUID> coinsurerIds;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruralPolicy")
    private List<RuralPolicyBranchInsuredObjectEntity> branchInsuredObjects;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruralPolicy")
    private List<RuralPolicyClaimEntity> claims = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruralPolicy")
    private List<RuralPolicyPremiumEntity> premiums = new ArrayList<>();

    public BaseBrandAndCompanyDataPolicies mapPolicyDto() {
        return new BaseBrandAndCompanyDataPolicies()
                .policyId(this.getRuralPolicyId().toString())
                .productName("Mock Insurer Rural Policy");
    }

    public ResponseInsuranceRuralPolicyInfo mapPolicyInfoDto() {
        return new ResponseInsuranceRuralPolicyInfo()
            .data(new InsuranceRuralPolicyInfo() 
                .documentType(InsuranceRuralPolicyInfo.DocumentTypeEnum.valueOf(this.getDocumentType()))
                .policyId(this.getPolicyId().toString())
                .susepProcessNumber(this.getSusepProcessNumber())
                .groupCertificateId(this.getGroupCertificateId())
                .issuanceType(InsuranceRuralPolicyInfo.IssuanceTypeEnum.valueOf(this.getIssuanceType()))
                .issuanceDate(this.getIssuanceDate())
                .termStartDate(this.getTermStartDate())
                .termEndDate(this.getTermEndDate())
                .leadInsurerCode(this.getLeadInsurerCode())
                .leadInsurerPolicyId(this.getLeadInsurerPolicyId())
                .maxLMG(new AmountDetails()
                    .amount(this.getMaxLMGAmount())
                    .unitType(UnitTypeEnum.valueOf(this.getMaxLMGUnitType()))
                    .unitTypeOthers(this.getMaxLMGUnitTypeOthers())
                    .unit(new AmountDetailsUnit().code(this.getMaxLMGUnitCode()).description(DescriptionEnum.valueOf(this.getMaxLMGUnitDescription())))
                )
                .proposalId(this.getProposalId())
                .insuredObjects(this.getInsuredObjects().stream().map(RuralPolicyInsuredObjectEntity::mapDto).toList())
                .coverages(this.getCoverages().stream().map(RuralPolicyCoverageEntity::mapDto).toList())
                .coinsuranceRetainedPercentage(this.getCoinsuranceRetainedPercentage())
                .branchInfo(new InsuranceRuralSpecificPolicyInfo()
                    .insuredObjects(this.getBranchInsuredObjects().stream().map(RuralPolicyBranchInsuredObjectEntity::mapDto).toList())
                )
            );
    }

    public ResponseInsuranceRuralPolicyInfoV2 mapPolicyInfoDtoV2() {
        return new ResponseInsuranceRuralPolicyInfoV2()
            .data(new InsuranceRuralPolicyInfoV2() 
                .documentType(InsuranceRuralPolicyInfoV2.DocumentTypeEnum.valueOf(this.getDocumentType()))
                .policyId(this.getPolicyId().toString())
                .susepProcessNumber(this.getSusepProcessNumber())
                .groupCertificateId(this.getGroupCertificateId())
                .issuanceType(InsuranceRuralPolicyInfoV2.IssuanceTypeEnum.valueOf(this.getIssuanceType()))
                .issuanceDate(this.getIssuanceDate())
                .termStartDate(this.getTermStartDate())
                .termEndDate(this.getTermEndDate())
                .leadInsurerCode(this.getLeadInsurerCode())
                .leadInsurerPolicyId(this.getLeadInsurerPolicyId())
                .maxLMG(new AmountDetails()
                    .amount(this.getMaxLMGAmount())
                    .unitType(UnitTypeEnum.valueOf(this.getMaxLMGUnitType()))
                    .unitTypeOthers(this.getMaxLMGUnitTypeOthers())
                    .unit(new AmountDetailsUnit().code(this.getMaxLMGUnitCode()).description(DescriptionEnum.valueOf(this.getMaxLMGUnitDescription())))
                )
                .proposalId(this.getProposalId())
                .insuredObjects(this.getInsuredObjects().stream().map(RuralPolicyInsuredObjectEntity::mapDtoV2).toList())
                .coverages(this.getCoverages().stream().map(RuralPolicyCoverageEntity::mapDtoV2).toList())
                .coinsuranceRetainedPercentage(this.getCoinsuranceRetainedPercentage())
                .branchInfo(new InsuranceRuralSpecificPolicyInfoV2()
                    .insuredObjects(this.getBranchInsuredObjects().stream().map(RuralPolicyBranchInsuredObjectEntity::mapDtoV2).toList())
                )
            );
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getRuralPolicyId().toString());
    }
}
