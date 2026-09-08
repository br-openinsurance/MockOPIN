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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "life_pension_contract_withdrawals")
public class LifePensionContractWithdrawalEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "life_pension_contract_withdrawal_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID withdrawalId;

    @Column(name = "life_pension_contract_id")
    private UUID lifePensionContractId;

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
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lifePensionContractWithdrawal")
    private List<LifePensionContractWithdrawalFIEEntity> fies = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "life_pension_contract_id", referencedColumnName = "life_pension_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private LifePensionContractEntity lifePensionContract;

    public InsuranceLifePensionWithdrawal mapDTO() {
        return new InsuranceLifePensionWithdrawal()
                .withdrawalOccurence(this.getWithdrawalOccurence())
                .type(InsuranceLifePensionWithdrawalType.fromValue(this.getType()))
                .requestDate(OffsetDateTime.parse(this.getRequestDate()))
                .liquidationDate(OffsetDateTime.parse(this.getLiquidationDate()))
                .nature(InsuranceLifePensionWithdrawalNature.fromValue(this.getNature()))
                .FIE(this.getFies().stream().map(LifePensionContractWithdrawalFIEEntity::getDTO).toList())
                .amount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getAmountUnitType()))
                        .amount(this.getAmount())
                )
                .postedChargedAmount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPostedChargedUnitType()))
                        .amount(this.getPostedChargedAmount())
                );
    }

    public InsuranceLifePensionWithdrawalV2 mapDTOV2() {
        return new InsuranceLifePensionWithdrawalV2()
                .withdrawalOccurence(this.getWithdrawalOccurence())
                .withdrawalInfo(List.of(new InsuranceLifePensionWithdrawalV2WithdrawalInfo()
                        .type(InsuranceLifePensionWithdrawalType.fromValue(this.getType()))
                        .requestDate(OffsetDateTime.parse(this.getRequestDate()))
                        .liquidationDate(OffsetDateTime.parse(this.getLiquidationDate()))
                        .nature(InsuranceLifePensionWithdrawalNature.fromValue(this.getNature()))
                        .FIE(this.getFies().stream().map(LifePensionContractWithdrawalFIEEntity::getDTOV2).toList())
                        .amount(new AmountDetails()
                                .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getAmountUnitType()))
                                .amount(this.getAmount())
                        )
                        .postedChargedAmount(new AmountDetails()
                                .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPostedChargedUnitType()))
                                .amount(this.getPostedChargedAmount())
                        )
                ));
    }
}
