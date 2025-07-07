package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "capitalization_title_plan_quotas")
public class CapitalizationTitlePlanQuotaEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "capitalization_title_plan_quota_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID quotaId;

    @Column(name = "quota")
    private Integer quota;
    
    @Column(name = "capitalization_quota")
    private String capitalizationQuota;
    
    @Column(name = "raffle_quota")
    private String raffleQuota;
    
    @Column(name = "charging_quota")
    private String chargingQuota;

    @Column(name = "capitalization_title_plan_series_id")
    private UUID capitalizationTitlePlanSeriesId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capitalization_title_plan_series_id", referencedColumnName = "capitalization_title_plan_series_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private CapitalizationTitlePlanSeriesEntity capitalizationTitlePlanSeries;


    public CapitalizationTitleQuotas getDTO() {
        return new CapitalizationTitleQuotas()
                .quota(this.getQuota())
                .capitalizationQuota(this.getCapitalizationQuota())
                .raffleQuota(this.getRaffleQuota())
                .chargingQuota(this.getChargingQuota());
    }
}