package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "financial_risk_policies")
public class FinancialRiskPolicyEntity extends BaseEntity implements HasStatusInterface {
    @Id
    @GeneratedValue
    @Column(name = "financial_risk_policy_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID financialRiskPolicyId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "financial_risk_id")
    private String financialRiskId;

    @Column(name = "status")
    private String status;

    @Column(name = "document_type")
    private String documentType;

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

    @Column(name = "max_lmg_currency")
    private String maxLMGCurrency;

    @Column(name = "proposal_id")
    private String proposalId;

    @ElementCollection
    @CollectionTable(name = "personal_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "financial_risk_policy_id"))
    @Column(name = "personal_id")
    private List<UUID> insuredIds;

    @ElementCollection
    @CollectionTable(name = "beneficiary_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "financial_risk_policy_id"))
    @Column(name = "beneficiary_id")
    private List<UUID> beneficiaryIds;

    @ElementCollection
    @CollectionTable(name = "principal_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "financial_risk_policy_id"))
    @Column(name = "principal_id")
    private List<UUID> principalIds;

    @ElementCollection
    @CollectionTable(name = "intermediary_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "financial_risk_policy_id"))
    @Column(name = "intermediary_id")
    private List<UUID> intermediaryIds;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "financialRiskPolicy")
    private List<FinancialRiskPolicyInsuredObjectEntity> insuredObjects = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "financialRiskPolicy")
    private List<FinancialRiskPolicyCoverageEntity> coverages = new ArrayList<>();

    @Column(name = "coinsurance_retained_percentage")
    private String coinsuranceRetainedPercentage;

    @ElementCollection
    @CollectionTable(name = "coinsurer_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "financial_risk_policy_id"))
    @Column(name = "coinsurer_id")
    private List<UUID> coinsurerIds;

    @Column(name = "identification")
    private String branchInfoIdentification;

    @Column(name = "user_group")
    private String branchInfoUserGroup;

    @Column(name = "technical_surplus")
    private String branchInfoTechnicalSurplus;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "financialRiskPolicy")
    private List<FinancialRiskPolicyPremiumEntity> premiums = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "financialRiskPolicy")
    private List<FinancialRiskPolicyClaimEntity> claims = new ArrayList<>();

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    public BaseBrandAndCompanyDataPolicies mapPolicyDTO() {
        return new BaseBrandAndCompanyDataPolicies()
                .policyId(this.getFinancialRiskPolicyId().toString())
                .productName(this.getProductName());
    }

    public ResponseInsuranceFinancialRiskPolicyInfo mapPolicyInfoDTO() {
        return new ResponseInsuranceFinancialRiskPolicyInfo()
                .data(new InsuranceFinancialRiskPolicyInfoData()
                        .documentType(InsuranceFinancialRiskPolicyInfoData.DocumentTypeEnum.fromValue(this.getDocumentType()))
                        .policyId(this.getFinancialRiskPolicyId().toString())
                        .susepProcessNumber(this.getSusepProcessNumber())
                        .groupCertificateId(this.getGroupCertificateId())
                        .issuanceType(InsuranceFinancialRiskPolicyInfoData.IssuanceTypeEnum.fromValue(this.getIssuanceType()))
                        .issuanceDate(this.getIssuanceDate())
                        .termStartDate(this.getTermStartDate())
                        .termEndDate(this.getTermEndDate())
                        .leadInsurerCode(this.getLeadInsurerCode())
                        .leadInsurerPolicyId(this.getLeadInsurerPolicyId())
                        .maxLMG(new AmountDetails()
                                .amount(this.getMaxLMGAmount())
                                .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getMaxLMGUnitType()))
                                .unitTypeOthers(this.getMaxLMGUnitTypeOthers())
                                .unit(new AmountDetailsUnit()
                                        .code(this.getMaxLMGUnitCode())
                                        .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getMaxLMGUnitDescription())))
                                .currency(AmountDetails.CurrencyEnum.fromValue(this.getMaxLMGCurrency())))
                        .proposalId(this.getProposalId())
                        .insuredObjects(this.getInsuredObjects().stream().map(FinancialRiskPolicyInsuredObjectEntity::mapDTO).toList())
                        .branchInfo(new InsuranceStopLossSpecificPolicyInfo()
                                .identification(this.getBranchInfoIdentification())
                                .userGroup(this.getBranchInfoUserGroup())
                                .technicalSurplus(this.getBranchInfoTechnicalSurplus()))
                );
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getFinancialRiskPolicyId().toString());
    }
}
