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
@Table(name = "claim_notification_persons")
public class ClaimNotificationPersonEntity extends ClaimNotificationEntity {

    @Column(name = "data")
    @Type(JsonType.class)
    private ClaimNotificationPersonData data;

    public static ClaimNotificationPersonEntity fromRequest(CreateClaimNotificationPerson req, String clientId, String consentId) {
        var entity = new ClaimNotificationPersonEntity();

        entity.setClientId(clientId);
        entity.setConsentId(consentId);
        entity.setData(req.getData());
        return entity;
    }

    public static ClaimNotificationPersonEntity fromRequestV2(CreateClaimNotificationPersonV2 req, String clientId, String consentId) {
        var entity = new ClaimNotificationPersonEntity();

        entity.setClientId(clientId);
        entity.setConsentId(consentId);
        entity.setData(req.getData());
        return entity;
    }

    public ResponseClaimNotificationPerson toResponse(String redirectLink) {
        this.data.setProtocolNumber("123456");
        this.data.setProtocolDateTime(OffsetDateTime.now());

        var resp = new ResponseClaimNotificationPerson();
        resp.setData(this.data);

        var links = new ClaimNotificationLinks();
        links.setRedirect(redirectLink);
        resp.setLinks(links);

        return resp;
    }

    public ResponseClaimNotificationPersonV2 toResponseV2(String redirectLink) {
        this.data.setProtocolNumber("123456");
        this.data.setProtocolDateTime(OffsetDateTime.now());

        var resp = new ResponseClaimNotificationPersonV2();
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
