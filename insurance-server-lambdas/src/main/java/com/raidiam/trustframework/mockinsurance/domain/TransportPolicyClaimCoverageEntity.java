package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceTransportClaimCoverage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "transport_policy_claim_coverages")
public class TransportPolicyClaimCoverageEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "transport_policy_claim_coverage_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID coverageId;

    @Column(name = "transport_policy_claim_id")
    private String transportPolicyClaimId;

    @Column(name = "insured_object_id")
    private String insuredObjectId;

    @Column(name = "branch")
    private String branch;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "warning_date")
    private LocalDate warningDate;

    @Column(name = "third_party_claim_date")
    private LocalDate thirdPartyClaimDate;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_policy_claim_id", referencedColumnName = "transport_policy_claim_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private TransportPolicyClaimEntity transportPolicyClaim;

    public InsuranceTransportClaimCoverage mapDto() {
        return new InsuranceTransportClaimCoverage()
                .insuredObjectId(this.getInsuredObjectId())
                .branch(this.getBranch())
                .code(InsuranceTransportClaimCoverage.CodeEnum.fromValue(this.getCode()))
                .description(this.getDescription())
                .warningDate(this.getWarningDate())
                .thirdPartyClaimDate(this.getThirdPartyClaimDate());
    }
}
