package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePersonClaimCoverage;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "person_claim_coverages")
public class PersonPolicyClaimCoverageEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "person_claim_coverage_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID personClaimCoverageId;

    @Column(name = "person_policy_claim_id")
    private UUID personPolicyClaimId;

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
    @JoinColumn(name = "person_policy_claim_id", referencedColumnName = "person_policy_claim_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PersonPolicyClaimEntity personPolicyClaim;

    public InsurancePersonClaimCoverage mapDTO() {
        return new InsurancePersonClaimCoverage()
                .insuredObjectId(this.getInsuredObjectId())
                .branch(this.getBranch())
                .code(InsurancePersonClaimCoverage.CodeEnum.valueOf(this.getCode()))
                .description(this.getDescription())
                .warningDate(this.getWarningDate())
                .thirdPartyClaimDate(this.getThirdPartyClaimDate());
    }
}
