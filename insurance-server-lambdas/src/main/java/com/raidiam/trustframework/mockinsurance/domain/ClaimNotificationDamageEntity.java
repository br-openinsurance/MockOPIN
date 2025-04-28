package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import java.time.OffsetDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "claim_notification_damages")
public class ClaimNotificationDamageEntity extends ClaimNotificationEntity {

    @Column(name = "data")
    @Type(JsonType.class)
    private ClaimNotificationDamageData data;

    public static ClaimNotificationDamageEntity fromRequest(CreateClaimNotificationDamage req, String clientId, String consentId) {
        var entity = new ClaimNotificationDamageEntity();

        entity.setClientId(clientId);
        entity.setConsentId(consentId);
        entity.setData(req.getData());
        return entity;
    }

    public ResponseClaimNotificationDamage toResponse(String redirectLink) {
        this.data.setProtocolNumber("123456");
        this.data.setProtocolDateTime(OffsetDateTime.now());

        var resp = new ResponseClaimNotificationDamage();
        resp.setData(this.data);

        var links = new ClaimNotificationLinks();
        links.setRedirect(redirectLink);
        resp.setLinks(links);

        return resp;
    }

    public ClaimNotificationData getClaimData() {
        return this.data;
    }
}
