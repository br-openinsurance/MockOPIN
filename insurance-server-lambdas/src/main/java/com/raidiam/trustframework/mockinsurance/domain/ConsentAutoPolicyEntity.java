package com.raidiam.trustframework.mockinsurance.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "consent_auto_policies")
public class ConsentAutoPolicyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id", unique = true, nullable = false, updatable = false, insertable = false)
    private Integer referenceId;

    @Column(name = "consent_id")
    private String consentId;

    @Column(name = "auto_policy_id")
    private String autoPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_id", referencedColumnName = "consent_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private ConsentEntity consent;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auto_policy_id", referencedColumnName = "auto_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private AutoPolicyEntity autoPolicy;

    public ConsentAutoPolicyEntity(ConsentEntity consent, AutoPolicyEntity autoPolicy) {
        this.consentId = consent.getConsentId();
        this.autoPolicyId = autoPolicy.getAutoPolicyId();
    }
}
