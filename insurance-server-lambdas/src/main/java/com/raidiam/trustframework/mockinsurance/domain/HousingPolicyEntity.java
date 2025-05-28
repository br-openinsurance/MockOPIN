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
@Table(name = "housing_policies")
public class HousingPolicyEntity extends BaseEntity implements HasStatusInterface {
    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "housing_policy_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID housingPolicyId;

    @Column(name = "housing_id")
    private String housingId;

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
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "housingPolicy")
    private List<HousingPolicyPremiumEntity> premiums = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "housingPolicy")
    private List<HousingPolicyClaimEntity> claims = new ArrayList<>();

    public BaseBrandAndCompanyDataPolicies mapPolicyDTO() {
        return new BaseBrandAndCompanyDataPolicies()
                .policyId(this.getHousingPolicyId().toString())
                .productName("Mock Insurer Financial Risk Policy");
    }

    public ResponseInsuranceHousingPolicyInfo mapPolicyInfoDTO() {
        return new ResponseInsuranceHousingPolicyInfo()
                .data(new InsuranceHousingPolicyInfoData()
                        .documentType(InsuranceHousingPolicyInfoData.DocumentTypeEnum.APOLICE_INDIVIDUAL)
                        .policyId("1111111")
                        .susepProcessNumber("string")
                        .groupCertificateId("string")
                        .issuanceType(InsuranceHousingPolicyInfoData.IssuanceTypeEnum.EMISSAO_PROPRIA)
                        .issuanceDate(LocalDate.of(2022, 12, 31))
                        .termStartDate(LocalDate.of(2022, 12, 31))
                        .termEndDate(LocalDate.of(2023, 12, 31))
                        .leadInsurerCode("string")
                        .leadInsurerPolicyId("string")
                        .maxLMG(new AmountDetails()
                                .amount("9871667727569.12")
                                .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                                .unit(new AmountDetailsUnit()
                                        .code("Br")
                                        .description(AmountDetailsUnit.DescriptionEnum.BRL)
                                )
                        )
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
                        .insuredObjects(List.of(new InsuranceHousingInsuredObject()
                                .identification("string")
                                .type(InsuranceHousingInsuredObject.TypeEnum.AUTOMOVEL)
                                .description("string")
                                .amount(new AmountDetails()
                                        .amount("9871667727569.12")
                                        .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                                        .unit(new AmountDetailsUnit()
                                                .code("Br")
                                                .description(AmountDetailsUnit.DescriptionEnum.BRL)
                                        )
                                )
                                .coverages(List.of(new InsuranceHousingInsuredObjectCoverage()
                                        .branch("0111")
                                        .code(InsuranceHousingInsuredObjectCoverage.CodeEnum.DANOS_ELETRICOS)
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
                                        .feature(InsuranceHousingInsuredObjectCoverage.FeatureEnum.MASSIFICADOS)
                                        .type(InsuranceHousingInsuredObjectCoverage.TypeEnum.PARAMETRICO)
                                        .gracePeriod(0)
                                        .gracePeriodicity(InsuranceHousingInsuredObjectCoverage.GracePeriodicityEnum.DIA)
                                        .gracePeriodCountingMethod(InsuranceHousingInsuredObjectCoverage.GracePeriodCountingMethodEnum.UTEIS)
                                        .premiumPeriodicity(InsuranceHousingInsuredObjectCoverage.PremiumPeriodicityEnum.ANUAL)
                                ))
                        ))
                        .branchInfo(new InsuranceHousingSpecificPolicyInfo()
                                .insuredObjects(List.of(new InsuranceHousingSpecificInsuredObject()
                                        .identification("string")
                                        .propertyType(InsuranceHousingSpecificInsuredObject.PropertyTypeEnum.APARTAMENTO)
                                        .postCode("10000000")
                                        .interestRate("10.00")
                                        .costRate("10.00")
                                        .updateIndex(InsuranceHousingSpecificInsuredObject.UpdateIndexEnum.IGPDI_FGV)
                                        .lenders(List.of(new InsuranceHousingSpecificInsuredObjectLenders()
                                                .companyName("string")
                                                .cnpjNumber("12345678901234")
                                        ))
                                ))
                                .insureds(List.of(new InsuranceHousingSpecificInsured()
                                        .identification("12345678900")
                                        .identificationType(InsuranceHousingSpecificInsured.IdentificationTypeEnum.CPF)
                                ))
                        )
                );
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getHousingPolicyId().toString());
    }
}
