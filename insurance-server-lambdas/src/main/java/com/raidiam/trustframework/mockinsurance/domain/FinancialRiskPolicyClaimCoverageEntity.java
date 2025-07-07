package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceFinancialRiskClaimCoverage;
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
@Table(name = "financial_risk_policy_claim_coverages")
public class FinancialRiskPolicyClaimCoverageEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "financial_risk_policy_claim_coverage_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID coverageId;

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

    @Column(name = "financial_risk_policy_claim_id")
    private UUID financialRiskPolicyClaimId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_risk_policy_claim_id", referencedColumnName = "financial_risk_policy_claim_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private FinancialRiskPolicyClaimEntity financialRiskPolicyClaim;

    public InsuranceFinancialRiskClaimCoverage mapDTO() {
        return new InsuranceFinancialRiskClaimCoverage()
                .insuredObjectId(this.getInsuredObjectId())
                .branch(this.getBranch())
                .code(InsuranceFinancialRiskClaimCoverage.CodeEnum.fromValue(this.getCode()))
                .description(this.getDescription())
                .warningDate(this.getWarningDate())
                .thirdPartyClaimDate(this.getThirdPartyClaimDate());
    }
}
