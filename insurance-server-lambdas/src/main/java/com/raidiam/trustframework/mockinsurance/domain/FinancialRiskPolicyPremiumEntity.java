package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "financial_risk_policy_premiums")
public class FinancialRiskPolicyPremiumEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "financial_risk_policy_premium_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID premiumId;

    @Column(name = "payments_quantity")
    private Integer paymentsQuantity;

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

    @Column(name = "currency")
    private String currency;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "financialRiskPolicyPremium")
    private List<FinancialRiskPolicyPremiumCoverageEntity> coverages = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "payment_ids", joinColumns = @JoinColumn(name = "reference_id"))
    @Column(name = "payment_id")
    private List<UUID> paymentIds;

    @Column(name = "financial_risk_policy_id")
    private UUID financialRiskPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_risk_policy_id", referencedColumnName = "financial_risk_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private FinancialRiskPolicyEntity financialRiskPolicy;

    public InsuranceFinancialRiskPremium mapDTO() {
        return new InsuranceFinancialRiskPremium()
                .paymentsQuantity(this.getPaymentsQuantity())
                .amount(new AmountDetails()
                        .amount(this.getAmount())
                        .unit(new AmountDetailsUnit()
                                .code(this.getUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getCurrency()))
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getUnitType()))
                        .unitTypeOthers(this.getUnitTypeOthers()))
                .coverages(this.getCoverages().stream().map(FinancialRiskPolicyPremiumCoverageEntity::mapDTO).toList());
    }
}
