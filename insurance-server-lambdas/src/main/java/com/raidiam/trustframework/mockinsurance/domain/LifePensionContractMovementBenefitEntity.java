package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "life_pension_contract_movement_benefits")
public class LifePensionContractMovementBenefitEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "life_pension_contract_movement_benefit_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID benefitId;

    @Column(name = "life_pension_contract_id")
    private UUID lifePensionContractId;

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
    @JoinColumn(name = "life_pension_contract_id", referencedColumnName = "life_pension_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private LifePensionContractEntity lifePensionContract;

    public InsuranceLifePensionMovementsMovementBenefits mapDTO() {
        return new InsuranceLifePensionMovementsMovementBenefits()
                .benefitAmount(new AmountDetails()
                        .amount(this.getBenefitAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getBenefitUnitType())))
                .benefitPaymentDate(this.getBenefitPaymentDate());
    }
}
