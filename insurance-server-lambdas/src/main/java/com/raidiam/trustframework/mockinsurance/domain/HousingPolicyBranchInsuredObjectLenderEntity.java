package com.raidiam.trustframework.mockinsurance.domain;

import java.util.UUID;

import org.hibernate.annotations.Generated;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceHousingSpecificInsuredObjectLenders;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "housing_policy_branch_insured_object_lenders")
public class HousingPolicyBranchInsuredObjectLenderEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated()
    @Column(name = "housing_policy_branch_insured_object_lender_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID housingPolicyBranchInsuredObjectLenderId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "cnpj_number")
    private String cnpjNumber;

    @Column(name = "housing_policy_branch_insured_object_id")
    private UUID housingPolicyBranchInsuredObjectId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_policy_branch_insured_object_id", referencedColumnName = "housing_policy_branch_insured_object_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private HousingPolicyBranchInsuredObjectEntity housingPolicyBranchInsuredObject;

    public InsuranceHousingSpecificInsuredObjectLenders mapDto() {
        return new InsuranceHousingSpecificInsuredObjectLenders()
            .companyName(this.getCompanyName())
            .cnpjNumber(this.getCnpjNumber());
    }
}