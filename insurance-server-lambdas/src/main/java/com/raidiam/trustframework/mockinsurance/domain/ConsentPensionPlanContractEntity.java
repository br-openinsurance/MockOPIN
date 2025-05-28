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
@Table(name = "consent_pension_plan_contracts")
public class ConsentPensionPlanContractEntity extends BaseEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id", unique = true, nullable = false, updatable = false, insertable = false)
    private Integer referenceId;

    @Column(name = "consent_id")
    private String consentId;

    @Column(name = "pension_plan_contract_id")
    private String pensionPlanContractId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_id", referencedColumnName = "consent_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private ConsentEntity consent;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pension_plan_contract_id", referencedColumnName = "pension_plan_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PensionPlanContractEntity pensionPlanContract;

    public ConsentPensionPlanContractEntity(ConsentEntity consent, PensionPlanContractEntity pensionPlanContract){
        this.consentId = consent.getConsentId();
        this.pensionPlanContractId = pensionPlanContract.getPensionPlanContractId();
    }
}