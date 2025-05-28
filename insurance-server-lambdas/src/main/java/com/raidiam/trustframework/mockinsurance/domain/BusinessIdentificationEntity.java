package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
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
@Table(name = "business_identifications")
public class BusinessIdentificationEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "business_identification_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID businessIdentificationId;

    @Column(name = "cnpj_number")
    private String cnpjNumber;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    public BusinessIdentificationData mapDto() {
        return new BusinessIdentificationData()
                .updateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()))
                .businessId(this.getBusinessIdentificationId().toString())
                .brandName("MockOPIN")
                .companyInfo(new CompanyInfo()
                        .cnpjNumber("01773247000563")
                        .name("MockOPIN"))
                .businessName("Luiza e Benjamin Assessoria Jurídica Ltda")
                .document(new BusinessDocument()
                        .businesscnpjNumber(this.getCnpjNumber()))
                .contact(new BusinessContact()
                        .postalAddresses(List.of(new BusinessPostalAddress()
                                .address("Av Naburo Ykesaki, 1270")
                                .townName("Sao Paulo")
                                .countrySubDivision(EnumCountrySubDivision.SP)
                                .postCode("17500001")
                                .country("Brasil"))));
    }
}
