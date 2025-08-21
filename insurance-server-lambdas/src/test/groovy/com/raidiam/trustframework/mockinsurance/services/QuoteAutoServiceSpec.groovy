package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.models.generated.*
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise
import java.time.LocalDate;

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
public class QuoteAutoServiceSpec extends CleanupSpecification {

    @Inject
    QuoteAutoService quoteAutoService

    def "We can create a quote"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteAuto(consentId)

        when:
        def newQuote = quoteAutoService.createQuote(quote)

        then:
        noExceptionThrown()
        newQuote.quoteId != null
        newQuote.consentId == consentId
    }

    def "We can fetch a quote that moves to ACPT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteAuto(consentId)
        quoteAutoRepository.save(quote)

        when:
        def quoteFetched = quoteAutoService.getQuote(consentId, quote.getClientId())

        then:
        noExceptionThrown()
        quoteFetched.quoteId == quote.quoteId
        quoteFetched.consentId == consentId
        quoteFetched.status == QuoteStatusEnum.ACPT.toString()
    }

    def "We can fetch a quote that moves to RJCT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteAuto(consentId)
        quote.setData(new QuoteAutoData()
            .quoteData(new QuoteDataAuto()
                .policyId("random_policy_id")
                .termStartDate(LocalDate.now().plusYears(1))
                .termEndDate(LocalDate.now().minusYears(1))
            )
        )
        quoteAutoRepository.save(quote)

        when:
        def quoteFetched = quoteAutoService.getQuote(consentId, quote.getClientId())

        then:
        noExceptionThrown()
        quoteFetched.quoteId == quote.quoteId
        quoteFetched.consentId == consentId
        quoteFetched.status == QuoteStatusEnum.RJCT.toString()
    }

    def "We can acknowledge a quote that is ACPT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteAuto(consentId)
        quote.setStatus(QuoteStatusEnum.ACPT.toString())
        quote = quoteAutoRepository.save(quote)

        def req = new PatchPayload()
                .data(new PatchPayloadData()
                        .status(PatchPayloadData.StatusEnum.ACKN)
                        .insurerQuoteId(quote.getQuoteId().toString())
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        def patchedQuote = quoteAutoService.patchQuote(req, consentId, quote.getClientId())

        then:
        noExceptionThrown()
        patchedQuote.quoteId == quote.quoteId
        patchedQuote.consentId == consentId
        patchedQuote.status == QuoteStatusEnum.ACKN.toString()
    }

    def "We can't acknowledge a quote that is not accepted"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteAuto(consentId)
        quote.setStatus(QuoteStatusEnum.EVAL.toString())
        quote = quoteAutoService.createQuote(quote)

        def req = new PatchPayload()
                .data(new PatchPayloadData()
                        .status(PatchPayloadData.StatusEnum.ACKN)
                        .insurerQuoteId(quote.getQuoteId().toString())
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        quoteAutoService.patchQuote(req, consentId, quote.getClientId())

        then:
        def e = thrown(HttpStatusException)
        e.getStatus() == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage().startsWith("NAO_INFORMADO")
    }

    def "We can cancel a quote"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteAuto(consentId)
        quote.setStatus(QuoteStatusEnum.EVAL.toString())
        quote = quoteAutoService.createQuote(quote)

        def req = new PatchPayload()
                .data(new PatchPayloadData()
                        .status(PatchPayloadData.StatusEnum.CANC)
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        def patchedQuote = quoteAutoService.patchQuote(req, consentId, quote.getClientId())

        then:
        noExceptionThrown()
        patchedQuote.quoteId == quote.quoteId
        patchedQuote.consentId == consentId
        patchedQuote.status == QuoteStatusEnum.CANC.toString()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
