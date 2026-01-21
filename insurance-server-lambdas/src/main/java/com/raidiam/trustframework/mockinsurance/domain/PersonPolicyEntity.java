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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "person_policies")
public class PersonPolicyEntity extends BaseEntity implements HasStatusInterface {
    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "person_policy_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID personPolicyId;

    @Column(name = "person_id")
    private String personId;

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
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "personPolicy")
    private List<PersonPolicyPremiumEntity> premiums = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "personPolicy")
    private List<PersonPolicyClaimEntity> claims = new ArrayList<>();

    public ResponseInsurancePersonBrandPolicies mapPolicyDTO() {
        return new ResponseInsurancePersonBrandPolicies()
                .policyId(this.getPersonPolicyId().toString())
                .productName("Mock Insurer Financial Risk Policy");
    }

    public ResponseInsurancePersonPolicyInfo mapPolicyInfoDTO() {
        var withdrawalInfo = new WithdrawalInfo();
        withdrawalInfo.add(new WithdrawalInfoInner()
                .occurrenceWithdrawal(false)
        );
        var portabilityInfo = new PortabilityInfo();
        portabilityInfo.add(new PortabilityInfoInner()
                .occurrencePortability(false)
        );

        return new ResponseInsurancePersonPolicyInfo()
                .data(new InsurancePersonPolicyInfoData()
                        .documentType(InsurancePersonPolicyInfoData.DocumentTypeEnum.APOLICE_INDIVIDUAL)
                        .policyId("1111111")
                        .susepProcessNumber("string")
                        .groupCertificateId("string")
                        .issuanceType(InsurancePersonPolicyInfoData.IssuanceTypeEnum.EMISSAO_PROPRIA)
                        .issuanceDate(LocalDate.of(2022, 12, 31))
                        .termStartDate(LocalDate.of(2022, 12, 31))
                        .termEndDate(LocalDate.of(2023, 12, 31))
                        .leadInsurerCode("string")
                        .leadInsurerPolicyId("string")
                        .withdrawals(withdrawalInfo)
                        .proposalId("string")
                        .insureds(List.of(new PersonalInfo()
                                .identification("12345678900")
                                .identificationType(PersonalInfo.IdentificationTypeEnum.CPF)
                                .identificationTypeOthers("RNE")
                                .name("Nome Sobrenome")
                                .birthDate(LocalDate.of(1999, 1, 1))
                                .postCode("10000000")
                                .email("example@email.com")
                                .city("string")
                                .state(PersonalInfo.StateEnum.AC)
                                .country(PersonalInfo.CountryEnum.BRA)
                                .address("address")
                        ))
                        .beneficiaries(List.of(new BeneficiaryInfo()
                                .identification("12345678900")
                                .identificationType(BeneficiaryInfo.IdentificationTypeEnum.CPF)
                                .identificationTypeOthers("RNE")
                                .name("Nome Sobrenome")
                        ))
                        .intermediaries(List.of(new Intermediary()
                                .type(Intermediary.TypeEnum.CORRETOR)
                                .identification("12345678900")
                                .brokerId("353970887")
                                .identificationType(Intermediary.IdentificationTypeEnum.CPF)
                                .identificationTypeOthers("RNE")
                                .name("Nome Sobrenome")
                                .postCode("10000000")
                                .city("string")
                                .state("AC")
                                .country(Intermediary.CountryEnum.BRA)
                                .address("address")
                        ))
                        .insuredObjects(List.of(new InsurancePersonInsuredObject()
                                .type(InsurancePersonInsuredObject.TypeEnum.AUTOMOVEL)
                                .description("string")
                                .amount(new AmountDetails()
                                        .amount("9871667727569.12")
                                        .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                                        .unit(new AmountDetailsUnit()
                                                .code("Br")
                                                .description(AmountDetailsUnit.DescriptionEnum.BRL)
                                        )
                                )
                                .coverages(List.of(new InsurancePersonInsuredObjectCoverage()
                                        .type(InsurancePersonInsuredObjectCoverage.TypeEnum.PARAMETRICO)
                                        .feature(InsurancePersonInsuredObjectCoverage.FeatureEnum.MASSIFICADOS)
                                        .branch("0111")
                                        .code(InsurancePersonInsuredObjectCoverage.CodeEnum.CIRURGIA)
                                        .description("string")
                                        .internalCode("string")
                                        .susepProcessNumber("string")
                                        .LMI(new AmountDetails()
                                                .amount("100")
                                                .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                                        )
                                        .isLMISublimit(true)
                                        .termStartDate(LocalDate.of(2022, 12, 31))
                                        .termEndDate(LocalDate.of(2023, 12, 31))
                                        .isMainCoverage(true)
                                        .feature(InsurancePersonInsuredObjectCoverage.FeatureEnum.MASSIFICADOS)
                                        .type(InsurancePersonInsuredObjectCoverage.TypeEnum.PARAMETRICO)
                                        .triggerEvent(InsurancePersonInsuredObjectCoverage.TriggerEventEnum.INVALIDEZ)
                                        .financialType(InsurancePersonInsuredObjectCoverage.FinancialTypeEnum.CAPITALIZACAO)
                                        .benefitPaymentModality(InsurancePersonInsuredObjectCoverage.BenefitPaymentModalityEnum.RENDA)
                                ))
                        ))
                        .pmBaC(new InsurancePersonPolicyInfoPMBaC()
                                .pmbacAmount(new AmountDetails()
                                        .amount("9871667727569.12")
                                        .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                                        .unit(new AmountDetailsUnit()
                                                .code("Br")
                                                .description(AmountDetailsUnit.DescriptionEnum.BRL)
                                        )
                                )
                        )
                        .portabilities(portabilityInfo)
                );
    }

    public ResponseInsurancePersonPolicyInfoV2 mapPolicyInfoDTOV2() {
        var withdrawalInfo = new WithdrawalInfo();
        withdrawalInfo.add(new WithdrawalInfoInner()
                .occurrenceWithdrawal(false)
        );
        var portabilityInfo = new PortabilityInfo();
        portabilityInfo.add(new PortabilityInfoInner()
                .occurrencePortability(false)
        );

        return new ResponseInsurancePersonPolicyInfoV2()
                .data(new InsurancePersonPolicyInfoDataV2()
                        .documentType(InsurancePersonPolicyInfoDataV2.DocumentTypeEnum.APOLICE_INDIVIDUAL)
                        .policyId("1111111")
                        .susepProcessNumber("string")
                        .groupCertificateId("string")
                        .issuanceType(InsurancePersonPolicyInfoDataV2.IssuanceTypeEnum.EMISSAO_PROPRIA)
                        .issuanceDate(LocalDate.of(2022, 12, 31))
                        .termStartDate(LocalDate.of(2022, 12, 31))
                        .termEndDate(LocalDate.of(2023, 12, 31))
                        .leadInsurerCode("string")
                        .leadInsurerPolicyId("string")
                        .withdrawals(withdrawalInfo)
                        .proposalId("string")
                        .insureds(List.of(new PersonalInfoV2()
                                .identification("12345678900")
                                .identificationType(PersonalInfoV2.IdentificationTypeEnum.CPF)
                                .identificationTypeOthers("RNE")
                                .name("Nome Sobrenome")
                                .birthDate(LocalDate.of(1999, 1, 1))
                                .email("example@email.com")
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
                        .beneficiaries(List.of(new BeneficiaryInfo()
                                .identification("12345678900")
                                .identificationType(BeneficiaryInfo.IdentificationTypeEnum.CPF)
                                .identificationTypeOthers("RNE")
                                .name("Nome Sobrenome")
                        ))
                        .intermediaries(List.of(new IntermediaryV2()
                                .type(IntermediaryV2.TypeEnum.CORRETOR)
                                .identification("12345678900")
                                .brokerId("353970887")
                                .identificationType(IntermediaryV2.IdentificationTypeEnum.CPF)
                                .identificationTypeOthers("RNE")
                                .name("Nome Sobrenome")
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
                        .insuredObjects(List.of(new InsurancePersonInsuredObjectV2()
                                .type(InsurancePersonInsuredObjectV2.TypeEnum.AUTOMOVEL)
                                .description("string")
                                .amount(new AmountDetails()
                                        .amount("9871667727569.12")
                                        .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                                        .unit(new AmountDetailsUnit()
                                                .code("Br")
                                                .description(AmountDetailsUnit.DescriptionEnum.BRL)
                                        )
                                )
                                .coverages(List.of(new InsurancePersonInsuredObjectCoverageV2()
                                        .type(InsurancePersonInsuredObjectCoverageV2.TypeEnum.PARAMETRICO)
                                        .feature(InsurancePersonInsuredObjectCoverageV2.FeatureEnum.MASSIFICADOS)
                                        .branch("0111")
                                        .code(InsurancePersonInsuredObjectCoverageV2.CodeEnum.CIRURGIA)
                                        .description("string")
                                        .internalCode("string")
                                        .susepProcessNumber("string")
                                        .LMI(new AmountDetails()
                                                .amount("100")
                                                .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                                        )
                                        .isLMISublimit(true)
                                        .termStartDate(LocalDate.of(2022, 12, 31))
                                        .termEndDate(LocalDate.of(2023, 12, 31))
                                        .isMainCoverage(true)
                                        .feature(InsurancePersonInsuredObjectCoverageV2.FeatureEnum.MASSIFICADOS)
                                        .type(InsurancePersonInsuredObjectCoverageV2.TypeEnum.PARAMETRICO)
                                        .triggerEvent(InsurancePersonInsuredObjectCoverageV2.TriggerEventEnum.INVALIDEZ)
                                        .financialType(InsurancePersonInsuredObjectCoverageV2.FinancialTypeEnum.CAPITALIZACAO)
                                        .benefitPaymentModality(InsurancePersonInsuredObjectCoverageV2.BenefitPaymentModalityEnum.RENDA)
                                ))
                        ))
                        .pmBaC(new InsurancePersonPolicyInfoPMBaC()
                                .pmbacAmount(new AmountDetails()
                                        .amount("9871667727569.12")
                                        .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                                        .unit(new AmountDetailsUnit()
                                                .code("Br")
                                                .description(AmountDetailsUnit.DescriptionEnum.BRL)
                                        )
                                )
                        )
                        .portabilities(portabilityInfo)
                );
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getPersonPolicyId().toString());
    }
}
