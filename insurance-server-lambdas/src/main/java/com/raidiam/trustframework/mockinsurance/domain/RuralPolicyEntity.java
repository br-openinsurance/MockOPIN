package com.raidiam.trustframework.mockinsurance.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails.CurrencyEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails.UnitTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit.DescriptionEnum;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "rural_policies")
public class RuralPolicyEntity extends BaseEntity implements HasStatusInterface {

    @Id
    @GeneratedValue
    @Column(name = "policy_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID policyId;

    @Column(name = "insurance_id")
    private String insuranceId;

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
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "policy")
    private List<RuralClaimEntity> claims = new ArrayList<>();

    public BaseBrandAndCompanyDataPolicies mapPolicyDto() {
        return new BaseBrandAndCompanyDataPolicies()
                .policyId(this.getPolicyId().toString())
                .productName("Mock Insurer Rural Policy");
    }

    public ResponseInsuranceRuralPolicyInfo mapPolicyInfoDto() {
        return new ResponseInsuranceRuralPolicyInfo()
            .data(new InsuranceRuralPolicyInfo()
                .documentType(InsuranceRuralPolicyInfo.DocumentTypeEnum.APOLICE_INDIVIDUAL)
                .policyId(this.getPolicyId().toString())
                .issuanceType(InsuranceRuralPolicyInfo.IssuanceTypeEnum.EMISSAO_PROPRIA)
                .issuanceDate(LocalDate.now())
                .termStartDate(LocalDate.now())
                .termEndDate(LocalDate.now().plusYears(1))
                .maxLMG(new AmountDetails()
                    .amount("2000.00")
                    .unitType(UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(DescriptionEnum.BRL))
                    .currency(CurrencyEnum.BRL)
                )
                .proposalId("123456")
                .insureds(List.of(new PersonalInfo()
                    .identification("12345678900")
                    .identificationType(PersonalInfo.IdentificationTypeEnum.CPF)
                    .name("John Doe")
                    .postCode("10000000")
                    .birthDate(LocalDate.now())
                    .city("Rio Branco")
                    .state(PersonalInfo.StateEnum.AC)
                    .country(PersonalInfo.CountryEnum.BRA)
                    .address("Rua John Doe, 13")
                ))
                .insuredObjects(List.of(new InsuranceRuralInsuredObject()
                    .identification("123456789")
                    .type(InsuranceRuralInsuredObject.TypeEnum.CONTRATO)
                    .description("string")
                    .coverages(List.of(new InsuranceRuralInsuredObjectCoverage()
                        .branch("0111")
                        .code(InsuranceRuralInsuredObjectCoverage.CodeEnum.GRANIZO)
                        .susepProcessNumber("string")
                        .LMI(new AmountDetails()
                            .amount("2000.00")
                            .unitType(UnitTypeEnum.MONETARIO)
                            .unit(new AmountDetailsUnit().code("R$").description(DescriptionEnum.BRL))
                            .currency(CurrencyEnum.BRL)
                        )
                        .isLMISublimit(true)
                        .termStartDate(LocalDate.now())
                        .termEndDate(LocalDate.now().plusYears(1))
                        .isMainCoverage(true)
                        .feature(InsuranceRuralInsuredObjectCoverage.FeatureEnum.MASSIFICADOS)
                        .type(InsuranceRuralInsuredObjectCoverage.TypeEnum.PARAMETRICO)
                        .gracePeriod(0)
                        .gracePeriodicity(InsuranceRuralInsuredObjectCoverage.GracePeriodicityEnum.DIA)
                        .gracePeriodCountingMethod(InsuranceRuralInsuredObjectCoverage.GracePeriodCountingMethodEnum.UTEIS)
                        .gracePeriodStartDate(LocalDate.now())
                        .gracePeriodEndDate(LocalDate.now().plusYears(1))
                        .premiumPeriodicity(InsuranceRuralInsuredObjectCoverage.PremiumPeriodicityEnum.MENSAL)
                    ))
                ))
            );
    }

    public ResponseInsuranceRuralPremium mapPremiumDto() {
        return new ResponseInsuranceRuralPremium()
            .data(new InsuranceRuralPremium()
                .paymentsQuantity(4)
                .amount(new AmountDetails()
                    .amount("2000.00")
                    .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL))
                    .currency(CurrencyEnum.BRL)
                )
                .coverages(List.of(new InsuranceRuralPremiumCoverage()
                    .branch("0111")
                    .code(InsuranceRuralPremiumCoverage.CodeEnum.GRANIZO)
                    .premiumAmount(new AmountDetails()
                        .amount("2000.00")
                        .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                        .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL))
                        .currency(CurrencyEnum.BRL)
                    )
                ))
                .payments(List.of(new Payment()
                    .movementDate(LocalDate.now())
                    .movementType(Payment.MovementTypeEnum.LIQUIDACAO_DE_PREMIO)
                    .movementPaymentsNumber("4")
                    .amount(new AmountDetails()
                        .amount("7.27")
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                        .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL))
                        .currency(CurrencyEnum.BRL)
                    )
                    .maturityDate(LocalDate.now())
                ))
            );
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getPolicyId().toString());
    }
}
