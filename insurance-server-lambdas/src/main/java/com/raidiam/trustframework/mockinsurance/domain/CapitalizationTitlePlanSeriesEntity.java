package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "capitalization_title_plan_series")
public class CapitalizationTitlePlanSeriesEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "capitalization_title_plan_series_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private String capitalizationTitlePlanSeriesId;

    @Column(name = "susep_process_number")
    private String susepProcessNumber;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capitalization_title_plan_id", referencedColumnName = "capitalization_title_plan_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private CapitalizationTitlePlanEntity capitalizationTitlePlan;

    public InsuranceCapitalizationTitleSeries toResponse() {
        var accountHolder = this.getCapitalizationTitlePlan().getAccountHolder();
        return new InsuranceCapitalizationTitleSeries()
                .planId(this.getCapitalizationTitlePlan().getCapitalizationTitlePlanId().toString())
                .seriesId(this.getCapitalizationTitlePlanSeriesId())
                .modality(InsuranceCapitalizationTitleSeries.ModalityEnum.TRADICIONAL)
                .susepProcessNumber(this.getSusepProcessNumber())
                .serieSize(1)
                .quotas(List.of(new CapitalizationTitleQuotas()
                        .quota(1)
                        .capitalizationQuota("0.000002")
                        .raffleQuota("0.000002")
                        .chargingQuota("0.000002")))
                .gracePeriodForFullRedemption(48)
                .updateIndex(InsuranceCapitalizationTitleSeries.UpdateIndexEnum.IGPM)
                .readjustmentIndex(InsuranceCapitalizationTitleSeries.ReadjustmentIndexEnum.IGPM)
                .bonusClause(false)
                .frequency(InsuranceCapitalizationTitleSeries.FrequencyEnum.MENSAL)
                .interestRate("10.00")
                .titles(List.of(new InsuranceCapitalizationTitleTitle()
                        .titleId(this.getCapitalizationTitlePlan().getCapitalizationTitleId())
                        .registrationForm("00000")
                        .issueTitleDate(LocalDate.now())
                        .termStartDate(LocalDate.now().minusDays(1))
                        .termEndDate(LocalDate.now().plusDays(1))
                        .rafflePremiumAmount(new AmountDetails()
                                .amount("62500.67")
                                .unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                        .contributionAmount(new AmountDetails()
                                .amount("62500.67")
                                .unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                        .subscriber(List.of(new InsuranceCapitalizationTitleSubscriber()
                                .subscriberName(accountHolder.getAccountHolderName())
                                .subscriberDocumentType(InsuranceCapitalizationTitleSubscriber.SubscriberDocumentTypeEnum.CPF)
                                .subscriberDocumentNumber(accountHolder.getDocumentIdentification())
                                .subscriberAddress("Av Naburo Ykesaki, 1270")
                                .subscriberTownName("Rio de Janeiro")
                                .subscriberCountrySubDivision(EnumCountrySubDivision.RJ)
                                .subscriberCountryCode("BRA")
                                .subscriberPostCode("17500001")))
                        .technicalProvisions(List.of(new InsuranceCapitalizationTitleTechnicalProvisions()
                                .pdbAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                                .prAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                                .pspAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))
                                .pmcAmount(new AmountDetails().amount("100.00").unitType(AmountDetails.UnitTypeEnum.MONETARIO))))));
    }
}
