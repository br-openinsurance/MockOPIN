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
@Table(name = "capitalization_title_plans")
public class CapitalizationTitlePlanEntity extends BaseEntity implements HasStatusInterface {

    @Id
    @GeneratedValue
    @Column(name = "capitalization_title_plan_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID capitalizationTitlePlanId;

    @Column(name = "capitalization_title_id")
    private String capitalizationTitleId;

    @Column(name = "status")
    private String status;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "capitalizationTitlePlan")
    private List<CapitalizationTitlePlanSeriesEntity> capitalizationTitlePlanSeries = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "capitalizationTitlePlan")
    private List<CapitalizationTitlePlanEventEntity> capitalizationTitlePlanEvents = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "capitalizationTitlePlan")
    private List<CapitalizationTitlePlanSettlementEntity> capitalizationTitlePlanSettlements = new ArrayList<>();

    public ResponseInsuranceCapitalizationTitleBrandProducts mapProductDto() {
        return new ResponseInsuranceCapitalizationTitleBrandProducts()
                .planId(this.getCapitalizationTitlePlanId().toString())
                .productName("Mock Insurer Capitalization Title Plan");
    }

    public ResponseResourceListData mapResourceDto() {
        return new ResponseResourceListData()
                .resourceId(this.getCapitalizationTitlePlanId().toString());
    }

    public ResponseInsuranceCapitalizationTitlePlanInfo mapPlanInfoDto() {
        return new ResponseInsuranceCapitalizationTitlePlanInfo()
                .data(new CapitalizationTitlePlanInfo()
                        .series(this.getCapitalizationTitlePlanSeries().stream().map(CapitalizationTitlePlanSeriesEntity::toResponse).toList()));
    }

}
