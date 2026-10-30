package com.raidiam.trustframework.mockinsurance.fapi;

import io.micronaut.context.propagation.slf4j.MdcPropagationContext;
import io.micronaut.core.order.Ordered;
import io.micronaut.core.propagation.MutablePropagatedContext;
import io.micronaut.http.annotation.RequestFilter;
import io.micronaut.http.annotation.ServerFilter;

/**
 * The Lambda runtime populates the MDC (AWSRequestId, AWS-XRAY-TRACE-ID, AWSFunctionName) on the
 * handler thread, but controllers are executed on a separate (virtual) thread where the MDC is empty.
 * Capturing the MDC into the propagated context makes Micronaut restore it on whichever thread
 * runs the rest of the request.
 */
@ServerFilter(ServerFilter.MATCH_ALL_PATTERN)
public class MdcPropagationFilter implements Ordered {

    @RequestFilter
    public void propagateMdc(MutablePropagatedContext propagatedContext) {
        propagatedContext.add(new MdcPropagationContext());
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}