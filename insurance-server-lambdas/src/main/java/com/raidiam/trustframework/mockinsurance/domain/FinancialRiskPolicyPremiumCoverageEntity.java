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
@Table(name = "financial_risk_policy_premium_coverages")
public class FinancialRiskPolicyPremiumCoverageEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "financial_risk_policy_premium_coverage_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID coverageId;

    @Column(name = "branch")
    private String branch;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "premium_amount")
    private String premiumAmount;

    @Column(name = "premium_unit_type")
    private String premiumUnitType;

    @Column(name = "premium_unit_type_others")
    private String premiumUnitTypeOthers;

    @Column(name = "premium_unit_code")
    private String premiumUnitCode;

    @Column(name = "premium_unit_description")
    private String premiumUnitDescription;

    @Column(name = "premium_currency")
    private String premiumCurrency;

    @Column(name = "financial_risk_policy_premium_id")
    private UUID financialRiskPolicyPremiumId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_risk_policy_premium_id", referencedColumnName = "financial_risk_policy_premium_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private FinancialRiskPolicyPremiumEntity financialRiskPolicyPremium;

    public InsuranceFinancialRiskPremiumCoverage mapDTO() {
        return new InsuranceFinancialRiskPremiumCoverage()
                .branch(this.getBranch())
                .code(InsuranceFinancialRiskPremiumCoverage.CodeEnum.fromValue(this.getCode()))
                .description(this.getDescription())
                .premiumAmount(new AmountDetails()
                        .amount(this.getPremiumAmount())
                        .unit(new AmountDetailsUnit()
                                .code(this.getPremiumUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getPremiumUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getPremiumCurrency()))
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPremiumUnitType()))
                        .unitTypeOthers(this.getPremiumUnitTypeOthers()));
    }
}
