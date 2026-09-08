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

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "conjugated_plan")
    private Boolean conjugatedPlan;

    @Column(name = "proposal_id")
    private String proposalId;

    @Column(name = "certificate_active")
    private Boolean certificateActive;

    @Column(name = "contracting_type")
    private String contractingType;

    @Column(name = "contract_id")
    private String contractId;

    @Column(name = "plan_type")
    private String planType;

    @Column(name = "effective_date_start")
    private LocalDate effectiveDateStart;

    @Column(name = "effective_date_end")
    private LocalDate effectiveDateEnd;

    @Column(name = "periodicity")
    private String periodicity;

    @Column(name = "tax_regime")
    private String taxRegime;

    @Column(name = "insured_document_type")
    private String insuredDocumentType;

    @Column(name = "insured_document_number")
    private String insuredDocumentNumber;

    @Column(name = "insured_name")
    private String insuredName;

    @Column(name = "insured_birth_date")
    private LocalDate insuredBirthDate;

    @Column(name = "insured_gender")
    private String insuredGender;

    @Column(name = "insured_post_code")
    private String insuredPostCode;

    @Column(name = "insured_town_name")
    private String insuredTownName;

    @Column(name = "insured_country_sub_division")
    private String insuredCountrySubDivision;

    @Column(name = "insured_country_code")
    private String insuredCountryCode;

    @Column(name = "insured_address")
    private String insuredAddress;

    @Column(name = "insured_district_name")
    private String insuredDistrictName;

    @Column(name = "insured_ibge_town_code")
    private String insuredIbgeTownCode;

    @Column(name = "insured_address_name")
    private String insuredAddressName;

    @Column(name = "insured_address_number")
    private String insuredAddressNumber;

    @Column(name = "insured_address_complementary_info")
    private String insuredAddressComplementaryInfo;

    @Column(name = "insured_address_type")
    private String insuredAddressType;

    @Column(name = "insured_address_flag_post_code")
    private String insuredAddressFlagPostCode;

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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lifePensionContract")
    private List<LifePensionContractSusepEntity> suseps = new ArrayList<>();

    public ResponseInsuranceLifePensionBrandContracts mapContractDTO() {
        return new ResponseInsuranceLifePensionBrandContracts()
                .certificateId(this.getLifePensionContractId().toString())
                .productName(this.getProductName());
    }

    public ResponseInsuranceLifePensionContractInfo mapContractInfoDTO() {
        return new ResponseInsuranceLifePensionContractInfo()
                .data(new InsuranceLifePensionContractInfo()
                        .certificateId(this.getLifePensionContractId().toString())
                        .productCode(this.getProductCode())
                        .conjugatedPlan(this.getConjugatedPlan())
                        .proposalId(this.getProposalId())
                        .certificateActive(this.getCertificateActive())
                        .insureds(new InsuranceLifePensionDocumentsInsured()
                                .documentType(InsuranceLifePensionDocumentsInsured.DocumentTypeEnum.fromValue(this.getInsuredDocumentType()))
                                .documentNumber(this.getInsuredDocumentNumber())
                                .name(this.getInsuredName())
                                .birthDate(this.getInsuredBirthDate())
                                .gender(InsuranceLifePensionDocumentsInsured.GenderEnum.fromValue(this.getInsuredGender()))
                                .postCode(this.getInsuredPostCode())
                                .townName(this.getInsuredTownName())
                                .countrySubDivision(EnumCountrySubDivision.fromValue(this.getInsuredCountrySubDivision()))
                                .countryCode(EnumCountryCode.fromValue(this.getInsuredCountryCode()))
                                .address(this.getInsuredAddress())
                        )
                        .contractingType(InsuranceLifePensionContractingType.fromValue(this.getContractingType()))
                        .contractId(this.getContractId())
                        .planType(InsuranceLifePensionPlanType.fromValue(this.getPlanType()))
                        .effectiveDateStart(this.getEffectiveDateStart())
                        .effectiveDateEnd(this.getEffectiveDateEnd())
                        .periodicity(InsuranceLifePensionPeriodicity.fromValue(this.getPeriodicity()))
                        .taxRegime(InsuranceLifePensionTaxRegime.fromValue(this.getTaxRegime()))
                        .suseps(this.getSuseps().stream().map(LifePensionContractSusepEntity::toResponse).toList())
                );
    }

    public ResponseInsuranceLifePensionContractInfoV2 mapContractInfoDTOV2() {
        return new ResponseInsuranceLifePensionContractInfoV2()
                .data(new InsuranceLifePensionContractInfoV2()
                        .certificateId(this.getLifePensionContractId().toString())
                        .productCode(this.getProductCode())
                        .conjugatedPlan(this.getConjugatedPlan())
                        .proposalId(this.getProposalId())
                        .certificateActive(this.getCertificateActive())
                        .insureds(new InsuranceLifePensionDocumentsInsuredV2()
                                .documentType(InsuranceLifePensionDocumentsInsuredV2.DocumentTypeEnum.fromValue(this.getInsuredDocumentType()))
                                .documentNumber(this.getInsuredDocumentNumber())
                                .name(this.getInsuredName())
                                .birthDate(this.getInsuredBirthDate())
                                .gender(InsuranceLifePensionDocumentsInsuredV2.GenderEnum.fromValue(this.getInsuredGender()))
                                .address(new Address()
                                        .flagPostCode(Address.FlagPostCodeEnum.fromValue(this.getInsuredAddressFlagPostCode()))
                                        .address((AllOfAddressAddress) new AllOfAddressAddress()
                                                .allOfAddressAddressName(this.getInsuredAddressName())
                                                .allOfAddressAddressNumber(this.getInsuredAddressNumber())
                                                .allOfAddressAddressAddressComplementaryInfo(this.getInsuredAddressComplementaryInfo())
                                                .allOfAddressAddressTownName(this.getInsuredTownName())
                                                .allOfAddressAddressCountrySubDivision(this.getInsuredCountrySubDivision())
                                                .allOfAddressAddressPostCode(this.getInsuredPostCode())
                                                .type(NationalAddress.TypeEnum.fromValue(this.getInsuredAddressType()))
                                                .name(this.getInsuredAddressName())
                                                .number(this.getInsuredAddressNumber())
                                                .addressComplementaryInfo(this.getInsuredAddressComplementaryInfo())
                                                .districtName(this.getInsuredDistrictName())
                                                .townName(this.getInsuredTownName())
                                                .ibgeTownCode(this.getInsuredIbgeTownCode())
                                                .countrySubDivision(EnumCountrySubDivision.fromValue(this.getInsuredCountrySubDivision()))
                                                .postCode(this.getInsuredPostCode())))
                        )
                        .contractingType(InsuranceLifePensionContractingType.fromValue(this.getContractingType()))
                        .contractId(this.getContractId())
                        .planType(InsuranceLifePensionPlanType.fromValue(this.getPlanType()))
                        .effectiveDateStart(this.getEffectiveDateStart())
                        .effectiveDateEnd(this.getEffectiveDateEnd())
                        .periodicity(InsuranceLifePensionPeriodicity.fromValue(this.getPeriodicity()))
                        .taxRegime(InsuranceLifePensionTaxRegime.fromValue(this.getTaxRegime()))
                        .suseps(this.getSuseps().stream().map(LifePensionContractSusepEntity::toResponseV2).toList())
                );
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getLifePensionContractId().toString());
    }
}
