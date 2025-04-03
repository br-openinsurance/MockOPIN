package com.raidiam.trustframework.mockinsurance.fapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.Pair;
import com.raidiam.trustframework.mockinsurance.domain.IdempotencyEntity;
import com.raidiam.trustframework.mockinsurance.repository.IdempotencyRepository;
import com.raidiam.trustframework.mockinsurance.utils.AnnotationsUtil;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Filter("/**")
public class IdempotencyFilter implements HttpServerFilter {
    private static final Logger LOG = LoggerFactory.getLogger(IdempotencyFilter.class);
    private static final String X_FAPI_IDEMPOTENCY_ID = "x-idempotency-key";
    private static final String IDEMPOTENCY_ID_VALIDATION_REGEX = "^([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12})$";
    private static final List<HttpStatus> SUCCESSFUL_STATUS_CODES = List.of(HttpStatus.CREATED, HttpStatus.OK);
    @Inject
    private IdempotencyRepository idempotencyRepository;
    @Inject
    private ObjectMapper objectMapper;
    private final List<Pair<HttpMethod, String>> requiredIdempotentRegexes = new LinkedList<>();

    private final ApplicationContext applicationContext;

    @Inject
    public IdempotencyFilter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    private void init() {
        AnnotationsUtil.performActionsOnControllerMethodByAnnotation(applicationContext, Idempotent.class, (fullPath, httpMethod, extractedAnnotation) -> {
            requiredIdempotentRegexes.add(Pair.of(httpMethod, fullPath));
            LOG.info("Added required x-idempotency-key header regex {} - {}", httpMethod, fullPath);
        });
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        if(!isRequired(request)) {
            return chain.proceed(request);
        }

        String idempotencyId = request.getHeaders().findFirst(X_FAPI_IDEMPOTENCY_ID)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "ERRO_IDEMPOTENCIA: x-idempotency-key is missing"));
        this.validate(idempotencyId);

        Optional<IdempotencyEntity> entityOp = this.idempotencyRepository.findByIdempotencyId(idempotencyId);
        if (entityOp.isEmpty()) {
            return Mono.from(chain.proceed(request)).map(response -> {
                if (!SUCCESSFUL_STATUS_CODES.contains(response.getStatus())) {
                    return response;
                }
                var entity = new IdempotencyEntity();
                entity.setIdempotencyId(idempotencyId);
                entity.setRequest(getRequestAsString(request).orElseThrow());
                entity.setResponse(getResponseAsString(response).orElseThrow());
                this.idempotencyRepository.save(entity);
                return response;
            });
        }

        var entity = entityOp.get();
        if (!entity.getRequest().equals(getRequestAsString(request).orElseThrow())) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "ERRO_IDEMPOTENCIA: invalid idempotency");
        }

        if (HttpMethod.POST.equals(request.getMethod())) {
            return Mono.just(HttpResponse.created(entity.getPlainResponse())
                    .contentType(io.micronaut.http.MediaType.APPLICATION_JSON));
        }
        return Mono.just(HttpResponse.ok(entity.getPlainResponse())
                .contentType(io.micronaut.http.MediaType.APPLICATION_JSON));
    }

    private Optional<String> getRequestAsString(HttpRequest<?> request) {
        // are we in a (netty) server situation and need to take special measures to get the body?
        if (request instanceof ServerHttpRequest) {
            return request.getAttribute(LocalBodyCachingFilter.CACHED_BODY_KEY).map(Object::toString);
        }

        return request.getBody(Object.class).map(o -> {
            try {
                return objectMapper.writeValueAsBytes(o);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).map(bytes -> Base64.getEncoder().encodeToString(bytes));
    }

    private Optional<String> getResponseAsString(MutableHttpResponse<?> response) {
        return response.getBody(Object.class).map(o -> {
            try {
                return objectMapper.writeValueAsBytes(o);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).map(bytes -> Base64.getEncoder().encodeToString(bytes));
    }

    private boolean isRequired(HttpRequest<?> request) {
        for (Pair<HttpMethod, String> requiredIdempotencyRule : requiredIdempotentRegexes) {
            HttpMethod method = requiredIdempotencyRule.getLeft();
            String regex = requiredIdempotencyRule.getRight();
            String requestPath = request.getPath();
            if (request.getMethod() == method && requestPath.matches(regex)) {
                LOG.info("found matching pattern - {} - {} for path {}", method, regex, requestPath);
                return true;
            }
        }
        LOG.info("no matching patterns found");
        return false;
    }

    private void validate(String interactionId){
        LOG.info("Validating {} - {}", X_FAPI_IDEMPOTENCY_ID, interactionId);
        if (!interactionId.matches(IDEMPOTENCY_ID_VALIDATION_REGEX)) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("ERRO_IDEMPOTENCIA: x-idempotency-key - %s is invalid", interactionId));
        }
    }

    @Override
    public int getOrder() {
        return ServerFilterPhase.SECURITY.after();
    }
}
