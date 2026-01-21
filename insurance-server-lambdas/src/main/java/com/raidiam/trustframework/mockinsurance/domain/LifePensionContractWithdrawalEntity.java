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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "life_pension_contract_id", referencedColumnName = "life_pension_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private LifePensionContractEntity lifePensionContract;

    public InsuranceLifePensionWithdrawal mapDTO() {
        return new InsuranceLifePensionWithdrawal()
                .withdrawalOccurence(true)
                .type(InsuranceLifePensionWithdrawalType.PARCIAL)
                .requestDate(OffsetDateTime.parse("2022-05-20T08:30:00Z"))
                .liquidationDate(OffsetDateTime.parse("2022-05-20T08:30:00Z"))
                .nature(InsuranceLifePensionWithdrawalNature.RESGATE_REGULAR)
                .FIE(List.of(new InsuranceLifePensionWithdrawalFIE()
                        .FIECNPJ("12345678901234")
                        .fiEName("RAZÃO SOCIAL")
                        .fiETradeName("NOME FANTASIA")
                ))
                .amount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                        .amount("90.85")
                )
                .postedChargedAmount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                        .amount("90.85")
                );
    }

    public InsuranceLifePensionWithdrawalV2 mapDTOV2() {
        return new InsuranceLifePensionWithdrawalV2()
                .withdrawalOccurence(true)
                .withdrawalInfo(List.of(new InsuranceLifePensionWithdrawalV2WithdrawalInfo()
                        .type(InsuranceLifePensionWithdrawalType.PARCIAL)
                        .requestDate(OffsetDateTime.parse("2022-05-20T08:30:00Z"))
                        .liquidationDate(OffsetDateTime.parse("2022-05-20T08:30:00Z"))
                        .nature(InsuranceLifePensionWithdrawalNature.RESGATE_REGULAR)
                        .FIE(List.of(new InsuranceLifePensionWithdrawalV2FIE()
                                .FIECNPJ("12345678901234")
                                .fiEName("RAZÃO SOCIAL")
                                .fiETradeName("NOME FANTASIA")
                        ))
                        .amount(new AmountDetails()
                                .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                                .amount("90.85")
                        )
                        .postedChargedAmount(new AmountDetails()
                                .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                                .amount("90.85")
                        )       
                ));
    }
}
