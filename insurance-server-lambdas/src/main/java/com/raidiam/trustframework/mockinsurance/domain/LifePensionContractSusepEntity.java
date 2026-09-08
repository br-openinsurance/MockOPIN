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
@Table(name = "life_pension_contract_suseps")
public class LifePensionContractSusepEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "life_pension_contract_susep_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID susepId;

    @Column(name = "coverage_code")
    private String coverageCode;

    @Column(name = "susep_process_number")
    private String susepProcessNumber;

    @Column(name = "structure_modality")
    private String structureModality;

    @Column(name = "type")
    private String type;

    @Column(name = "locked_plan")
    private Boolean lockedPlan;

    @Column(name = "qualified_proposer")
    private Boolean qualifiedProposer;

    @Column(name = "benefit_payment_method")
    private String benefitPaymentMethod;

    @Column(name = "financial_result_reversal")
    private Boolean financialResultReversal;

    @Column(name = "calculation_basis")
    private String calculationBasis;

    @Column(name = "life_pension_contract_id")
    private UUID lifePensionContractId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lifePensionContractSusep")
    private List<LifePensionContractSusepFIEEntity> susepFies = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "life_pension_contract_id", referencedColumnName = "life_pension_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private LifePensionContractEntity lifePensionContract;

    public InsuranceLifePensionSuseps toResponse() {
        return new InsuranceLifePensionSuseps()
                .coverageCode(this.getCoverageCode())
                .susepProcessNumber(this.getSusepProcessNumber())
                .structureModality(InsuranceLifePensionSuseps.StructureModalityEnum.fromValue(this.getStructureModality()))
                .type(InsuranceLifePensionSuseps.TypeEnum.fromValue(this.getType()))
                .lockedPlan(this.getLockedPlan())
                .qualifiedProposer(this.getQualifiedProposer())
                .benefitPaymentMethod(InsuranceLifePensionSuseps.BenefitPaymentMethodEnum.fromValue(this.getBenefitPaymentMethod()))
                .financialResultReversal(this.getFinancialResultReversal())
                .calculationBasis(InsuranceLifePensionSuseps.CalculationBasisEnum.fromValue(this.getCalculationBasis()))
                .FIE(this.getSusepFies().stream().map(LifePensionContractSusepFIEEntity::getDTO).toList());
    }

    public InsuranceLifePensionSusepsV2 toResponseV2() {
        return new InsuranceLifePensionSusepsV2()
                .coverageCode(this.getCoverageCode())
                .susepProcessNumber(this.getSusepProcessNumber())
                .structureModality(InsuranceLifePensionSusepsV2.StructureModalityEnum.fromValue(this.getStructureModality()))
                .type(InsuranceLifePensionSusepsV2.TypeEnum.fromValue(this.getType()))
                .lockedPlan(this.getLockedPlan())
                .qualifiedProposer(this.getQualifiedProposer())
                .benefitPaymentMethod(InsuranceLifePensionSusepsV2.BenefitPaymentMethodEnum.fromValue(this.getBenefitPaymentMethod()))
                .financialResultReversal(this.getFinancialResultReversal())
                .calculationBasis(InsuranceLifePensionSusepsV2.CalculationBasisEnum.fromValue(this.getCalculationBasis()))
                .FIE(this.getSusepFies().stream().map(LifePensionContractSusepFIEEntity::getDTOV2).toList());
    }
}
