package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.Address;
import com.raidiam.trustframework.mockinsurance.models.generated.AllOfAddressAddress;
import com.raidiam.trustframework.mockinsurance.models.generated.EnumCountrySubDivision;
import com.raidiam.trustframework.mockinsurance.models.generated.NationalAddress;
import com.raidiam.trustframework.mockinsurance.models.generated.PrincipalInfo;
import com.raidiam.trustframework.mockinsurance.models.generated.PrincipalsV2;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import static com.raidiam.trustframework.mockinsurance.utils.AddressParser.parseNationalAddressNameAndNumber;
import static com.raidiam.trustframework.mockinsurance.utils.AddressParser.parseNationalAddressType;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "principal_info")
public class PrincipalInfoEntity extends BaseIdEntity {

    @Column(name = "post_code")
    private String postCode;

    @Column(name = "email")
    private String email;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "city")
    private String city;

    @Column(name = "town_code")
    private String townCode;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Column(name = "address")
    private String address;

    @Column(name = "address_additional_info")
    private String addressAdditionalInfo;

    @Column(name = "flag_post_code")
    private String flagPostCode;

    public PrincipalInfo mapDTO() {
        return new PrincipalInfo()
                .address(this.getAddress())
                .addressAdditionalInfo(this.getAddressAdditionalInfo())
                .city(this.getCity())
                .email(this.getEmail())
                .country(PrincipalInfo.CountryEnum.fromValue(this.getCountry()))
                .identification(this.getIdentification())
                .identificationType(PrincipalInfo.IdentificationTypeEnum.fromValue(this.getIdentificationType()))
                .identificationTypeOthers(this.getIdentificationTypeOthers())
                .name(this.getName())
                .postCode(this.getPostCode())
                .state(PrincipalInfo.StateEnum.fromValue(this.getState()));
    }

    public PrincipalsV2 mapDTOV2() {
        NationalAddress.TypeEnum addressType = parseNationalAddressType(this.getAddress());
        String[] addressNameAndNumber = parseNationalAddressNameAndNumber(this.getAddress());
        return new PrincipalsV2()
                .address(new Address()
                    .flagPostCode(Address.FlagPostCodeEnum.valueOf(this.getFlagPostCode()))
                    .address((AllOfAddressAddress) new AllOfAddressAddress()
                        .allOfAddressAddressName(addressNameAndNumber[0])
                        .allOfAddressAddressNumber(addressNameAndNumber[1])
                        .allOfAddressAddressAddressComplementaryInfo(this.getAddressAdditionalInfo())
                        .allOfAddressAddressTownName(this.getCity())
                        .allOfAddressAddressCountrySubDivision(this.getState())
                        .allOfAddressAddressPostCode(this.getPostCode())
                        .type(addressType)
                        .name(addressNameAndNumber[0])
                        .number(addressNameAndNumber[1])
                        .addressComplementaryInfo(this.getAddressAdditionalInfo())
                        .districtName(this.getDistrictName())
                        .townName(this.getCity())
                        .ibgeTownCode(this.getTownCode())
                        .countrySubDivision(EnumCountrySubDivision.fromValue(this.getState()))
                        .postCode(this.getPostCode())
                    ))
                .email(this.getEmail())
                .identification(this.getIdentification())
                .identificationType(PrincipalsV2.IdentificationTypeEnum.fromValue(this.getIdentificationType()))
                .identificationTypeOthers(this.getIdentificationTypeOthers())
                .name(this.getName());
    }
}
