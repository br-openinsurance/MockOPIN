package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "life_pension_contracts")
public class LifePensionContractEntity extends BaseEntity implements HasStatusInterface {
    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "life_pension_contract_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID lifePensionContractId;

    @Column(name = "life_pension_id")
    private String lifePensionId;

    @Column(name = "status")
    private String status;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lifePensionContract")
    private List<LifePensionContractMovementBenefitEntity> movements = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lifePensionContract")
    private List<LifePensionContractPortabilityInfoEntity> portabilities = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lifePensionContract")
    private List<LifePensionContractWithdrawalEntity> withdrawals = new ArrayList<>();

    public ResponseInsuranceLifePensionBrandContracts mapContractDTO() {
        return new ResponseInsuranceLifePensionBrandContracts()
                .certificateId(this.getLifePensionContractId().toString())
                .productName("Mock Insurer Life Pension Contract");
    }

    public ResponseInsuranceLifePensionContractInfo mapContractInfoDTO() {
        return new ResponseInsuranceLifePensionContractInfo()
                .data(new InsuranceLifePensionContractInfo()
                        .certificateId(this.getLifePensionContractId().toString())
                        .productCode("1234")
                        .conjugatedPlan(true)
                        .proposalId("987")
                        .certificateActive(true)
                        .insureds(new InsuranceLifePensionDocumentsInsured()
                                .documentType(InsuranceLifePensionDocumentsInsured.DocumentTypeEnum.CPF)
                                .documentNumber("12345678910")
                                .name("JOAO DA SILVA")
                                .birthDate(LocalDate.of(2021, 5, 1))
                                .gender(InsuranceLifePensionDocumentsInsured.GenderEnum.FEMININO)
                                .postCode("17500001")
                                .townName("Rio de Janeiro")
                                .countrySubDivision(EnumCountrySubDivision.RJ)
                                .countryCode(EnumCountryCode.BRA)
                                .address("Av Naburo Ykesaki, 1270")
                        )
                        .contractingType(InsuranceLifePensionContractingType.INDIVIDUAL)
                        .contractId("681")
                        .planType(InsuranceLifePensionPlanType.AVERBADO)
                        .effectiveDateStart(LocalDate.of(2021, 5, 21))
                        .effectiveDateEnd(LocalDate.of(2023, 5, 21))
                        .periodicity(InsuranceLifePensionPeriodicity.MENSAL)
                        .taxRegime(InsuranceLifePensionTaxRegime.PROGRESSIVO)
                        .suseps(List.of(new InsuranceLifePensionSuseps()
                                .coverageCode("1999")
                                .susepProcessNumber("12345")
                                .structureModality(InsuranceLifePensionSuseps.StructureModalityEnum.BENEFICIO_DEFINIDO)
                                .type(InsuranceLifePensionSuseps.TypeEnum.PGBL)
                                .lockedPlan(false)
                                .qualifiedProposer(false)
                                .benefitPaymentMethod(InsuranceLifePensionSuseps.BenefitPaymentMethodEnum.RENDA)
                                .financialResultReversal(false)
                                .calculationBasis(InsuranceLifePensionSuseps.CalculationBasisEnum.MENSAL)
                                .FIE(List.of(new SusepsFIE()
                                        .FIECNPJ("12345678901234")
                                        .fiEName("RAZÃO SOCIAL")
                                        .fiETradeName("NOME FANTASIA")
                                        .pmbacAmount(new AmountDetails()
                                                .amount("90.85")
                                                .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                                        )
                                        .provisionSurplusAmount(new AmountDetails()
                                                .amount("90.85")
                                                .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                                        )
                                ))
                        ))
                );
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getLifePensionContractId().toString());
    }
}
