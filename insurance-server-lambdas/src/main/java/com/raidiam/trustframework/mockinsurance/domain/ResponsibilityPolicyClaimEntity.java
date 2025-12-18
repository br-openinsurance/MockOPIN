package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceResponsibilityClaim;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceResponsibilityClaimCoverage;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceResponsibilityClaimV2;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "responsibility_policy_claims")
public class ResponsibilityPolicyClaimEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "responsibility_policy_claim_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID claimId;

    @Column(name = "responsibility_policy_id")
    private UUID responsibilityPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsibility_policy_id", referencedColumnName = "responsibility_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private ResponsibilityPolicyEntity responsibilityPolicy;

    public InsuranceResponsibilityClaim mapDTO() {
        return new InsuranceResponsibilityClaim()
                .identification("string")
                .documentationDeliveryDate(LocalDate.of(2023, 10, 1))
                .status(InsuranceResponsibilityClaim.StatusEnum.ABERTO)
                .statusAlterationDate(LocalDate.of(2023, 10, 1))
                .occurrenceDate(LocalDate.of(2023, 10, 1))
                .warningDate(LocalDate.of(2023, 10, 1))
                .thirdPartyClaimDate(LocalDate.of(2022, 10, 1))
                .amount(new AmountDetails()
                        .amount("16")
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                )
                .denialJustification(InsuranceResponsibilityClaim.DenialJustificationEnum.PRESCRICAO)
                .denialJustificationDescription("string")
                .coverages(List.of(new InsuranceResponsibilityClaimCoverage()
                        .insuredObjectId("string")
                        .branch("0111")
                        .code(InsuranceResponsibilityClaimCoverage.CodeEnum.ALAGAMENTO_E_OU_INUNDACAO)
                        .description("string")
                        .warningDate(LocalDate.of(2023, 10, 1))
                        .thirdPartyClaimDate(LocalDate.of(2023, 10, 1))
                ));
    }

    public InsuranceResponsibilityClaimV2 mapDTOV2() {
        return new InsuranceResponsibilityClaimV2()
                .identification("string")
                .documentationDeliveryDate(LocalDate.of(2023, 10, 1))
                .status(InsuranceResponsibilityClaimV2.StatusEnum.ABERTO)
                .statusAlterationDate(LocalDate.of(2023, 10, 1))
                .occurrenceDate(LocalDate.of(2023, 10, 1))
                .warningDate(LocalDate.of(2023, 10, 1))
                .thirdPartyClaimDate(LocalDate.of(2022, 10, 1))
                .amount(new AmountDetails()
                        .amount("16")
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                )
                .denialJustification(InsuranceResponsibilityClaimV2.DenialJustificationEnum.PRESCRICAO)
                .denialJustificationDescription("string")
                .coverages(List.of(new InsuranceResponsibilityClaimCoverage()
                        .insuredObjectId("string")
                        .branch("0111")
                        .code(InsuranceResponsibilityClaimCoverage.CodeEnum.ALAGAMENTO_E_OU_INUNDACAO)
                        .description("string")
                        .warningDate(LocalDate.of(2023, 10, 1))
                        .thirdPartyClaimDate(LocalDate.of(2023, 10, 1))
                ));
    }
}
