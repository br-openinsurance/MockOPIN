package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceCapitalizationTitleTechnicalProvisions;
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
@Table(name = "capitalization_title_plan_technical_provisions")
public class CapitalizationTitlePlanTechnicalProvisionsEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "capitalization_title_plan_technical_provision_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID technicalProvisionsId;


    @Column(name = "pmc_amount")
    private String pmcAmount;

    @Column(name = "pmc_unit_type")
    private String pmcUnitType;

    @Column(name = "pmc_unit_type_others")
    private String pmcUnitTypeOthers;

    @Column(name = "pmc_unit_code")
    private String pmcUnitCode;

    @Column(name = "pmc_unit_description")
    private String pmcUnitDescription;

    @Column(name = "pmc_currency")
    private String pmcCurrency;

    @Column(name = "pdb_amount")
    private String pdbAmount;

    @Column(name = "pdb_unit_type")
    private String pdbUnitType;

    @Column(name = "pdb_unit_type_others")
    private String pdbUnitTypeOthers;

    @Column(name = "pdb_unit_code")
    private String pdbUnitCode;

    @Column(name = "pdb_unit_description")
    private String pdbUnitDescription;

    @Column(name = "pdb_currency")
    private String pdbCurrency;

    @Column(name = "pr_amount")
    private String prAmount;

    @Column(name = "pr_unit_type")
    private String prUnitType;

    @Column(name = "pr_unit_type_others")
    private String prUnitTypeOthers;

    @Column(name = "pr_unit_code")
    private String prUnitCode;

    @Column(name = "pr_unit_description")
    private String prUnitDescription;

    @Column(name = "pr_currency")
    private String prCurrency;

    @Column(name = "psp_amount")
    private String pspAmount;

    @Column(name = "psp_unit_type")
    private String pspUnitType;

    @Column(name = "psp_unit_type_others")
    private String pspUnitTypeOthers;

    @Column(name = "psp_unit_code")
    private String pspUnitCode;

    @Column(name = "psp_unit_description")
    private String pspUnitDescription;

    @Column(name = "psp_currency")
    private String pspCurrency;

    @Column(name = "capitalization_title_plan_title_id")
    private UUID capitalizationTitlePlanTitleId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capitalization_title_plan_title_id", referencedColumnName = "capitalization_title_plan_title_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private CapitalizationTitlePlanTitleEntity capitalizationTitleTitle;

    public InsuranceCapitalizationTitleTechnicalProvisions getDTO() {
        return new InsuranceCapitalizationTitleTechnicalProvisions()
                .pdbAmount(new AmountDetails()
                        .amount(this.getPdbAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPdbUnitType()))
                        .unitTypeOthers(this.getPdbUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getPdbUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getPdbUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getPdbCurrency())))
                .pmcAmount(new AmountDetails()
                        .amount(this.getPmcAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPmcUnitType()))
                        .unitTypeOthers(this.getPmcUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getPmcUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getPmcUnitDescription())))
                .currency(AmountDetails.CurrencyEnum.fromValue(this.getPmcCurrency())))
                .prAmount(new AmountDetails()
                        .amount(this.getPrAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPrUnitType()))
                        .unitTypeOthers(this.getPrUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getPrUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getPrUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getPrCurrency())))
                .pspAmount(new AmountDetails()
                        .amount(this.getPspAmount())
                        .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getPspUnitType()))
                        .unitTypeOthers(this.getPspUnitTypeOthers())
                        .unit(new AmountDetailsUnit()
                                .code(this.getPspUnitCode())
                                .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getPspUnitDescription())))
                        .currency(AmountDetails.CurrencyEnum.fromValue(this.getPspCurrency())));
    }
}