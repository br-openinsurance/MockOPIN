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
@Table(name = "pension_plan_contracts")
public class PensionPlanContractEntity extends BaseEntity implements HasStatusInterface {
    @Id
    @Column(name = "pension_plan_contract_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private String pensionPlanContractId;

    @Column(name = "status")
    private String status;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @Column(name = "contracting_type")
    private String contractingType;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pensionPlanContract")
    private List<PensionPlanContractDocumentEntity> documents = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    public ResponseInsurancePensionPlanBrandContracts mapContractDTO() {
        return new ResponseInsurancePensionPlanBrandContracts()
                .pensionIdentification(this.getPensionPlanContractId())
                .productName("Mock Insurer Pension Plan Contract");
    }

    public ResponseInsurancePensionPlanContractInfo mapContractInfoDTO() {
        return new ResponseInsurancePensionPlanContractInfo()
                .data(new InsurancePensionPlanContractInfo()
                        .pensionIdentification(this.getPensionPlanContractId())
                        .contractingType(InsurancePensionPlanContractInfo.ContractingTypeEnum.fromValue(this.getContractingType()))
                        .documents(this.getDocuments().stream().map(PensionPlanContractDocumentEntity::getDTO).toList()));
    }

    public ResponseInsurancePensionPlanContractInfoV2 mapContractInfoDTOV2() {
        return new ResponseInsurancePensionPlanContractInfoV2()
                .data(new InsurancePensionPlanContractInfoV2()
                        .pensionIdentification(this.getPensionPlanContractId())
                        .contractingType(InsurancePensionPlanContractInfoV2.ContractingTypeEnum.fromValue(this.getContractingType()))
                        .documents(this.getDocuments().stream().map(PensionPlanContractDocumentEntity::getDTOV2).toList()));
    }

    public ResponseResourceListData mapResourceDTO() {
        return new ResponseResourceListData()
                .resourceId(this.getPensionPlanContractId());
    }
}
