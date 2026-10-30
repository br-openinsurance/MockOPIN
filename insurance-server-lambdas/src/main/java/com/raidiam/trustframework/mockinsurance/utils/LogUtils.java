package com.raidiam.trustframework.mockinsurance.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public final class LogUtils {

    private LogUtils() {
    }

    public static void logIfPresent(String message, String value, Logger logger) {
        if (StringUtils.isNotEmpty(value)) {
            logger.info(message, value);
        }
    }

    public static void logObject(ObjectMapper mapper, Object res, Logger logger) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        try {
            String response = mapper.writeValueAsString(res);
            logger.debug("{} - {}", res.getClass().getSimpleName(), response);
        } catch (JsonProcessingException e) {
            logger.error("{} - Error writing object as JSON: ", res.getClass().getSimpleName(), e);
        }
    }
}
