package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceTransportClaim;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceTransportClaimV2;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "transport_policy_claims")
public class TransportPolicyClaimEntity extends BaseEntity {
    @Id
    @Column(name = "transport_policy_claim_id", unique = true, nullable = false, updatable = false)
    private String transportPolicyClaimId;

    @Column(name = "transport_policy_id")
    private String transportPolicyId;

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
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "transportPolicyClaim")
    private List<TransportPolicyClaimCoverageEntity> coverages = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_policy_id", referencedColumnName = "transport_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private TransportPolicyEntity transportPolicy;

    public InsuranceTransportClaim mapDto() {
        return new InsuranceTransportClaim()
                .identification(this.getIdentification())
                .documentationDeliveryDate(this.getDocumentationDeliveryDate())
                .status(InsuranceTransportClaim.StatusEnum.fromValue(this.getStatus()))
                .statusAlterationDate(this.getStatusAlterationDate())
                .occurrenceDate(this.getOccurrenceDate())
                .warningDate(this.getWarningDate())
                .thirdPartyClaimDate(this.getThirdPartyClaimDate())
                .amount(this.mapAmount())
                .denialJustification(InsuranceTransportClaim.DenialJustificationEnum.fromValue(this.getDenialJustification()))
                .denialJustificationDescription(this.getDenialJustificationDescription())
                .coverages(this.getCoverages().stream().map(TransportPolicyClaimCoverageEntity::mapDto).toList());
    }

    public InsuranceTransportClaimV2 mapDtoV2() {
        return new InsuranceTransportClaimV2()
                .identification(this.getIdentification())
                .documentationDeliveryDate(this.getDocumentationDeliveryDate())
                .status(InsuranceTransportClaimV2.StatusEnum.fromValue(this.getStatus()))
                .statusAlterationDate(this.getStatusAlterationDate())
                .occurrenceDate(this.getOccurrenceDate())
                .warningDate(this.getWarningDate())
                .thirdPartyClaimDate(this.getThirdPartyClaimDate())
                .amount(this.mapAmount())
                .denialJustification(InsuranceTransportClaimV2.DenialJustificationEnum.fromValue(this.getDenialJustification()))
                .denialJustificationDescription(this.getDenialJustificationDescription())
                .coverages(this.getCoverages().stream().map(TransportPolicyClaimCoverageEntity::mapDto).toList());
    }

    private AmountDetails mapAmount() {
        return new AmountDetails()
                .amount(this.getAmount())
                .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getUnitType()))
                .unitTypeOthers(this.getUnitTypeOthers())
                .unit(new AmountDetailsUnit()
                        .code(this.getUnitCode())
                        .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getUnitDescription())))
                .currency(AmountDetails.CurrencyEnum.fromValue(this.getCurrency()));
    }
}
