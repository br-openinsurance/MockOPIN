package com.raidiam.trustframework.mockinsurance.domain;

import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePatrimonialCoverageCode;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePremiumCoverage;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "patrimonial_premium_coverages")
public class PatrimonialPremiumCoverageEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "patrimonial_premium_coverage_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID patrimonialPremiumCoverageId;

    @Column(name = "patrimonial_premium_id")
    private UUID patrimonialPremiumId;

    @Column(name = "branch")
    private String branch;

    @Column(name = "code")
    private String code;

    @Column(name = "amount")
    private String amount;

    @Column(name = "unit_type")
    private String unitType;

    @Column(name = "unit_code")
    private String unitCode;

    @Column(name = "unit_description")
    private String unitDescription;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patrimonial_premium_id", referencedColumnName = "patrimonial_premium_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PatrimonialPremiumEntity patrimonialPremium;

    public InsurancePremiumCoverage mapDto() {
        return new InsurancePremiumCoverage()
            .branch(this.getBranch())
            .code(InsurancePatrimonialCoverageCode.valueOf(this.getCode()))
            .premiumAmount(new AmountDetails()
                .amount(this.getAmount())
                .currency(AmountDetails.CurrencyEnum.BRL)
            );
    }
}
