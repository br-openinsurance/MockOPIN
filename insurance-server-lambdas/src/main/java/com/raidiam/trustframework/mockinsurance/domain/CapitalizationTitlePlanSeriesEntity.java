package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "capitalization_title_plan_series")
public class CapitalizationTitlePlanSeriesEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "capitalization_title_plan_series_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID seriesId;

    @Column(name = "susep_process_number")
    private String susepProcessNumber;

    @Column(name = "modality")
    private String modality;

    @Column(name = "commercial_denomination")
    private String commercialDenomination;

    @Column(name = "serie_size")
    private Integer serieSize;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "capitalizationTitlePlanSeries")
    private List<CapitalizationTitlePlanQuotaEntity> quotas = new ArrayList<>();

    @Column(name = "grace_period_redemption")
    private Integer gracePeriodRedemption;

    @Column(name = "grace_period_for_full_redemption")
    private Integer gracePeriodForFullRedemption;

    @Column(name = "update_index")
    private String updateIndex;

    @Column(name = "update_index_others")
    private String updateIndexOthers;

    @Column(name = "readjustment_index")
    private String readjustmentIndex;

    @Column(name = "readjustment_index_others")
    private String readjustmentIndexOthers;

    @Column(name = "bonus_clause")
    private Boolean bonusClause;

    @Column(name = "frequency")
    private String frequency;

    @Column(name = "frequency_description")
    private String frequencyDescription;

    @Column(name = "interest_rate")
    private String interestRate;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "capitalizationTitlePlanSeries")
    private List<CapitalizationTitlePlanBrokerEntity> brokers = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "capitalizationTitlePlanSeries")
    private List<CapitalizationTitlePlanTitleEntity> titles = new ArrayList<>();

    @Column(name = "capitalization_title_plan_id")
    private UUID capitalizationTitlePlanId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capitalization_title_plan_id", referencedColumnName = "capitalization_title_plan_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private CapitalizationTitlePlanEntity capitalizationTitlePlan;

    public InsuranceCapitalizationTitleSeries toResponse() {
        return new InsuranceCapitalizationTitleSeries()
                .planId(this.getCapitalizationTitlePlan().getCapitalizationTitlePlanId().toString())
                .seriesId(String.valueOf(this.getSeriesId()))
                .modality(InsuranceCapitalizationTitleSeries.ModalityEnum.fromValue(this.getModality()))
                .susepProcessNumber(this.getSusepProcessNumber())
                .serieSize(this.getSerieSize())
                .quotas(this.getQuotas().stream().map(CapitalizationTitlePlanQuotaEntity::getDTO).toList())
                .gracePeriodForFullRedemption(this.getGracePeriodForFullRedemption())
                .gracePeriodRedemption(this.getGracePeriodRedemption())
                .updateIndex(InsuranceCapitalizationTitleSeries.UpdateIndexEnum.fromValue(this.getUpdateIndex()))
                .updateIndexOthers(this.getUpdateIndexOthers())
                .readjustmentIndex(InsuranceCapitalizationTitleSeries.ReadjustmentIndexEnum.fromValue(this.getReadjustmentIndex()))
                .readjustmentIndexOthers(this.getReadjustmentIndexOthers())
                .bonusClause(this.getBonusClause())
                .frequency(InsuranceCapitalizationTitleSeries.FrequencyEnum.fromValue(this.getFrequency()))
                .frequencyDescription(this.getFrequencyDescription())
                .interestRate(this.getInterestRate())
                .commercialDenomination(this.getCommercialDenomination())
                .titles(this.getTitles().stream().map(CapitalizationTitlePlanTitleEntity::getDTO).toList())
                .broker(this.getBrokers().stream().map(CapitalizationTitlePlanBrokerEntity::getDTO).toList());
    }

    public InsuranceCapitalizationTitleSeriesV2 toResponseV2() {
        return new InsuranceCapitalizationTitleSeriesV2()
                .planId(this.getCapitalizationTitlePlan().getCapitalizationTitlePlanId().toString())
                .seriesId(String.valueOf(this.getSeriesId()))
                .modality(InsuranceCapitalizationTitleSeriesV2.ModalityEnum.fromValue(this.getModality()))
                .susepProcessNumber(this.getSusepProcessNumber())
                .serieSize(this.getSerieSize())
                .quotas(this.getQuotas().stream().map(CapitalizationTitlePlanQuotaEntity::getDTO).toList())
                .gracePeriodForFullRedemption(this.getGracePeriodForFullRedemption())
                .gracePeriodRedemption(this.getGracePeriodRedemption())
                .updateIndex(InsuranceCapitalizationTitleSeriesV2.UpdateIndexEnum.fromValue(this.getUpdateIndex()))
                .updateIndexOthers(this.getUpdateIndexOthers())
                .readjustmentIndex(InsuranceCapitalizationTitleSeriesV2.ReadjustmentIndexEnum.fromValue(this.getReadjustmentIndex()))
                .readjustmentIndexOthers(this.getReadjustmentIndexOthers())
                .bonusClause(this.getBonusClause())
                .frequency(InsuranceCapitalizationTitleSeriesV2.FrequencyEnum.fromValue(this.getFrequency()))
                .frequencyDescription(this.getFrequencyDescription())
                .interestRate(this.getInterestRate())
                .commercialDenomination(this.getCommercialDenomination())
                .titles(this.getTitles().stream().map(CapitalizationTitlePlanTitleEntity::getDTOV2).toList())
                .broker(this.getBrokers().stream().map(CapitalizationTitlePlanBrokerEntity::getDTO).toList());
    }
}
