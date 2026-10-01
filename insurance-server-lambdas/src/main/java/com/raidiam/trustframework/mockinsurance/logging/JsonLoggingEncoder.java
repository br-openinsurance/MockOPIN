package com.raidiam.trustframework.mockinsurance.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.encoder.EncoderBase;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.raidiam.trustframework.mockinsurance.logging.LogRedactor.redact;

public class JsonLoggingEncoder extends EncoderBase<ILoggingEvent> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
    private static final byte[] EMPTY = new byte[0];

    @Override
    public byte[] headerBytes() {
        return EMPTY;
    }

    @Override
    public byte[] footerBytes() {
        return EMPTY;
    }

    @Override
    public byte[] encode(ILoggingEvent event) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("level", event.getLevel().toString());
        fields.put("time", TIMESTAMP_FORMAT.format(Instant.ofEpochMilli(event.getTimeStamp())));
        fields.put("class", event.getLoggerName());

        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (throwableProxy != null) {
            fields.put("stack", redact(ThrowableProxyUtil.asString(throwableProxy)));
            fields.put("rootstack", rootStackTraceElement(throwableProxy));
        }

        fields.put("message", redact(event.getFormattedMessage()));

        putIfPresent(fields, "awsrequestid", event.getMDCPropertyMap().get("AWSRequestId"));
        putIfPresent(fields, "awstraceid", event.getMDCPropertyMap().get("AWS-XRAY-TRACE-ID"));
        putIfPresent(fields, "awsfunctionname", event.getMDCPropertyMap().get("AWSFunctionName"));

        try {
            return (MAPPER.writeValueAsString(fields) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return ("{\"level\":\"ERROR\",\"class\":\"" + JsonLoggingEncoder.class.getName()
                    + "\",\"message\":\"Failed to encode log event\"}" + System.lineSeparator())
                    .getBytes(StandardCharsets.UTF_8);
        }
    }

    private static void putIfPresent(Map<String, Object> fields, String key, String value) {
        if (value != null && !value.isEmpty()) {
            fields.put(key, value);
        }
    }

    private static String rootStackTraceElement(IThrowableProxy throwableProxy) {
        IThrowableProxy root = throwableProxy;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        StackTraceElementProxy[] trace = root.getStackTraceElementProxyArray();
        return trace.length == 0
                ? root.getClassName()
                : root.getClassName() + ": " + trace[0].getStackTraceElement();
    }
}
