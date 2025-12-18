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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pension_plan_contract_id", referencedColumnName = "pension_plan_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PensionPlanContractEntity pensionPlanContract;

    public InsurancePensionPlanWithdrawal mapDTO() {
        return new InsurancePensionPlanWithdrawal()
                .withdrawalOccurence(true)
                .type(InsurancePensionPlanWithdrawal.TypeEnum.PARCIAL)
                .requestDate(OffsetDateTime.parse("2022-05-20T08:30:00Z"))
                .liquidationDate(OffsetDateTime.parse("2022-05-20T08:30:00Z"))
                .nature(InsurancePensionPlanWithdrawal.NatureEnum.RESGATE_REGULAR)
                .amount(new AmountDetails().unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM).amount("90.85"))
                .postedChargedAmount(new AmountDetails().unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM).amount("90.85"));
    }

    public InsurancePensionPlanWithdrawalV2 mapDTOV2() {
        return new InsurancePensionPlanWithdrawalV2()
                .withdrawalOccurence(true)
                .withdrawalInfo(new InsurancePensionPlanWithdrawalV2WithdrawalInfo()
                    .type(InsurancePensionPlanWithdrawalV2WithdrawalInfo.TypeEnum.PARCIAL)
                    .requestDate(OffsetDateTime.parse("2022-05-20T08:30:00Z"))
                    .liquidationDate(OffsetDateTime.parse("2022-05-20T08:30:00Z"))
                    .nature(InsurancePensionPlanWithdrawalV2WithdrawalInfo.NatureEnum.RESGATE_REGULAR)
                    .amount(new AmountDetails().unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM).amount("90.85"))
                    .postedChargedAmount(new AmountDetails().unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM).amount("90.85"))
                );
    }
}
