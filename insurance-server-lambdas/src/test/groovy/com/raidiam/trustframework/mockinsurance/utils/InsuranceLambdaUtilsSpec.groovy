package com.raidiam.trustframework.mockinsurance.utils

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.TestRequestDataFactory
import com.raidiam.trustframework.mockinsurance.domain.AccountHolderEntity
import com.raidiam.trustframework.mockinsurance.domain.ConsentEntity
import com.raidiam.trustframework.mockinsurance.exceptions.TrustframeworkException
import com.raidiam.trustframework.mockinsurance.models.generated.*
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Unroll

import java.time.*
import java.time.temporal.ChronoUnit

@MicronautTest(transactional = false, environments = ["db"])
class InsuranceLambdaUtilsSpec extends CleanupSpecification {

    @Inject
    InsuranceLambdaUtils insuranceLambdaUtils

    @Shared
    AccountHolderEntity testAccountHolder

    @Shared
    private TimeZone originalTimeZone

    def setup() {
        if (runSetup) {
            originalTimeZone = TimeZone.getDefault()
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
            testAccountHolder = accountHolderRepository.save(TestEntityDataFactory.anAccountHolder())
            runSetup = false
        }
    }

    @Shared
    private final String CERT = "-----BEGIN CERTIFICATE-----\n" +
            "MIIHADCCBeigAwIBAgIUHzap6tr22LPCl0gAz6jkUZbszwQwDQYJKoZIhvcNAQEL\n" +
            "BQAwcTELMAkGA1UEBhMCQlIxHDAaBgNVBAoTE09wZW4gQmFua2luZyBCcmFzaWwx\n" +
            "FTATBgNVBAsTDE9wZW4gQmFua2luZzEtMCsGA1UEAxMkT3BlbiBCYW5raW5nIFNB\n" +
            "TkRCT1ggSXNzdWluZyBDQSAtIEcxMB4XDTIyMDgxMjEyNDgwMFoXDTIzMDkxMTEy\n" +
            "NDgwMFowggEcMQswCQYDVQQGEwJCUjELMAkGA1UECBMCU1AxDzANBgNVBAcTBkxP\n" +
            "TkRPTjEcMBoGA1UEChMTT3BlbiBCYW5raW5nIEJyYXNpbDEtMCsGA1UECxMkNzRl\n" +
            "OTI5ZDktMzNiNi00ZDg1LThiYTctYzE0NmM4NjdhODE3MR8wHQYDVQQDExZtb2Nr\n" +
            "LXRwcC0xLnJhaWRpYW0uY29tMRcwFQYDVQQFEw40MzE0MjY2NjAwMDE5NzEdMBsG\n" +
            "A1UEDxMUUHJpdmF0ZSBPcmdhbml6YXRpb24xEzARBgsrBgEEAYI3PAIBAxMCVUsx\n" +
            "NDAyBgoJkiaJk/IsZAEBEyQxMDEyMDM0MC0zMzE4LTRiYWYtOTllMi0wYjU2NzI5\n" +
            "YzRhYjIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC5ILYWgl9nlspD\n" +
            "4+vfoZEPHg9STbCy3YgAYqan4tlIWOYqpgAkcuuma9zfk6f9SD3OCfmYyp4pXpT0\n" +
            "wdgwjxu9MTgixsuHPHYLMENO7/OGIHbmFXC2tONPId2OVkC9zdBxPTTtQ8tUQM3Y\n" +
            "rNV6pEWMukOIBYG9RcPklRl0FB+O0gTdkorg9RTkiBRIdDCiEn1h9Tzq+SF4mwpD\n" +
            "Mic85+VpCzot0nGnSx1xb0Wp7WWBPJeDip1pgPm1BL03NBPbyvsAkwklLXU0zZKz\n" +
            "KfW+vGgkGIvKDHREhr+aZPvTzeQ1oukc4S5yLBfgPXESIa9qyIO9GRozzH8IXNCx\n" +
            "4agzkTeNAgMBAAGjggLhMIIC3TAMBgNVHRMBAf8EAjAAMB0GA1UdDgQWBBQm8XFu\n" +
            "PrFYnrEgpmR+Z4hnce2ZWjAfBgNVHSMEGDAWgBSGf1itF/WCtk60BbP7sM4RQ99M\n" +
            "vjBMBggrBgEFBQcBAQRAMD4wPAYIKwYBBQUHMAGGMGh0dHA6Ly9vY3NwLnNhbmRi\n" +
            "b3gucGtpLm9wZW5iYW5raW5nYnJhc2lsLm9yZy5icjBLBgNVHR8ERDBCMECgPqA8\n" +
            "hjpodHRwOi8vY3JsLnNhbmRib3gucGtpLm9wZW5iYW5raW5nYnJhc2lsLm9yZy5i\n" +
            "ci9pc3N1ZXIuY3JsMCEGA1UdEQQaMBiCFm1vY2stdHBwLTEucmFpZGlhbS5jb20w\n" +
            "DgYDVR0PAQH/BAQDAgWgMBMGA1UdJQQMMAoGCCsGAQUFBwMCMIIBqAYDVR0gBIIB\n" +
            "nzCCAZswggGXBgorBgEEAYO6L2QBMIIBhzCCATYGCCsGAQUFBwICMIIBKAyCASRU\n" +
            "aGlzIENlcnRpZmljYXRlIGlzIHNvbGVseSBmb3IgdXNlIHdpdGggUmFpZGlhbSBT\n" +
            "ZXJ2aWNlcyBMaW1pdGVkIGFuZCBvdGhlciBwYXJ0aWNpcGF0aW5nIG9yZ2FuaXNh\n" +
            "dGlvbnMgdXNpbmcgUmFpZGlhbSBTZXJ2aWNlcyBMaW1pdGVkcyBUcnVzdCBGcmFt\n" +
            "ZXdvcmsgU2VydmljZXMuIEl0cyByZWNlaXB0LCBwb3NzZXNzaW9uIG9yIHVzZSBj\n" +
            "b25zdGl0dXRlcyBhY2NlcHRhbmNlIG9mIHRoZSBSYWlkaWFtIFNlcnZpY2VzIEx0\n" +
            "ZCBDZXJ0aWNpY2F0ZSBQb2xpY3kgYW5kIHJlbGF0ZWQgZG9jdW1lbnRzIHRoZXJl\n" +
            "aW4uMEsGCCsGAQUFBwIBFj9odHRwOi8vcmVwb3NpdG9yeS5zYW5kYm94LnBraS5v\n" +
            "cGVuYmFua2luZ2JyYXNpbC5vcmcuYnIvcG9saWNpZXMwDQYJKoZIhvcNAQELBQAD\n" +
            "ggEBAGaESJ0UBfEB0mI8Fh98D6261BWUBdR9vdcD4IX53EubFvOCWE75skpYYyMz\n" +
            "s0dsoU6q/ivHVudhWUWaXCK9UNDgFHb8hE/YaDOoOJLRYllGq0qEyo8u0tJa0XmW\n" +
            "BfXMwNajEvlu3RdKWQ09x+KwEDjCIiJE7hK0cXReJuE6cDc5EPVjQ/fM7TBMQza0\n" +
            "hkZqJgA7555HCi8+k7bovGiV7i9sElvcuWKl2In3AWJke85K0zJaWRXsmkwFTE7i\n" +
            "nkob4yXb4SyGmCnlFEGUZMhqScAsiIrltE5cgLScRZrwymq+1rvYbqgUVOKLVzov\n" +
            "Js11ZIb3W0cRkjSGD9nXP3lfU4s=\n" +
            "-----END CERTIFICATE-----"


    @Unroll
    def "we generate the pagination links correctly"() {
        given:
        Links links = new Links()

        when:
        insuranceLambdaUtils.decorateResponse({ a -> links = a }, 1, "test", page, total)

        then:
        prev == (links.prev != null)
        next == (links.next != null)

        where:
        total | page | prev  | next
        1     | 0    | false | false
        2     | 0    | false | true
        2     | 1    | true  | false
        3     | 1    | true  | true
    }

    @Unroll
    def "we get dates out of http request params properly"() {
        given:
        def request = HttpRequest.GET("https://www.example.com/examplepage?${param}=${value}")

        when:
        def retrieved = insuranceLambdaUtils.getDateFromRequest(request, param)

        then:
        retrieved.get() == LocalDate.parse(value)

        where:
        param         | value
        "fromDueDate" | "2021-01-01"
        "toDueDate"   | "2022-06-05"
    }

    @Unroll
    def "we get the right sort of exception for bad param formatting"() {
        given:
        def request = HttpRequest.GET("https://www.example.com/examplepage?${param}=${value}")

        when:
        insuranceLambdaUtils.getDateFromRequest(request, param)

        then:
        HttpStatusException e = thrown()
        e.getStatus() == HttpStatus.BAD_REQUEST

        where:
        param         | value
        "fromDueDate" | "111"
        "toDueDate"   | "22-06-05"
        "toDueDate"   | "22-06-2021"
        "toDueDate"   | "banana"
        "toDueDate"   | ""
        "toDueDate"   | null
    }

    @Unroll
    def "we can get payeeMcc"() {
        given:
        def request = HttpRequest.GET("https://www.example.com/examplepage${param}")

        when:
        def retrieved = insuranceLambdaUtils.getPayeeMCCFromRequest(request)

        then:
        if (value == null) {
            retrieved.isEmpty()
        } else {
            retrieved.get() == value
        }

        where:
        param         | value
        "?payeeMCC=1" | 1
        "?payeeMCC=2" | 2
        "?payeeMcc=2" | null
        null          | null
    }

    @Unroll
    def "we can get certificate cn"() {
        given:
        def request = HttpRequest.POST("https://www.example.com/examplepage", null)
        request.parameters.add("BANK-TLS-Certificate", param)
        when:
        def retrieved = insuranceLambdaUtils.getCertificateCNFromRequest(request)

        then:
        if (value == null) {
            retrieved.isEmpty()
        } else {
            retrieved.get() == value
        }

        where:
        param | value
        CERT  | "mock-tpp-1.raidiam.com"
        null  | null
    }

    @Unroll
    def "we can get certificate cn from dn header"() {
        given:
        def request = HttpRequest.POST("https://www.example.com/examplepage", null)
        if (param != null) {
            request.headers.add("X-BANK-Certificate-DN", param)
        }

        when:
        def retrieved = insuranceLambdaUtils.getCertificateCNFromDNHeaderInRequest(request)

        then:
        retrieved.orElse("empty_string") == value

        where:
        param                                                          | value
        "serialNumber=43142666000197,CN=mock-tpp-1.raidiam.com"        | "mock-tpp-1.raidiam.com"
        "CN=mock-tpp-1.raidiam.com,serialNumber=43142666000197"        | "mock-tpp-1.raidiam.com"
        "OU=org,CN=mock-tpp-1.raidiam.com,serialNumber=43142666000197" | "mock-tpp-1.raidiam.com"
        null                                                           | "empty_string"
    }

    def "We can get dates and times correctly"() {
        when: "We can get the timezone of Brazil"
        def zone = InsuranceLambdaUtils.getBrasilZoneId()

        then:
        noExceptionThrown()
        zone.getId() == "America/Sao_Paulo"

        when: "We can retrieve the date/time and adjust for timezones"
        def offsetBrt = InsuranceLambdaUtils.getOffsetDateTimeInBrasil().truncatedTo(ChronoUnit.SECONDS)
        def offsetUtc = InsuranceLambdaUtils.getOffsetDateTimeUTC().truncatedTo(ChronoUnit.SECONDS)
        def offsetUtc2 = insuranceLambdaUtils.getOffsetDateTimeUTCV2().truncatedTo(ChronoUnit.SECONDS)
        def instantBrt = InsuranceLambdaUtils.getInstantInBrasil().truncatedTo(ChronoUnit.SECONDS)

        then:
        noExceptionThrown()
        offsetBrt == OffsetDateTime.now(zone).truncatedTo(ChronoUnit.SECONDS)
        offsetUtc == OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        offsetUtc2 == OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        instantBrt == Instant.now().atZone(zone).toInstant().truncatedTo(ChronoUnit.SECONDS)
    }

    def "We can convert dates and times correctly"() {
        when: "We can convert Date objects correctly"
        def instant = Instant.now()
        def date = Date.from(instant)
        def localDateTime = InsuranceLambdaUtils.timestampToLocalDateTime(date)
        def offsetDateTime = InsuranceLambdaUtils.dateToOffsetDate(date)
        def offsetDateTimeBrt = InsuranceLambdaUtils.dateToOffsetDateTimeInBrasil(date)
        def localDate = InsuranceLambdaUtils.dateToLocalDate(date)
        def localDateBrt = InsuranceLambdaUtils.dateToLocalDateInBrasil(date)

        then:
        noExceptionThrown()
        localDateTime == LocalDateTime.ofInstant(instant, InsuranceLambdaUtils.getBrasilZoneId()).truncatedTo(ChronoUnit.MILLIS)
        offsetDateTime == OffsetDateTime.ofInstant(instant, ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS)
        offsetDateTimeBrt == OffsetDateTime.ofInstant(instant, InsuranceLambdaUtils.getBrasilZoneId()).truncatedTo(ChronoUnit.MILLIS)
        localDate == LocalDate.ofInstant(Instant.now(), ZoneOffset.UTC)
        localDateBrt == LocalDate.ofInstant(Instant.now(), InsuranceLambdaUtils.getBrasilZoneId())

        when: "We can convert OffsetDateTime objects correctly"
        def offsetDate = OffsetDateTime.ofInstant(instant, ZoneOffset.UTC)
        def date2 = InsuranceLambdaUtils.offsetDateToDate(offsetDate)

        then:
        noExceptionThrown()
        date2 == Date.from(instant)

        when: "We can convert LocalDate objects correctly"
        def localDate2 = LocalDate.ofInstant(instant, ZoneOffset.UTC)
        def offsetDate2 = InsuranceLambdaUtils.localDateToOffsetDate(localDate2)
        def date3 = InsuranceLambdaUtils.localDateToDate(localDate2)

        then:
        noExceptionThrown()
        offsetDate2 == OffsetDateTime.of(LocalDate.now(), LocalTime.NOON, ZoneOffset.UTC)
        date3 == Date.from(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC))
    }

    def "We can correctly extract values from request data"() {
        when:
        double amount = 123.45
        String value = InsuranceLambdaUtils.doubleToString(amount)
        String value2 = InsuranceLambdaUtils.formatDoubleToLongString(amount)
        String value3 = InsuranceLambdaUtils.formatAmountV2(amount)

        then:
        noExceptionThrown()
        value == "123.45"
        value2 == "123.450000"
        value3 == "123.45"
    }

    def "We can successfully throw an exception and execute custom behaviour"() {
        given:
        def req = TestRequestDataFactory.createConsentRequest(
                testAccountHolder.getDocumentIdentification(),
                testAccountHolder.getDocumentRel(),
                OffsetDateTime.now().plusDays(1),
                PermissionGroup.PERSONAL_REGISTRATION.getPermissions().toList()
        )
        ConsentEntity testEntity = consentRepository.save(ConsentEntity.fromRequest(req, testAccountHolder.accountHolderId, UUID.randomUUID().toString()))

        when:
        insuranceLambdaUtils.throwWithoutRollback(
                () -> consentRepository.findByConsentId(testEntity.getConsentId()).ifPresent(paymentConsent -> {
                    paymentConsent.setStatus(EnumConsentStatus.CONSUMED.toString())
                    consentRepository.update(paymentConsent)
                }),
                new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "testing"))

        then:
        HttpStatusException e = thrown()
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == "testing"
        consentRepository.findByConsentId(testEntity.getConsentId()).get().getStatus() == EnumConsentStatus.CONSUMED.toString()
    }

    def "We can correctly format pageable responses"() {
        given:
        HttpRequest request = HttpRequest.POST("https://www.example.com/examplepage", TestRequestDataFactory.createQuotePatrimonialLeadRequest())
        request.parameters.add("page", "1")
        request.parameters.add("page-size", "25")

        when:
        InsuranceLambdaUtils.buildGetSelfLink(request)

        then:
        request.getUri().toString() == "https://www.example.com/examplepage?page=1&page-size=25"

        when:
        Pageable unsorted = InsuranceLambdaUtils.adjustPageable(Pageable.from(1, 10), request, 25)

        then:
        noExceptionThrown()
        unsorted.getSize() == 10

        when:
        Pageable sorted =  InsuranceLambdaUtils.adjustAndSortPageable(Pageable.from(2, 20), request, 30)

        then:
        noExceptionThrown()
        sorted.getSize() == 20
        sorted.getNumber() == 1
    }

    def "We can't set an invalid pageable response"() {
        given:
        HttpRequest request = HttpRequest.POST("https://www.example.com/examplepage", TestRequestDataFactory.createQuotePatrimonialLeadRequest())
        request.parameters.add("page", pageParam)
        request.parameters.add("page-size", pageSizeParam)

        when:
        InsuranceLambdaUtils.adjustPageable(Pageable.from(page, pageSize), request, 25)

        then:
        HttpStatusException e = thrown()
        e.status == HttpStatus.UNPROCESSABLE_ENTITY
        e.message == message

        where:
        pageParam   | pageSizeParam | page  | pageSize  | message
        "0"         | "25"          | 0     | 25        | "Pages are 1-indexed, value of 0 not valid"
        "1"         | "25"          | 1     | -1        | "Page size must be positive, value of -1 not valid"
        "1"         | "25"          | 1     | 1001      | "Page size must not be over 1000, value of 1001 not valid"
    }

    def "We can extract request properties correctly"() {
        given:
        HttpRequest request = HttpRequest.POST("https://www.example.com/examplepage", TestRequestDataFactory.createQuotePatrimonialLeadRequest())
        request.setAttribute("expirationDateTime", "something")

        when:
        def expirationDateTime = insuranceLambdaUtils.getAttributeFromRequest(request, "expirationDateTime")

        then:
        noExceptionThrown()
        expirationDateTime != null
        expirationDateTime.get() == "something"
    }

    def "We can decorate errors correctly"() {
        given:
        HttpRequest request = HttpRequest.POST("https://www.example.com/examplepage", TestRequestDataFactory.createQuotePatrimonialLeadRequest())

        when:
        ResponseError errorResponse = new ResponseError().addErrorsItem(new ResponseErrorErrors().code("code").title("title").detail("detail"))
        insuranceLambdaUtils.decorateResponseError(errorResponse, request)

        then:
        noExceptionThrown()
        def error = errorResponse.errors.get(0)
        error.title == "title"
        error.code == "code"
        error.detail == "detail"
        error.requestDateTime == null

        when: "We can decorate errors with the request date time when needed"
        request = HttpRequest.POST("https://www.example.com/open-insurance/consents/v2/consents", TestRequestDataFactory.createQuotePatrimonialLeadRequest())
        insuranceLambdaUtils.decorateResponseError(errorResponse, request)

        then:
        noExceptionThrown()
        def error2 = errorResponse.errors.get(0)
        error2.title == "title"
        error2.code == "code"
        error2.detail == "detail"
        error2.requestDateTime != null
    }

    def "We can decorate responses correctly"() {
        given:
        ResponseQuote quote = TestEntityDataFactory.aQuotePatrimonialLead("dshsdfhsdfh").toResponse()
        String self = "https://www.example.com/examplepage"

        when:
        InsuranceLambdaUtils.decorateResponse(quote::setLinks, quote::setMeta, self, 11)

        then:
        noExceptionThrown()
        quote.links != null
        quote.links.self == "https://www.example.com/examplepage"
        quote.meta != null
        quote.meta.totalRecords == 11
        quote.meta.totalPages == 1
        quote.meta.requestDateTime != null

        when:
        InsuranceLambdaUtils.decorateResponse(quote::setLinks, quote::setMeta, self, 10, 20)

        then:
        noExceptionThrown()
        quote.links != null
        quote.links.self == "https://www.example.com/examplepage"
        quote.meta != null
        quote.meta.totalRecords == 10
        quote.meta.totalPages == 1
        quote.meta.requestDateTime != null

        when:
        InsuranceLambdaUtils.decorateResponseBrasilTimeZone(quote::setLinks, quote::setMeta, self, 12)

        then:
        noExceptionThrown()
        quote.links != null
        quote.links.self == "https://www.example.com/examplepage"
        quote.meta != null
        quote.meta.totalRecords == 12
        quote.meta.totalPages == 1
        quote.meta.requestDateTime != null

        when:
        quote = TestEntityDataFactory.aQuotePatrimonialLead("dshsdfhsdfh").toResponse()
        InsuranceLambdaUtils.decorateResponse(quote::setLinks, 25, self, 2, 3)

        then:
        noExceptionThrown()
        quote.links != null
        quote.links.self == "https://www.example.com/examplepage?page-size=25&page=3"
        quote.links.first == "https://www.example.com/examplepage?page-size=25&page=1"
        quote.links.prev == "https://www.example.com/examplepage?page-size=25&page=2"
        quote.links.last == "https://www.example.com/examplepage?page-size=25&page=3"
        quote.meta == null

        when:
        quote = TestEntityDataFactory.aQuotePatrimonialLead("dshsdfhsdfh").toResponse()
        InsuranceLambdaUtils.decorateResponse(quote::setLinks, self)

        then:
        noExceptionThrown()
        quote.links != null
        quote.links.self == "https://www.example.com/examplepage"
        quote.meta == null

        when:
        InsuranceLambdaUtils.decorateResponseSimpleMeta(quote::setLinks, quote::setMeta, 1, self, 2, 4)

        then:
        noExceptionThrown()
        quote.links != null
        quote.links.self == "https://www.example.com/examplepage?page-size=1&page=3"
        quote.links.first == "https://www.example.com/examplepage?page-size=1&page=1"
        quote.links.prev == "https://www.example.com/examplepage?page-size=1&page=2"
        quote.links.next == "https://www.example.com/examplepage?page-size=1&page=4"
        quote.links.last == "https://www.example.com/examplepage?page-size=1&page=4"
        quote.meta != null
        quote.meta.totalRecords == null
        quote.meta.totalPages == null
        quote.meta.requestDateTime != null

        when:
        InsuranceLambdaUtils.decorateResponseSimpleLinkMeta(quote::setLinks, quote::setMeta, self)

        then:
        noExceptionThrown()
        quote.links != null
        quote.links.self == "https://www.example.com/examplepage"
        quote.meta != null
        quote.meta.totalRecords == 1
        quote.meta.totalPages == 1
        quote.meta.requestDateTime == null

        when:
        String fromDate = LocalDate.now().minusDays(3).toString()
        String toDate = LocalDate.now().toString()

        quote = TestEntityDataFactory.aQuotePatrimonialLead("dshsdfhsdfh").toResponse()
        InsuranceLambdaUtils.decorateResponse(quote::setLinks, 20, self, 3, 5, "fromDueDate", fromDate, "toDueDate", toDate)

        then:
        noExceptionThrown()
        quote.links != null
        quote.links.self == "https://www.example.com/examplepage?page-size=20&page=4&fromDueDate=" + fromDate + "&toDueDate=" + toDate
        quote.links.first == "https://www.example.com/examplepage?page-size=20&page=1&fromDueDate=" + fromDate + "&toDueDate=" + toDate
        quote.links.prev == "https://www.example.com/examplepage?page-size=20&page=3&fromDueDate=" + fromDate + "&toDueDate=" + toDate
        quote.links.next == "https://www.example.com/examplepage?page-size=20&page=5&fromDueDate=" + fromDate + "&toDueDate=" + toDate
        quote.links.last == "https://www.example.com/examplepage?page-size=20&page=5&fromDueDate=" + fromDate + "&toDueDate=" + toDate
        quote.meta == null

        when:
        InsuranceLambdaUtils.decorateResponseSimpleLinkMetaBrasilTimeZone(quote::setLinks, quote::setMeta, self)

        then:
        noExceptionThrown()
        quote.links != null
        quote.links.self == "https://www.example.com/examplepage"
        quote.meta != null
        quote.meta.totalRecords == null
        quote.meta.totalPages == null
        quote.meta.requestDateTime != null


        when: "A response has too many dateName parameters, we throw an error"
        quote = TestEntityDataFactory.aQuotePatrimonialLead("dshsdfhsdfh").toResponse()
        InsuranceLambdaUtils.decorateResponse(quote::setLinks, 20, self, 3, 5, "fromDueDate", LocalDate.now().minusDays(3).toString(), "toDueDate", LocalDate.now().toString(), "fooDueDate", "bar")

        then:
        TrustframeworkException e = thrown()
        e.message == "dateNames parameter should contain the following - fromDateName, fromDate, toDateName, toDate strings"
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true
        TimeZone.setDefault(originalTimeZone)

        then:
        runCleanup
    }
}
