package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceTransportCoverage;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceTransportCoverageV2;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "transport_policy_coverages")
public class TransportPolicyCoverageEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "transport_policy_coverage_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID coverageId;

    @Column(name = "transport_policy_id")
    private String transportPolicyId;

    @Column(name = "branch")
    private String branch;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "deductible_id")
    private UUID deductibleId;

    @Column(name = "pos_id")
    private UUID posId;

    @OneToOne(cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "deductible_id", referencedColumnName = "reference_id", insertable = false, updatable = false)
    private DeductibleEntity deductible;

    @OneToOne(cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "pos_id", referencedColumnName = "pos_id", insertable = false, updatable = false)
    private POSEntity pos;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_policy_id", referencedColumnName = "transport_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private TransportPolicyEntity transportPolicy;

    public InsuranceTransportCoverage mapDto() {
        return new InsuranceTransportCoverage()
                .branch(this.getBranch())
                .code(InsuranceTransportCoverage.CodeEnum.fromValue(this.getCode()))
                .description(this.getDescription())
                .deductible(this.getDeductible().mapDTO())
                .POS(this.getPos().mapDTO());
    }

    public InsuranceTransportCoverageV2 mapDtoV2() {
        return new InsuranceTransportCoverageV2()
                .branch(this.getBranch())
                .code(InsuranceTransportCoverageV2.CodeEnum.fromValue(this.getCode()))
                .description(this.getDescription())
                .deductible(this.getDeductible().mapDTOV2())
                .POS(this.getPos().mapDTOV2());
    }
}
