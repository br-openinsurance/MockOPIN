package com.raidiam.trustframework.mockinsurance.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails.CurrencyEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails.UnitTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit.DescriptionEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralClaim;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralClaimCoverage;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "rural_policy_claims")
public class RuralClaimEntity extends BaseEntity {
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
    private RuralPolicyEntity policy;

    @Column(name = "identification")
    private String identification;

    public InsuranceRuralClaim toResponse() {
        return new InsuranceRuralClaim()
            .identification(this.getIdentification())
            .documentationDeliveryDate(LocalDate.now())
            .status(InsuranceRuralClaim.StatusEnum.ABERTO)
            .statusAlterationDate(LocalDate.now())
            .occurrenceDate(LocalDate.now())
            .warningDate(LocalDate.now())
            .thirdPartyClaimDate(LocalDate.now())
            .amount(new AmountDetails()
                .amount("2000.00")
                .unitType(UnitTypeEnum.MONETARIO)
                .unit(new AmountDetailsUnit().code("R$").description(DescriptionEnum.BRL))
                .currency(CurrencyEnum.BRL)
            )
            .coverages(List.of(new InsuranceRuralClaimCoverage()
                .branch("0111")
                .code(InsuranceRuralClaimCoverage.CodeEnum.GRANIZO)
            ));
    }
}
