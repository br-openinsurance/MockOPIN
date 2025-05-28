package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.PersonalQualificationData;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "personal_qualifications")
public class PersonalQualificationEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "personal_qualification_id", unique = true, nullable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private UUID personalQualificationId;

    @Column(name = "account_holder_id")
    private UUID accountHolderId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id", referencedColumnName = "account_holder_id", insertable = false, nullable = false, updatable = false)
    private AccountHolderEntity accountHolder;

    public PersonalQualificationData mapDto() {
        return new PersonalQualificationData()
                .updateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()))
                .pepIdentification(PersonalQualificationData.PepIdentificationEnum.NAO_EXPOSTO)
                .lifePensionPlans(PersonalQualificationData.LifePensionPlansEnum.SIM);
    }
}
