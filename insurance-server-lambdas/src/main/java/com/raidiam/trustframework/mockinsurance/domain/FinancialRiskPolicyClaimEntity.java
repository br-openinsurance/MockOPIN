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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "financial_risk_policy_claims")
public class FinancialRiskPolicyClaimEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "financial_risk_policy_claim_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID claimId;

    @Column(name = "identification")
    private String identification;

    @Column(name = "documentation_delivery_date")
    private LocalDate documentationDeliveryDate;

    @Column(name = "status")
    private String status;

    @Column(name = "status_alteration_date")
    private LocalDate statusAlterationDate;

    @Column(name = "occurrence_date")
    private LocalDate occurrenceDate;

    @Column(name = "warning_date")
    private LocalDate warningDate;

    @Column(name = "third_party_claim_date")
    private LocalDate thirdPartyClaimDate;

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

    @Column(name = "currency")
    private String currency;

    @Column(name = "denial_justification")
    private String denialJustification;

    @Column(name = "denial_justification_description")
    private String denialJustificationDescription;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "financialRiskPolicyClaim")
    private List<FinancialRiskPolicyClaimCoverageEntity> coverages = new ArrayList<>();

    @Column(name = "financial_risk_policy_id")
    private UUID financialRiskPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_risk_policy_id", referencedColumnName = "financial_risk_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private FinancialRiskPolicyEntity financialRiskPolicy;

    public InsuranceFinancialRiskClaim mapDTO() {
        return new InsuranceFinancialRiskClaim()
                .identification(this.getIdentification())
                .documentationDeliveryDate(this.getDocumentationDeliveryDate())
                .status(InsuranceFinancialRiskClaim.StatusEnum.fromValue(this.getStatus()))
                .statusAlterationDate(this.getStatusAlterationDate())
                .occurrenceDate(this.getOccurrenceDate())
                .warningDate(this.getWarningDate())
                .thirdPartyClaimDate(this.getThirdPartyClaimDate())
                .amount(new AmountDetails()
                        .amount(this.getAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getUnitType()))
                        .unitTypeOthers(this.getUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getCurrency())))
                .denialJustification(InsuranceFinancialRiskClaim.DenialJustificationEnum.fromValue(this.getDenialJustification()))
                .denialJustificationDescription(this.getDenialJustificationDescription())
                .coverages(this.getCoverages().stream().map(FinancialRiskPolicyClaimCoverageEntity::mapDTO).toList());
    }
}
