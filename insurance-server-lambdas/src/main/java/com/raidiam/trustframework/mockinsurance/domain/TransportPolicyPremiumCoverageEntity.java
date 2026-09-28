package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceTransportPremiumCoverage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "transport_policy_premium_coverages")
public class TransportPolicyPremiumCoverageEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "transport_policy_premium_coverage_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID coverageId;

    @Column(name = "transport_policy_premium_id")
    private UUID transportPolicyPremiumId;

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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_policy_premium_id", referencedColumnName = "transport_policy_premium_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private TransportPolicyPremiumEntity transportPolicyPremium;

    public InsuranceTransportPremiumCoverage mapDto() {
        return new InsuranceTransportPremiumCoverage()
                .branch(this.getBranch())
                .code(InsuranceTransportPremiumCoverage.CodeEnum.fromValue(this.getCode()))
                .description(this.getDescription())
                .premiumAmount(new AmountDetails()
                        .amount(this.getPremiumAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPremiumUnitType()))
                        .unitTypeOthers(this.getPremiumUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getPremiumUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getPremiumUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getPremiumCurrency())));
    }
}
