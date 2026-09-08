package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "life_pension_contract_withdrawal_fies")
public class LifePensionContractWithdrawalFIEEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "life_pension_contract_withdrawal_fie_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID withdrawalFieId;

    @Column(name = "fie_cnpj")
    private String fieCnpj;

    @Column(name = "fie_name")
    private String fieName;

    @Column(name = "fie_trade_name")
    private String fieTradeName;

    @Column(name = "life_pension_contract_withdrawal_id")
    private UUID lifePensionContractWithdrawalId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "life_pension_contract_withdrawal_id", referencedColumnName = "life_pension_contract_withdrawal_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private LifePensionContractWithdrawalEntity lifePensionContractWithdrawal;

    public InsuranceLifePensionWithdrawalFIE getDTO() {
        return new InsuranceLifePensionWithdrawalFIE()
                .FIECNPJ(this.getFieCnpj())
                .fiEName(this.getFieName())
                .fiETradeName(this.getFieTradeName());
    }

    public InsuranceLifePensionWithdrawalV2FIE getDTOV2() {
        return new InsuranceLifePensionWithdrawalV2FIE()
                .FIECNPJ(this.getFieCnpj())
                .fiEName(this.getFieName())
                .fiETradeName(this.getFieTradeName());
    }
}
