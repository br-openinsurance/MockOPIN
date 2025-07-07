package com.raidiam.trustframework.mockinsurance.fapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raidiam.trustframework.mockinsurance.services.OverrideService;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import jakarta.inject.Inject;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@Filter("/open-insurance/**")
public class OverrideFilter implements HttpServerFilter {
    private static final Logger LOG = LoggerFactory.getLogger(OverrideFilter.class);

    @Inject
    private OverrideService overrideService;

    @Inject
    protected ObjectMapper mapper;

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        String clientId = InsuranceLambdaUtils.getClientIdFromRequest(request);
        LOG.info("Verifying response override for client {}", clientId);
        var overrideEntityOp = overrideService.getOverride(clientId, request.getPath(), request.getMethodName());
        if (overrideEntityOp.isEmpty()) {
            LOG.info("No response override found");
            return chain.proceed(request);
        }

        try {
            return Mono.just(HttpResponse
                    .ok(mapper.writeValueAsString(overrideEntityOp.get().getResponse().getBody()))
                    .contentType(MediaType.APPLICATION_JSON));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getOrder() {
        return ServerFilterPhase.SECURITY.after();
    }
}
