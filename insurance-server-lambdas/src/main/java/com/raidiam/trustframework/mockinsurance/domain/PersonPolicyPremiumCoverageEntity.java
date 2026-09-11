package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePersonPremiumCoverage;

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
@Table(name = "person_premium_coverages")
public class PersonPolicyPremiumCoverageEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "person_premium_coverage_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID personPremiumCoverageId;

    @Column(name = "person_policy_premium_id")
    private UUID personPolicyPremiumId;

    @Column(name = "branch")
    private String branch;

    @Column(name = "code")
    private String code;

    @Column(name = "premium_amount")
    private String premiumAmount;

    @Column(name = "premium_unit_type")
    private String premiumUnitType;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_policy_premium_id", referencedColumnName = "person_policy_premium_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PersonPolicyPremiumEntity personPolicyPremium;

    public InsurancePersonPremiumCoverage mapDTO() {
        return new InsurancePersonPremiumCoverage()
                .branch(this.getBranch())
                .code(InsurancePersonPremiumCoverage.CodeEnum.valueOf(this.getCode()))
                .premiumAmount(new AmountDetails()
                        .amount(this.getPremiumAmount())
                        .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getPremiumUnitType()))
                );
    }
}
