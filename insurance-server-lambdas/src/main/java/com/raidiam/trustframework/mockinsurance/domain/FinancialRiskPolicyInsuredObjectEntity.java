package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "financial_risk_policy_insured_objects")
public class FinancialRiskPolicyInsuredObjectEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "financial_risk_policy_insured_object_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID insuredObjectId;

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

    @Column(name = "currency")
    private String currency;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "financialRiskInsuredObject")
    private List<FinancialRiskPolicyInsuredObjectCoverageEntity> coverages = new ArrayList<>();


    @Column(name = "financial_risk_policy_id")
    private UUID financialRiskPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_risk_policy_id", referencedColumnName = "financial_risk_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private FinancialRiskPolicyEntity financialRiskPolicy;

    public InsuranceFinancialRiskInsuredObject mapDTO() {
        return new InsuranceFinancialRiskInsuredObject()
                .identification(this.getIdentification())
                .type(InsuranceFinancialRiskInsuredObject.TypeEnum.fromValue(this.getType()))
                .typeAdditionalInfo(this.getTypeAdditionalInfo())
                .description(this.getDescription())
                .amount(new AmountDetails()
                        .amount(this.getAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getUnitType()))
                        .unitTypeOthers(this.getUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getCurrency())))
                .coverages(this.getCoverages().stream().map(FinancialRiskPolicyInsuredObjectCoverageEntity::mapDTO).toList());
    }
}
