package com.raidiam.trustframework.mockinsurance.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceClaim;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceClaimCoverage;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceClaimV2;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails.UnitTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit.DescriptionEnum;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "acceptance_and_branches_abroad_policy_claims")
public class AcceptanceAndBranchesAbroadClaimEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "claim_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID claimId;

    @Column(name = "policy_id")
    private UUID policyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", referencedColumnName = "policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private AcceptanceAndBranchesAbroadPolicyEntity policy;

    @Column(name = "identification")
    private String identification;

    public InsuranceClaim toResponse() {
        return new InsuranceClaim()
            .identification(this.getIdentification())
            .documentationDeliveryDate(LocalDate.now())
            .status(InsuranceClaim.StatusEnum.ABERTO)
            .statusAlterationDate(LocalDate.now())
            .occurrenceDate(LocalDate.now())
            .warningDate(LocalDate.now())
            .thirdPartyClaimDate(LocalDate.now())
            .amount(new AmountDetails()
                .amount("0372122770.41")
                .unitType(UnitTypeEnum.PORCENTAGEM)
                .unit(new AmountDetailsUnit().code("R$").description(DescriptionEnum.BRL))
            )
            .coverages(List.of(new InsuranceClaimCoverage()
                .branch("0111")
                .code(InsuranceClaimCoverage.CodeEnum.OUTRAS)
            ));
    }

    public InsuranceClaimV2 toResponseV2() {
        return new InsuranceClaimV2()
            .identification(this.getIdentification())
            .documentationDeliveryDate(LocalDate.now())
            .status(InsuranceClaimV2.StatusEnum.ABERTO)
            .statusAlterationDate(LocalDate.now())
            .occurrenceDate(LocalDate.now())
            .warningDate(LocalDate.now())
            .thirdPartyClaimDate(LocalDate.now())
            .amount(new AmountDetails()
                .amount("0372122770.41")
                .unitType(UnitTypeEnum.PORCENTAGEM)
                .unit(new AmountDetailsUnit().code("R$").description(DescriptionEnum.BRL))
            )
            .coverages(List.of(new InsuranceClaimCoverage()
                .branch("0111")
                .code(InsuranceClaimCoverage.CodeEnum.OUTRAS)
            ));
    }
}
