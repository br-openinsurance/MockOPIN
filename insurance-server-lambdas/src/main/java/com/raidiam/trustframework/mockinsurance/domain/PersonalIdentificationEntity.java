package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "personal_identifications")
public class PersonalIdentificationEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "personal_identification_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID personalIdentificationsId;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    public PersonalIdentificationData mapDto() {
        return new PersonalIdentificationData()
                .updateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()))
                .personalId(this.getPersonalIdentificationsId().toString())
                .brandName("Mock OPIN")
                .civilName(this.getAccountHolder().getAccountHolderName())
                .cpfNumber(this.getAccountHolder().getDocumentIdentification())
                .companyInfo(new CompanyInfo()
                        .name("Mock Bank")
                        .cnpjNumber("01773247000563"))
                .hasBrazilianNationality(true)
                .contact(new PersonalContact()
                        .postalAddresses(List.of(new PersonalPostalAddress()
                                .address("Av Naburo Ykesaki, 1270")
                                .townName("Sao Paulo")
                                .countrySubDivision(EnumCountrySubDivision.SP)
                                .postCode("17500001")
                                .country(PersonalPostalAddress.CountryEnum.BRA))));
    }

    public PersonalIdentificationDataV2 mapDtoV2() {
        return new PersonalIdentificationDataV2()
                .updateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()))
                .personalId(this.getPersonalIdentificationsId().toString())
                .brandName("Mock OPIN")
                .civilName(this.getAccountHolder().getAccountHolderName())
                .cpfNumber(this.getAccountHolder().getDocumentIdentification())
                .companyInfo(new CompanyInfoV2()
                        .name("Mock Bank")
                        .cnpjNumber("01773247000563"))
                .hasBrazilianNationality(true)
                .contact(new PersonalContactV2()
                        .postalAddresses(List.of(new PersonalPostalAddressV2()
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
                                                .postCode("10000000"))))));
    }
}
