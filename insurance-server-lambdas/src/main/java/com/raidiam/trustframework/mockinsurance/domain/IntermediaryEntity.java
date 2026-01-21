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
@Table(name = "intermediaries")
public class IntermediaryEntity extends BaseIdEntity {

    @Column(name = "broker_id")
    private String brokerId;

    @Column(name = "type")
    private String type;

    @Column(name = "type_others")
    private String typeOthers;

    @Column(name = "post_code")
    private String postCode;

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

    public Intermediary mapDTO() {
        return new Intermediary()
                .identificationType(Intermediary.IdentificationTypeEnum.fromValue(this.getIdentificationType()))
                .country(Intermediary.CountryEnum.fromValue(this.getCountry()))
                .state(this.getState())
                .city(this.getCity())
                .address(this.getAddress())
                .identification(this.getIdentification())
                .brokerId(this.getBrokerId())
                .identificationTypeOthers(this.getIdentificationTypeOthers())
                .name(this.getName())
                .postCode(this.getPostCode())
                .type(Intermediary.TypeEnum.fromValue(this.getType()))
                .typeOthers(this.getTypeOthers());
    }

    public IntermediaryV2 mapDTOV2() {
        return new IntermediaryV2()
                .identificationType(IntermediaryV2.IdentificationTypeEnum.fromValue(this.getIdentificationType()))
                .address(new Address()
                    .flagPostCode(Address.FlagPostCodeEnum.valueOf(this.getFlagPostCode()))
                    .address((AllOfAddressAddress) new AllOfAddressAddress()
                        .allOfAddressAddressName(this.getAddress().split(" ", 2)[1].split(",")[0])
                        .allOfAddressAddressNumber(this.getAddress().split(" ", 2)[1].split(",")[1].trim())
                        .allOfAddressAddressAddressComplementaryInfo(this.getAddressAdditionalInfo())
                        .allOfAddressAddressTownName(this.getCity())
                        .allOfAddressAddressCountrySubDivision(this.getState())
                        .allOfAddressAddressPostCode(this.getPostCode())
                        .type(NationalAddress.TypeEnum.valueOf(this.getAddress().split(" ")[0].toUpperCase()))
                        .name(this.getAddress().split(" ", 2)[1].split(",")[0])
                        .number(this.getAddress().split(" ", 2)[1].split(",")[1].trim())
                        .addressComplementaryInfo(this.getAddressAdditionalInfo())
                        .districtName(this.getDistrictName())
                        .townName(this.getCity())
                        .ibgeTownCode(this.getTownCode())
                        .countrySubDivision(EnumCountrySubDivision.fromValue(this.getState()))
                        .postCode(this.getPostCode())
                        ))
                .identification(this.getIdentification())
                .brokerId(this.getBrokerId())
                .identificationTypeOthers(this.getIdentificationTypeOthers())
                .name(this.getName())
                .type(IntermediaryV2.TypeEnum.fromValue(this.getType()))
                .typeOthers(this.getTypeOthers());
    }
}
