package com.raidiam.trustframework.mockinsurance.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Generated;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceHousingSpecificInsuredObject;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceHousingSpecificInsuredObjectV2;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "housing_policy_branch_insured_objects")
public class HousingPolicyBranchInsuredObjectEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated()
    @Column(name = "housing_policy_branch_insured_object_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID housingPolicyBranchInsuredObjectId;

    @Column(name = "identification")
    private String identification;

    @Column(name = "property_type")
    private String propertyType;

    @Column(name = "postcode")
    private String postcode;

    @Column(name = "interest_rate")
    private String interestRate;

    @Column(name = "cost_rate")
    private String costRate;

    @Column(name = "update_index")
    private String updateIndex;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "housingPolicyBranchInsuredObject")
    private List<HousingPolicyBranchInsuredObjectLenderEntity> lenders = new ArrayList<>();

    @Column(name = "housing_policy_id")
    private UUID housingPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_policy_id", referencedColumnName = "housing_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private HousingPolicyEntity housingPolicy;

    public InsuranceHousingSpecificInsuredObject mapDto() {
        return new InsuranceHousingSpecificInsuredObject()
            .identification(this.getIdentification())
            .propertyType(InsuranceHousingSpecificInsuredObject.PropertyTypeEnum.valueOf(this.getPropertyType()))
            .postCode(this.getPostcode())
            .interestRate(this.getInterestRate())
            .costRate(this.getCostRate())
            .updateIndex(InsuranceHousingSpecificInsuredObject.UpdateIndexEnum.valueOf(this.getUpdateIndex()))
            .lenders(this.getLenders().stream().map(HousingPolicyBranchInsuredObjectLenderEntity::mapDto).toList());
    }

    public InsuranceHousingSpecificInsuredObjectV2 mapDtoV2() {
        return new InsuranceHousingSpecificInsuredObjectV2()
            .identification(this.getIdentification())
            .propertyType(InsuranceHousingSpecificInsuredObjectV2.PropertyTypeEnum.valueOf(this.getPropertyType()))
            .postCode(this.getPostcode())
            .interestRate(this.getInterestRate())
            .costRate(this.getCostRate())
            .updateIndex(InsuranceHousingSpecificInsuredObjectV2.UpdateIndexEnum.valueOf(this.getUpdateIndex()))
            .lenders(this.getLenders().stream().map(HousingPolicyBranchInsuredObjectLenderEntity::mapDtoV2).toList());
    }
}