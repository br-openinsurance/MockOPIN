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
@Table(name = "life_pension_contract_portability_fies")
public class LifePensionContractPortabilityFIEEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "life_pension_contract_portability_fie_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID portabilityFieId;

    @Column(name = "fie_cnpj")
    private String fieCnpj;

    @Column(name = "fie_name")
    private String fieName;

    @Column(name = "fie_trade_name")
    private String fieTradeName;

    @Column(name = "ported_type")
    private String portedType;

    @Column(name = "life_pension_contract_portability_id")
    private UUID lifePensionContractPortabilityId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "life_pension_contract_portability_id", referencedColumnName = "life_pension_contract_portability_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private LifePensionContractPortabilityInfoEntity lifePensionContractPortability;

    public InsuranceLifePensionPortabilityFIE getDTO() {
        return new InsuranceLifePensionPortabilityFIE()
                .FIECNPJ(this.getFieCnpj())
                .fiEName(this.getFieName())
                .fiETradeName(this.getFieTradeName())
                .portedType(InsuranceLifePensionPortabilityFIE.PortedTypeEnum.fromValue(this.getPortedType()));
    }

    public InsuranceLifePensionPortabilityV2FIE getDTOV2() {
        return new InsuranceLifePensionPortabilityV2FIE()
                .FIECNPJ(this.getFieCnpj())
                .fiEName(this.getFieName())
                .fiETradeName(this.getFieTradeName())
                .portedType(InsuranceLifePensionPortabilityV2FIE.PortedTypeEnum.fromValue(this.getPortedType()));
    }
}
