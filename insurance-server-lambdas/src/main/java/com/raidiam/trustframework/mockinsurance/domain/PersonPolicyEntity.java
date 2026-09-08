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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "person_policies")
public class PersonPolicyEntity extends BaseEntity implements HasStatusInterface {
    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "person_policy_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID personPolicyId;

    @Column(name = "person_id")
    private String personId;

    @Column(name = "status")
    private String status;

    @Column(name = "product_name")
    private String productName;

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

    @Column(name = "proposal_id")
    private String proposalId;

    @Column(name = "pmbac_amount")
    private String pmbacAmount;

    @Column(name = "pmbac_unit_type")
    private String pmbacUnitType;

    @Column(name = "pmbac_unit_code")
    private String pmbacUnitCode;

    @Column(name = "pmbac_unit_description")
    private String pmbacUnitDescription;

    @Column(name = "occurrence_withdrawal")
    private Boolean occurrenceWithdrawal;

    @Column(name = "occurrence_portability")
    private Boolean occurrencePortability;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    @ElementCollection
    @CollectionTable(name = "personal_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "person_policy_id"))
    @Column(name = "personal_id")
    private List<UUID> insuredIds;

    @ElementCollection
    @CollectionTable(name = "beneficiary_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "person_policy_id"))
    @Column(name = "beneficiary_id")
    private List<UUID> beneficiaryIds;

    @ElementCollection
    @CollectionTable(name = "intermediary_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "person_policy_id"))
    @Column(name = "intermediary_id")
    private List<UUID> intermediaryIds;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "personPolicy")
    private List<PersonPolicyPremiumEntity> premiums = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "personPolicy")
    private List<PersonPolicyClaimEntity> claims = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "personPolicy")
    private List<PersonPolicyInsuredObjectEntity> insuredObjects = new ArrayList<>();

    public ResponseInsurancePersonBrandPolicies mapPolicyDTO() {
        return new ResponseInsurancePersonBrandPolicies()
                .policyId(this.getPersonPolicyId().toString())
                .productName(this.getProductName());
    }

    public ResponseInsurancePersonPolicyInfo mapPolicyInfoDTO() {
        var withdrawalInfo = new WithdrawalInfo();
        withdrawalInfo.add(new WithdrawalInfoInner().occurrenceWithdrawal(this.getOccurrenceWithdrawal()));
        var portabilityInfo = new PortabilityInfo();
        portabilityInfo.add(new PortabilityInfoInner().occurrencePortability(this.getOccurrencePortability()));

        return new ResponseInsurancePersonPolicyInfo()
                .data(new InsurancePersonPolicyInfoData()
                        .documentType(InsurancePersonPolicyInfoData.DocumentTypeEnum.valueOf(this.getDocumentType()))
                        .policyId(this.getPersonPolicyId().toString())
                        .susepProcessNumber(this.getSusepProcessNumber())
                        .groupCertificateId(this.getGroupCertificateId())
                        .issuanceType(InsurancePersonPolicyInfoData.IssuanceTypeEnum.valueOf(this.getIssuanceType()))
                        .issuanceDate(this.getIssuanceDate())
                        .termStartDate(this.getTermStartDate())
                        .termEndDate(this.getTermEndDate())
                        .leadInsurerCode(this.getLeadInsurerCode())
                        .leadInsurerPolicyId(this.getLeadInsurerPolicyId())
                        .withdrawals(withdrawalInfo)
                        .proposalId(this.getProposalId())
                        .insuredObjects(this.getInsuredObjects().stream().map(PersonPolicyInsuredObjectEntity::mapDTO).toList())
                        .pmBaC(new InsurancePersonPolicyInfoPMBaC()
                                .pmbacAmount(new AmountDetails()
                                        .amount(this.getPmbacAmount())
                                        .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getPmbacUnitType()))
                                        .unit(new AmountDetailsUnit()
                                                .code(this.getPmbacUnitCode())
                                                .description(AmountDetailsUnit.DescriptionEnum.valueOf(this.getPmbacUnitDescription()))
                                        )
                                )
                        )
                        .portabilities(portabilityInfo)
                );
    }

    public ResponseInsurancePersonPolicyInfoV2 mapPolicyInfoDTOV2() {
        var withdrawalInfo = new WithdrawalInfo();
        withdrawalInfo.add(new WithdrawalInfoInner().occurrenceWithdrawal(this.getOccurrenceWithdrawal()));
        var portabilityInfo = new PortabilityInfo();
        portabilityInfo.add(new PortabilityInfoInner().occurrencePortability(this.getOccurrencePortability()));

        return new ResponseInsurancePersonPolicyInfoV2()
                .data(new InsurancePersonPolicyInfoDataV2()
                        .documentType(InsurancePersonPolicyInfoDataV2.DocumentTypeEnum.valueOf(this.getDocumentType()))
                        .policyId(this.getPersonPolicyId().toString())
                        .susepProcessNumber(this.getSusepProcessNumber())
                        .groupCertificateId(this.getGroupCertificateId())
                        .issuanceType(InsurancePersonPolicyInfoDataV2.IssuanceTypeEnum.valueOf(this.getIssuanceType()))
                        .issuanceDate(this.getIssuanceDate())
                        .termStartDate(this.getTermStartDate())
                        .termEndDate(this.getTermEndDate())
                        .leadInsurerCode(this.getLeadInsurerCode())
                        .leadInsurerPolicyId(this.getLeadInsurerPolicyId())
                        .withdrawals(withdrawalInfo)
                        .proposalId(this.getProposalId())
                        .insuredObjects(this.getInsuredObjects().stream().map(PersonPolicyInsuredObjectEntity::mapDTOV2).toList())
                        .pmBaC(new InsurancePersonPolicyInfoPMBaC()
                                .pmbacAmount(new AmountDetails()
                                        .amount(this.getPmbacAmount())
                                        .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getPmbacUnitType()))
                                        .unit(new AmountDetailsUnit()
                                                .code(this.getPmbacUnitCode())
                                                .description(AmountDetailsUnit.DescriptionEnum.valueOf(this.getPmbacUnitDescription()))
                                        )
                                )
                        )
                        .portabilities(portabilityInfo)
                );
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getPersonPolicyId().toString());
    }
}
