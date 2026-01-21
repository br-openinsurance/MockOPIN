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
@Table(name = "patrimonial_policies")
public class PatrimonialPolicyEntity extends BaseEntity implements HasStatusInterface {

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

    @Column(name = "branch")
    private String branch;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "policy")
    private List<PatrimonialClaimEntity> claims = new ArrayList<>();

    public BaseBrandAndCompanyDataPolicies mapPolicyDto() {
        return new BaseBrandAndCompanyDataPolicies()
                .policyId(this.getPolicyId().toString())
                .productName("Mock Insurer Patrimonial Policy");
    }

    public ResponseInsurancePatrimonialPolicyInfo mapPolicyInfoDto() {
        return new ResponseInsurancePatrimonialPolicyInfo()
            .data(new InsurancePatrimonialPolicyInfo()
                .documentType(InsurancePatrimonialPolicyInfo.DocumentTypeEnum.APOLICE_INDIVIDUAL)
                .policyId(this.getPolicyId().toString())
                .issuanceType(InsurancePatrimonialPolicyInfo.IssuanceTypeEnum.EMISSAO_PROPRIA)
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
                .insuredObjects(List.of(new InsurancePatrimonialInsuredObject()
                    .identification("123456789")
                    .type(InsurancePatrimonialInsuredObject.TypeEnum.CONTRATO)
                    .description("string")
                    .coverages(List.of(new InsurancePatrimonialInsuredObjectCoverage()
                        .branch(this.getBranch())
                        .code(InsurancePatrimonialCoverageCode.IMOVEL_BASICA)
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
                        .feature(InsurancePatrimonialInsuredObjectCoverage.FeatureEnum.MASSIFICADOS)
                        .type(InsurancePatrimonialInsuredObjectCoverage.TypeEnum.PARAMETRICO)
                        .gracePeriod(0)
                        .gracePeriodicity(InsurancePatrimonialInsuredObjectCoverage.GracePeriodicityEnum.DIA)
                        .gracePeriodCountingMethod(InsurancePatrimonialInsuredObjectCoverage.GracePeriodCountingMethodEnum.UTEIS)
                        .gracePeriodStartDate(LocalDate.now())
                        .gracePeriodEndDate(LocalDate.now().plusYears(1))
                        .premiumPeriodicity(InsurancePatrimonialInsuredObjectCoverage.PremiumPeriodicityEnum.MENSAL)
                    ))
                ))
            );
    }

    public ResponseInsurancePatrimonialPolicyInfoV2 mapPolicyInfoDtoV2() {
        return new ResponseInsurancePatrimonialPolicyInfoV2()
            .data(new InsurancePatrimonialPolicyInfoV2()
                .documentType(InsurancePatrimonialPolicyInfoV2.DocumentTypeEnum.APOLICE_INDIVIDUAL)
                .policyId(this.getPolicyId().toString())
                .issuanceType(InsurancePatrimonialPolicyInfoV2.IssuanceTypeEnum.EMISSAO_PROPRIA)
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
                .insureds(List.of(new PersonalInfoV2()
                    .identification("12345678900")
                    .identificationType(PersonalInfoV2.IdentificationTypeEnum.CPF)
                    .name("John Doe")
                    .birthDate(LocalDate.now())
                    .address(new Address()
                        .flagPostCode(Address.FlagPostCodeEnum.NACIONAL)
                        .address((AllOfAddressAddress) new AllOfAddressAddress()
                                .allOfAddressAddressName("Naburo Ykesaki")
                                .allOfAddressAddressNumber("1270")
                                .allOfAddressAddressAddressComplementaryInfo("Fundos")
                                .allOfAddressAddressTownName("Sao Paulo")
                                .allOfAddressAddressCountrySubDivision(EnumCountrySubDivision.SP.toString())
                                .allOfAddressAddressPostCode("10000000")
                                .type(NationalAddress.TypeEnum.AVENIDA)
                                .name("Naburo Ykesaki")
                                .number("1270")
                                .addressComplementaryInfo("Fundos")
                                .districtName("Liberdade")
                                .townName("Sao Paulo")
                                .ibgeTownCode("5002704")
                                .countrySubDivision(EnumCountrySubDivision.SP)
                                .postCode("10000000")))
                ))
                .insuredObjects(List.of(new InsurancePatrimonialInsuredObjectV2()
                    .identification("123456789")
                    .type(InsurancePatrimonialInsuredObjectV2.TypeEnum.CONTRATO)
                    .description("string")
                    .coverages(List.of(new InsurancePatrimonialInsuredObjectCoverage()
                        .branch(this.getBranch())
                        .code(InsurancePatrimonialCoverageCode.IMOVEL_BASICA)
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
                        .feature(InsurancePatrimonialInsuredObjectCoverage.FeatureEnum.MASSIFICADOS)
                        .type(InsurancePatrimonialInsuredObjectCoverage.TypeEnum.PARAMETRICO)
                        .gracePeriod(0)
                        .gracePeriodicity(InsurancePatrimonialInsuredObjectCoverage.GracePeriodicityEnum.DIA)
                        .gracePeriodCountingMethod(InsurancePatrimonialInsuredObjectCoverage.GracePeriodCountingMethodEnum.UTEIS)
                        .gracePeriodStartDate(LocalDate.now())
                        .gracePeriodEndDate(LocalDate.now().plusYears(1))
                        .premiumPeriodicity(InsurancePatrimonialInsuredObjectCoverage.PremiumPeriodicityEnum.MENSAL)
                    ))
                ))
            );
    }

    public ResponseInsurancePatrimonialPremium mapPremiumDto() {
        return new ResponseInsurancePatrimonialPremium()
            .data(new InsurancePremium()
                .paymentsQuantity(4)
                .amount(new AmountDetails()
                    .amount("2000.00")
                    .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL))
                    .currency(CurrencyEnum.BRL)
                )
                .coverages(List.of(new InsurancePremiumCoverage()
                    .branch("0111")
                    .code(InsurancePatrimonialCoverageCode.IMOVEL_BASICA)
                    .premiumAmount(new AmountDetails()
                        .amount("2000.00")
                        .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                        .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL))
                        .currency(CurrencyEnum.BRL)
                    )
                ))
                .payments(List.of(new Payment()
                    .movementDate(LocalDate.now())
                    .movementType(Payment.MovementTypeEnum.LIQUIDACAO_DE_RESTITUICAO_DE_PREMIO)
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
