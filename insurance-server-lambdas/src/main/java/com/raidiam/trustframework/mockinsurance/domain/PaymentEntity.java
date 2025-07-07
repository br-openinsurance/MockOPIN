package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "payments")
public class PaymentEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "payment_id", unique = true, nullable = false, updatable = false, insertable = false)
    private UUID paymentId;

    @Column(name = "movement_date")
    private LocalDate movementDate;
    
    @Column(name = "movement_type")
    private String movementType;
    
    @Column(name = "movement_origin")
    private String movementOrigin;

    @Column(name = "movement_payments_number")
    private String movementPaymentsNumber;

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

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @Column(name = "teller_id")
    private String tellerId;
    
    @Column(name = "teller_id_type")
    private String tellerIdType;

    @Column(name = "teller_id_type_others")
    private String tellerIdTypeOthers;

    @Column(name = "teller_name")
    private String tellerName;

    @Column(name = "financial_institution_code")
    private String financialInstitutionCode;

    @Column(name = "payment_type")
    private String paymentType;

    public Payment mapDTO() {
        return new Payment()
                .movementDate(this.getMovementDate())
                .movementType(Payment.MovementTypeEnum.fromValue(this.getMovementType()))
                .movementOrigin(Payment.MovementOriginEnum.fromValue(this.getMovementOrigin()))
                .movementPaymentsNumber(this.getMovementPaymentsNumber())
                .amount(new AmountDetails()
                        .amount(this.getAmount())
                        .unit(new AmountDetailsUnit()
                                .code(this.getUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getCurrency()))
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getUnitType()))
                        .unitTypeOthers(this.getUnitTypeOthers()))
                .maturityDate(this.getMaturityDate())
                .tellerId(this.getTellerId())
                .tellerIdType(Payment.TellerIdTypeEnum.fromValue(this.getTellerIdType()))
                .tellerIdTypeOthers(this.getTellerIdTypeOthers())
                .tellerName(this.getTellerName())
                .financialInstitutionCode(this.getFinancialInstitutionCode())
                .paymentType(Payment.PaymentTypeEnum.fromValue(this.getPaymentType()));
    }
}
