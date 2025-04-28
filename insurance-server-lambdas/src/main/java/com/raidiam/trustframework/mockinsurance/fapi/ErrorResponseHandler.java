package com.raidiam.trustframework.mockinsurance.fapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseError;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseErrorErrors;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.response.ErrorContext;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import io.micronaut.security.authentication.AuthorizationException;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Produces
@Singleton
@Requires(classes = {ErrorResponseProcessor.class})
public class ErrorResponseHandler implements ErrorResponseProcessor<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorResponseHandler.class);

    private final ObjectMapper objectMapper;
    private final InsuranceLambdaUtils insuranceLambdaUtils;

    public ErrorResponseHandler(ObjectMapper objectMapper, InsuranceLambdaUtils insuranceLambdaUtils) {
        this.objectMapper = objectMapper;
        this.insuranceLambdaUtils = insuranceLambdaUtils;
    }

    @Override
    public @NonNull MutableHttpResponse<Object> processResponse(
            @NonNull ErrorContext errorContext,
            @NonNull MutableHttpResponse<?> baseResponse
    ) {
        LOG.info("Processing error response");
        errorContext.getErrors().forEach(e -> LOG.error("error: {}", e.getMessage()));
        return buildJsonError(errorContext, baseResponse);
    }

    @SneakyThrows
    private MutableHttpResponse<Object> buildJsonError(ErrorContext errorContext, MutableHttpResponse<?> response) {
        final ResponseError error = new ResponseError();
        error.addErrorsItem(errorContext.getRootCause()
                .filter(AuthorizationException.class::isInstance)
                .map(AuthorizationException.class::cast)
                .filter(AuthorizationException::isForbidden)
                .map(authException -> generateForbiddenError())
                .orElseGet(() -> generateResponseError(errorContext.getErrors().get(0).getMessage())));

        insuranceLambdaUtils.decorateResponseError(error, errorContext.getRequest());
        Object respError = error;
        if (HttpStatus.UNPROCESSABLE_ENTITY.equals(response.getStatus())) {
            respError = Map.of("errors", error.getErrors().get(0));
        }
        InsuranceLambdaUtils.logObject(objectMapper, respError);
        String json = objectMapper.writeValueAsString(respError);
        response.getHeaders().remove(HttpHeaders.CONTENT_TYPE);
        response.contentType(MediaType.APPLICATION_JSON_TYPE);
        return response.body(json);
    }

    private ResponseErrorErrors generateForbiddenError() {
        return new ResponseErrorErrors()
                .code(String.valueOf(HttpStatus.FORBIDDEN.getCode()))
                .title(HttpStatus.FORBIDDEN.toString())
                .detail(HttpStatus.FORBIDDEN.toString());
    }

    private ResponseErrorErrors generateResponseError(String message){
        String code = generateCode(message);
        String detail = generateDetail(message);
        return new ResponseErrorErrors()
                .code(code)
                .title(code)
                .detail(detail);
    }

    public String generateDetail(String message){
        try {
            return message.split(": ")[1];
        } catch (ArrayIndexOutOfBoundsException e){
            return message.split(": ")[0];
        }
    }

    public String generateCode(String message){
        return message.split(":")[0];
    }
}
