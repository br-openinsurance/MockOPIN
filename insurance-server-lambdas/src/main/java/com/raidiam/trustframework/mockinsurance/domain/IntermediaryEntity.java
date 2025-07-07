package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.UUID;

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

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Column(name = "address")
    private String address;

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
}
