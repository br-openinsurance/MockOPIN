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
import java.util.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "life_pension_contract_portabilities")
public class LifePensionContractPortabilityInfoEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "life_pension_contract_portability_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID portabilityId;

    @Column(name = "life_pension_contract_id")
    private UUID lifePensionContractId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "life_pension_contract_id", referencedColumnName = "life_pension_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private LifePensionContractEntity lifePensionContract;

    public InsuranceLifePensionPortabilityPortabilityInfo mapDTO() {
        return new InsuranceLifePensionPortabilityPortabilityInfo()
                .direction(InsuranceLifePensionPortabilityPortabilityInfo.DirectionEnum.ENTRADA)
                .type(InsuranceLifePensionPortabilityPortabilityInfo.TypeEnum.PARCIAL)
                .amount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                        .amount("90.85")
                )
                .requestDate(OffsetDateTime.parse("2022-05-20T08:30:00Z"))
                .liquidationDate(OffsetDateTime.parse("2022-05-20T08:30:00Z"))
                .postedChargedAmount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                        .amount("90.85")
                )
                .sourceEntity("12345678901234")
                .targetEntity("12345678901234")
                .susepProcess("12345")
                .taxRegime(InsuranceLifePensionPortabilityPortabilityInfo.TaxRegimeEnum.PROGRESSIVO)
                .FIE(List.of(new InsuranceLifePensionPortabilityFIE()
                        .FIECNPJ("12345678901234")
                        .fiEName("RAZÃO SOCIAL")
                        .fiETradeName("NOME FANTASIA")
                        .portedType(InsuranceLifePensionPortabilityFIE.PortedTypeEnum.ORIGEM)
                )
        );
    }

    public InsuranceLifePensionPortabilityV2PortabilityInfo mapDTOV2() {
        return new InsuranceLifePensionPortabilityV2PortabilityInfo()
                .direction(InsuranceLifePensionPortabilityV2PortabilityInfo.DirectionEnum.ENTRADA)
                .type(InsuranceLifePensionPortabilityV2PortabilityInfo.TypeEnum.PARCIAL)
                .amount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                        .amount("90.85")
                )
                .requestDate(OffsetDateTime.parse("2022-05-20T08:30:00Z"))
                .liquidationDate(OffsetDateTime.parse("2022-05-20T08:30:00Z"))
                .postedChargedAmount(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                        .amount("90.85")
                )
                .sourceEntity("12345678901234")
                .targetEntity("12345678901234")
                .susepProcess("12345")
                .taxRegime(InsuranceLifePensionPortabilityV2PortabilityInfo.TaxRegimeEnum.PROGRESSIVO)
                .FIE(List.of(new InsuranceLifePensionPortabilityV2FIE()
                        .FIECNPJ("12345678901234")
                        .fiEName("RAZÃO SOCIAL")
                        .fiETradeName("NOME FANTASIA")
                        .portedType(InsuranceLifePensionPortabilityV2FIE.PortedTypeEnum.ORIGEM)
                )
        );
    }
}
