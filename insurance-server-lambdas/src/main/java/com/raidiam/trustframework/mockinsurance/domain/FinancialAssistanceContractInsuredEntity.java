package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceFinancialAssistanceInsured;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "financial_assistance_contract_insureds")
public class FinancialAssistanceContractInsuredEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Generated()
    @Column(name = "financial_assistance_contract_insured_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID financialAssistanceContractInsuredId;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_type_others")
    private String documentTypeOthers;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "financial_assistance_contract_id")
    private String financialAssistanceContractId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_assistance_contract_id", referencedColumnName = "financial_assistance_contract_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private FinancialAssistanceContractEntity financialAssistanceContract;

    public InsuranceFinancialAssistanceInsured mapDto() {
        return new InsuranceFinancialAssistanceInsured()
                .documentType(InsuranceFinancialAssistanceInsured.DocumentTypeEnum.valueOf(this.getDocumentType()))
                .documentTypeOthers(this.getDocumentTypeOthers())
                .documentNumber(this.getDocumentNumber())
                .name(this.getName());
    }
}
