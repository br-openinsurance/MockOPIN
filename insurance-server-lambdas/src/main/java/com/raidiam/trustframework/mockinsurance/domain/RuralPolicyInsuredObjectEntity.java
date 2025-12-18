package com.raidiam.trustframework.mockinsurance.domain;

import java.util.List;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit.DescriptionEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralInsuredObject;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceRuralInsuredObjectV2;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "rural_policy_insured_objects")
public class RuralPolicyInsuredObjectEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "rural_policy_insured_object_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID ruralPolicyInsuredObjectId;

    @Column(name = "rural_policy_id")
    private UUID ruralPolicyId;

    @Column(name = "identification")
    private String identification;

    @Column(name = "type")
    private String type;

    @Column(name = "type_additional_info")
    private String typeAdditionalInfo;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private String amount;

    @Column(name = "unit_type")
    private String unitType;

    @Column(name = "unit_type_others")
    private String unitTypeOthers;

    @Column(name = "unit_code")
    private String unitCode;

    @Column(name = "unit_description")
    private String unitDescription;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruralPolicyInsuredObject")
    private List<RuralPolicyInsuredObjectCoverageEntity> coverages;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rural_policy_id", referencedColumnName = "rural_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private RuralPolicyEntity ruralPolicy;

    public InsuranceRuralInsuredObject mapDto() {
        return new InsuranceRuralInsuredObject()
            .identification(this.getIdentification())
            .type(InsuranceRuralInsuredObject.TypeEnum.valueOf(this.getType()))
            .typeAdditionalInfo(this.getTypeAdditionalInfo())
            .description(this.getDescription())
            .amount(new AmountDetails()
                .amount(this.getAmount())
                .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getUnitType()))
                .unitTypeOthers(this.getUnitTypeOthers())
                .unit(new AmountDetailsUnit().code(this.getUnitCode()).description(DescriptionEnum.valueOf(this.getUnitDescription())))
            )
            .coverages(this.getCoverages().stream().map(RuralPolicyInsuredObjectCoverageEntity::mapDto).toList());
    }

    public InsuranceRuralInsuredObjectV2 mapDtoV2() {
        return new InsuranceRuralInsuredObjectV2()
            .identification(this.getIdentification())
            .type(InsuranceRuralInsuredObjectV2.TypeEnum.valueOf(this.getType()))
            .typeAdditionalInfo(this.getTypeAdditionalInfo())
            .description(this.getDescription())
            .amount(new AmountDetails()
                .amount(this.getAmount())
                .unitType(AmountDetails.UnitTypeEnum.valueOf(this.getUnitType()))
                .unitTypeOthers(this.getUnitTypeOthers())
                .unit(new AmountDetailsUnit().code(this.getUnitCode()).description(DescriptionEnum.valueOf(this.getUnitDescription())))
            )
            .coverages(this.getCoverages().stream().map(RuralPolicyInsuredObjectCoverageEntity::mapDto).toList());
    }
}
