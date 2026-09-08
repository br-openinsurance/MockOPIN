package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePensionPlanMovementsMovementBenefits;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "pension_plan_contract_movement_benefits")
public class PensionPlanContractMovementBenefitEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "pension_plan_contract_movement_benefit_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID benefitId;

    @Column(name = "pension_plan_contract_id")
    private String pensionPlanContractId;

    @Column(name = "benefit_amount")
    private String benefitAmount;

    @Column(name = "benefit_unit_type")
    private String benefitUnitType;

    @Column(name = "benefit_unit_type_others")
    private String benefitUnitTypeOthers;

    @Column(name = "benefit_unit_code")
    private String benefitUnitCode;

    @Column(name = "benefit_unit_description")
    private String benefitUnitDescription;

    @Column(name = "benefit_currency")
    private String benefitCurrency;

    @Column(name = "benefit_payment_date")
    private LocalDate benefitPaymentDate;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pension_plan_contract_id", referencedColumnName = "pension_plan_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PensionPlanContractEntity pensionPlanContract;

    public InsurancePensionPlanMovementsMovementBenefits mapDTO() {
        return new InsurancePensionPlanMovementsMovementBenefits()
                .benefitAmount(new AmountDetails()
                        .amount(this.getBenefitAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getBenefitUnitType())))
                .benefitPaymentDate(this.getBenefitPaymentDate());
    }
}
