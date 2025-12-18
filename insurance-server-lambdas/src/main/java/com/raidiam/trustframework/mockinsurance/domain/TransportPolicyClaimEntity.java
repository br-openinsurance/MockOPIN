package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
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
@Table(name = "transport_policy_claims")
public class TransportPolicyClaimEntity extends BaseEntity {
    @Id
    @Column(name = "transport_policy_claim_id", unique = true, nullable = false, updatable = false)
    private String transportPolicyClaimId;

    @Column(name = "status")
    private String status;

    @Column(name = "transport_policy_id")
    private String transportPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_policy_id", referencedColumnName = "transport_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private TransportPolicyEntity transportPolicy;

    public InsuranceTransportClaim mapDto() {
        return new InsuranceTransportClaim()
                .identification(this.getTransportPolicyClaimId())
                .status(InsuranceTransportClaim.StatusEnum.fromValue(this.getStatus()))
                .statusAlterationDate(InsuranceLambdaUtils.dateToLocalDate(this.getUpdatedAt()))
                .occurrenceDate(InsuranceLambdaUtils.dateToLocalDate(this.getCreatedAt()))
                .warningDate(InsuranceLambdaUtils.dateToLocalDate(this.getCreatedAt()))
                .amount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                .coverages(List.of(new InsuranceTransportClaimCoverage()
                        .branch("0111")
                        .code(InsuranceTransportClaimCoverage.CodeEnum.ACIDENTES_PESSOAIS_COM_PASSAGEIROS)));
    }

    public InsuranceTransportClaimV2 mapDtoV2() {
        return new InsuranceTransportClaimV2()
                .identification(this.getTransportPolicyClaimId())
                .status(InsuranceTransportClaimV2.StatusEnum.fromValue(this.getStatus()))
                .statusAlterationDate(InsuranceLambdaUtils.dateToLocalDate(this.getUpdatedAt()))
                .occurrenceDate(InsuranceLambdaUtils.dateToLocalDate(this.getCreatedAt()))
                .warningDate(InsuranceLambdaUtils.dateToLocalDate(this.getCreatedAt()))
                .amount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                .coverages(List.of(new InsuranceTransportClaimCoverage()
                        .branch("0111")
                        .code(InsuranceTransportClaimCoverage.CodeEnum.ACIDENTES_PESSOAIS_COM_PASSAGEIROS)));
    }
}
