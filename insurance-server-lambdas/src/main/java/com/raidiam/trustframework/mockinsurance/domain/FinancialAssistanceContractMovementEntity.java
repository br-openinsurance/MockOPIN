package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceFinancialAssistanceMovements;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "financial_assistance_contract_movements")
public class FinancialAssistanceContractMovementEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "financial_assistance_contract_movement_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID financialAssistanceContractMovementId;

    @Column(name = "financial_assistance_contract_id")
    private String financialAssistanceContractId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_assistance_contract_id", referencedColumnName = "financial_assistance_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private FinancialAssistanceContractEntity financialAssistanceContract;

    public InsuranceFinancialAssistanceMovements mapDto() {
        return new InsuranceFinancialAssistanceMovements()
                .updatedDebitAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                .remainingCounterInstallmentsQuantity(4)
                .remainingUnpaidCounterInstallmentsQuantity(4);
    }
}
