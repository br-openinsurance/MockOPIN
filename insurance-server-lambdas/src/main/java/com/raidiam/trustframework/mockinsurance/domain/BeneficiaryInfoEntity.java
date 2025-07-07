package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.BeneficiaryInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Audited
@Table(name = "beneficiary_info")
public class BeneficiaryInfoEntity extends BaseIdEntity {

    public BeneficiaryInfo mapDTO() {
        return new BeneficiaryInfo()
                .name(this.getName())
                .identification(this.getIdentification())
                .identificationType(BeneficiaryInfo.IdentificationTypeEnum.fromValue(this.getIdentificationType()))
                .identificationTypeOthers(this.getIdentificationTypeOthers());
    }
}
