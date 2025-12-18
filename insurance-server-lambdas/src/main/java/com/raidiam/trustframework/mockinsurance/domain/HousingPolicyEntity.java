package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "housing_policies")
public class HousingPolicyEntity extends BaseEntity implements HasStatusInterface {
    @Id
    @GeneratedValue
    @Column(name = "housing_policy_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID housingPolicyId;

    @Column(name = "housing_id")
    private String housingId;

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

    @Column(name = "max_lmg_unit_code")
    private String maxLMGUnitCode;

    @Column(name = "max_lmg_unit_description")
    private String maxLMGUnitDescription;

    @Column(name = "proposal_id")
    private String proposalId;

    @ElementCollection
    @CollectionTable(name = "personal_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "housing_policy_id"))
    @Column(name = "personal_id")
    private List<UUID> insuredIds;

    @ElementCollection
    @CollectionTable(name = "beneficiary_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "housing_policy_id"))
    @Column(name = "beneficiary_id")
    private List<UUID> beneficiaryIds;

    @ElementCollection
    @CollectionTable(name = "intermediary_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "housing_policy_id"))
    @Column(name = "intermediary_id")
    private List<UUID> intermediaryIds;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "housingPolicy")
    private List<HousingPolicyInsuredObjectEntity> insuredObjects;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "housingPolicy")
    private List<HousingPolicyBranchInsuredObjectEntity> branchInsuredObjects;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "housingPolicy")
    private List<HousingPolicyBranchInsuredEntity> branchInsureds;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "housingPolicy")
    private List<HousingPolicyPremiumEntity> premiums = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "housingPolicy")
    private List<HousingPolicyClaimEntity> claims = new ArrayList<>();

    public BaseBrandAndCompanyDataPolicies mapPolicyDTO() {
        return new BaseBrandAndCompanyDataPolicies()
                .policyId(this.getHousingPolicyId().toString())
                .productName(this.getProductName());
    }

    public ResponseInsuranceHousingPolicyInfo mapPolicyInfoDTO() {
        return new ResponseInsuranceHousingPolicyInfo()
                .data(new InsuranceHousingPolicyInfoData()
                        .documentType(InsuranceHousingPolicyInfoData.DocumentTypeEnum.valueOf(this.getDocumentType()))
                        .policyId(this.getPolicyId())
                        .susepProcessNumber(this.getSusepProcessNumber())
                        .groupCertificateId(this.getGroupCertificateId())
                        .issuanceType(InsuranceHousingPolicyInfoData.IssuanceTypeEnum.valueOf(this.getIssuanceType()))
                        .issuanceDate(this.getIssuanceDate())
                        .termStartDate(this.getTermStartDate())
                        .termEndDate(this.getTermEndDate())
                        .leadInsurerCode(this.getLeadInsurerCode())
                        .leadInsurerPolicyId(this.getLeadInsurerPolicyId())
                        .maxLMG(new AmountDetails()
                                .amount(this.getMaxLMGAmount())
                                .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getMaxLMGUnitType()))
                                .unit(new AmountDetailsUnit()
                                        .code(this.getMaxLMGUnitCode())
                                        .description(AmountDetailsUnit.DescriptionEnum.valueOf(this.getMaxLMGUnitDescription()))
                                )
                        )
                        .proposalId(this.proposalId)
                        .insuredObjects(this.getInsuredObjects().stream().map(HousingPolicyInsuredObjectEntity::mapDto).toList())
                        .branchInfo(new InsuranceHousingSpecificPolicyInfo()
                                .insuredObjects(this.getBranchInsuredObjects().stream().map(HousingPolicyBranchInsuredObjectEntity::mapDto).toList())
                                .insureds(this.getBranchInsureds().stream().map(HousingPolicyBranchInsuredEntity::mapDto).toList())
                        )
                );
    }

    public ResponseInsuranceHousingPolicyInfoV2 mapPolicyInfoDTOV2() {
        return new ResponseInsuranceHousingPolicyInfoV2()
                .data(new InsuranceHousingPolicyInfoDataV2()
                        .documentType(InsuranceHousingPolicyInfoDataV2.DocumentTypeEnum.valueOf(this.getDocumentType()))
                        .policyId(this.getPolicyId())
                        .susepProcessNumber(this.getSusepProcessNumber())
                        .groupCertificateId(this.getGroupCertificateId())
                        .issuanceType(InsuranceHousingPolicyInfoDataV2.IssuanceTypeEnum.valueOf(this.getIssuanceType()))
                        .issuanceDate(this.getIssuanceDate())
                        .termStartDate(this.getTermStartDate())
                        .termEndDate(this.getTermEndDate())
                        .leadInsurerCode(this.getLeadInsurerCode())
                        .leadInsurerPolicyId(this.getLeadInsurerPolicyId())
                        .maxLMG(new AmountDetails()
                                .amount(this.getMaxLMGAmount())
                                .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getMaxLMGUnitType()))
                                .unit(new AmountDetailsUnit()
                                        .code(this.getMaxLMGUnitCode())
                                        .description(AmountDetailsUnit.DescriptionEnum.valueOf(this.getMaxLMGUnitDescription()))
                                )
                        )
                        .proposalId(this.proposalId)
                        .insuredObjects(this.getInsuredObjects().stream().map(HousingPolicyInsuredObjectEntity::mapDtoV2).toList())
                        .branchInfo(new InsuranceHousingSpecificPolicyInfoV2()
                                .insuredObjects(this.getBranchInsuredObjects().stream().map(HousingPolicyBranchInsuredObjectEntity::mapDtoV2).toList())
                                .insureds(this.getBranchInsureds().stream().map(HousingPolicyBranchInsuredEntity::mapDto).toList())
                        )
                );
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getHousingPolicyId().toString());
    }
}
