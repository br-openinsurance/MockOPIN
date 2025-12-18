package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePersonClaim;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePersonClaimCoverage;
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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_policy_id", referencedColumnName = "person_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PersonPolicyEntity personPolicy;

    public InsurancePersonClaim mapDTO() {
        return new InsurancePersonClaim()
                .identification("string")
                .documentationDeliveryDate(LocalDate.of(2023, 10, 1))
                .status(InsurancePersonClaim.StatusEnum.ABERTO)
                .statusAlterationDate(LocalDate.of(2023, 10, 1))
                .occurrenceDate(LocalDate.of(2023, 10, 1))
                .warningDate(LocalDate.of(2023, 10, 1))
                .warningRegisterDate(LocalDate.of(2023, 10, 1))
                .thirdPartyClaimDate(LocalDate.of(2022, 10, 1))
                .amount(new AmountDetails()
                        .amount("16")
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                )
                .denialJustification(InsurancePersonClaim.DenialJustificationEnum.PRESCRICAO)
                .denialJustificationDescription("string")
                .coverages(List.of(new InsurancePersonClaimCoverage()
                        .insuredObjectId("string")
                        .branch("0111")
                        .code(InsurancePersonClaimCoverage.CodeEnum.CIRURGIA)
                        .description("string")
                        .warningDate(LocalDate.of(2023, 10, 1))
                        .thirdPartyClaimDate(LocalDate.of(2023, 10, 1))
                ));
    }

    public InsurancePersonClaimV2 mapDTOV2() {
        return new InsurancePersonClaimV2()
                .identification("string")
                .documentationDeliveryDate(LocalDate.of(2023, 10, 1))
                .status(InsurancePersonClaimV2.StatusEnum.ABERTO)
                .statusAlterationDate(LocalDate.of(2023, 10, 1))
                .occurrenceDate(LocalDate.of(2023, 10, 1))
                .warningDate(LocalDate.of(2023, 10, 1))
                .warningRegisterDate(LocalDate.of(2023, 10, 1))
                .thirdPartyClaimDate(LocalDate.of(2022, 10, 1))
                .amount(new AmountDetails()
                        .amount("16")
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                )
                .denialJustification(InsurancePersonClaimV2.DenialJustificationEnum.PRESCRICAO)
                .denialJustificationDescription("string")
                .coverages(List.of(new InsurancePersonClaimCoverage()
                        .insuredObjectId("string")
                        .branch("0111")
                        .code(InsurancePersonClaimCoverage.CodeEnum.CIRURGIA)
                        .description("string")
                        .warningDate(LocalDate.of(2023, 10, 1))
                        .thirdPartyClaimDate(LocalDate.of(2023, 10, 1))
                ));
    }
}
