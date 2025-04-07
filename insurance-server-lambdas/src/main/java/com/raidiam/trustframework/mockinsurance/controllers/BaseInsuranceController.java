package com.raidiam.trustframework.mockinsurance.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.server.exceptions.HttpStatusHandler;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseInsuranceController {

    private static final Logger LOG = LoggerFactory.getLogger(BaseInsuranceController.class);

    @Value("${mockinsurance.mockinsuranceUrl}")
    protected String appBaseUrl;

    @Inject
    protected ObjectMapper mapper;

    @Inject
    protected InsuranceLambdaUtils insuranceLambdaUtils;

    @Inject
    private HttpStatusHandler statusHandler;

    @PostConstruct
    public void init() {
        if (!appBaseUrl.startsWith("https://")) {
            appBaseUrl = "https://" + appBaseUrl;
        }

        LOG.info("Base Insurance URL initialized: {}", appBaseUrl);
    }

    // Log out http status errors, so that we can see what happened, then pass to the default handler
    @Error(exception = HttpStatusException.class)
    public HttpResponse<?> error(HttpRequest<?> request, HttpStatusException exception) {
        LOG.info("Received exception {}, code {}, on request. Message - \"{}\"", exception.getClass(), exception.getStatus(), exception.getMessage());
        return statusHandler.handle(request, exception);
    }
}
