package com.raidiam.trustframework.mockinsurance.domain;

import java.util.List;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePatrimonialInsuredObject;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePatrimonialInsuredObjectV2;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "patrimonial_insured_objects")
public class PatrimonialInsuredObjectEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "patrimonial_insured_object_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID patrimonialInsuredObjectId;

    @Column(name = "policy_id")
    private UUID policyId;

    @Column(name = "identification")
    private String identification;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "patrimonialInsuredObject")
    private List<PatrimonialInsuredObjectCoverageEntity> coverages;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", referencedColumnName = "policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PatrimonialPolicyEntity patrimonialPolicy;

    public InsurancePatrimonialInsuredObject mapDto() {
        return new InsurancePatrimonialInsuredObject()
            .identification(this.getIdentification())
            .type(InsurancePatrimonialInsuredObject.TypeEnum.valueOf(this.getType()))
            .description(this.getDescription())
            .coverages(this.getCoverages().stream().map(PatrimonialInsuredObjectCoverageEntity::mapDto).toList());
    }

    public InsurancePatrimonialInsuredObjectV2 mapDtoV2() {
        return new InsurancePatrimonialInsuredObjectV2()
            .identification(this.getIdentification())
            .type(InsurancePatrimonialInsuredObjectV2.TypeEnum.valueOf(this.getType()))
            .description(this.getDescription())
            .coverages(this.getCoverages().stream().map(PatrimonialInsuredObjectCoverageEntity::mapDto).toList());
    }
}
