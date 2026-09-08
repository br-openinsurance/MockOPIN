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
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "life_pension_contract_claims")
public class LifePensionContractClaimEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "life_pension_contract_claim_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID claimId;

    @Column(name = "life_pension_contract_id")
    private UUID lifePensionContractId;

    @Column(name = "event_status")
    private String eventStatus;

    @Column(name = "event_alert_date")
    private LocalDate eventAlertDate;

    @Column(name = "event_register_date")
    private LocalDate eventRegisterDate;

    @Column(name = "beneficiary_document")
    private String beneficiaryDocument;

    @Column(name = "beneficiary_document_type")
    private String beneficiaryDocumentType;

    @Column(name = "beneficiary_name")
    private String beneficiaryName;

    @Column(name = "beneficiary_category")
    private String beneficiaryCategory;

    @Column(name = "beneficiary_birth_date")
    private LocalDate beneficiaryBirthDate;

    @Column(name = "income_type")
    private String incomeType;

    @Column(name = "reversed_income")
    private Boolean reversedIncome;

    @Column(name = "income_amount")
    private String incomeAmount;

    @Column(name = "income_unit_type")
    private String incomeUnitType;

    @Column(name = "income_unit_type_others")
    private String incomeUnitTypeOthers;

    @Column(name = "income_unit_code")
    private String incomeUnitCode;

    @Column(name = "income_unit_description")
    private String incomeUnitDescription;

    @Column(name = "income_currency")
    private String incomeCurrency;

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(name = "benefit_amount")
    private Integer benefitAmount;

    @Column(name = "granted_date")
    private LocalDate grantedDate;

    @Column(name = "monetary_update_index")
    private String monetaryUpdateIndex;

    @Column(name = "last_update_date")
    private LocalDate lastUpdateDate;

    @Column(name = "deferment_due_date")
    private LocalDate defermentDueDate;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "life_pension_contract_id", referencedColumnName = "life_pension_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private LifePensionContractEntity lifePensionContract;

    public InsuranceLifePensionClaim mapDTO() {
        return new InsuranceLifePensionClaim()
                .eventInfo(new EventInfo()
                        .eventStatus(EventInfo.EventStatusEnum.fromValue(this.getEventStatus()))
                        .eventAlertDate(this.getEventAlertDate())
                        .eventRegisterDate(this.getEventRegisterDate())
                )
                .incomeInfo(new InsuranceLifePensionClaimIncomeInfo()
                        .beneficiaryDocument(this.getBeneficiaryDocument())
                        .beneficiaryDocumentType(InsuranceLifePensionClaimIncomeInfo.BeneficiaryDocumentTypeEnum.fromValue(this.getBeneficiaryDocumentType()))
                        .beneficiaryName(this.getBeneficiaryName())
                        .beneficiaryCategory(InsuranceLifePensionClaimIncomeInfo.BeneficiaryCategoryEnum.fromValue(this.getBeneficiaryCategory()))
                        .beneficiaryBirthDate(this.getBeneficiaryBirthDate())
                        .incomeType(InsuranceLifePensionClaimIncomeInfo.IncomeTypeEnum.fromValue(this.getIncomeType()))
                        .reversedIncome(this.getReversedIncome())
                        .incomeAmount(new AmountDetails()
                                .amount(this.getIncomeAmount())
                                .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getIncomeUnitType()))
                                .unit(new AmountDetailsUnit()
                                        .code(this.getIncomeUnitCode())
                                        .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getIncomeUnitDescription()))
                                )
                        )
                        .paymentTerms(this.getPaymentTerms())
                        .benefitAmount(this.getBenefitAmount())
                        .grantedDate(this.getGrantedDate())
                        .monetaryUpdateIndex(InsuranceLifePensionClaimIncomeInfo.MonetaryUpdateIndexEnum.fromValue(this.getMonetaryUpdateIndex()))
                        .lastUpdateDate(this.getLastUpdateDate())
                        .defermentDueDate(this.getDefermentDueDate())
                );
    }

    public InsuranceLifePensionClaimV2 mapDTOV2() {
        return new InsuranceLifePensionClaimV2()
                .eventInfo(new EventInfo()
                        .eventStatus(EventInfo.EventStatusEnum.fromValue(this.getEventStatus()))
                        .eventAlertDate(this.getEventAlertDate())
                        .eventRegisterDate(this.getEventRegisterDate())
                )
                .incomeInfo(new InsuranceLifePensionClaimV2IncomeInfo()
                        .beneficiaryDocument(this.getBeneficiaryDocument())
                        .beneficiaryDocumentType(InsuranceLifePensionClaimV2IncomeInfo.BeneficiaryDocumentTypeEnum.fromValue(this.getBeneficiaryDocumentType()))
                        .beneficiaryName(this.getBeneficiaryName())
                        .beneficiaryCategory(InsuranceLifePensionClaimV2IncomeInfo.BeneficiaryCategoryEnum.fromValue(this.getBeneficiaryCategory()))
                        .beneficiaryBirthDate(this.getBeneficiaryBirthDate())
                        .incomeType(InsuranceLifePensionClaimV2IncomeInfo.IncomeTypeEnum.fromValue(this.getIncomeType()))
                        .reversedIncome(this.getReversedIncome())
                        .incomeAmount(new AmountDetails()
                                .amount(this.getIncomeAmount())
                                .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getIncomeUnitType()))
                                .unit(new AmountDetailsUnit()
                                        .code(this.getIncomeUnitCode())
                                        .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getIncomeUnitDescription()))
                                )
                        )
                        .paymentTerms(this.getPaymentTerms())
                        .benefitAmount(this.getBenefitAmount())
                        .grantedDate(this.getGrantedDate())
                        .monetaryUpdateIndex(InsuranceLifePensionClaimV2IncomeInfo.MonetaryUpdateIndexEnum.fromValue(this.getMonetaryUpdateIndex()))
                        .lastUpdateDate(this.getLastUpdateDate())
                        .defermentDueDate(this.getDefermentDueDate())
                );
    }
}
