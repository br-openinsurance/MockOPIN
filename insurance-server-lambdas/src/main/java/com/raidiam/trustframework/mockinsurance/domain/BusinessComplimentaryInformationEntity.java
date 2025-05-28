package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.BusinessComplimentaryInformationData;
import com.raidiam.trustframework.mockinsurance.models.generated.BusinessComplimentaryInformationDataProductsServices;
import com.raidiam.trustframework.mockinsurance.models.generated.EnumProductServiceType;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "business_complimentary_information")
public class BusinessComplimentaryInformationEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "business_complimentary_information_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID businessComplimentaryInfoId;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    public BusinessComplimentaryInformationData mapDto() {
        return new BusinessComplimentaryInformationData()
                .updateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()))
                .startDate(InsuranceLambdaUtils.dateToLocalDate(this.getCreatedAt()))
                .productsServices(List.of(new BusinessComplimentaryInformationDataProductsServices()
                        .contract("12345")
                        .type(EnumProductServiceType.MICROSSEGUROS)));
    }
}
