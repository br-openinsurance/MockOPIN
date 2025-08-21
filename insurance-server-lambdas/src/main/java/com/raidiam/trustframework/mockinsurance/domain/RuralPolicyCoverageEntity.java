package com.raidiam.trustframework.mockinsurance.domain;

import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralCoverage;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "rural_policy_coverages")
public class RuralPolicyCoverageEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "rural_policy_coverage_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID ruralPolicyCoverageId;

    @Column(name = "rural_policy_id")
    private UUID ruralPolicyId;

    @Column(name = "branch")
    private String branch;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "deductible_id")
    private UUID deductibleId;

    @OneToOne(cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "deductible_id", referencedColumnName = "reference_id", insertable = false, updatable = false)
    private DeductibleEntity deductible;

    @Column(name = "pos_id")
    private UUID posId;

    @OneToOne(cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "pos_id", referencedColumnName = "pos_id", insertable = false, updatable = false)
    private POSEntity pos;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rural_policy_id", referencedColumnName = "rural_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private RuralPolicyEntity ruralPolicy;

    public InsuranceRuralCoverage mapDto() {
        return new InsuranceRuralCoverage()
            .branch(this.getBranch())
            .code(InsuranceRuralCoverage.CodeEnum.valueOf(this.getCode()))
            .description(this.getDescription())
            .deductible(this.getDeductible().mapDTO())
            .POS(this.getPos().mapDTO());
    }
}
