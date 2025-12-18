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
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePatrimonialClaim;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePatrimonialClaimCoverage;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePatrimonialClaimV2;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePatrimonialCoverageCode;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "patrimonial_policy_claims")
public class PatrimonialClaimEntity extends BaseEntity {
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
    private PatrimonialPolicyEntity policy;

    @Column(name = "identification")
    private String identification;

    public InsurancePatrimonialClaim toResponse() {
        return new InsurancePatrimonialClaim()
            .identification(this.getIdentification())
            .documentationDeliveryDate(LocalDate.now())
            .status(InsurancePatrimonialClaim.StatusEnum.ABERTO)
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
            .coverages(List.of(new InsurancePatrimonialClaimCoverage()
                .branch("0111")
                .code(InsurancePatrimonialCoverageCode.IMOVEL_BASICA)
            ));
    }

    public InsurancePatrimonialClaimV2 toResponseV2() {
        return new InsurancePatrimonialClaimV2()
            .identification(this.getIdentification())
            .documentationDeliveryDate(LocalDate.now())
            .status(InsurancePatrimonialClaimV2.StatusEnum.ABERTO)
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
            .coverages(List.of(new InsurancePatrimonialClaimCoverage()
                .branch("0111")
                .code(InsurancePatrimonialCoverageCode.IMOVEL_BASICA)
            ));
    }
}
