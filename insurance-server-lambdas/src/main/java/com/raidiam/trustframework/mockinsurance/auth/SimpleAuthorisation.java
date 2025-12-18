package com.raidiam.trustframework.mockinsurance.auth;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.PlainObject;
import com.nimbusds.jose.util.Pair;
import com.raidiam.trustframework.mockinsurance.utils.AnnotationsUtil;
import io.micronaut.context.ApplicationContext;
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyServletRequest;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.ClientAuthentication;
import io.micronaut.security.filters.AuthenticationFetcher;
import io.reactivex.Flowable;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.entry;


public class SimpleAuthorisation implements AuthenticationFetcher<HttpRequest<?>> {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleAuthorisation.class);
    private static final Pattern BEARER = Pattern.compile("^Bearer\\s(?<token>.+)");
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ALLOWED = "ALLOWED";

    private final Map<String, String> scopesToRoles = Map.ofEntries(
            entry("openid", "OPENID"),
            entry("consents", "CONSENTS_MANAGE"),
            entry("resources", "RESOURCES_READ"),
            entry("endorsement", "ENDORSEMENT_REQUEST_MANAGE"),
            entry("claim-notification", "CLAIM_NOTIFICATION_REQUEST_MANAGE"),
            entry("quote-patrimonial-lead", "QUOTE_PATRIMONIAL_LEAD_MANAGE"),
            entry("quote-patrimonial-business", "QUOTE_PATRIMONIAL_BUSINESS_MANAGE"),
            entry("quote-patrimonial-home", "QUOTE_PATRIMONIAL_HOME_MANAGE"),
            entry("quote-patrimonial-condominium", "QUOTE_PATRIMONIAL_CONDOMINIUM_MANAGE"),
            entry("quote-patrimonial-diverse-risks", "QUOTE_PATRIMONIAL_DIVERSE_RISKS_MANAGE"),
            entry("insurance-patrimonial", "PATRIMONIAL_MANAGE"),
            entry("quote-financial-risk-lead", "QUOTE_FINANCIAL_RISK_LEAD_MANAGE"),
            entry("quote-acceptance-and-branches-abroad-lead", "QUOTE_ACCEPTANCE_AND_BRANCHES_ABROAD_LEAD_MANAGE"),
            entry("insurance-acceptance-and-branches-abroad", "ACCEPTANCE_AND_BRANCHES_ABROAD_MANAGE"),
            entry("quote-housing-lead", "QUOTE_HOUSING_LEAD_MANAGE"),
            entry("quote-responsibility-lead", "QUOTE_RESPONSIBILITY_LEAD_MANAGE"),
            entry("quote-transport-lead", "QUOTE_TRANSPORT_LEAD_MANAGE"),
            entry("quote-rural-lead", "QUOTE_RURAL_LEAD_MANAGE"),
            entry("insurance-rural", "RURAL_MANAGE"),
            entry("quote-auto-lead", "QUOTE_AUTO_LEAD_MANAGE"),
            entry("quote-auto", "QUOTE_AUTO_MANAGE"),
            entry("quote-person-lead", "QUOTE_PERSON_LEAD_MANAGE"),
            entry("quote-person-life", "QUOTE_PERSON_LIFE_MANAGE"),
            entry("quote-person-travel", "QUOTE_PERSON_TRAVEL_MANAGE"),
            entry("contract-life-pension-lead", "QUOTE_LIFE_PENSION_LEAD_MANAGE"),
            entry("contract-life-pension", "QUOTE_LIFE_PENSION_MANAGE"),
            entry("quote-capitalization-title-lead", "QUOTE_CAPITALIZATION_TITLE_LEAD_MANAGE"),
            entry("quote-capitalization-title", "QUOTE_CAPITALIZATION_TITLE_MANAGE"),
            entry("quote-capitalization-title-raffle", "QUOTE_CAPITALIZATION_TITLE_RAFFLE_MANAGE"),
            entry("customers", "CUSTOMERS_MANAGE"),
            entry("capitalization-title", "CAPITALIZATION_TITLE_MANAGE"),
            entry("insurance-financial-risk", "FINANCIAL_RISK_MANAGE"),
            entry("insurance-housing", "HOUSING_MANAGE"),
            entry("insurance-responsibility", "RESPONSIBILITY_MANAGE"),
            entry("insurance-person", "PERSON_MANAGE"),
            entry("insurance-life-pension", "LIFE_PENSION_MANAGE"),
            entry("insurance-financial-assistance", "FINANCIAL_ASSISTANCE_MANAGE"),
            entry("insurance-auto", "AUTO_MANAGE"),
            entry("insurance-pension-plan", "PENSION_PLAN_MANAGE"),
            entry("insurance-transport", "TRANSPORT_MANAGE"),
            entry("dynamic-fields", "DYNAMIC_FIELDS_READ"),
            // op-related scopes, are these real? They govern the PUT endpoints needed for administration.
            entry("op:consent", "CONSENTS_FULL_MANAGE"),
            entry("op:admin", "ADMIN_FULL_MANAGE"),
            entry("override", "OVERRIDE_MANAGE")
    );

    private final ApplicationContext applicationContext;

    private final List<Pair<HttpMethod, String>> requiredClientCredentialsRegexes = new LinkedList<>();
    private final List<Pair<HttpMethod, String>> requiredAuthorisationCodeRegexes = new LinkedList<>();

    @Inject
    public SimpleAuthorisation(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    private void init() {
        AnnotationsUtil.performActionsOnControllerMethodByAnnotation(applicationContext, RequiredAuthenticationGrant.class, (fullPath, httpMethod, extractedAnnotation) ->
                extractedAnnotation.enumValue("value", AuthenticationGrant.class).ifPresent(grant -> {
                    switch (grant) {
                        case AUTHORISATION_CODE:
                            requiredAuthorisationCodeRegexes.add(Pair.of(httpMethod, fullPath));
                            LOG.info("Added required authorisation code regex {} - {}", httpMethod, fullPath);
                            break;
                        case CLIENT_CREDENTIALS:
                            requiredClientCredentialsRegexes.add(Pair.of(httpMethod, fullPath));
                            LOG.info("Added required client credentials regex {} - {}", httpMethod, fullPath);
                            break;
                    }
                }));
    }

    @Override
    public Publisher<Authentication> fetchAuthentication(HttpRequest<?> request) {
        LOG.info("Looking for scopes on a {} request to {}", request.getMethod(), request.getPath());
        if (request instanceof ApiGatewayProxyServletRequest) {
            return Optional.ofNullable(handleLambdaRequest((ApiGatewayProxyServletRequest<?>) request))
                    .map(Flowable::just)
                    .orElse(Flowable.empty());
        } else {
            return Optional.ofNullable(handleHttpRequest(request))
                    .map(this::toFlow)
                    .orElse(Flowable.empty());
        }
    }

    private Authentication handleLambdaRequest(ApiGatewayProxyServletRequest<?> request) {
        LOG.info("We're a lambda");
        Map<String, Object> authorizer = null;
        try {
            authorizer = getAuthContext(request);
        } catch(ConfigException e) {
            LOG.info("No authorizer - returning 403");
            return null;
        }

        String token = (String) authorizer.get("access_token");
        if (token == null || token.isBlank()) {
            token = request.getHeaders().get("access_token");
        }

        return makeAuthentication(request, token);
    }

    private Authentication handleHttpRequest(HttpRequest<?> request) {
        String token = request.getHeaders().get("access_token");
        if (token == null || token.isEmpty()) {
            token = request.getHeaders().get("Authorization");
            if (token != null) {
                Matcher matcher = BEARER.matcher(token);
                if(!matcher.matches()) {
                    LOG.info("Authorization header doesn't appear to be a bearer token");
                    return null;
                }
                token = matcher.group("token");

                try {
                    token = PlainObject.parse(token).getPayload().toString();
                } catch (ParseException e) {
                    LOG.error("Exception unpacking access token", e);
                    return null;
                }
            }
        }
        if(token == null) {
            LOG.info("No Authorization header");
            return null;
        }

        return makeAuthentication(request, token);
    }

    private Authentication makeAuthentication(HttpRequest<?> request, String token) {
        Map<String, String> deserialized;
        try {
            deserialized = objectMapper.readValue(token, Map.class);
        } catch (JsonProcessingException e) {
            LOG.error("Exception unpacking access token", e);
            return null;
        }
        String[] scopes = deserialized.getOrDefault("scope", "").split(" ");
        String clientId = deserialized.get("client_id");
        String orgId = deserialized.get("org_id");
        String ssId = deserialized.get("software_id");
        String subject = deserialized.get("sub");
        checkAuthenticationGrant(request, subject);
        LOG.info("Scopes in token: {}", String.join(",", scopes));
        LOG.info("Org ID in token: {}", orgId);
        setRequestCallerInfo(request, scopes, clientId, subject, orgId, ssId);
        LOG.info("Returning new Client Authentication");
        try {
            List<String> roles = getRoles(scopes);
            return makeClientAuthenticationWithRoles(roles);
        } catch (Exception e) {
            LOG.error("Exception setting default auth", e);
            return null;
        }
    }

    private Flowable<Authentication> toFlow(Authentication authentication) {
        return Flowable.just(authentication);
    }

    private Map<String, Object> getAuthContext(ApiGatewayProxyServletRequest<?> request) throws ConfigException {
        return Optional.ofNullable(request.getNativeRequest())
                .map(APIGatewayProxyRequestEvent::getRequestContext)
                .map(APIGatewayProxyRequestEvent.ProxyRequestContext::getAuthorizer)
                .orElseThrow(ConfigException::new);
    }

    private static class ConfigException extends Exception {

    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    private List<String> getRoles (String[] scopes) {
        LOG.info("Scopes in token: {}", String.join(",", scopes));
        List<String> roles = Arrays.stream(scopes)
                .map(scopesToRoles::get)
                .filter(Objects::nonNull)
                .toList();
        LOG.info("Roles inferred: {}", String.join(",", roles));
        return roles;
    }

    private void setRequestCallerInfo(HttpRequest<?> request, String[] scopes, String clientId, String subject, String orgId, String ssId){
        final String dynamicScopePrefix = request.getPath().contains("automatic-payments") ? "recurring-consent:" : "consent:";
        String consentId = Arrays.stream(scopes)
                .filter(Objects::nonNull)
                .filter(a -> !a.isEmpty())
                .filter(a -> a.startsWith(dynamicScopePrefix + "urn:raidiaminsurance:"))
                .findFirst().orElse(null);
        LOG.info("Consent Id inferred: {}", consentId);
        if(consentId != null) {
            consentId = consentId.replace(dynamicScopePrefix,"");
            request.setAttribute("consentId", consentId);
        }

        final String enrollmentScopePrefix = "enrollment:";
        String enrollmentId = Arrays.stream(scopes)
                .filter(Objects::nonNull)
                .filter(a -> !a.isEmpty())
                .filter(a -> a.startsWith(enrollmentScopePrefix + "urn:raidiaminsurance:"))
                .findFirst().orElse(null);
        LOG.info("Enrollment Id inferred: {}", enrollmentId);
        if(enrollmentId != null) {
            enrollmentId = enrollmentId.replace(enrollmentScopePrefix,"");
            request.setAttribute("enrollmentId", enrollmentId);
        }

        LOG.info("Setting clientId: {}", clientId);
        request.setAttribute("clientId", clientId);
        if(orgId != null) {
            LOG.info("Setting orgId: {}", clientId);
            request.setAttribute("orgId", orgId);
        }
        if(ssId != null) {
            LOG.info("Setting software statement id: {}", ssId);
            request.setAttribute("ssId", ssId);
        }

        if(subject != null) {
            LOG.info("Setting subject: {}", subject);
            request.setAttribute("sub", subject);
        }

    }

    private ClientAuthentication makeClientAuthenticationWithRoles(List<String> roles) {
        return new ClientAuthentication(ALLOWED, Map.of("roles", roles));
    }

    private void checkAuthenticationGrant(HttpRequest<?> request, String subject) {
        //authorization_code grant has sub in the token
        requiredAuthorisationCodeRegexes.forEach(pair -> {
            HttpMethod method = pair.getLeft();
            String regex = pair.getRight();
            if (request.getPath().matches(regex) && request.getMethod().equals(method) && subject == null) {
                String message = String.format("%s %s does not accept client_credentials token - returning 401", method, request.getPath());
                throw new HttpStatusException(HttpStatus.UNAUTHORIZED, message);
            }
        });

        requiredClientCredentialsRegexes.forEach(pair -> {
            HttpMethod method = pair.getLeft();
            String regex = pair.getRight();
            if (request.getPath().matches(regex) && request.getMethod().equals(method) && subject != null) {
                String message = String.format("%s %s does not accept authorization_code token - returning 401", method, request.getPath());
                throw new HttpStatusException(HttpStatus.UNAUTHORIZED, message);
            }
        });
    }
}

