package com.raidiam.trustframework.mockinsurance.domain;

import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceHousingPremiumCoverage;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "housing_policy_premium_coverages")
public class HousingPolicyPremiumCoverageEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "housing_policy_premium_coverage_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID premiumCoverageId;

    @Column(name = "branch")
    private String branch;

    @Column(name = "code")
    private String code;

    @Column(name = "amount")
    private String amount;

    @Column(name = "unit_type")
    private String unitType;

    @Column(name = "housing_policy_premium_id")
    private UUID housingPolicyPremiumId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_policy_premium_id", referencedColumnName = "housing_policy_premium_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private HousingPolicyPremiumEntity housingPolicyPremium;

    public InsuranceHousingPremiumCoverage mapDto() {
        return new InsuranceHousingPremiumCoverage()
            .branch(this.getBranch())
            .code(InsuranceHousingPremiumCoverage.CodeEnum.valueOf(this.getCode()))
            .premiumAmount(new AmountDetails()
                    .amount(this.getAmount())
                    .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getUnitType()))
            );
    }
}