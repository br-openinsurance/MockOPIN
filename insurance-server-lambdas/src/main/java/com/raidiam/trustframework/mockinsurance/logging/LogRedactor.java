package com.raidiam.trustframework.mockinsurance.logging;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Masks credentials and personal identifiers in log output before it reaches CloudWatch.
 * Applied by {@link com.raidiam.trustframework.bank.logging.JsonLoggingEncoder} to every message and stack trace, so call sites
 * don't need to remember to sanitise what they log.
 */
public final class LogRedactor {

    static final String REDACTED = "[REDACTED]";

    private static final String SENSITIVE_KEYS =
            "access_token|refresh_token|id_token|client_secret|client_assertion|password|authorization";

    private static final List<Rule> RULES = List.of(
            // "access_token":"..." inside serialised JSON bodies
            new Rule("(?i)(\"(?:" + SENSITIVE_KEYS + ")\"\\s*:\\s*)\"[^\"]*\"", "$1\"" + REDACTED + "\""),
            // access_token=... in form-encoded bodies and query strings
            new Rule("(?i)\\b((?:" + SENSITIVE_KEYS + ")=)[^&\\s\"]+", "$1" + REDACTED),
            // Authorization header values
            new Rule("(?i)\\b(Bearer|Basic)\\s+[A-Za-z0-9\\-._~+/]+=*", "$1 " + REDACTED),
            // Compact JWS/JWE (header segment always starts with base64url "{")
            new Rule("\\beyJ[A-Za-z0-9_-]*(?:\\.[A-Za-z0-9_-]*){2,4}", REDACTED),
            // CNPJ: 00.000.000/0000-00 or 14 bare digits
            new Rule("\\b\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}\\b", REDACTED),
            new Rule("(?<![\\w-])\\d{14}(?![\\w-])", REDACTED),
            // CPF: 000.000.000-00 or 11 bare digits
            new Rule("\\b\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}\\b", REDACTED),
            new Rule("(?<![\\w-])\\d{11}(?![\\w-])", REDACTED)
    );

    private LogRedactor() {
    }

    public static String redact(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String result = value;
        for (Rule rule : RULES) {
            result = rule.apply(result);
        }
        return result;
    }

    private record Rule(Pattern pattern, String replacement) {
        Rule(String regex, String replacement) {
            this(Pattern.compile(regex), replacement);
        }

        String apply(String input) {
            Matcher matcher = pattern.matcher(input);
            return matcher.find() ? matcher.replaceAll(replacement) : input;
        }
    }
}
