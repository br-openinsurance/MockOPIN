package com.raidiam.trustframework.mockinsurance.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails.CurrencyEnum;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "patrimonial_policies")
public class PatrimonialPolicyEntity extends BaseEntity implements HasStatusInterface {

    @Id
    @GeneratedValue
    @Column(name = "policy_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID policyId;

    @Column(name = "insurance_id")
    private String insuranceId;

    @Column(name = "status")
    private String status;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @Column(name = "branch")
    private String branch;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "issuance_type")
    private String issuanceType;

    @Column(name = "issuance_date")
    private LocalDate issuanceDate;

    @Column(name = "term_start_date")
    private LocalDate termStartDate;

    @Column(name = "term_end_date")
    private LocalDate termEndDate;

    @Column(name = "proposal_id")
    private String proposalId;

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

    @ElementCollection
    @CollectionTable(name = "personal_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "policy_id"))
    @Column(name = "personal_id")
    private List<UUID> insuredIds;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "policy")
    private List<PatrimonialClaimEntity> claims = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "patrimonialPolicy")
    private List<PatrimonialInsuredObjectEntity> insuredObjects = new ArrayList<>();

    public BaseBrandAndCompanyDataPolicies mapPolicyDto() {
        return new BaseBrandAndCompanyDataPolicies()
                .policyId(this.getPolicyId().toString())
                .productName("Mock Insurer Patrimonial Policy");
    }

    public ResponseInsurancePatrimonialPolicyInfo mapPolicyInfoDto() {
        return new ResponseInsurancePatrimonialPolicyInfo()
            .data(new InsurancePatrimonialPolicyInfo()
                .documentType(InsurancePatrimonialPolicyInfo.DocumentTypeEnum.valueOf(this.getDocumentType()))
                .policyId(this.getPolicyId().toString())
                .issuanceType(InsurancePatrimonialPolicyInfo.IssuanceTypeEnum.valueOf(this.getIssuanceType()))
                .issuanceDate(this.getIssuanceDate())
                .termStartDate(this.getTermStartDate())
                .termEndDate(this.getTermEndDate())
                .maxLMG(new AmountDetails()
                    .amount(this.getMaxLMGAmount())
                    .currency(CurrencyEnum.BRL)
                )
                .proposalId(this.getProposalId())
                .insuredObjects(this.getInsuredObjects().stream().map(PatrimonialInsuredObjectEntity::mapDto).toList())
            );
    }

    public ResponseInsurancePatrimonialPolicyInfoV2 mapPolicyInfoDtoV2() {
        return new ResponseInsurancePatrimonialPolicyInfoV2()
            .data(new InsurancePatrimonialPolicyInfoV2()
                .documentType(InsurancePatrimonialPolicyInfoV2.DocumentTypeEnum.valueOf(this.getDocumentType()))
                .policyId(this.getPolicyId().toString())
                .issuanceType(InsurancePatrimonialPolicyInfoV2.IssuanceTypeEnum.valueOf(this.getIssuanceType()))
                .issuanceDate(this.getIssuanceDate())
                .termStartDate(this.getTermStartDate())
                .termEndDate(this.getTermEndDate())
                .maxLMG(new AmountDetails()
                    .amount(this.getMaxLMGAmount())
                    .currency(CurrencyEnum.BRL)
                )
                .proposalId(this.getProposalId())
                .insuredObjects(this.getInsuredObjects().stream().map(PatrimonialInsuredObjectEntity::mapDtoV2).toList())
            );
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getPolicyId().toString());
    }
}
