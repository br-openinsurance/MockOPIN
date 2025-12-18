package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

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
        return new PrincipalsV2()
                .address(new Address()
                    .flagPostCode(Address.FlagPostCodeEnum.valueOf(this.getFlagPostCode()))
                    .address((AllOfAddressAddress) new AllOfAddressAddress()
                        .type(NationalAddress.TypeEnum.valueOf(this.getAddress().split(" ")[0].toUpperCase()))
                        .name(this.getAddress().split(" ", 2)[1].split(",")[0])
                        .number(this.getAddress().split(" ", 2)[1].split(",")[1])
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
