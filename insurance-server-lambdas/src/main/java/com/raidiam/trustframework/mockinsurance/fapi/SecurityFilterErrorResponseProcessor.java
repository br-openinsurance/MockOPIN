package com.raidiam.trustframework.mockinsurance.fapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseError;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseErrorErrors;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Filter("/**")
public class SecurityFilterErrorResponseProcessor implements HttpServerFilter {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityFilterErrorResponseProcessor.class);
    private final ObjectMapper objectMapper;
    private final InsuranceLambdaUtils insuranceLambdaUtils;
    private static final List<HttpStatus> ERROR_RESPONSE_LIST = List.of(
            HttpStatus.UNAUTHORIZED,
            HttpStatus.FORBIDDEN
    );

    public SecurityFilterErrorResponseProcessor(ObjectMapper objectMapper, InsuranceLambdaUtils insuranceLambdaUtils) {
        this.objectMapper = objectMapper;
        this.insuranceLambdaUtils = insuranceLambdaUtils;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest request, ServerFilterChain chain) {
        return Publishers.map(chain.proceed(request), response -> {
            if (ERROR_RESPONSE_LIST.contains(response.getStatus())) {
                // Check if the response body is empty
                if (response.getBody().isEmpty()) {
                    LOG.info("Generating error response for request with incorrect scopes for endpoint: {} Code: {}", request.getPath(), response.getStatus());
                    return response.body(buildError(request, response));
                }

                response.getBody().ifPresent(body -> {
                    if (body instanceof ResponseErrorErrors) {
                        response.body(buildResponse(request, response, (ResponseErrorErrors) body));
                    }
                });
            }
            return response;
        });    }

    private String buildError(HttpRequest<?> request, MutableHttpResponse<?> response) {
        final ResponseErrorErrors error = new ResponseErrorErrors()
                .code(String.valueOf(response.getStatus().getCode()))
                .title(response.getStatus().toString())
                .detail(response.getStatus().toString());
        return buildResponse(request, response, error);
    }

    @SneakyThrows
    private String buildResponse(HttpRequest<?> request, MutableHttpResponse<?> response, ResponseErrorErrors error) {
        final ResponseError errorResponse = new ResponseError();

        errorResponse.addErrorsItem(error);
        insuranceLambdaUtils.decorateResponseError(errorResponse, request);

        response.getHeaders().remove(HttpHeaders.CONTENT_TYPE);
        response.contentType(MediaType.APPLICATION_JSON_TYPE);

        return objectMapper.writeValueAsString(errorResponse);
    }
}
