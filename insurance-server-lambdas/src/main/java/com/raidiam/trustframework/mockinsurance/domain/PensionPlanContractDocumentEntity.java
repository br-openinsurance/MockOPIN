package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
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
@Table(name = "pension_plan_contract_documents")
public class PensionPlanContractDocumentEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "pension_plan_contract_document_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID documentId;

    @Column(name = "pension_plan_contract_id")
    private String pensionPlanContractId;

    @Column(name = "certificate_id")
    private String certificateId;

    @Column(name = "effective_date_start")
    private LocalDate effectiveDateStart;

    @Column(name = "effective_date_end")
    private LocalDate effectiveDateEnd;

    @Column(name = "proposal_id")
    private String proposalId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pensionPlanContractDocument")
    private List<PensionPlanContractDocumentInsuredEntity> insureds = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pensionPlanContractDocument")
    private List<PensionPlanContractDocumentCoverageEntity> coverages = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pension_plan_contract_id", referencedColumnName = "pension_plan_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private PensionPlanContractEntity pensionPlanContract;

    public InsurancePensionPlanDocuments getDTO() {
        return new InsurancePensionPlanDocuments()
                .certificateId(this.getCertificateId())
                .effectiveDateStart(this.getEffectiveDateStart())
                .effectiveDateEnd(this.getEffectiveDateEnd())
                .proposalId(this.getProposalId())
                .insureds(this.getInsureds().stream().map(PensionPlanContractDocumentInsuredEntity::getDTO).toList())
                .plans(new InsurancePensionPlanDocumentsPlans()
                        .coverages(this.getCoverages().stream().map(PensionPlanContractDocumentCoverageEntity::getDTO).toList()));
    }

    public InsurancePensionPlanDocumentsV2 getDTOV2() {
        return new InsurancePensionPlanDocumentsV2()
                .certificateId(this.getCertificateId())
                .effectiveDateStart(this.getEffectiveDateStart())
                .effectiveDateEnd(this.getEffectiveDateEnd())
                .proposalId(this.getProposalId())
                .insureds(this.getInsureds().stream().map(PensionPlanContractDocumentInsuredEntity::getDTOV2).toList())
                .plans(new InsurancePensionPlanDocumentsPlansV2()
                        .coverages(this.getCoverages().stream().map(PensionPlanContractDocumentCoverageEntity::getDTOV2).toList()));
    }
}
