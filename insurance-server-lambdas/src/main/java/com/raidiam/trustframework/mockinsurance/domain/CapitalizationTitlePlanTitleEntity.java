package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "capitalization_title_plan_titles")
public class CapitalizationTitlePlanTitleEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "capitalization_title_plan_title_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID titleId;
    
    @Column(name = "registration_form")
    private String registrationForm;
    
    @Column(name = "issue_title_date")
    private LocalDate issueTitleDate;
    
    @Column(name = "term_start_date")
    private LocalDate termStartDate;
    
    @Column(name = "term_end_date")
    private LocalDate termEndDate;

    @Column(name = "raffle_premium_amount")
    private String rafflePremiumAmount;

    @Column(name = "raffle_premium_unit_type")
    private String rafflePremiumUnitType;

    @Column(name = "raffle_premium_unit_type_others")
    private String rafflePremiumUnitTypeOthers;

    @Column(name = "raffle_premium_unit_code")
    private String rafflePremiumUnitCode;

    @Column(name = "raffle_premium_unit_description")
    private String rafflePremiumUnitDescription;

    @Column(name = "raffle_premium_currency")
    private String rafflePremiumCurrency;

    @Column(name = "contribution_amount")
    private String contributionAmount;

    @Column(name = "contribution_unit_type")
    private String contributionUnitType;

    @Column(name = "contribution_unit_type_others")
    private String contributionUnitTypeOthers;

    @Column(name = "contribution_unit_code")
    private String contributionUnitCode;

    @Column(name = "contribution_unit_description")
    private String contributionUnitDescription;

    @Column(name = "contribution_currency")
    private String contributionCurrency;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "capitalizationTitleTitle")
    private List<CapitalizationTitlePlanSubscriberEntity> subscribers = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "capitalizationTitleTitle")
    private List<CapitalizationTitlePlanTechnicalProvisionsEntity> technicalProvisions = new ArrayList<>();

    @Column(name = "capitalization_title_plan_series_id")
    private UUID capitalizationTitlePlanSeriesId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capitalization_title_plan_series_id", referencedColumnName = "capitalization_title_plan_series_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private CapitalizationTitlePlanSeriesEntity capitalizationTitlePlanSeries;


    public InsuranceCapitalizationTitleTitle getDTO() {
        return new InsuranceCapitalizationTitleTitle()
                .titleId(String.valueOf(this.getTitleId()))
                .registrationForm(this.getRegistrationForm())
                .issueTitleDate(this.getIssueTitleDate())
                .termStartDate(this.getTermStartDate())
                .termEndDate(this.getTermEndDate())
                .rafflePremiumAmount(new AmountDetails()
                        .amount(this.getRafflePremiumAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getRafflePremiumUnitType()))
                        .unitTypeOthers(this.getRafflePremiumUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getRafflePremiumUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getRafflePremiumUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getRafflePremiumCurrency())))
                .contributionAmount(new AmountDetails()
                        .amount(this.getContributionAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getContributionUnitType()))
                        .unitTypeOthers(this.getContributionUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getContributionUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getContributionUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getContributionCurrency())))
                .subscriber(this.getSubscribers().stream().map(CapitalizationTitlePlanSubscriberEntity::getDTO).toList())
                .technicalProvisions(this.getTechnicalProvisions().stream().map(CapitalizationTitlePlanTechnicalProvisionsEntity::getDTO).toList());
    }
}