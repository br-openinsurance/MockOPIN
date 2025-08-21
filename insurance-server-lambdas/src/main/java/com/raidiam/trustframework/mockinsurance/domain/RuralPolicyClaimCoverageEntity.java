package com.raidiam.trustframework.mockinsurance.domain;


import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralClaimCoverage;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "rural_policy_claim_coverages")
public class RuralPolicyClaimCoverageEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "rural_policy_claim_coverage_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID claimCoverageId;

    @Column(name = "rural_policy_claim_id")
    private UUID ruralPolicyClaimId;

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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rural_policy_claim_id", referencedColumnName = "rural_policy_claim_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private RuralPolicyClaimEntity ruralPolicyClaim;

    public InsuranceRuralClaimCoverage mapDto() {
        return new InsuranceRuralClaimCoverage()
            .insuredObjectId(this.getInsuredObjectId())
            .branch(this.getBranch())
            .code(InsuranceRuralClaimCoverage.CodeEnum.valueOf(this.getCode()))
            .description(this.getDescription())
            .warningDate(this.getWarningDate())
            .thirdPartyClaimDate(this.getThirdPartyClaimDate());
    }
}
