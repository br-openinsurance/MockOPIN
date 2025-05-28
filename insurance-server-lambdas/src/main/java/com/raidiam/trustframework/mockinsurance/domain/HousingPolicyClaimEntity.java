package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceHousingClaim;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceHousingClaimCoverage;
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
@Table(name = "housing_policy_claims")
public class HousingPolicyClaimEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "housing_policy_claim_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID claimId;

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
                .identification("string")
                .documentationDeliveryDate(LocalDate.of(2023, 10, 1))
                .status(InsuranceHousingClaim.StatusEnum.ABERTO)
                .statusAlterationDate(LocalDate.of(2023, 10, 1))
                .occurrenceDate(LocalDate.of(2023, 10, 1))
                .warningDate(LocalDate.of(2023, 10, 1))
                .thirdPartyClaimDate(LocalDate.of(2022, 10, 1))
                .amount(new AmountDetails()
                        .amount("16")
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                )
                .denialJustification(InsuranceHousingClaim.DenialJustificationEnum.PRESCRICAO)
                .denialJustificationDescription("string")
                .coverages(List.of(new InsuranceHousingClaimCoverage()
                        .insuredObjectId("string")
                        .branch("0111")
                        .code(InsuranceHousingClaimCoverage.CodeEnum.DANOS_ELETRICOS)
                        .description("string")
                        .warningDate(LocalDate.of(2023, 10, 1))
                        .thirdPartyClaimDate(LocalDate.of(2023, 10, 1))
                ));
    }
}
