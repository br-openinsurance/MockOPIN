package com.raidiam.trustframework.mockinsurance.domain;

import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralPremiumCoverage;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails.UnitTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit.DescriptionEnum;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "rural_policy_premium_coverages")
public class RuralPolicyPremiumCoverageEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "rural_policy_premium_coverage_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID ruralPolicyPremiumCoverageId;

    @Column(name = "rural_policy_premium_id")
    private UUID ruralPolicyPremiumId;

    @Column(name = "branch")
    private String branch;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private String amount;

    @Column(name = "unit_type")
    private String unitType;

    @Column(name = "unit_type_others")
    private String unitTypeOthers;

    @Column(name = "unit_code")
    private String unitCode;

    @Column(name = "unit_description")
    private String unitDescription;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rural_policy_premium_id", referencedColumnName = "rural_policy_premium_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private RuralPolicyPremiumEntity ruralPolicyPremium;

    public InsuranceRuralPremiumCoverage mapDto() {
    return new InsuranceRuralPremiumCoverage()
        .branch(this.getBranch())
        .code(InsuranceRuralPremiumCoverage.CodeEnum.valueOf(this.getCode()))
        .description(this.getDescription())
        .premiumAmount(new AmountDetails()
            .amount(this.getAmount())
            .unitType(UnitTypeEnum.valueOf(this.getUnitType()))
            .unitTypeOthers(this.getUnitTypeOthers())
            .unit(new AmountDetailsUnit().code(this.getUnitCode()).description(DescriptionEnum.valueOf(this.getUnitDescription())))
        );
    }
}
