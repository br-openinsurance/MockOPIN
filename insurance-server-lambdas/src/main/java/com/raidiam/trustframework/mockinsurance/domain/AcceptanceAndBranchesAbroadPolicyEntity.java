package com.raidiam.trustframework.mockinsurance.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
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
@Table(name = "acceptance_and_branches_abroad_policies")
public class AcceptanceAndBranchesAbroadPolicyEntity extends BaseEntity  implements HasStatusInterface {

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
    private List<AcceptanceAndBranchesAbroadClaimEntity> claims = new ArrayList<>();

    public BaseBrandAndCompanyDataPolicies mapPolicyDto() {
        return new BaseBrandAndCompanyDataPolicies()
                .policyId(this.getPolicyId().toString())
                .productName("Mock Insurer Acceptance And Branches Abroad Policy");
    }

    public ResponseInsuranceAcceptanceAndBranchesAbroadPolicyInfo mapPolicyInfoDto() {
        return new ResponseInsuranceAcceptanceAndBranchesAbroadPolicyInfo()
            .data(new InsuranceAcceptanceAndBranchesAbroadPolicyInfo()
                .documentType(InsuranceAcceptanceAndBranchesAbroadPolicyInfo.DocumentTypeEnum.APOLICE_INDIVIDUAL)
                .policyId(this.getPolicyId().toString())
                .issuanceType(InsuranceAcceptanceAndBranchesAbroadPolicyInfo.IssuanceTypeEnum.EMISSAO_PROPRIA)
                .issuanceDate(LocalDate.now())
                .termStartDate(LocalDate.now())
                .termEndDate(LocalDate.now().plusYears(1))
                .maxLMG(new AmountDetails()
                    .amount("100.00")
                    .unitType(UnitTypeEnum.PORCENTAGEM)
                    .unit(new AmountDetailsUnit().code("R$").description(DescriptionEnum.BRL))
                )
                .proposalId("123456")
                .insureds(List.of(new PersonalInfo()
                    .identification("12345678900")
                    .identificationType(PersonalInfo.IdentificationTypeEnum.CPF)
                    .name("Nome Sobrenome")
                    .postCode("10000000")
                    .birthDate(LocalDate.now())
                    .city("string")
                    .state(PersonalInfo.StateEnum.SP)
                    .country(PersonalInfo.CountryEnum.BRA)
                    .address("Rua John Doe, 13")
                ))
                .insuredObjects(List.of(new InsuranceAcceptanceAndBranchesAbroadInsuredObject()
                    .identification("string")
                    .type(InsuranceAcceptanceAndBranchesAbroadInsuredObject.TypeEnum.CONTRATO)
                    .description("string")
                    .coverages(List.of(new InsuranceAcceptanceAndBranchesAbroadInsuredObjectCoverage()
                        .branch("0111")
                        .code(InsuranceAcceptanceAndBranchesAbroadInsuredObjectCoverage.CodeEnum.OUTRAS)
                        .susepProcessNumber("string")
                        .LMI(new AmountDetails()
                            .amount("3.02")
                            .unitType(UnitTypeEnum.PORCENTAGEM)
                            .unit(new AmountDetailsUnit().code("R$").description(DescriptionEnum.BRL))
                        )
                        .isLMISublimit(true)
                        .termStartDate(LocalDate.now())
                        .termEndDate(LocalDate.now().plusYears(1))
                        .isMainCoverage(true)
                        .feature(InsuranceAcceptanceAndBranchesAbroadInsuredObjectCoverage.FeatureEnum.MASSIFICADOS)
                        .type(InsuranceAcceptanceAndBranchesAbroadInsuredObjectCoverage.TypeEnum.PARAMETRICO)
                        .gracePeriod(0)
                        .gracePeriodicity(InsuranceAcceptanceAndBranchesAbroadInsuredObjectCoverage.GracePeriodicityEnum.DIA)
                        .gracePeriodCountingMethod(InsuranceAcceptanceAndBranchesAbroadInsuredObjectCoverage.GracePeriodCountingMethodEnum.UTEIS)
                        .gracePeriodStartDate(LocalDate.now())
                        .gracePeriodEndDate(LocalDate.now().plusYears(1))
                        .premiumPeriodicity(InsuranceAcceptanceAndBranchesAbroadInsuredObjectCoverage.PremiumPeriodicityEnum.MENSAL)
                    ))
                ))
                .branchInfo(new InsuranceAcceptanceAndBranchesAbroadSpecificPolicyInfo()
                    .riskCountry(InsuranceAcceptanceAndBranchesAbroadSpecificPolicyInfo.RiskCountryEnum.BRA)
                    .hasForum(true)
                    .transferorId("12345678912")
                    .transferorName("Nome Sobrenome")
                    .groupBranches(List.of("0111"))
                )
            );
    }

    public ResponseInsuranceAcceptanceAndBranchesAbroadPolicyInfoV2 mapPolicyInfoDtoV2() {
        return new ResponseInsuranceAcceptanceAndBranchesAbroadPolicyInfoV2()
            .data(new InsuranceAcceptanceAndBranchesAbroadPolicyInfoV2()
                .documentType(InsuranceAcceptanceAndBranchesAbroadPolicyInfoV2.DocumentTypeEnum.APOLICE_INDIVIDUAL)
                .policyId(this.getPolicyId().toString())
                .issuanceType(InsuranceAcceptanceAndBranchesAbroadPolicyInfoV2.IssuanceTypeEnum.EMISSAO_PROPRIA)
                .issuanceDate(LocalDate.now())
                .termStartDate(LocalDate.now())
                .termEndDate(LocalDate.now().plusYears(1))
                .maxLMG(new AmountDetails()
                    .amount("100.00")
                    .unitType(UnitTypeEnum.PORCENTAGEM)
                    .unit(new AmountDetailsUnit().code("R$").description(DescriptionEnum.BRL))
                )
                .proposalId("123456")
                .insureds(List.of(new PersonalInfoV2()
                    .identification("12345678900")
                    .identificationType(PersonalInfoV2.IdentificationTypeEnum.CPF)
                    .name("Nome Sobrenome")
                    .birthDate(LocalDate.now())
                    .address(new Address()
                        .flagPostCode(Address.FlagPostCodeEnum.NACIONAL)
                        .address((AllOfAddressAddress) new AllOfAddressAddress()
                            .type(NationalAddress.TypeEnum.AVENIDA)
                            .name("Naburo Ykesaki")
                            .number("1270")
                            .districtName("Liberdade")
                            .townName("Sao Paulo")
                            .ibgeTownCode("5002704")
                            .countrySubDivision(EnumCountrySubDivision.SP)
                            .postCode("10000000")))
                ))
                .insuredObjects(List.of(new InsuranceAcceptanceAndBranchesAbroadInsuredObjectV2()
                    .identification("string")
                    .type(InsuranceAcceptanceAndBranchesAbroadInsuredObjectV2.TypeEnum.CONTRATO)
                    .description("string")
                    .coverages(List.of(new InsuranceAcceptanceAndBranchesAbroadInsuredObjectCoverage()
                        .branch("0111")
                        .code(InsuranceAcceptanceAndBranchesAbroadInsuredObjectCoverage.CodeEnum.OUTRAS)
                        .susepProcessNumber("string")
                        .LMI(new AmountDetails()
                            .amount("3.02")
                            .unitType(UnitTypeEnum.PORCENTAGEM)
                            .unit(new AmountDetailsUnit().code("R$").description(DescriptionEnum.BRL))
                        )
                        .isLMISublimit(true)
                        .termStartDate(LocalDate.now())
                        .termEndDate(LocalDate.now().plusYears(1))
                        .isMainCoverage(true)
                        .feature(InsuranceAcceptanceAndBranchesAbroadInsuredObjectCoverage.FeatureEnum.MASSIFICADOS)
                        .type(InsuranceAcceptanceAndBranchesAbroadInsuredObjectCoverage.TypeEnum.PARAMETRICO)
                        .gracePeriod(0)
                        .gracePeriodicity(InsuranceAcceptanceAndBranchesAbroadInsuredObjectCoverage.GracePeriodicityEnum.DIA)
                        .gracePeriodCountingMethod(InsuranceAcceptanceAndBranchesAbroadInsuredObjectCoverage.GracePeriodCountingMethodEnum.UTEIS)
                        .gracePeriodStartDate(LocalDate.now())
                        .gracePeriodEndDate(LocalDate.now().plusYears(1))
                        .premiumPeriodicity(InsuranceAcceptanceAndBranchesAbroadInsuredObjectCoverage.PremiumPeriodicityEnum.MENSAL)
                    ))
                ))
                .branchInfo(new InsuranceAcceptanceAndBranchesAbroadSpecificPolicyInfo()
                    .riskCountry(InsuranceAcceptanceAndBranchesAbroadSpecificPolicyInfo.RiskCountryEnum.BRA)
                    .hasForum(true)
                    .transferorId("12345678912")
                    .transferorName("Nome Sobrenome")
                    .groupBranches(List.of("0111"))
                )
            );
    }

    public ResponseInsuranceAcceptanceAndBranchesAbroadPremium mapPremiumDto() {
        return new ResponseInsuranceAcceptanceAndBranchesAbroadPremium()
            .data(new InsuranceAcceptanceAndBranchesAbroadPremium()
                .paymentsQuantity("4")
                .amount(new AmountDetails()
                    .amount("05")
                    .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL))
                )
                .coverages(List.of(new InsuranceAcceptanceAndBranchesAbroadPremiumCoverage()
                    .branch("0111")
                    .code(InsuranceAcceptanceAndBranchesAbroadPremiumCoverage.CodeEnum.OUTRAS)
                    .premiumAmount(new AmountDetails()
                        .amount("8")
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                        .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL))
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
                    )
                    .maturityDate(LocalDate.now())
                ))
            );
    }

    public ResponseInsuranceAcceptanceAndBranchesAbroadPremiumV2 mapPremiumDtoV2() {
        return new ResponseInsuranceAcceptanceAndBranchesAbroadPremiumV2()
            .data(new InsuranceAcceptanceAndBranchesAbroadPremiumV2()
                .paymentsQuantity("4")
                .amount(new AmountDetails()
                    .amount("05")
                    .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL))
                )
                .coverages(List.of(new InsuranceAcceptanceAndBranchesAbroadPremiumCoverage()
                    .branch("0111")
                    .code(InsuranceAcceptanceAndBranchesAbroadPremiumCoverage.CodeEnum.OUTRAS)
                    .premiumAmount(new AmountDetails()
                        .amount("8")
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                        .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL))
                    )
                ))
                .payments(List.of(new PaymentV2()
                    .movementDate(LocalDate.now())
                    .movementType(PaymentV2.MovementTypeEnum.LIQUIDACAO_DE_PREMIO)
                    .movementPaymentsNumber("4")
                    .amount(new AmountDetails()
                        .amount("7.27")
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                        .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL))
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
