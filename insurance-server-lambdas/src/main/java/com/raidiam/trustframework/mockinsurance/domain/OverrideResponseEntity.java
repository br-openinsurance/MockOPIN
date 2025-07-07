package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.OverrideData;
import com.raidiam.trustframework.mockinsurance.models.generated.OverrideDataResponse;
import com.raidiam.trustframework.mockinsurance.models.generated.OverridePayload;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "override_responses")
public class OverrideResponseEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(name = "override_id", unique = true, nullable = false, updatable = false, insertable = false, columnDefinition = "uuid NOT NULL DEFAULT uuid_generate_v4()")
    private UUID overrideId;

    @Column(name = "path")
    private String path;

    @Column(name = "method")
    private String method;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "expires_in")
    private Integer expiresIn;

    @Column(name = "response")
    @Type(JsonType.class)
    private OverrideDataResponse response;

    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()).plusSeconds(expiresIn));
    }

    public void updateFromRequest(OverridePayload req, String clientId) {
        this.setClientId(clientId);
        this.setPath(req.getData().getPath());
        this.setMethod(req.getData().getMethod().toString());
        this.setExpiresIn(req.getData().getTimeout() != null ? req.getData().getTimeout() : 60);
        this.setResponse(req.getData().getResponse());
        this.setUpdatedAt(InsuranceLambdaUtils.offsetDateToDate(InsuranceLambdaUtils.getOffsetDateTimeUTC()));
    }

    public OverridePayload toResponse() {
        return new OverridePayload()
                .data(new OverrideData()
                        .path(this.getPath())
                        .method(OverrideData.MethodEnum.fromValue(this.getMethod()))
                        .timeout(this.getExpiresIn())
                        .response(this.getResponse()));
    }
}
