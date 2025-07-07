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
@Table(name = "capitalization_title_plan_brokers")
public class CapitalizationTitlePlanBrokerEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "capitalization_title_plan_broker_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID brokerId;

    @Column(name = "susep_broker_code")
    private String susepBrokerCode;

    @Column(name = "broker_description")
    private String brokerDescription;

    @Column(name = "capitalization_title_plan_series_id")
    private UUID capitalizationTitlePlanSeriesId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capitalization_title_plan_series_id", referencedColumnName = "capitalization_title_plan_series_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private CapitalizationTitlePlanSeriesEntity capitalizationTitlePlanSeries;

    public InsuranceCapitalizationTitleBroker getDTO() {
        return new InsuranceCapitalizationTitleBroker()
                .susepBrokerCode(this.getSusepBrokerCode())
                .brokerDescription(this.getBrokerDescription());
    }
}