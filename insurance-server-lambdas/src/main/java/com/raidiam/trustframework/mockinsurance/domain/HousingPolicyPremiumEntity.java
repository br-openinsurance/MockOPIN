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
@Table(name = "housing_policy_premiums")
public class HousingPolicyPremiumEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "housing_policy_premium_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID premiumId;

    @Column(name = "housing_policy_id")
    private UUID housingPolicyId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_policy_id", referencedColumnName = "housing_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private HousingPolicyEntity housingPolicy;

    public InsuranceHousingPremium mapDTO() {
        return new InsuranceHousingPremium()
                .paymentsQuantity(4)
                .amount(new AmountDetails()
                        .amount("16")
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                )
                .coverages(List.of(new InsuranceHousingPremiumCoverage()
                        .branch("0111")
                        .code(InsuranceHousingPremiumCoverage.CodeEnum.DANOS_ELETRICOS)
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
