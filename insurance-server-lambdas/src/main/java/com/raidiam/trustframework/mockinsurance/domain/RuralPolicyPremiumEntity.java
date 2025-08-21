package com.raidiam.trustframework.mockinsurance.domain;

import java.util.List;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralPremium;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "rural_policy_premiums")
public class RuralPolicyPremiumEntity extends BaseEntity { 

    @Id
    @GeneratedValue
    @Column(name = "rural_policy_premium_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID ruralPolicyPremiumId;

    @Column(name = "rural_policy_id")
    private UUID ruralPolicyId;

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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruralPolicyPremium")
    private List<RuralPolicyPremiumCoverageEntity> coverages;

    @ElementCollection
    @CollectionTable(name = "payment_ids", joinColumns = @JoinColumn(name = "reference_id"))
    @Column(name = "payment_id")
    private List<UUID> paymentIds;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rural_policy_id", referencedColumnName = "rural_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private RuralPolicyEntity ruralPolicy;


    public InsuranceRuralPremium mapDto() {
        return new InsuranceRuralPremium() 
            .paymentsQuantity(this.getPaymentsQuantity())
            .amount(new AmountDetails()
                .amount(this.getAmount())
                .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getUnitType()))
                .unit(new AmountDetailsUnit().code(this.getUnitCode()).description(AmountDetailsUnit.DescriptionEnum.valueOf(this.getUnitDescription())))
            )
            .coverages(this.getCoverages().stream().map(RuralPolicyPremiumCoverageEntity::mapDto).toList());
    }
}
