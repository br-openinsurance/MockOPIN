package com.raidiam.trustframework.mockinsurance.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.nimbusds.jose.util.Pair;
import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity;
import com.raidiam.trustframework.mockinsurance.exceptions.TrustframeworkException;
import com.raidiam.trustframework.mockinsurance.fapi.ResponseErrorWithRequestDateTime;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.repository.ConsentRepository;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpParameters;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Singleton
public class InsuranceLambdaUtils {

    private static final Logger LOG = LoggerFactory.getLogger(InsuranceLambdaUtils.class);

    private static final String BRASIL_ZONE_ID = "America/Sao_Paulo";

    private static String appBaseUrl;
    private static int maxPageSize;


    private static final List<Pair<HttpMethod, String>> ERROR_WITH_REQUEST_DATE_TIME_REGEXES = new LinkedList<>();

    @Inject
    public void setProperties(
            @Property(name = "mockinsurance.mockinsuranceUrl") String appBaseUrl,
            @Property(name = "mockinsurance.max-page-size") int maxPageSize) {
        InsuranceLambdaUtils.appBaseUrl = appBaseUrl;
        InsuranceLambdaUtils.maxPageSize = maxPageSize;
    }

    @Inject
    public void setResponseErrorRegexes(ApplicationContext context) {
        AnnotationsUtil.performActionsOnControllerMethodByAnnotation(context, ResponseErrorWithRequestDateTime.class, (fullPath, httpMethod, extractedAnnotation) -> {
            ERROR_WITH_REQUEST_DATE_TIME_REGEXES.add(Pair.of(httpMethod, fullPath));
        });
    }

    public static Date offsetDateToDate(OffsetDateTime offset) {
        return Optional.ofNullable(offset).map(OffsetDateTime::toInstant).map(Date::from).orElse(null);
    }

    public static OffsetDateTime dateToOffsetDateTimeInBrasil(Date date) {
        return Optional.ofNullable(date).map(newDate -> newDate.toInstant().atZone(getBrasilZoneId()).toOffsetDateTime()).orElse(null);
    }

    public static ZoneId getBrasilZoneId() {
        return ZoneId.of(BRASIL_ZONE_ID);
    }

    public static OffsetDateTime getOffsetDateTimeInBrasil() {
        return OffsetDateTime.now(getBrasilZoneId());
    }

    public static OffsetDateTime getOffsetDateTimeUTC() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    public OffsetDateTime getOffsetDateTimeUTCV2() {
        return InsuranceLambdaUtils.getOffsetDateTimeUTC();
    }

    public static Instant getInstantInBrasil() {
        return Instant.now().atZone(getBrasilZoneId()).toInstant();
    }

    public static LocalDateTime timestampToLocalDateTime(Date timestamp) {
        return Optional.ofNullable(timestamp).map(Date::toInstant).map(a -> a.atZone(ZoneId.of(BRASIL_ZONE_ID)).toLocalDateTime()).orElse(null);
    }

    public static OffsetDateTime dateToOffsetDate(Date date) {
        return Optional.ofNullable(date).map(Date::toInstant).map(a -> a.atOffset(ZoneOffset.UTC)).orElse(null);
    }

    public static OffsetDateTime localDateToOffsetDate(LocalDate date) {
        return Optional.ofNullable(date).map(a -> OffsetDateTime.of(a, LocalTime.NOON, ZoneOffset.UTC)).orElse(null);
    }

    public static Date localDateToDate(LocalDate localDate) {
        return java.util.Date.from(localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static LocalDate dateToLocalDate(Date date) {
        return Optional.ofNullable(date).map(Date::getTime).map(java.sql.Date::new).map(java.sql.Date::toLocalDate).orElse(null);
    }

    public static LocalDate dateToLocalDateInBrasil(Date date) {
        return Optional.ofNullable(date).map(Date::toInstant).map(d -> d.atZone(getBrasilZoneId()).toLocalDate()).orElse(null);
    }

    public static String doubleToString(Double value) {
        return Optional.ofNullable(value).map(Objects::toString).orElse("0.0");
    }

    public static String formatDoubleToLongString(Double value) {
        DecimalFormat formatter = new DecimalFormat("#0.000000");
        return formatter.format(Optional.ofNullable(value).orElse(0.00));
    }

    public static void logObject(ObjectMapper mapper, Object res) {
        try {
            String response = mapper.writeValueAsString(res);
            LOG.info("{} - {}", res.getClass().getSimpleName(), response);
        } catch (JsonProcessingException e) {
            LOG.error("{} - Error writing object as JSON: ", res.getClass().getSimpleName(), e);
        }
    }

    public static String formatAmountV2(double amount) {
        return new DecimalFormat("0.00##", new DecimalFormatSymbols(Locale.US)).format(amount);
    }

    public static class RequestMeta {

        RequestMeta(List<String> roles, String consentId, String enrollmentId, String clientId, String jti, String jwtPayload) {
            this.roles = roles;
            this.consentId = consentId;
            this.enrollmentId = enrollmentId;
            this.clientId = clientId;
            this.jti = jti;
            this.jwtPayload = jwtPayload;
        }

        @Getter
        private final List<String> roles;
        @Getter
        private final String consentId;
        @Getter
        private final String enrollmentId;
        @Getter
        private final String clientId;
        @Getter
        private final String jti;
        @Getter
        private final String jwtPayload;
    }

    public static RequestMeta getRequestMeta(HttpRequest<?> request) {
        LOG.info("getCallerInfo() from the request");
        String jti = null;
        String jwtPayload = null;
        try {
            Optional<Object> attribute = request.getAttribute("micronaut.AUTHENTICATION");
            if (attribute.isPresent()) {
                LOG.info("There is an authentication present on the request");
                Authentication authentication = (Authentication) attribute.get();
                List<String> roles = (List<String>) authentication.getAttributes().get("roles");

                Optional<Object> clientIdOpt = request.getAttribute("clientId");
                if (clientIdOpt.isEmpty()) {
                    throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Access token did not contain a client ID");
                }
                String clientId = clientIdOpt.get().toString();
                String consentId = request.getAttribute("consentId").map(Object::toString).orElse(null);
                String enrollmentId = request.getAttribute("enrollmentId").map(Object::toString).orElse(null);
                LOG.info("Roles: {}", String.join(",", roles));
                LOG.info("Request made by client id: {}", clientId);
                LOG.info("Request made with consent Id: {}", consentId);
                LOG.info("Request made with enrollment Id: {}", enrollmentId);
                LOG.info("Request made with JTI: {}", jti);
                LOG.info("Request made with JWT payload: {}", jwtPayload);
                return new RequestMeta(roles, consentId, enrollmentId, clientId, jti, jwtPayload);
            }
        } catch (Exception e) {
            LOG.error("Exception  getting caller info. Error: ", e);
        }
        LOG.info("No authentication present");
        return new RequestMeta(Collections.emptyList(), null, null, null, jti, jwtPayload);
    }

    public static String getIdempotencyKey(HttpRequest<?> request) {
        return Optional.ofNullable(request.getHeaders().get("x-idempotency-key"))
                .orElseThrow(() -> new HttpStatusException(HttpStatus.BAD_REQUEST, "No Idempotency header"));
    }

    private static LocalDate getLocalDateFromString (String dateValue) {
        try {
            return LocalDate.parse(dateValue);
        } catch (DateTimeParseException e) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Could not parse date from String " + dateValue);
        }
    }

    public Optional<LocalDate> getDateFromRequest(HttpRequest<?> request, String parameterName) {
        return Optional.of(request).map(HttpRequest::getParameters).map(params -> params.get(parameterName)).map(InsuranceLambdaUtils::getLocalDateFromString);
    }

    public Optional<BigDecimal> getPayeeMCCFromRequest(HttpRequest<?> request) {
        return Optional.of(request).map(HttpRequest::getParameters).map(params -> params.get("payeeMCC")).map(BigDecimal::new);
    }

    public Optional<String> getCertificateCNFromDNHeaderInRequest(HttpRequest<?> request) {
        var dnString = Optional.ofNullable(request.getHeaders().get("X-BANK-Certificate-DN"));
        LOG.info("X-BANK-Certificate-DN: {}", dnString.orElse("the header was not informed"));

        // Split the distinguished name by comma, then try to find the first chunk that starts with "cn=".
        // If nothing is found, set cnString to an empty string.
        var cnString = dnString.flatMap(string -> Arrays.stream(string.split(","))
                .filter(s -> s.toLowerCase().startsWith("cn=")).findFirst()).orElse("");
        LOG.info("Common name: {}", cnString);
        if(StringUtils.isBlank(cnString)) {
            return Optional.empty();
        }

        cnString = cnString.replace("CN=", "").replace("cn=", "");
        return Optional.of(cnString);
    }

    public Optional<String> getCertificateCNFromRequest(HttpRequest<?> request) {
        var certificateString = Optional.ofNullable(request.getParameters().get("BANK-TLS-Certificate"));
        if(certificateString.isPresent()) {
            try {
                byte[] certificateBytes = certificateString.get().getBytes();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(certificateBytes);

                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(byteArrayInputStream);

                String commonName = "";
                if (certificate != null) {
                    X500Name x500name = X500Name.getInstance(certificate.getSubjectX500Principal().getEncoded());
                    RDN[] cnArray = x500name.getRDNs(BCStyle.CN);
                    if (cnArray.length == 0) {
                        return Optional.empty();
                    }
                    commonName = IETFUtils.valueToString(cnArray[0].getFirst().getValue());
                }
                if (Strings.isNullOrEmpty(commonName)) {
                    return Optional.empty();
                }
                LOG.info("CN extracted: {}", commonName);
                return Optional.of(commonName);
            } catch (CertificateException err) {
                throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not parse the certificate");
            }
        }
        return Optional.empty();
    }

    public Optional<String> getAttributeFromRequest(HttpRequest<?> request, String attributeName) {
        return request.getAttribute(attributeName).map(Object::toString);
    }

    public static void decorateResponse(Consumer<Links> setLinks, Consumer<Meta> setMeta, String self, int records) {
        setLinks.accept(new Links().self(self));
        setMeta.accept(new Meta().totalPages(1).totalRecords(records).requestDateTime(OffsetDateTime.now()));
    }

    public static void decorateResponseBrasilTimeZone(Consumer<Links> setLinks, Consumer<Meta> setMeta, String self, int records) {
        setLinks.accept(new Links().self(self));
        setMeta.accept(new Meta().totalPages(1).totalRecords(records).requestDateTime(OffsetDateTime.now(InsuranceLambdaUtils.getBrasilZoneId())));
    }

    public static void decorateResponse(Consumer<Links> setLinks, Consumer<Meta> setMeta, String self, int records, int pageSize) {
        setLinks.accept(new Links().self(self));
        var page = pageSize <= 0 ? 0 : (int) Math.ceil((double) records / pageSize);
        setMeta.accept(new Meta().totalPages(page).totalRecords(records).requestDateTime(OffsetDateTime.now()));
    }

    public void decorateResponseError(ResponseError resp, HttpRequest<?> request) {
        if(!shouldAddRequestDateTimeToError(request)) {
            return;
        }

        resp.getErrors().forEach(e -> e.setRequestDateTime(OffsetDateTime.now()));
    }

    private boolean shouldAddRequestDateTimeToError(HttpRequest<?> request) {
        for (Pair<HttpMethod, String> dateTimeOnlyRule : ERROR_WITH_REQUEST_DATE_TIME_REGEXES) {
            HttpMethod method = dateTimeOnlyRule.getLeft();
            String regex = dateTimeOnlyRule.getRight();
            String requestPath = request.getPath();
            if (request.getMethod() == method && requestPath.matches(regex)) {
                LOG.info("found matching pattern - {} - {} for path {}", method, regex, requestPath);
                return true;
            }
        }
        LOG.info("no matching patterns found");
        return false;
    }

    public static String buildGetSelfLink(HttpRequest<?> request) {
        var selfLink = appBaseUrl + request.getPath();

        List<String> q = new ArrayList<>();
        request.getParameters().forEach(p -> {
            var key = p.getKey();
            var value = p.getValue().get(0);
            if("page-size".equals(key) && Integer.parseInt(value) > maxPageSize) {
                value = Integer.toString(maxPageSize);
            }
            q.add(p.getKey() + "=" + value);
        });
        selfLink += q.isEmpty() ? "" : "?" + String.join("&", q);

        return selfLink;
    }


    /**
     * Decorate a response with the correct links for the given pagination
     *
     * @param setLinks      Method to use to set the links
     * @param pageSize      The size of the page
     * @param self          The self link
     * @param pageNumber    The current page - note this is 0-indexed, and the output format is 1-indexed
     */

    private static final String PAGE_TEMPLATE = "%s?page-size=%d&page=%d";
    public static void decorateResponse(Consumer<Links> setLinks, int pageSize, String self, int pageNumber, int totalNumberOfPages) {
        decorate(setLinks, pageSize, self, pageNumber, totalNumberOfPages, PAGE_TEMPLATE);
    }

    public static void decorateResponseSimpleMeta(Consumer<Links> setLinks, Consumer<Meta> setMeta, int pageSize, String self, int pageNumber, int totalNumberOfPages) {
        decorate(setLinks, pageSize, self, pageNumber, totalNumberOfPages, PAGE_TEMPLATE);
        setMeta.accept(new Meta().requestDateTime(OffsetDateTime.now()).totalRecords(null).totalRecords(null));
    }

    public static void decorateResponseSimpleLinkMeta(Consumer<Links> setLinks, Consumer<Meta> setMeta, String self) {
        setLinks.accept(new Links().self(self));
        setMeta.accept(new Meta().totalRecords(1).totalPages(1));
    }

    public static void decorateResponseSimpleLinkMeta(Consumer<Links> setLinks, Consumer<Meta> setMeta, String self, int records, int pageSize) {
        setLinks.accept(new Links().self(self));
        var page = pageSize <= 0 ? 0 : (int) Math.ceil((double) records / pageSize);
        setMeta.accept(new Meta().totalPages(page).totalRecords(records));
    }

    public static void decorateResponse(Consumer<Links> setLinks, String self) {
        setLinks.accept(new Links().self(self));
    }

    public static void decorateResponseSimpleLinkMetaBrasilTimeZone(Consumer<Links> setLinks, Consumer<Meta> setMeta, String self) {
        setLinks.accept(new Links().self(self));
        setMeta.accept(new Meta().requestDateTime(OffsetDateTime.now(InsuranceLambdaUtils.getBrasilZoneId())).totalRecords(null).totalRecords(null));
    }

    public static void decorateResponse(Consumer<Links> setLinks, int pageSize, String self, int pageNumber, int totalNumberOfPages,
                                        String... dateNames) {
        if(dateNames.length != 4){
            throw new TrustframeworkException("dateNames parameter should contain the following - fromDateName, fromDate, toDateName, toDate strings");
        }

        var datesParameters = String.format("&%s=%s&%s=%s", dateNames[0], dateNames[1], dateNames[2], dateNames[3]);

        decorate(setLinks, pageSize, self, pageNumber, totalNumberOfPages, PAGE_TEMPLATE.concat(datesParameters));
    }

    private static void decorate(Consumer<Links> setLinks, int pageSize, String self, int pageNumber, int totalNumberOfPages, String template) {
        var links = new Links()
                .self(String.format(template, self, pageSize, pageNumber + 1))
                .first(String.format(template, self, pageSize, 1))
                .last(String.format(template, self, pageSize, totalNumberOfPages == 0 ? 1 : totalNumberOfPages));
        if (pageNumber > 0) {
            links.setPrev(String.format(template, self, pageSize, pageNumber));
        }
        if (pageNumber < totalNumberOfPages - 1) {
            links.setNext(String.format(template, self, pageSize, pageNumber + 2));
        }
        setLinks.accept(links);
    }

    public static Meta getMeta(Page<?> page) {
        return getMeta(page, true);
    }

    public static Meta getMeta(Page<?> page, boolean setRequestDateTime) {
        var meta = new Meta();
        if (page != null) {
            meta.setTotalRecords((int) page.getTotalSize());
            meta.setTotalPages(page.getTotalPages());
        } else {
            meta.setTotalRecords(0);
            meta.setTotalPages(0);
        }
        if (setRequestDateTime) {
            meta.setRequestDateTime(OffsetDateTime.now());
        }
        return meta;
    }



    public static Pageable adjustPageable(Pageable inboundPageable, HttpRequest<?> request, int maxPageSize) {
        return adjustPageable(inboundPageable, request, maxPageSize, null);
    }

    public static Pageable adjustAndSortPageable(Pageable inboundPageable, HttpRequest<?> request, int maxPageSize) {
        // Convert an incoming pageable and also sort in the way the most recent records come first.
        return adjustPageable(inboundPageable, request, maxPageSize, Sort.of(Sort.Order.desc("createdAt")));
    }

    /**
     * Convert an incoming pageable (1-based page numbers) to micronaut's
     * expected pageable (0-based)
     *
     * @param inboundPageable the pageable from the query
     * @param maxPageSize the maximum page size
     * @return A new pageable with 0-based page numbers
     */
    public static Pageable adjustPageable(Pageable inboundPageable, HttpRequest<?> request, int maxPageSize, Sort sort) {
        LOG.info("Incoming request page details - Page {}, Page Size {}", inboundPageable.getNumber(), inboundPageable.getSize());
        HttpParameters params = request.getParameters();
        if ((inboundPageable.getNumber() < 0)
                || (params.contains("page") && inboundPageable.getNumber() == 0)) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Pages are 1-indexed, value of %d not valid", inboundPageable.getNumber()));
        }
        int newNumber = inboundPageable.getNumber();
        if(params.contains("page")) {
            newNumber--;
        }

        if(inboundPageable.getSize() <= 0) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Page size must be positive, value of %d not valid", inboundPageable.getSize()));
        }


        if(inboundPageable.getSize() > 1000) {
            throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Page size must not be over 1000, value of %d not valid", inboundPageable.getSize()));
        }
        int size = Math.min(inboundPageable.getSize(), maxPageSize);
        return Pageable.from(newNumber, size, sort);
    }

    /**
     * Special sub-transaction.
     *
     * This can complete an action, like a database update, and then throw without unwinding that action.
     *
     * @param action    The action to run
     * @param exception The exception to throw after the action completes
     *
     * @throws HttpStatusException at the end of the process
     */
    @Transactional(value=Transactional.TxType.REQUIRES_NEW, dontRollbackOn={HttpStatusException.class})
    public void throwWithoutRollback(Runnable action, HttpStatusException exception) throws HttpStatusException {
        action.run();
        throw exception;
    }

    public static ConsentEntity getConsent(String consentId, ConsentRepository consentRepository) {
        return consentRepository.findByConsentId(consentId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Consent Id " + consentId + " not found"));
    }

    public static void checkAuthorisationStatus(ConsentEntity consentEntity) {
        var status = EnumConsentStatus.fromValue(consentEntity.getStatus());
        if (!EnumConsentStatus.AUTHORISED.equals(status)) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Bad request, consent not Authorised!");
        }
    }

    public static void checkConsentPermissions(ConsentEntity consent, EnumConsentPermission permissionsEnum, EnumConsentV3Permission permissionsEnumV3) {
        if (!consent.getPermissions().contains(permissionsEnum.name()) &&
            !consent.getPermissions().contains(permissionsEnumV3.name())) {
            throw new HttpStatusException(HttpStatus.FORBIDDEN, "You do not have the correct permission");
        }
    }

    public static String getConsentIdFromRequest(HttpRequest<?> request) {
        return Optional.of(InsuranceLambdaUtils.getRequestMeta(request)).map(InsuranceLambdaUtils.RequestMeta::getConsentId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.FORBIDDEN, "Request has no associated consent Id"));
    }

    public static String getClientIdFromRequest(HttpRequest<?> request) {
        return Optional.of(InsuranceLambdaUtils.getRequestMeta(request)).map(InsuranceLambdaUtils.RequestMeta::getClientId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.FORBIDDEN, "Request has no associated client Id"));
    }

    public static Set<EnumConsentPermission> getConsentPermissions(ConsentEntity consent) {
        return consent.getPermissions().stream()
                .map(EnumConsentPermission::fromValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public static Set<EnumConsentV3Permission> getConsentV3Permissions(ConsentEntity consent) {
        return consent.getPermissions().stream()
                .map(EnumConsentV3Permission::fromValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
