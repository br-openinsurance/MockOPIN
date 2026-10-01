package com.raidiam.trustframework.mockinsurance.controllers.admin;

import com.raidiam.trustframework.mockinsurance.aop.LogInvocation;
import com.raidiam.trustframework.mockinsurance.controllers.BaseInsuranceController;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseWebhook;
import com.raidiam.trustframework.mockinsurance.models.generated.UpdateWebhook;
import com.raidiam.trustframework.mockinsurance.services.WebhookService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.annotation.Secured;

import java.util.Objects;

@Controller("/admin/webhook")
@Secured({"ADMIN_FULL_MANAGE"})
public class WebhookAdminController extends BaseInsuranceController {

    private final WebhookService webhookService;

    @Value("${mockinsurance.operatorClientId}")
    protected String operatorClientId;

    public WebhookAdminController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Put("/{clientId}")
    @LogInvocation
    public ResponseWebhook updateWebhookUri(@PathVariable("clientId") String clientId, @Body UpdateWebhook webhook, HttpRequest<?> request) {
        requireOperatorCaller(request);
        return webhookService.updateWebhook(webhook, clientId);
    }

    private void requireOperatorCaller(HttpRequest<?> request) {
        String callerClientId = InsuranceLambdaUtils.getRequestMeta(request).getClientId();
        if (!Objects.equals(callerClientId, operatorClientId)) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "Only the operator client may manage webhook registrations");
        }
    }
}
