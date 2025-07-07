package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "financial_risk_policy_coverages")
public class FinancialRiskPolicyCoverageEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "financial_risk_policy_coverage_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID coverageId;

    @Column(name = "branch")
    private String branch;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "deductible_id")
    private UUID deductibleId;

    @Column(name = "pos_id")
    private UUID POSId;

    @OneToOne(cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "deductible_id", referencedColumnName = "reference_id", insertable = false, updatable = false)
    private DeductibleEntity deductible;

    @OneToOne(cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "pos_id", referencedColumnName = "pos_id", insertable = false, updatable = false)
    private POSEntity pos;

    @Column(name = "financial_risk_policy_id")
    private UUID financialRiskPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_risk_policy_id", referencedColumnName = "financial_risk_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private FinancialRiskPolicyEntity financialRiskPolicy;

    public InsuranceFinancialRiskCoverage mapDTO() {
        return new InsuranceFinancialRiskCoverage()
                .branch(this.getBranch())
                .code(InsuranceFinancialRiskCoverage.CodeEnum.fromValue(this.getCode()))
                .description(this.getDescription())
                .deductible(this.getDeductible().mapDTO())
                .POS(this.getPos().mapDTO());
    }
}
