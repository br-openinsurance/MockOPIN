package com.raidiam.trustframework.mockinsurance.fapi;

import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.ServerHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Filter({"/**"})
public class LocalBodyCachingFilter implements HttpServerFilter {

    public static final String CACHED_BODY_KEY = "cachedJWTBody";

    private static final Logger LOG = LoggerFactory.getLogger(LocalBodyCachingFilter.class);

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        boolean hasBody = request.getMethod() == HttpMethod.POST || request.getMethod() == HttpMethod.PUT || request.getMethod() == HttpMethod.PATCH;
        if (!hasBody) {
            return chain.proceed(request);
        }

        if (request instanceof ServerHttpRequest<?> serverRequest) {
            try (var body = serverRequest.byteBody().split()) {
                return Flux.from(body.toByteBufferPublisher()) // Get the body as a Flux of byte arrays.
                        .reduce(new ByteArrayOutputStream(), (outputStream, bytes) -> {
                            try {
                                // Accumulate the chunks.
                                outputStream.write(bytes.toByteArray());
                            } catch (IOException e) {
                                LOG.error("Failed to write body chunk", e);
                            }
                            return outputStream;
                        })
                        .map(outputStream -> {
                            LOG.info("Received body: {}", outputStream);
                            // Cache the body for future use.
                            request.setAttribute(CACHED_BODY_KEY, outputStream.toString());
                            return request;
                        })
                        .flatMapMany(ignored -> Flux.from(chain.proceed(request))) // Proceed with the filter chain.
                        .singleOrEmpty(); // Convert the Flux back to a Publisher.
            }
        }

        return chain.proceed(request);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}