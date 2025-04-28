package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.ResponseWebhook;
import com.raidiam.trustframework.mockinsurance.models.generated.UpdateWebhook;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "client_webhook_uri")
public class WebhookEntity extends BaseEntity {

    @Id
    @Column(name = "client_id", nullable = false, updatable = false)
    private String clientId;

    @Column(name = "webhook_uri")
    private String webhookUri;

    public ResponseWebhook toResponse() {
        return new ResponseWebhook()
                .clientId(this.clientId)
                .webhookUri(this.webhookUri);
    }

    public static WebhookEntity fromRequest(UpdateWebhook webhook, String clientId) {
        var webhookEntity =  new WebhookEntity();
        webhookEntity.setClientId(clientId);
        webhookEntity.setWebhookUri(webhook.getWebhookUri());

        return webhookEntity;
    }
}
