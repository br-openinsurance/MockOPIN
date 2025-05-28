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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "life_pension_contract_id", referencedColumnName = "life_pension_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private LifePensionContractEntity lifePensionContract;

    public InsuranceLifePensionMovementsMovementBenefits mapDTO() {
        return new InsuranceLifePensionMovementsMovementBenefits()
                .benefitAmount(new AmountDetails()
                        .amount("95.90")
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                .benefitPaymentDate(LocalDate.of(2023, 10, 1));
    }
}
