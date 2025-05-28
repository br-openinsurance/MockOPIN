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
@Table(name = "consent_capitalization_title_plans")
public class ConsentCapitalizationTitlePlanEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id", unique = true, nullable = false, updatable = false, insertable = false)
    private Integer referenceId;

    @Column(name = "consent_id")
    private String consentId;

    @Column(name = "capitalization_title_plan_id")
    private UUID capitalizationTitlePlanId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_id", referencedColumnName = "consent_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private ConsentEntity consent;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capitalization_title_plan_id", referencedColumnName = "capitalization_title_plan_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private CapitalizationTitlePlanEntity capitalizationTitlePlan;

    public ConsentCapitalizationTitlePlanEntity(ConsentEntity consent, CapitalizationTitlePlanEntity capitalizationTitlePlan) {
        this.consentId = consent.getConsentId();
        this.capitalizationTitlePlanId = capitalizationTitlePlan.getCapitalizationTitlePlanId();
    }
}
