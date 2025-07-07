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
@Table(name = "capitalization_title_plan_holders")
public class CapitalizationTitlePlanHolderEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "capitalization_title_plan_holder_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID holderId;

    @Column(name = "holder_name")
    private String holderName;
    
    @Column(name = "holder_document_type")
    private String holderDocumentType;
    
    @Column(name = "holder_document_type_others")
    private String holderDocumentTypeOthers;
    
    @Column(name = "holder_document_number")
    private String holderDocumentNumber;

    @Column(name = "holder_country_calling_code")
    private String holderCountryCallingCode;

    @Column(name = "holder_area_code")
    private String holderAreaCode;

    @Column(name = "holder_number")
    private String holderNumber;
    
    @Column(name = "holder_address")
    private String holderAddress;
    
    @Column(name = "holder_address_additional_info")
    private String holderAddressAdditionalInfo;
    
    @Column(name = "holder_town_name")
    private String holderTownName;
    
    @Column(name = "holder_country_subdivision")
    private String holderCountrySubdivision;
    
    @Column(name = "holder_country_code")
    private String holderCountryCode;
    
    @Column(name = "holder_postcode")
    private String holderPostcode;
    
    @Column(name = "holder_redemption")
    private Boolean holderRedemption;
    
    @Column(name = "holder_raffle")
    private Boolean holderRaffle;

    @Column(name = "capitalization_title_plan_subscriber_id")
    private UUID capitalizationTitleSubscriberId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capitalization_title_plan_subscriber_id", referencedColumnName = "capitalization_title_plan_subscriber_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private CapitalizationTitlePlanSubscriberEntity capitalizationTitleSubscriber;

    public InsuranceCapitalizationTitleHolder getDTO() {
        return new InsuranceCapitalizationTitleHolder()
                .holderName(this.getHolderName())
                .holderDocumentType(InsuranceCapitalizationTitleHolder.HolderDocumentTypeEnum.fromValue(this.getHolderDocumentType()))
                .holderDocumentTypeOthers(this.getHolderDocumentTypeOthers())
                .holderDocumentNumber(this.getHolderDocumentNumber())
                .holderPhones(List.of(new RequestorPhone()
                        .countryCallingCode(this.getHolderCountryCallingCode())
                        .areaCode(EnumAreaCode.fromValue(this.getHolderAreaCode()))
                        .number(this.getHolderNumber())))
                .holderAddress(this.getHolderAddress())
                .holderAddressAdditionalInfo(this.getHolderAddressAdditionalInfo())
                .holderTownName(this.getHolderTownName())
                .holderCountrySubDivision(EnumCountrySubDivision.fromValue(this.getHolderCountrySubdivision()))
                .holderCountryCode(this.getHolderCountryCode())
                .holderPostCode(this.getHolderPostcode())
                .holderRedemption(this.getHolderRedemption())
                .holderRaffle(this.getHolderRaffle());
    }
}