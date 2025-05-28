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
@Table(name = "consent_responsibility_policies")
public class ConsentResponsibilityPolicyEntity extends BaseEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id", unique = true, nullable = false, updatable = false, insertable = false)
    private Integer referenceId;

    @Column(name = "consent_id")
    private String consentId;

    @Column(name = "responsibility_policy_id")
    private UUID responsibilityPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_id", referencedColumnName = "consent_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private ConsentEntity consent;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsibility_policy_id", referencedColumnName = "responsibility_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private ResponsibilityPolicyEntity responsibilityPolicy;

    public ConsentResponsibilityPolicyEntity(ConsentEntity consent, ResponsibilityPolicyEntity responsibilityPolicy){
        this.consentId = consent.getConsentId();
        this.responsibilityPolicyId = responsibilityPolicy.getResponsibilityPolicyId();
    }
}