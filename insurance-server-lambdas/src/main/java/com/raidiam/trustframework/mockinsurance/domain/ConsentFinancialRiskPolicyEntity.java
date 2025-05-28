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
@Table(name = "consent_financial_risk_policies")
public class ConsentFinancialRiskPolicyEntity extends BaseEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id", unique = true, nullable = false, updatable = false, insertable = false)
    private Integer referenceId;

    @Column(name = "consent_id")
    private String consentId;

    @Column(name = "financial_risk_policy_id")
    private UUID financialRiskPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_id", referencedColumnName = "consent_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private ConsentEntity consent;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_risk_policy_id", referencedColumnName = "financial_risk_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private FinancialRiskPolicyEntity financialRiskPolicy;

    public ConsentFinancialRiskPolicyEntity(ConsentEntity consent, FinancialRiskPolicyEntity financialRiskPolicy){
        this.consentId = consent.getConsentId();
        this.financialRiskPolicyId = financialRiskPolicy.getFinancialRiskPolicyId();
    }
}