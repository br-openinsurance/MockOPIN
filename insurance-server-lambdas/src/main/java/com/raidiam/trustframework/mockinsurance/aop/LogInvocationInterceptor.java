package com.raidiam.trustframework.mockinsurance.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raidiam.trustframework.mockinsurance.utils.LogUtils;
import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@InterceptorBean(LogInvocation.class)
public class LogInvocationInterceptor implements MethodInterceptor<Object, Object> {

    private final ObjectMapper mapper;

    public LogInvocationInterceptor(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @SuppressWarnings("java:S2139")
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        Logger log = LoggerFactory.getLogger(context.getDeclaringType());
        String methodName = context.getMethodName();

        log.info("Entering {}", methodName);
        logBodyArgument(context, log);

        long startedAt = System.nanoTime();
        try {
            Object result = context.proceed();
            log.info("Exiting {} ({} ms)", methodName, elapsedMs(startedAt));
            Object resultBody = result instanceof HttpResponse<?> httpResponse ? httpResponse.body() : result;
            if (resultBody != null) {
                LogUtils.logObject(mapper, resultBody, log);
            }
            return result;
        } catch (RuntimeException e) {
            log.warn("{} failed after {} ms: {}", methodName, elapsedMs(startedAt), e.getMessage());
            throw e;
        }
    }

    private void logBodyArgument(MethodInvocationContext<Object, Object> context, Logger log) {
        Argument<?>[] arguments = context.getArguments();
        Object[] parameterValues = context.getParameterValues();
        for (int i = 0; i < arguments.length; i++) {
            if (parameterValues[i] != null && arguments[i].isAnnotationPresent(Body.class)) {
                LogUtils.logObject(mapper, parameterValues[i], log);
            }
        }
    }

    private long elapsedMs(long startedAtNanos) {
        return (System.nanoTime() - startedAtNanos) / 1_000_000;
    }
}
