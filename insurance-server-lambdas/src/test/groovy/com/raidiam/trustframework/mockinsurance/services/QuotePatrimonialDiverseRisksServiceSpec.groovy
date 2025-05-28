package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.models.generated.*
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class QuotePatrimonialDiverseRisksServiceSpec extends CleanupSpecification {

    @Inject
    QuotePatrimonialDiverseRisksService quotePatrimonialDiverseRisksService

    def "We can create a quote"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialDiverseRisks(consentId)

        when:
        def newQuote = quotePatrimonialDiverseRisksService.createQuote(quote)

        then:
        noExceptionThrown()
        newQuote.quoteId != null
        newQuote.consentId == consentId
    }

    def "We can fetch a quote that moves to ACPT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialDiverseRisks(consentId)
        quotePatrimonialDiverseRisksRepository.save(quote)

        when:
        def quoteFetched = quotePatrimonialDiverseRisksService.getQuote(consentId, quote.getClientId())

        then:
        noExceptionThrown()
        quoteFetched.quoteId == quote.quoteId
        quoteFetched.consentId == consentId
        quoteFetched.status == QuoteStatusEnum.ACPT.toString()
    }

    def "We can fetch a quote that moves to RJCT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialDiverseRisks(consentId)
        quote.setData(new QuotePatrimonialDiverseRisksData()
                .quoteData(new QuoteDataPatrimonialDiverseRisks()))
        quotePatrimonialDiverseRisksRepository.save(quote)

        when:
        def quoteFetched = quotePatrimonialDiverseRisksService.getQuote(consentId, quote.getClientId())

        then:
        noExceptionThrown()
        quoteFetched.quoteId == quote.quoteId
        quoteFetched.consentId == consentId
        quoteFetched.status == QuoteStatusEnum.RJCT.toString()
    }

    def "We can acknowledge a quote that is ACPT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialDiverseRisks(consentId)
        quote.setStatus(QuoteStatusEnum.ACPT.toString())
        quote = quotePatrimonialDiverseRisksRepository.save(quote)

        def req = new PatchPayload()
                .data(new PatchPayloadData()
                        .status(PatchPayloadData.StatusEnum.ACKN)
                        .insurerQuoteId(quote.getQuoteId().toString())
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        def patchedQuote = quotePatrimonialDiverseRisksService.patchQuote(req, consentId, quote.getClientId())

        then:
        noExceptionThrown()
        patchedQuote.quoteId == quote.quoteId
        patchedQuote.consentId == consentId
        patchedQuote.status == QuoteStatusEnum.ACKN.toString()
    }

    def "We can't acknowledge a quote that is not accepted"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialDiverseRisks(consentId)
        quote.setStatus(QuoteStatusEnum.EVAL.toString())
        quote = quotePatrimonialDiverseRisksService.createQuote(quote)

        def req = new PatchPayload()
                .data(new PatchPayloadData()
                        .status(PatchPayloadData.StatusEnum.ACKN)
                        .insurerQuoteId(quote.getQuoteId().toString())
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        quotePatrimonialDiverseRisksService.patchQuote(req, consentId, quote.getClientId())

        then:
        def e = thrown(HttpStatusException)
        e.getStatus() == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage().startsWith("NAO_INFORMADO")
    }

    def "We can cancel a quote"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialDiverseRisks(consentId)
        quote.setStatus(QuoteStatusEnum.EVAL.toString())
        quote = quotePatrimonialDiverseRisksService.createQuote(quote)

        def req = new PatchPayload()
                .data(new PatchPayloadData()
                        .status(PatchPayloadData.StatusEnum.CANC)
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        def patchedQuote = quotePatrimonialDiverseRisksService.patchQuote(req, consentId, quote.getClientId())

        then:
        noExceptionThrown()
        patchedQuote.quoteId == quote.quoteId
        patchedQuote.consentId == consentId
        patchedQuote.status == QuoteStatusEnum.CANC.toString()
    }

    def "A quote with missing required fields for its max LMG details will throw a 400"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialDiverseRisks(consentId)
        quote.setData(new QuotePatrimonialDiverseRisksData()
                .quoteData(new QuoteDataPatrimonialDiverseRisks()
                .maxLMG(new AmountDetails()
                        .unit(new AmountDetailsUnit()
                                .code("code")
                                .description(AmountDetailsUnit.DescriptionEnum.ADP))
                        .unitTypeOthers("unitTypeOthers")
                )))

        when:
        quotePatrimonialDiverseRisksService.createQuote(quote)

        then:
        def e = thrown(HttpStatusException)
        e.getStatus() == HttpStatus.BAD_REQUEST
        e.getMessage() == "NAO_INFORMADO: Amount missing from amount details"

        when:
        quote.data.quoteData.maxLMG.setAmount("1000.00")
        quotePatrimonialDiverseRisksService.createQuote(quote)

        then:
        def e2 = thrown(HttpStatusException)
        e2.getStatus() == HttpStatus.BAD_REQUEST
        e2.getMessage() == "NAO_INFORMADO: Unit type missing from amount details"
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }

}
