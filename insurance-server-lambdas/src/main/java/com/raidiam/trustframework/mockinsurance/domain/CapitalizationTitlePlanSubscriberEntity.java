package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "capitalization_title_plan_subscribers")
public class CapitalizationTitlePlanSubscriberEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "capitalization_title_plan_subscriber_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID subscriberId;

    @Column(name = "subscriber_name")
    private String subscriberName;
    
    @Column(name = "subscriber_document_type")
    private String subscriberDocumentType;
    
    @Column(name = "subscriber_document_type_others")
    private String subscriberDocumentTypeOthers;
    
    @Column(name = "subscriber_document_number")
    private String subscriberDocumentNumber;

    @Column(name = "subscriber_country_calling_code")
    private String subscriberCountryCallingCode;

    @Column(name = "subscriber_area_code")
    private String subscriberAreaCode;

    @Column(name = "subscriber_number")
    private String subscriberNumber;
    
    @Column(name = "subscriber_address")
    private String subscriberAddress;
    
    @Column(name = "subscriber_address_additional_info")
    private String subscriberAddressAdditionalInfo;
    
    @Column(name = "subscriber_flag_post_code")
    private String subscriberFlagPostCode;
    
    @Column(name = "subscriber_district_name")
    private String subscriberDistrictName;
    
    @Column(name = "subscriber_town_name")
    private String subscriberTownName;
    
    @Column(name = "subscriber_town_code")
    private String subscriberTownCode;
    
    @Column(name = "subscriber_country_sub_division")
    private String subscriberCountrySubDivision;
    
    @Column(name = "subscriber_country_code")
    private String subscriberCountryCode;
    
    @Column(name = "subscriber_postcode")
    private String subscriberPostcode;
    
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "capitalizationTitleSubscriber")
    private List<CapitalizationTitlePlanHolderEntity> holders;

    @Column(name = "capitalization_title_plan_title_id")
    private UUID capitalizationTitlePlanTitleId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capitalization_title_plan_title_id", referencedColumnName = "capitalization_title_plan_title_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private CapitalizationTitlePlanTitleEntity capitalizationTitleTitle;

    public InsuranceCapitalizationTitleSubscriber getDTO() {
        return new InsuranceCapitalizationTitleSubscriber()
                .subscriberName(this.getSubscriberName())
                .subscriberDocumentType(InsuranceCapitalizationTitleSubscriber.SubscriberDocumentTypeEnum.fromValue(this.getSubscriberDocumentType()))
                .subscriberDocumentTypeOthers(this.getSubscriberDocumentTypeOthers())
                .subscriberDocumentNumber(this.getSubscriberDocumentNumber())
                .subscriberAddress(this.getSubscriberAddress())
                .subscriberAddressAdditionalInfo(this.getSubscriberAddressAdditionalInfo())
                .subscriberTownName(this.getSubscriberTownName())
                .subscriberCountrySubDivision(EnumCountrySubDivision.fromValue(this.getSubscriberCountrySubDivision()))
                .subscriberCountryCode(this.getSubscriberCountryCode())
                .subscriberPostCode(this.getSubscriberPostcode())
                .subscriberPhones(List.of(new RequestorPhone()
                        .countryCallingCode(this.getSubscriberCountryCallingCode())
                        .areaCode(EnumAreaCode.fromValue(this.getSubscriberAreaCode()))
                        .number(this.getSubscriberNumber())))
                .holder(this.getHolders().stream().map(CapitalizationTitlePlanHolderEntity::getDTO).toList());
    }

    public InsuranceCapitalizationTitleSubscriberV2 getDTOV2() {
        return new InsuranceCapitalizationTitleSubscriberV2()
                .subscriberName(this.getSubscriberName())
                .subscriberDocumentType(InsuranceCapitalizationTitleSubscriberV2.SubscriberDocumentTypeEnum.fromValue(this.getSubscriberDocumentType()))
                .subscriberDocumentTypeOthers(this.getSubscriberDocumentTypeOthers())
                .subscriberDocumentNumber(this.getSubscriberDocumentNumber())
                .subscriberAddress(new Address()
                    .flagPostCode(Address.FlagPostCodeEnum.valueOf(this.getSubscriberFlagPostCode()))
                    .address((AllOfAddressAddress) new AllOfAddressAddress()
                        .type(NationalAddress.TypeEnum.valueOf(this.getSubscriberAddress().split(" ")[0].toUpperCase()))
                        .name(this.getSubscriberAddress().split(" ", 2)[1].split(",")[0])
                        .number(this.getSubscriberAddress().split(" ", 2)[1].split(",")[1])
                        .addressComplementaryInfo(this.getSubscriberAddressAdditionalInfo())
                        .districtName(this.getSubscriberDistrictName())
                        .townName(this.getSubscriberTownName())
                        .ibgeTownCode(this.getSubscriberTownCode())
                        .countrySubDivision(EnumCountrySubDivision.fromValue(this.getSubscriberCountrySubDivision()))
                        .postCode(this.getSubscriberPostcode())
                        ))
                .subscriberPhones(List.of(new RequestorPhone()
                        .countryCallingCode(this.getSubscriberCountryCallingCode())
                        .areaCode(EnumAreaCode.fromValue(this.getSubscriberAreaCode()))
                        .number(this.getSubscriberNumber())))
                .holder(this.getHolders().stream().map(CapitalizationTitlePlanHolderEntity::getDTOV2).toList());
    }
}