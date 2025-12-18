package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "transport_policies")
public class TransportPolicyEntity extends BaseEntity implements HasStatusInterface {

    @Id
    @Column(name = "transport_policy_id", unique = true, nullable = false, updatable = false)
    private String transportPolicyId;

    @Column(name = "status")
    private String status;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "proposal_id")
    private String proposalId;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    public BaseBrandAndCompanyDataPolicies mapPolicyDto() {
        return new BaseBrandAndCompanyDataPolicies()
                .policyId(this.getTransportPolicyId())
                .productName("Mock Insurer Transport Policy Plan");
    }

    public ResponseInsuranceTransportPolicyInfo mapInfoDto() {
        return new ResponseInsuranceTransportPolicyInfo()
                .data(new InsuranceTransportPolicyInfoData()
                        .documentType(InsuranceTransportPolicyInfoData.DocumentTypeEnum.fromValue(this.getDocumentType()))
                        .policyId(this.getTransportPolicyId())
                        .issuanceType(InsuranceTransportPolicyInfoData.IssuanceTypeEnum.EMISSAO_PROPRIA)
                        .issuanceDate(LocalDate.now())
                        .termStartDate(LocalDate.now().minusDays(10))
                        .termEndDate(LocalDate.now().plusDays(10))
                        .maxLMG(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                        .proposalId(this.getProposalId())
                        .insureds(List.of(new PersonalInfo()
                                .identification(this.getAccountHolder().getDocumentIdentification())
                                .identificationType(PersonalInfo.IdentificationTypeEnum.CPF)
                                .name(this.getAccountHolder().getAccountHolderName())
                                .birthDate(LocalDate.of(2000, 1, 1))
                                .postCode("10000000")
                                .city("Sao Paulo")
                                .state(PersonalInfo.StateEnum.SP)
                                .country(PersonalInfo.CountryEnum.BRA)
                                .address("Av Naburo Ykesaki, 1270")))
                        .insuredObjects(List.of(new InsuranceTransportInsuredObject()
                                .identification(this.getAccountHolder().getDocumentIdentification())
                                .type(InsuranceTransportInsuredObject.TypeEnum.CONTRATO)
                                .description("contrato")
                                .coverages(List.of(new InsuranceTransportInsuredObjectCoverage()
                                        .branch("0111")
                                        .code(InsuranceTransportInsuredObjectCoverage.CodeEnum.ACIDENTES_PESSOAIS_COM_PASSAGEIROS)
                                        .susepProcessNumber("12345")
                                        .LMI(new AmountDetails().amount("100").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                                        .termStartDate(LocalDate.now().minusDays(10))
                                        .termEndDate(LocalDate.now().plusDays(10))
                                        .isMainCoverage(true)
                                        .feature(InsuranceTransportInsuredObjectCoverage.FeatureEnum.MASSIFICADOS)
                                        .type(InsuranceTransportInsuredObjectCoverage.TypeEnum.PARAMETRICO)
                                        .premiumPeriodicity(InsuranceTransportInsuredObjectCoverage.PremiumPeriodicityEnum.MENSAL)))))
                        .branchInfo(new InsuranceTransportSpecificPolicyInfo()
                                .addEndorsementsItem(new InsuranceTransportSpecificPolicyInfoEndorsements()
                                        .travelType(InsuranceTransportSpecificPolicyInfoEndorsements.TravelTypeEnum.INTERNACIONAL_IMPORTACAO)
                                        .transportType(InsuranceTransportSpecificPolicyInfoEndorsements.TransportTypeEnum.AEREO)
                                        .shipmentsNumber(10)
                                        .branch("0111")
                                        .shipmentsPremium(new AmountDetails().amount("100").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                                        .shipmentsPremiumBRL("2000.00")
                                        .shipmentsInsuredsAmount(new AmountDetails().amount("100").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                                        .minInsuredAmount(new AmountDetails().amount("100").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                                        .maxInsuredAmount(new AmountDetails().amount("100").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                                )
                        ));
    }

    public ResponseInsuranceTransportPolicyInfoV2 mapInfoDtoV2() {
        return new ResponseInsuranceTransportPolicyInfoV2()
                .data(new InsuranceTransportPolicyInfoDataV2()
                        .documentType(InsuranceTransportPolicyInfoDataV2.DocumentTypeEnum.fromValue(this.getDocumentType()))
                        .policyId(this.getTransportPolicyId())
                        .issuanceType(InsuranceTransportPolicyInfoDataV2.IssuanceTypeEnum.EMISSAO_PROPRIA)
                        .issuanceDate(LocalDate.now())
                        .termStartDate(LocalDate.now().minusDays(10))
                        .termEndDate(LocalDate.now().plusDays(10))
                        .maxLMG(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                        .proposalId(this.getProposalId())
                        .insureds(List.of(new PersonalInfoV2()
                                .identification(this.getAccountHolder().getDocumentIdentification())
                                .identificationType(PersonalInfoV2.IdentificationTypeEnum.CPF)
                                .name(this.getAccountHolder().getAccountHolderName())
                                .birthDate(LocalDate.of(2000, 1, 1))
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
                                                .postCode("10000000")))))
                        .insuredObjects(List.of(new InsuranceTransportInsuredObjectV2()
                                .identification(this.getAccountHolder().getDocumentIdentification())
                                .type(InsuranceTransportInsuredObjectV2.TypeEnum.CONTRATO)
                                .description("contrato")
                                .coverages(List.of(new InsuranceTransportInsuredObjectCoverage()
                                        .branch("0111")
                                        .code(InsuranceTransportInsuredObjectCoverage.CodeEnum.ACIDENTES_PESSOAIS_COM_PASSAGEIROS)
                                        .susepProcessNumber("12345")
                                        .LMI(new AmountDetails().amount("100").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                                        .termStartDate(LocalDate.now().minusDays(10))
                                        .termEndDate(LocalDate.now().plusDays(10))
                                        .isMainCoverage(true)
                                        .feature(InsuranceTransportInsuredObjectCoverage.FeatureEnum.MASSIFICADOS)
                                        .type(InsuranceTransportInsuredObjectCoverage.TypeEnum.PARAMETRICO)
                                        .premiumPeriodicity(InsuranceTransportInsuredObjectCoverage.PremiumPeriodicityEnum.MENSAL)))))
                        .branchInfo(new InsuranceTransportSpecificPolicyInfo()
                                .addEndorsementsItem(new InsuranceTransportSpecificPolicyInfoEndorsements()
                                        .travelType(InsuranceTransportSpecificPolicyInfoEndorsements.TravelTypeEnum.INTERNACIONAL_IMPORTACAO)
                                        .transportType(InsuranceTransportSpecificPolicyInfoEndorsements.TransportTypeEnum.AEREO)
                                        .shipmentsNumber(10)
                                        .branch("0111")
                                        .shipmentsPremium(new AmountDetails().amount("100").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                                        .shipmentsPremiumBRL("2000.00")
                                        .shipmentsInsuredsAmount(new AmountDetails().amount("100").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                                        .minInsuredAmount(new AmountDetails().amount("100").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                                        .maxInsuredAmount(new AmountDetails().amount("100").unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM))
                                )
                        ));
    }

    public ResponseInsuranceTransportPremium mapPremiumDto() {
        return new ResponseInsuranceTransportPremium()
                .data(new InsuranceTransportPremium()
                        .paymentsQuantity(3)
                        .amount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                        .coverages(List.of(new InsuranceTransportPremiumCoverage()
                                .branch("0111")
                                .code(InsuranceTransportPremiumCoverage.CodeEnum.ACIDENTES_PESSOAIS_COM_PASSAGEIROS)
                                .premiumAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))))
                        .payments(List.of(new Payment()
                                .movementDate(LocalDate.now().minusDays(5))
                                .movementType(Payment.MovementTypeEnum.LIQUIDACAO_DE_PREMIO)
                                .movementPaymentsNumber("1")
                                .amount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                                .maturityDate(LocalDate.now()))));
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getTransportPolicyId());
    }
}
