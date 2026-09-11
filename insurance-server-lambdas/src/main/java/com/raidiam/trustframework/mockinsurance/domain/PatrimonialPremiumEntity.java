package com.raidiam.trustframework.mockinsurance.domain;

import java.util.List;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePremium;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "patrimonial_premiums")
public class PatrimonialPremiumEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "patrimonial_premium_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID patrimonialPremiumId;

    @Column(name = "policy_id")
    private UUID policyId;

    @Column(name = "payments_quantity")
    private Integer paymentsQuantity;

    @Column(name = "amount")
    private String amount;

    @Column(name = "unit_type")
    private String unitType;

    @Column(name = "unit_code")
    private String unitCode;

    @Column(name = "unit_description")
    private String unitDescription;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "patrimonialPremium")
    private List<PatrimonialPremiumCoverageEntity> coverages;

    @ElementCollection
    @CollectionTable(name = "payment_ids", joinColumns = @JoinColumn(name = "reference_id"))
    @Column(name = "payment_id")
    private List<UUID> paymentIds;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", referencedColumnName = "policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PatrimonialPolicyEntity patrimonialPolicy;

    public InsurancePremium mapDto() {
        return new InsurancePremium()
            .paymentsQuantity(this.getPaymentsQuantity())
            .amount(new AmountDetails()
                .amount(this.getAmount())
                .currency(AmountDetails.CurrencyEnum.BRL)
            )
            .coverages(this.getCoverages().stream().map(PatrimonialPremiumCoverageEntity::mapDto).toList());
    }
}
