package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "personal_info")
public class PersonalInfoEntity extends BaseIdEntity {

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "post_code")
    private String postCode;

    @Column(name = "email")
    private String email;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Column(name = "address")
    private String address;

    @Column(name = "address_additional_info")
    private String addressAdditionalInfo;

    public PersonalInfo mapDTO() {
        return new PersonalInfo()
                .address(this.getAddress())
                .addressAdditionalInfo(this.getAddressAdditionalInfo())
                .city(this.getCity())
                .email(this.getEmail())
                .country(PersonalInfo.CountryEnum.fromValue(this.getCountry()))
                .birthDate(this.getBirthDate())
                .identification(this.getIdentification())
                .identificationType(PersonalInfo.IdentificationTypeEnum.fromValue(this.getIdentificationType()))
                .identificationTypeOthers(this.getIdentificationTypeOthers())
                .name(this.getName())
                .postCode(this.getPostCode())
                .state(PersonalInfo.StateEnum.fromValue(this.getState()));
    }
}
