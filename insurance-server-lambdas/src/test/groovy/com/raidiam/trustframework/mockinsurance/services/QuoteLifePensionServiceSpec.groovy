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
class QuoteLifePensionServiceSpec extends CleanupSpecification {

    @Inject
    QuoteLifePensionService quoteLifePensionService

    def "We can create a quote"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteLifePension(consentId)

        when:
        def newQuote = quoteLifePensionService.createQuote(quote)

        then:
        noExceptionThrown()
        newQuote.quoteId != null
        newQuote.consentId == consentId
    }

    def "We can fetch a quote that moves to ACPT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteLifePension(consentId)
        quoteLifePensionRepository.save(quote)

        when:
        def quoteFetched = quoteLifePensionService.getQuote(consentId, quote.getClientId())

        then:
        noExceptionThrown()
        quoteFetched.quoteId == quote.quoteId
        quoteFetched.consentId == consentId
        quoteFetched.status == QuoteStatusEnum.ACPT.toString()
    }

    def "We can fetch a quote that moves to RJCT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteLifePension(consentId)
        quote.data.quoteData.products[0].initialContribution.unitType = AmountDetails.UnitTypeEnum.PORCENTAGEM
        quote.data.quoteData.products[0].initialContribution.amount = "1000.00"
        quoteLifePensionRepository.save(quote)

        when:
        def quoteFetched = quoteLifePensionService.getQuote(consentId, quote.getClientId())

        then:
        noExceptionThrown()
        quoteFetched.quoteId == quote.quoteId
        quoteFetched.consentId == consentId
        quoteFetched.status == QuoteStatusEnum.RJCT.toString()
    }

    def "We can acknowledge a quote that is ACPT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteLifePension(consentId)
        quote.setStatus(QuoteStatusEnum.ACPT.toString())
        quote = quoteLifePensionRepository.save(quote)

        def req = new PatchQuotePayload()
                .data(new PatchQuotePayloadData()
                        .status(PatchQuotePayloadData.StatusEnum.ACKN)
                        .insurerQuoteId(quote.getQuoteId().toString())
                        .author(new RevokeQuotePatchPayloadDataAuthor()
                                .identificationType(RevokeQuotePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        def patchedQuote = quoteLifePensionService.patchQuote(req, consentId, quote.getClientId())

        then:
        noExceptionThrown()
        patchedQuote.quoteId == quote.quoteId
        patchedQuote.consentId == consentId
        patchedQuote.status == QuoteStatusEnum.ACKN.toString()
    }

    def "We can't acknowledge a quote that is not accepted"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteLifePension(consentId)
        quote.setStatus(QuoteStatusEnum.EVAL.toString())
        quote = quoteLifePensionService.createQuote(quote)

        def req = new PatchQuotePayload()
                .data(new PatchQuotePayloadData()
                        .status(PatchQuotePayloadData.StatusEnum.ACKN)
                        .insurerQuoteId(quote.getQuoteId().toString())
                        .author(new RevokeQuotePatchPayloadDataAuthor()
                                .identificationType(RevokeQuotePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        quoteLifePensionService.patchQuote(req, consentId, quote.getClientId())

        then:
        def e = thrown(HttpStatusException)
        e.getStatus() == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage().startsWith("NAO_INFORMADO")
    }

    def "We can cancel a quote"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteLifePension(consentId)
        quote.setStatus(QuoteStatusEnum.EVAL.toString())
        quote = quoteLifePensionService.createQuote(quote)

        def req = new PatchQuotePayload()
                .data(new PatchQuotePayloadData()
                        .status(PatchQuotePayloadData.StatusEnum.CANC)
                        .author(new RevokeQuotePatchPayloadDataAuthor()
                                .identificationType(RevokeQuotePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        def patchedQuote = quoteLifePensionService.patchQuote(req, consentId, quote.getClientId())

        then:
        noExceptionThrown()
        patchedQuote.quoteId == quote.quoteId
        patchedQuote.consentId == consentId
        patchedQuote.status == QuoteStatusEnum.CANC.toString()
    }

    def "A quote with missing required fields for its max LMG details will throw a 400"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteLifePension(consentId)

        quote.data.quoteData.products[0].initialContribution.unitType = null

        when:
        quoteLifePensionService.createQuote(quote)

        then:
        def e = thrown(HttpStatusException)
        e.getStatus() == HttpStatus.BAD_REQUEST
        e.getMessage() == "NAO_INFORMADO: Unit type missing from amount details"

        when:
        quote.data.quoteData.products[0].initialContribution.unitType = AmountDetails.UnitTypeEnum.MONETARIO
        quote.data.quoteData.products[0].initialContribution.amount = null
        quoteLifePensionService.createQuote(quote)

        then:
        def e2 = thrown(HttpStatusException)
        e2.getStatus() == HttpStatus.BAD_REQUEST
        e2.getMessage() == "NAO_INFORMADO: Amount missing from amount details"
    }

    def "enable cleanup"() {
        when:
        runCleanup = true

        then:
        runCleanup
    }

}
