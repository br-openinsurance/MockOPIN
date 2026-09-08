package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "pension_plan_contract_document_insureds")
public class PensionPlanContractDocumentInsuredEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "pension_plan_contract_document_insured_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID insuredId;

    @Column(name = "pension_plan_contract_document_id")
    private UUID pensionPlanContractDocumentId;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    private String gender;

    @Column(name = "post_code")
    private String postCode;

    @Column(name = "town_name")
    private String townName;

    @Column(name = "country_sub_division")
    private String countrySubDivision;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "address")
    private String address;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "ibge_town_code")
    private String ibgeTownCode;

    @Column(name = "address_name")
    private String addressName;

    @Column(name = "address_number")
    private String addressNumber;

    @Column(name = "address_complementary_info")
    private String addressComplementaryInfo;

    @Column(name = "address_type")
    private String addressType;

    @Column(name = "flag_post_code")
    private String flagPostCode;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pension_plan_contract_document_id", referencedColumnName = "pension_plan_contract_document_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PensionPlanContractDocumentEntity pensionPlanContractDocument;

    public InsurancePensionPlanDocumentsInsured getDTO() {
        return new InsurancePensionPlanDocumentsInsured()
                .documentType(InsurancePensionPlanDocumentsInsured.DocumentTypeEnum.fromValue(this.getDocumentType()))
                .documentNumber(this.getDocumentNumber())
                .name(this.getName())
                .birthDate(this.getBirthDate())
                .gender(InsurancePensionPlanDocumentsInsured.GenderEnum.fromValue(this.getGender()))
                .postCode(this.getPostCode())
                .townName(this.getTownName())
                .countrySubDivision(EnumCountrySubDivision.fromValue(this.getCountrySubDivision()))
                .countryCode(EnumCountryCode.fromValue(this.getCountryCode()))
                .address(this.getAddress());
    }

    public InsurancePensionPlanDocumentsInsuredV2 getDTOV2() {
        return new InsurancePensionPlanDocumentsInsuredV2()
                .documentType(InsurancePensionPlanDocumentsInsuredV2.DocumentTypeEnum.fromValue(this.getDocumentType()))
                .documentNumber(this.getDocumentNumber())
                .name(this.getName())
                .birthDate(this.getBirthDate())
                .gender(InsurancePensionPlanDocumentsInsuredV2.GenderEnum.fromValue(this.getGender()))
                .address(new Address()
                        .flagPostCode(Address.FlagPostCodeEnum.fromValue(this.getFlagPostCode()))
                        .address((AllOfAddressAddress) new AllOfAddressAddress()
                                .allOfAddressAddressName(this.getAddressName())
                                .allOfAddressAddressNumber(this.getAddressNumber())
                                .allOfAddressAddressAddressComplementaryInfo(this.getAddressComplementaryInfo())
                                .allOfAddressAddressTownName(this.getTownName())
                                .allOfAddressAddressCountrySubDivision(this.getCountrySubDivision())
                                .allOfAddressAddressPostCode(this.getPostCode())
                                .type(NationalAddress.TypeEnum.fromValue(this.getAddressType()))
                                .name(this.getAddressName())
                                .number(this.getAddressNumber())
                                .addressComplementaryInfo(this.getAddressComplementaryInfo())
                                .districtName(this.getDistrictName())
                                .townName(this.getTownName())
                                .ibgeTownCode(this.getIbgeTownCode())
                                .countrySubDivision(EnumCountrySubDivision.fromValue(this.getCountrySubDivision()))
                                .postCode(this.getPostCode())));
    }
}
