package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceAutoClaim;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceAutoClaimCoverage;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "auto_policy_claims")
public class AutoPolicyClaimEntity extends BaseEntity {
    @Id
    @Column(name = "auto_policy_claim_id", unique = true, nullable = false, updatable = false)
    private String autoPolicyClaimId;

    @Column(name = "status")
    private String status;

    @Column(name = "auto_policy_id")
    private String autoPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auto_policy_id", referencedColumnName = "auto_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private AutoPolicyEntity autoPolicy;

    public InsuranceAutoClaim mapDto() {
        return new InsuranceAutoClaim()
                .identification(this.getAutoPolicyClaimId())
                .status(InsuranceAutoClaim.StatusEnum.fromValue(this.getStatus()))
                .statusAlterationDate(InsuranceLambdaUtils.dateToLocalDate(this.getUpdatedAt()))
                .occurrenceDate(InsuranceLambdaUtils.dateToLocalDate(this.getCreatedAt()))
                .warningDate(InsuranceLambdaUtils.dateToLocalDate(this.getCreatedAt()))
                .amount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                .coverages(List.of(new InsuranceAutoClaimCoverage()
                        .branch("0111")
                        .code(InsuranceAutoClaimCoverage.CodeEnum.ACESSORIOS_E_EQUIPAMENTOS)));
    }
}
