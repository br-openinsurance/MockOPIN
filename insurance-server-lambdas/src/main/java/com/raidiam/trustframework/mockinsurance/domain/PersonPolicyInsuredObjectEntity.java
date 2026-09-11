package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePersonInsuredObject;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePersonInsuredObjectV2;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "person_insured_objects")
public class PersonPolicyInsuredObjectEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "person_insured_object_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID personInsuredObjectId;

    @Column(name = "person_policy_id")
    private UUID personPolicyId;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

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
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "personInsuredObject")
    private List<PersonPolicyInsuredObjectCoverageEntity> coverages;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_policy_id", referencedColumnName = "person_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PersonPolicyEntity personPolicy;

    public InsurancePersonInsuredObject mapDTO() {
        return new InsurancePersonInsuredObject()
                .type(InsurancePersonInsuredObject.TypeEnum.valueOf(this.getType()))
                .description(this.getDescription())
                .amount(new AmountDetails()
                        .amount(this.getAmount())
                        .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getUnitType()))
                        .unit(new AmountDetailsUnit()
                                .code(this.getUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.valueOf(this.getUnitDescription()))
                        )
                )
                .coverages(this.getCoverages().stream().map(PersonPolicyInsuredObjectCoverageEntity::mapDTO).toList());
    }

    public InsurancePersonInsuredObjectV2 mapDTOV2() {
        return new InsurancePersonInsuredObjectV2()
                .type(InsurancePersonInsuredObjectV2.TypeEnum.valueOf(this.getType()))
                .description(this.getDescription())
                .amount(new AmountDetails()
                        .amount(this.getAmount())
                        .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getUnitType()))
                        .unit(new AmountDetailsUnit()
                                .code(this.getUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.valueOf(this.getUnitDescription()))
                        )
                )
                .coverages(this.getCoverages().stream().map(PersonPolicyInsuredObjectCoverageEntity::mapDTOV2).toList());
    }
}
