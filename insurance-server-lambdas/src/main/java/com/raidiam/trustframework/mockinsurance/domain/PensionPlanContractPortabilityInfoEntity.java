package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.InsurancePensionPlanPortabilityPortabilityInfo;
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
@Table(name = "pension_plan_contract_portabilities")
public class PensionPlanContractPortabilityInfoEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "pension_plan_contract_portability_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID portabilityId;

    @Column(name = "pension_plan_contract_id")
    private String pensionPlanContractId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pension_plan_contract_id", referencedColumnName = "pension_plan_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PensionPlanContractEntity pensionPlanContract;

    public InsurancePensionPlanPortabilityPortabilityInfo mapDTO() {
        return new InsurancePensionPlanPortabilityPortabilityInfo()
                .direction(InsurancePensionPlanPortabilityPortabilityInfo.DirectionEnum.ENTRADA)
                .type(InsurancePensionPlanPortabilityPortabilityInfo.TypeEnum.PARCIAL)
                .amount(new AmountDetails().unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM).amount("90.85"))
                .requestDate(OffsetDateTime.parse("2022-05-20T08:30:00Z"))
                .liquidationDate(OffsetDateTime.parse("2022-05-20T08:30:00Z"))
                .chargingValue(new AmountDetails().unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM).amount("90.85"))
                .sourceEntity("12345678901234")
                .targetEntity("12345678901234")
                .susepProcess("12345");
    }
}
