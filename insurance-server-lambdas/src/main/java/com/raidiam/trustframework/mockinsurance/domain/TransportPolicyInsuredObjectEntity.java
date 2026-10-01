package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetailsUnit;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceTransportInsuredObject;
import com.raidiam.trustframework.mockinsurance.models.generated.InsuranceTransportInsuredObjectV2;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "transport_policy_insured_objects")
public class TransportPolicyInsuredObjectEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "transport_policy_insured_object_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID insuredObjectId;

    @Column(name = "transport_policy_id")
    private String transportPolicyId;

    @Column(name = "identification")
    private String identification;

    @Column(name = "type")
    private String type;

    @Column(name = "type_additional_info")
    private String typeAdditionalInfo;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private String amount;

    @Column(name = "unit_type")
    private String unitType;

    @Column(name = "unit_type_others")
    private String unitTypeOthers;

    @Column(name = "unit_code")
    private String unitCode;

    @Column(name = "unit_description")
    private String unitDescription;

    @Column(name = "currency")
    private String currency;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "transportInsuredObject")
    private List<TransportPolicyInsuredObjectCoverageEntity> coverages = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_policy_id", referencedColumnName = "transport_policy_id", insertable = false, nullable = false, updatable = false)
    @NotAudited
    private TransportPolicyEntity transportPolicy;

    public InsuranceTransportInsuredObject mapDto() {
        return new InsuranceTransportInsuredObject()
                .identification(this.getIdentification())
                .type(InsuranceTransportInsuredObject.TypeEnum.fromValue(this.getType()))
                .typeAdditionalInfo(this.getTypeAdditionalInfo())
                .description(this.getDescription())
                .amount(this.mapAmount())
                .coverages(this.getCoverages().stream().map(TransportPolicyInsuredObjectCoverageEntity::mapDto).toList());
    }

    public InsuranceTransportInsuredObjectV2 mapDtoV2() {
        return new InsuranceTransportInsuredObjectV2()
                .identification(this.getIdentification())
                .type(InsuranceTransportInsuredObjectV2.TypeEnum.fromValue(this.getType()))
                .typeAdditionalInfo(this.getTypeAdditionalInfo())
                .description(this.getDescription())
                .amount(this.mapAmount())
                .coverages(this.getCoverages().stream().map(TransportPolicyInsuredObjectCoverageEntity::mapDto).toList());
    }

    private AmountDetails mapAmount() {
        return new AmountDetails()
                .amount(this.getAmount())
                .unitType(AmountDetails.UnitTypeEnum.fromValue(this.getUnitType()))
                .unitTypeOthers(this.getUnitTypeOthers())
                .unit(new AmountDetailsUnit()
                        .code(this.getUnitCode())
                        .description(AmountDetailsUnit.DescriptionEnum.fromValue(this.getUnitDescription())))
                .currency(AmountDetails.CurrencyEnum.fromValue(this.getCurrency()));
    }
}
