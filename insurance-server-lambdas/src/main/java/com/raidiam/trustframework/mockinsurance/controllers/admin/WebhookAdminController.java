package com.raidiam.trustframework.mockinsurance.controllers.admin;

import com.raidiam.trustframework.mockinsurance.controllers.BaseInsuranceController;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseWebhook;
import com.raidiam.trustframework.mockinsurance.models.generated.UpdateWebhook;
import com.raidiam.trustframework.mockinsurance.services.WebhookService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/admin/webhook")
@Secured({"ADMIN_FULL_MANAGE"})
public class WebhookAdminController extends BaseInsuranceController {
    private static final Logger LOG = LoggerFactory.getLogger(WebhookAdminController.class);

    @Inject
    private WebhookService webhookService;

    @Put("/{clientId}")
    public ResponseWebhook updateWebhookUri(@PathVariable("clientId") String clientId, @Body UpdateWebhook webhook) {
        LOG.info("Registering new webhook URI for client {}", clientId);
        var response = webhookService.updateWebhook(webhook, clientId);
        LOG.info("Returning webhook response");
        InsuranceLambdaUtils.logObject(mapper, response);
        return response;
    }
}
