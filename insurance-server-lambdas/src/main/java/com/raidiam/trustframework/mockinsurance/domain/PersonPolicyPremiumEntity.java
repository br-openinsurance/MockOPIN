package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "person_policy_premiums")
public class PersonPolicyPremiumEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "person_policy_premium_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID premiumId;

    @Column(name = "person_policy_id")
    private UUID personPolicyId;

    @Column(name = "payments_quantity")
    private Integer paymentsQuantity;

    @Column(name = "amount")
    private String amount;

    @Column(name = "unit_type")
    private String unitType;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_policy_id", referencedColumnName = "person_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PersonPolicyEntity personPolicy;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "personPolicyPremium")
    private List<PersonPolicyPremiumCoverageEntity> coverages = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "payment_ids", joinColumns = @JoinColumn(name = "reference_id"))
    @Column(name = "payment_id")
    private List<UUID> paymentIds;

    public InsurancePersonPremium mapDTO() {
        return new InsurancePersonPremium()
                .paymentsQuantity(this.getPaymentsQuantity())
                .amount(new AmountDetails()
                        .amount(this.getAmount())
                        .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getUnitType()))
                )
                .coverages(this.getCoverages().stream().map(PersonPolicyPremiumCoverageEntity::mapDTO).toList());
    }
}
