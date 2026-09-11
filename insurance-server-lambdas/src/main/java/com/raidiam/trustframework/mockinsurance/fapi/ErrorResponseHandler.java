package com.raidiam.trustframework.mockinsurance.fapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseError;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseErrorErrors;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpMethod;
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
import java.util.regex.Pattern;

import static io.micronaut.http.HttpMethod.POST;
import static io.micronaut.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Produces
@Singleton
@Requires(classes = {ErrorResponseProcessor.class})
public class ErrorResponseHandler implements ErrorResponseProcessor<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorResponseHandler.class);

    private static final String DEFAULT_CODE = "NAO_INFORMADO";
    private static final String ERRORS_KEY = "errors";
    private static final Pattern KNOWN_ERROR_CODE = Pattern.compile("^[A-Z_]+$");
    private static final Pattern JAVA_TYPE_REFERENCE = Pattern.compile("`[a-z][\\w.$]*`");

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
        String path = errorContext.getRequest().getPath();
        Object respError = error;

        if (isSingleErrorResponse(path, response.getStatus(), errorContext.getRequest().getMethod())) {
            respError = singleErrorResponse(error);
        }

        InsuranceLambdaUtils.logObject(objectMapper, respError);
        String json = objectMapper.writeValueAsString(respError);
        response.getHeaders().remove(HttpHeaders.CONTENT_TYPE);
        response.contentType(MediaType.APPLICATION_JSON_TYPE);
        return response.body(json);
    }

    private Map<String, ResponseErrorErrors> singleErrorResponse(ResponseError error) {
        return Map.of(ERRORS_KEY, error.getErrors().get(0));
    }

    private boolean isSingleErrorResponse(
            String path, HttpStatus status, HttpMethod method) {

        return UNPROCESSABLE_ENTITY.equals(status) &&
                (path.contains("consents/v2") || isPostWithSingleError(path, method));
    }

    private boolean isPostWithSingleError(
            String path, HttpMethod method) {
        return POST.equals(method) &&
                path.contains("/v1") &&
                (path.contains("/quote-")
                        || path.contains("/withdrawal/")
                        || path.contains("/claim-notification/")
                        || path.contains("/endorsement/"));
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
        String[] parts = message.split(":", 2);
        if (parts.length == 2 && KNOWN_ERROR_CODE.matcher(parts[0].trim()).matches()) {
            return parts[1].trim();
        }
        return JAVA_TYPE_REFERENCE.matcher(message).replaceAll("the requested type").trim();
    }

    public String generateCode(String message){
        String candidate = message.split(":", 2)[0].trim();
        return KNOWN_ERROR_CODE.matcher(candidate).matches() ? candidate : DEFAULT_CODE;
    }
}
