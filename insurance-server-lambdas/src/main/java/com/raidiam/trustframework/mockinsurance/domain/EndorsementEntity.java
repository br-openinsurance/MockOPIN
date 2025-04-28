package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseEndorsementData.EndorsementTypeEnum;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "endorsements")
public class EndorsementEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "endorsement_id", unique = true, nullable = false, updatable = false, insertable = false)
    private UUID endorsementId;

    @Column(name = "consent_id", nullable = false)
    private String consentId;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "data")
    @Type(JsonType.class)
    private CreateEndorsementData data;

    public static EndorsementEntity fromRequest(CreateEndorsement req, String consentId, String clientId) {
        EndorsementEntity entity = new EndorsementEntity();
        entity.setConsentId(consentId);
        entity.setClientId(clientId);
        entity.setData(req.getData());

        return entity;
    }

    public ResponseEndorsement toResponse() {
        ResponseEndorsementData data = new ResponseEndorsementData();
        data.setCustomData(this.getData().getCustomData());
        data.setEndorsementType(EndorsementTypeEnum.valueOf(this.getData().getEndorsementType().toString()));
        data.setInsuredObjectId(this.getData().getInsuredObjectId());
        data.setPolicyId(this.getData().getPolicyId());
        data.setProposalId(this.getData().getProposalId());
        data.setProtocolDateTime(InsuranceLambdaUtils.getOffsetDateTimeInBrasil());
        data.setProtocolNumber("string");
        data.setRequestDate(this.getData().getRequestDate());
        data.setRequestDescription(this.getData().getRequestDescription());


        return new ResponseEndorsement().data(data);
    }
}
