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
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "responsibility_policy_premiums")
public class ResponsibilityPolicyPremiumEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "responsibility_policy_premium_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID premiumId;

    @Column(name = "responsibility_policy_id")
    private UUID responsibilityPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsibility_policy_id", referencedColumnName = "responsibility_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private ResponsibilityPolicyEntity responsibilityPolicy;

    public InsuranceResponsibilityPremium mapDTO() {
        return new InsuranceResponsibilityPremium()
                .paymentsQuantity(4)
                .amount(new AmountDetails()
                        .amount("16")
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                )
                .coverages(List.of(new InsuranceResponsibilityPremiumCoverage()
                        .branch("0111")
                        .code(InsuranceResponsibilityPremiumCoverage.CodeEnum.ALAGAMENTO_E_OU_INUNDACAO)
                        .premiumAmount(new AmountDetails()
                                .amount("1680.71")
                                .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                        )
                ))
                .payments(List.of(new Payment()
                        .movementDate(LocalDate.of(2023, 10, 1))
                        .movementType(Payment.MovementTypeEnum.COMPENSACAO_FINANCEIRA)
                        .movementOrigin(Payment.MovementOriginEnum.DIRETA)
                        .movementPaymentsNumber("str")
                        .amount(new AmountDetails()
                                .amount("680.71")
                                .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                                .unit(new AmountDetailsUnit()
                                        .code("Br")
                                        .description(AmountDetailsUnit.DescriptionEnum.BRL)
                                )
                        )
                        .maturityDate(LocalDate.of(2023, 10, 1))
                        .tellerId("string")
                        .tellerIdType(Payment.TellerIdTypeEnum.CPF)
                        .tellerIdTypeOthers("RNE")
                        .tellerName("string")
                        .financialInstitutionCode("string")
                        .paymentType(Payment.PaymentTypeEnum.BOLETO)
                ));
    }
}
