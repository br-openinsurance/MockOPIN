package com.raidiam.trustframework.mockinsurance.domain;

import java.util.UUID;

import org.hibernate.annotations.Generated;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceHousingSpecificInsured;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "housing_policy_branch_insureds")
public class HousingPolicyBranchInsuredEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated()
    @Column(name = "housing_policy_branch_insured_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID housingPolicyBranchInsuredId;

    @Column(name = "identification")
    private String identification;

    @Column(name = "identification_type")
    private String identificationType;

    @Column(name = "housing_policy_id")
    private UUID housingPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_policy_id", referencedColumnName = "housing_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private HousingPolicyEntity housingPolicy;

    public InsuranceHousingSpecificInsured mapDto() {
        return new InsuranceHousingSpecificInsured()
            .identification(this.getIdentification())
            .identificationType(InsuranceHousingSpecificInsured.IdentificationTypeEnum.valueOf(this.getIdentificationType()));
    }
}