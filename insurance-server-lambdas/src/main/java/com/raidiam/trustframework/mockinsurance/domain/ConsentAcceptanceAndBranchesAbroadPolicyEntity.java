package com.raidiam.trustframework.mockinsurance.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "consent_acceptance_and_branches_abroad_policies")
public class ConsentAcceptanceAndBranchesAbroadPolicyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id", unique = true, nullable = false, updatable = false, insertable = false)
    private Integer referenceId;

    @Column(name = "consent_id")
    private String consentId;

    @Column(name = "policy_id")
    private UUID policyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_id", referencedColumnName = "consent_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private ConsentEntity consent;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", referencedColumnName = "policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private AcceptanceAndBranchesAbroadPolicyEntity policy;

    public ConsentAcceptanceAndBranchesAbroadPolicyEntity(ConsentEntity consent, AcceptanceAndBranchesAbroadPolicyEntity policy) {
        this.consentId = consent.getConsentId();
        this.policyId = policy.getPolicyId();
    }
}
