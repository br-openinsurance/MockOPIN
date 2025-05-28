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
@Table(name = "financial_risk_policies")
public class FinancialRiskPolicyEntity extends BaseEntity implements HasStatusInterface {
    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "financial_risk_policy_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID financialRiskPolicyId;

    @Column(name = "financial_risk_id")
    private String financialRiskId;

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
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "financialRiskPolicy")
    private List<FinancialRiskPolicyPremiumEntity> premiums = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "financialRiskPolicy")
    private List<FinancialRiskPolicyClaimEntity> claims = new ArrayList<>();

    public BaseBrandAndCompanyDataPolicies mapPolicyDTO() {
        return new BaseBrandAndCompanyDataPolicies()
                .policyId(this.getFinancialRiskPolicyId().toString())
                .productName("Mock Insurer Financial Risk Policy");
    }

    public ResponseInsuranceFinancialRiskPolicyInfo mapPolicyInfoDTO() {
        return new ResponseInsuranceFinancialRiskPolicyInfo()
                .data(new InsuranceFinancialRiskPolicyInfoData()
                        .documentType(InsuranceFinancialRiskPolicyInfoData.DocumentTypeEnum.APOLICE_INDIVIDUAL)
                        .policyId("1111111")
                        .susepProcessNumber("string")
                        .groupCertificateId("string")
                        .issuanceType(InsuranceFinancialRiskPolicyInfoData.IssuanceTypeEnum.EMISSAO_PROPRIA)
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
                        .insuredObjects(List.of(new InsuranceFinancialRiskInsuredObject()
                                .identification("string")
                                .type(InsuranceFinancialRiskInsuredObject.TypeEnum.AUTOMOVEL)
                                .description("string")
                                .amount(new AmountDetails()
                                        .amount("9871667727569.12")
                                        .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                                        .unit(new AmountDetailsUnit()
                                                .code("Br")
                                                .description(AmountDetailsUnit.DescriptionEnum.BRL)
                                        )
                                )
                                .coverages(List.of(new InsuranceFinancialRiskInsuredObjectCoverage()
                                        .branch("0111")
                                        .code(InsuranceFinancialRiskInsuredObjectCoverage.CodeEnum.PROTECAO_DE_BENS)
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
                                        .feature(InsuranceFinancialRiskInsuredObjectCoverage.FeatureEnum.MASSIFICADOS)
                                        .type(InsuranceFinancialRiskInsuredObjectCoverage.TypeEnum.PARAMETRICO)
                                        .gracePeriod(0)
                                        .gracePeriodicity(InsuranceFinancialRiskInsuredObjectCoverage.GracePeriodicityEnum.DIA)
                                        .gracePeriodCountingMethod(InsuranceFinancialRiskInsuredObjectCoverage.GracePeriodCountingMethodEnum.UTEIS)
                                        .premiumPeriodicity(InsuranceFinancialRiskInsuredObjectCoverage.PremiumPeriodicityEnum.ANUAL)
                                ))
                        ))
                        .branchInfo(new InsuranceStopLossSpecificPolicyInfo()
                                .identification("string")
                                .userGroup("string")
                                .technicalSurplus("10.00")
                        )
                );
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getFinancialRiskPolicyId().toString());
    }
}
