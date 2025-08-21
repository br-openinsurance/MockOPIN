package com.raidiam.trustframework.mockinsurance.domain;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceHousingClaimCoverage;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "housing_policy_claim_coverages")
public class HousingPolicyClaimCoverageEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "housing_policy_claim_coverage_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID claimCoverageId;

    @Column(name = "insured_object_id")
    private String insuredObjectId;

    @Column(name = "branch")
    private String branch;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "warning_date")
    private LocalDate warningDate;

    @Column(name = "third_party_claim_date")
    private LocalDate thirdPartyClaimDate;

    @Column(name = "housing_policy_claim_id")
    private UUID housingPolicyClaimId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_policy_claim_id", referencedColumnName = "housing_policy_claim_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private HousingPolicyClaimEntity housingPolicyClaim;

    public InsuranceHousingClaimCoverage mapDto() {
        return new InsuranceHousingClaimCoverage()
            .insuredObjectId(this.getInsuredObjectId())
            .branch(this.getBranch())
            .code(InsuranceHousingClaimCoverage.CodeEnum.valueOf(this.getCode()))
            .description(this.getDescription())
            .warningDate(this.getWarningDate())
            .thirdPartyClaimDate(this.getThirdPartyClaimDate());
    }
}