package com.raidiam.trustframework.mockinsurance.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Generated;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceHousingInsuredObject;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "housing_policy_insured_objects")
public class HousingPolicyInsuredObjectEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated()
    @Column(name = "housing_policy_insured_object_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID housingPolicyInsuredObjectId;

    @Column(name = "identification")
    private String identification;

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
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "housingPolicyInsuredObject")
    private List<HousingPolicyInsuredObjectCoverageEntity> coverages = new ArrayList<>();

    @Column(name = "housing_policy_id")
    private UUID housingPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_policy_id", referencedColumnName = "housing_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private HousingPolicyEntity housingPolicy;

    public InsuranceHousingInsuredObject mapDto() {
        return new InsuranceHousingInsuredObject()
        .identification(this.getIdentification())
            .type(InsuranceHousingInsuredObject.TypeEnum.valueOf(this.getType()))
            .description(this.getDescription())
            .amount(new AmountDetails()
                    .amount(this.getAmount())
                    .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getUnitType()))
                    .unit(new AmountDetailsUnit()
                            .code(this.getUnitCode())
                            .description(AmountDetailsUnit.DescriptionEnum.valueOf(this.getUnitDescription()))
                    )
            )
            .coverages(this.getCoverages().stream().map(HousingPolicyInsuredObjectCoverageEntity::mapDto).toList());
    }
}