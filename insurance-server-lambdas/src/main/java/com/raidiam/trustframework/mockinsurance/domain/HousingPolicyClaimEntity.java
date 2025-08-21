package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceHousingClaim;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "housing_policy_claims")
public class HousingPolicyClaimEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "housing_policy_claim_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
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

    @Column(name = "denial_justification")
    private String denialJustification;

    @Column(name = "denial_justification_description")
    private String denialJustificationDescription;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "housingPolicyClaim")
    private List<HousingPolicyClaimCoverageEntity> coverages;

    @Column(name = "housing_policy_id")
    private UUID housingPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_policy_id", referencedColumnName = "housing_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private HousingPolicyEntity housingPolicy;

    public InsuranceHousingClaim mapDTO() {
        return new InsuranceHousingClaim()
                .identification(this.getIdentification())
                .documentationDeliveryDate(this.getDocumentationDeliveryDate())
                .status(InsuranceHousingClaim.StatusEnum.valueOf(this.getStatus()))
                .statusAlterationDate(this.getStatusAlterationDate())
                .occurrenceDate(this.getOccurrenceDate())
                .warningDate(this.getWarningDate())
                .thirdPartyClaimDate(this.getThirdPartyClaimDate())
                .amount(new AmountDetails()
                        .amount(this.getAmount())
                        .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getUnitType()))
                )
                .denialJustification(InsuranceHousingClaim.DenialJustificationEnum.valueOf(this.getDenialJustification()))
                .denialJustificationDescription(this.getDenialJustificationDescription())
                .coverages(this.getCoverages().stream().map(HousingPolicyClaimCoverageEntity::mapDto).toList());
    }
}
