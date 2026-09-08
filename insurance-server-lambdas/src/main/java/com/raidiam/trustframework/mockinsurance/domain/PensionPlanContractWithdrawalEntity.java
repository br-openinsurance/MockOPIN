package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePensionPlanWithdrawal;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePensionPlanWithdrawalV2;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePensionPlanWithdrawalV2WithdrawalInfo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "pension_plan_contract_withdrawals")
public class PensionPlanContractWithdrawalEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "pension_plan_contract_withdrawal_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID withdrawalId;

    @Column(name = "pension_plan_contract_id")
    private String pensionPlanContractId;

    @Column(name = "withdrawal_occurence")
    private Boolean withdrawalOccurence;

    @Column(name = "type")
    private String type;

    @Column(name = "nature")
    private String nature;

    @Column(name = "request_date")
    private String requestDate;

    @Column(name = "liquidation_date")
    private String liquidationDate;

    @Column(name = "amount")
    private String amount;

    @Column(name = "amount_unit_type")
    private String amountUnitType;

    @Column(name = "amount_unit_type_others")
    private String amountUnitTypeOthers;

    @Column(name = "amount_unit_code")
    private String amountUnitCode;

    @Column(name = "amount_unit_description")
    private String amountUnitDescription;

    @Column(name = "amount_currency")
    private String amountCurrency;

    @Column(name = "posted_charged_amount")
    private String postedChargedAmount;

    @Column(name = "posted_charged_unit_type")
    private String postedChargedUnitType;

    @Column(name = "posted_charged_unit_type_others")
    private String postedChargedUnitTypeOthers;

    @Column(name = "posted_charged_unit_code")
    private String postedChargedUnitCode;

    @Column(name = "posted_charged_unit_description")
    private String postedChargedUnitDescription;

    @Column(name = "posted_charged_currency")
    private String postedChargedCurrency;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pension_plan_contract_id", referencedColumnName = "pension_plan_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PensionPlanContractEntity pensionPlanContract;

    public InsurancePensionPlanWithdrawal mapDTO() {
        return new InsurancePensionPlanWithdrawal()
                .withdrawalOccurence(this.getWithdrawalOccurence())
                .type(InsurancePensionPlanWithdrawal.TypeEnum.fromValue(this.getType()))
                .requestDate(OffsetDateTime.parse(this.getRequestDate()))
                .liquidationDate(OffsetDateTime.parse(this.getLiquidationDate()))
                .nature(InsurancePensionPlanWithdrawal.NatureEnum.fromValue(this.getNature()))
                .amount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getAmountUnitType()))
                        .amount(this.getAmount()))
                .postedChargedAmount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPostedChargedUnitType()))
                        .amount(this.getPostedChargedAmount()));
    }

    public InsurancePensionPlanWithdrawalV2 mapDTOV2() {
        return new InsurancePensionPlanWithdrawalV2()
                .withdrawalOccurence(this.getWithdrawalOccurence())
                .withdrawalInfo(List.of(new InsurancePensionPlanWithdrawalV2WithdrawalInfo()
                    .type(InsurancePensionPlanWithdrawalV2WithdrawalInfo.TypeEnum.fromValue(this.getType()))
                    .requestDate(OffsetDateTime.parse(this.getRequestDate()))
                    .liquidationDate(OffsetDateTime.parse(this.getLiquidationDate()))
                    .nature(InsurancePensionPlanWithdrawalV2WithdrawalInfo.NatureEnum.fromValue(this.getNature()))
                    .amount(new AmountDetails()
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getAmountUnitType()))
                            .amount(this.getAmount()))
                    .postedChargedAmount(new AmountDetails()
                            .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPostedChargedUnitType()))
                            .amount(this.getPostedChargedAmount()))
                ));
    }
}
