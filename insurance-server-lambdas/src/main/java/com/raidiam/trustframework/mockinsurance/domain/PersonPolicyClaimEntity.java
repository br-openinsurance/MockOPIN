package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePersonClaim;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePersonClaimV2;

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
@Table(name = "person_policy_claims")
public class PersonPolicyClaimEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "person_policy_claim_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID claimId;

    @Column(name = "person_policy_id")
    private UUID personPolicyId;

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

    @Column(name = "warning_register_date")
    private LocalDate warningRegisterDate;

    @Column(name = "third_party_claim_date")
    private LocalDate thirdPartyClaimDate;

    @Column(name = "amount")
    private String amount;

    @Column(name = "amount_unit_type")
    private String amountUnitType;

    @Column(name = "denial_justification")
    private String denialJustification;

    @Column(name = "denial_justification_description")
    private String denialJustificationDescription;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_policy_id", referencedColumnName = "person_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PersonPolicyEntity personPolicy;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "personPolicyClaim")
    private List<PersonPolicyClaimCoverageEntity> coverages = new ArrayList<>();

    public InsurancePersonClaim mapDTO() {
        return new InsurancePersonClaim()
                .identification(this.getIdentification())
                .documentationDeliveryDate(this.getDocumentationDeliveryDate())
                .status(InsurancePersonClaim.StatusEnum.valueOf(this.getStatus()))
                .statusAlterationDate(this.getStatusAlterationDate())
                .occurrenceDate(this.getOccurrenceDate())
                .warningDate(this.getWarningDate())
                .warningRegisterDate(this.getWarningRegisterDate())
                .thirdPartyClaimDate(this.getThirdPartyClaimDate())
                .amount(new AmountDetails()
                        .amount(this.getAmount())
                        .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getAmountUnitType()))
                )
                .denialJustification(InsurancePersonClaim.DenialJustificationEnum.valueOf(this.getDenialJustification()))
                .denialJustificationDescription(this.getDenialJustificationDescription())
                .coverages(this.getCoverages().stream().map(PersonPolicyClaimCoverageEntity::mapDTO).toList());
    }

    public InsurancePersonClaimV2 mapDTOV2() {
        return new InsurancePersonClaimV2()
                .identification(this.getIdentification())
                .documentationDeliveryDate(this.getDocumentationDeliveryDate())
                .status(InsurancePersonClaimV2.StatusEnum.valueOf(this.getStatus()))
                .statusAlterationDate(this.getStatusAlterationDate())
                .occurrenceDate(this.getOccurrenceDate())
                .warningDate(this.getWarningDate())
                .warningRegisterDate(this.getWarningRegisterDate())
                .thirdPartyClaimDate(this.getThirdPartyClaimDate())
                .amount(new AmountDetails()
                        .amount(this.getAmount())
                        .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getAmountUnitType()))
                )
                .denialJustification(InsurancePersonClaimV2.DenialJustificationEnum.valueOf(this.getDenialJustification()))
                .denialJustificationDescription(this.getDenialJustificationDescription())
                .coverages(this.getCoverages().stream().map(PersonPolicyClaimCoverageEntity::mapDTO).toList());
    }
}
