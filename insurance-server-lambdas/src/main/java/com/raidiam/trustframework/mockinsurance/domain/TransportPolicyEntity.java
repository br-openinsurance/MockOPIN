package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.BaseBrandAndCompanyDataPolicies;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceTransportPolicyInfoData;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceTransportPolicyInfoDataV2;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceTransportSpecificPolicyInfo;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceTransportPolicyInfo;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseInsuranceTransportPolicyInfoV2;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseResourceListData;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "transport_policies")
public class TransportPolicyEntity extends BaseEntity implements HasStatusInterface {

    @Id
    @Column(name = "transport_policy_id", unique = true, nullable = false, updatable = false)
    private String transportPolicyId;

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

    @Column(name = "coinsurance_retained_percentage")
    private String coinsuranceRetainedPercentage;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    @ElementCollection
    @CollectionTable(name = "transport_personal_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "transport_policy_id"))
    @Column(name = "personal_id")
    private List<UUID> insuredIds;

    @ElementCollection
    @CollectionTable(name = "transport_beneficiary_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "transport_policy_id"))
    @Column(name = "beneficiary_id")
    private List<UUID> beneficiaryIds;

    @ElementCollection
    @CollectionTable(name = "transport_principal_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "transport_policy_id"))
    @Column(name = "principal_id")
    private List<UUID> principalIds;

    @ElementCollection
    @CollectionTable(name = "transport_intermediary_info_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "transport_policy_id"))
    @Column(name = "intermediary_id")
    private List<UUID> intermediaryIds;

    @ElementCollection
    @CollectionTable(name = "transport_coinsurer_ids", joinColumns = @JoinColumn(name = "reference_id", referencedColumnName = "transport_policy_id"))
    @Column(name = "coinsurer_id")
    private List<UUID> coinsurerIds;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "transportPolicy")
    private List<TransportPolicyInsuredObjectEntity> insuredObjects = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "transportPolicy")
    private List<TransportPolicyCoverageEntity> coverages = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "transportPolicy")
    private List<TransportPolicyEndorsementEntity> endorsements = new ArrayList<>();

    public BaseBrandAndCompanyDataPolicies mapPolicyDto() {
        return new BaseBrandAndCompanyDataPolicies()
                .policyId(this.getTransportPolicyId())
                .productName(this.getProductName());
    }

    public ResponseInsuranceTransportPolicyInfo mapInfoDto() {
        return new ResponseInsuranceTransportPolicyInfo()
                .data(new InsuranceTransportPolicyInfoData()
                        .documentType(InsuranceTransportPolicyInfoData.DocumentTypeEnum.fromValue(this.getDocumentType()))
                        .policyId(this.getTransportPolicyId())
                        .susepProcessNumber(this.getSusepProcessNumber())
                        .groupCertificateId(this.getGroupCertificateId())
                        .issuanceType(InsuranceTransportPolicyInfoData.IssuanceTypeEnum.fromValue(this.getIssuanceType()))
                        .issuanceDate(this.getIssuanceDate())
                        .termStartDate(this.getTermStartDate())
                        .termEndDate(this.getTermEndDate())
                        .leadInsurerCode(this.getLeadInsurerCode())
                        .leadInsurerPolicyId(this.getLeadInsurerPolicyId())
                        .maxLMG(this.mapMaxLMG())
                        .proposalId(this.getProposalId())
                        .insuredObjects(this.getInsuredObjects().stream().map(TransportPolicyInsuredObjectEntity::mapDto).toList())
                        .coverages(this.getCoverages().stream().map(TransportPolicyCoverageEntity::mapDto).toList())
                        .coinsuranceRetainedPercentage(this.getCoinsuranceRetainedPercentage())
                        .branchInfo(new InsuranceTransportSpecificPolicyInfo()
                                .endorsements(this.getEndorsements().stream().map(TransportPolicyEndorsementEntity::mapDto).toList())));
    }

    public ResponseInsuranceTransportPolicyInfoV2 mapInfoDtoV2() {
        return new ResponseInsuranceTransportPolicyInfoV2()
                .data(new InsuranceTransportPolicyInfoDataV2()
                        .documentType(InsuranceTransportPolicyInfoDataV2.DocumentTypeEnum.fromValue(this.getDocumentType()))
                        .policyId(this.getTransportPolicyId())
                        .susepProcessNumber(this.getSusepProcessNumber())
                        .groupCertificateId(this.getGroupCertificateId())
                        .issuanceType(InsuranceTransportPolicyInfoDataV2.IssuanceTypeEnum.fromValue(this.getIssuanceType()))
                        .issuanceDate(this.getIssuanceDate())
                        .termStartDate(this.getTermStartDate())
                        .termEndDate(this.getTermEndDate())
                        .leadInsurerCode(this.getLeadInsurerCode())
                        .leadInsurerPolicyId(this.getLeadInsurerPolicyId())
                        .maxLMG(this.mapMaxLMG())
                        .proposalId(this.getProposalId())
                        .insuredObjects(this.getInsuredObjects().stream().map(TransportPolicyInsuredObjectEntity::mapDtoV2).toList())
                        .coverages(this.getCoverages().stream().map(TransportPolicyCoverageEntity::mapDtoV2).toList())
                        .coinsuranceRetainedPercentage(this.getCoinsuranceRetainedPercentage())
                        .branchInfo(new InsuranceTransportSpecificPolicyInfo()
                                .endorsements(this.getEndorsements().stream().map(TransportPolicyEndorsementEntity::mapDto).toList())));
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getTransportPolicyId());
    }

    private AmountDetails mapMaxLMG() {
        return new AmountDetails()
                .amount(this.getMaxLMGAmount())
                .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getMaxLMGUnitType()))
                .unitTypeOthers(this.getMaxLMGUnitTypeOthers())
                .unit(new AmountDetailsUnit()
                        .code(this.getMaxLMGUnitCode())
                        .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getMaxLMGUnitDescription())))
                .currency(AmountDetails.CurrencyEnum.fromValue(this.getMaxLMGCurrency()));
    }
}
