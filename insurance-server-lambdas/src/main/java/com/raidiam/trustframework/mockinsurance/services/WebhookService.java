package com.raidiam.trustframework.mockinsurance.services;

import com.raidiam.trustframework.mockinsurance.domain.WebhookEntity;
import com.raidiam.trustframework.mockinsurance.exceptions.TrustframeworkException;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseWebhook;
import com.raidiam.trustframework.mockinsurance.models.generated.UpdateWebhook;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Singleton
@Transactional
public class WebhookService extends BaseInsuranceService {

    public static final int MAX_WEBHOOK_NOTIFICATION_TRIES = 3;
    public static final long NOTIFICATION_DELAY_MILLIS = 100;
    private static final Logger LOG = LoggerFactory.getLogger(WebhookService.class);
    @Inject
    @Client
    private HttpClient httpClient;

    private static final String SWAGGER_VERSION = "1.0.0";

    public ResponseWebhook setWebhookUri(UpdateWebhook req) {
        LOG.info("Saving req URI for client {}", req.getClientId());
        webhookRepository.findByClientId(req.getClientId()).ifPresentOrElse(
                e -> {
                    LOG.info("Client {} webhook uri already exists, updating it", req.getClientId());
                    e.setWebhookUri(req.getWebhookUri());
                    webhookRepository.update(e);
                },
                () -> {
                    LOG.info("Client {} webhook uri does not exist, creating it", req.getClientId());
                    webhookRepository.save(WebhookEntity.fromRequest(req));
                }
        );

        return webhookRepository.findByClientId(req.getClientId())
                .orElseThrow(() -> new TrustframeworkException("Could not find recently saved WebhookEntity"))
                .toResponse();
    }

    public void notify(String clientId, String path) {
        Optional<WebhookEntity> entityOp = this.webhookRepository.findByClientId(clientId);
        if (entityOp.isEmpty()) {
            LOG.info("Could not find webhook for client {}", clientId);
            return;
        }

        var entity = entityOp.get();
        if (StringUtils.isEmpty(entity.getWebhookUri())) {
            LOG.info("Webhook uri for client {} is empty", clientId);
            return;
        }

        String webhookUri = entityOp.get().getWebhookUri() + path;
        LOG.info("Webhook URI found - sending timestamp for status update to {}", webhookUri);

        OffsetDateTime now = InsuranceLambdaUtils.getOffsetDateTimeUTC();
        DateTimeFormatter zuluFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String payload = String.format("{\"data\": {\"timestamp\": \"%s\"}}", now.format(zuluFormatter));
        HttpRequest<String> request = HttpRequest.POST(webhookUri, payload)
                .header("x-webhook-interaction-id", UUID.randomUUID().toString())
                .header("x-v", SWAGGER_VERSION)
                .contentType(MediaType.APPLICATION_JSON);

        sendNotification(request);
    }

    protected void sendNotification(HttpRequest<?> request) {
        LOG.info("Trying to send the notification");
        for(int i = 0; i < MAX_WEBHOOK_NOTIFICATION_TRIES; i++) {
            try {
                HttpResponse<String> response = httpClient.toBlocking().exchange(request, String.class);
                int status = response.code();
                LOG.info("Notification resulted in status: {}", status);
                if(status >= 200 && status < 300) {
                    LOG.info("Successful notification");
                    break;
                }
                this.sleep();
            } catch (Exception e) {
                LOG.error("Error sending notification: {}", e.getMessage());
            }
        }
    }

    protected void sleep() {
        try {
            Thread.sleep(NOTIFICATION_DELAY_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
