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
@Table(name = "consent_financial_assistance_contracts")
public class ConsentFinancialAssistanceContractEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id", unique = true, nullable = false, updatable = false, insertable = false)
    private Integer referenceId;

    @Column(name = "consent_id")
    private String consentId;

    @Column(name = "financial_assistance_contract_id")
    private String financialAssistanceContractId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_id", referencedColumnName = "consent_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private ConsentEntity consent;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_assistance_contract_id", referencedColumnName = "financial_assistance_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private FinancialAssistanceContractEntity financialAssistanceContract;

    public ConsentFinancialAssistanceContractEntity(ConsentEntity consent, FinancialAssistanceContractEntity financialAssistanceContract){
        this.consentId = consent.getConsentId();
        this.financialAssistanceContractId = financialAssistanceContract.getFinancialAssistanceContractId();
    }
}
