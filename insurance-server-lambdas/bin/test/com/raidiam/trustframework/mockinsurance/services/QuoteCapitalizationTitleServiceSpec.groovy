package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.models.generated.*
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
public class QuoteCapitalizationTitleServiceSpec extends CleanupSpecification {

    @Inject
    QuoteCapitalizationTitleService quoteCapitalizationTitleService

    def "We can create a quote"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteCapitalizationTitle(consentId)

        when:
        def newQuote = quoteCapitalizationTitleService.createQuote(quote)

        then:
        noExceptionThrown()
        newQuote.quoteId != null
        newQuote.consentId == consentId
    }

    def "We can't create a quote if payment type and payment are incompatible"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteCapitalizationTitle(consentId)
        quote.getData().setV1(new QuoteCapitalizationTitleData()
                .quoteData(new QuoteDataCapitalizationTitle()
                        .paymentType(QuoteDataCapitalizationTitle.PaymentTypeEnum.MENSAL)
                        .singlePayment(new AmountDetails()
                                .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                                .amount("100.00")
                        )
                ))

        when:
        def newQuote = quoteCapitalizationTitleService.createQuote(quote)

        then:
        def e = thrown(HttpStatusException)
        e.getStatus() == HttpStatus.BAD_REQUEST
        e.getMessage().startsWith("NAO_INFORMADO")
    }

    def "We can fetch a quote that moves to ACPT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteCapitalizationTitle(consentId)
        quoteCapitalizationTitleRepository.save(quote)

        when:
        def quoteFetched = quoteCapitalizationTitleService.getQuote(consentId, quote.getClientId())

        then:
        noExceptionThrown()
        quoteFetched.quoteId == quote.quoteId
        quoteFetched.consentId == consentId
        quoteFetched.status == QuoteStatusEnum.ACPT.toString()
    }

    def "We can fetch a quote that moves to RJCT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteCapitalizationTitle(consentId)
        quote.getData().getV1().setQuoteData(new QuoteDataCapitalizationTitle()
                .paymentType(QuoteDataCapitalizationTitle.PaymentTypeEnum.UNICO)
                .singlePayment(new AmountDetails()
                        .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                        .amount("1000.00")))

        quoteCapitalizationTitleRepository.save(quote)

        when:
        def quoteFetched = quoteCapitalizationTitleService.getQuote(consentId, quote.getClientId())

        then:
        noExceptionThrown()
        quoteFetched.quoteId == quote.quoteId
        quoteFetched.consentId == consentId
        quoteFetched.status == QuoteStatusEnum.RJCT.toString()
    }

    def "We can acknowledge a quote that is ACPT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteCapitalizationTitle(consentId)
        quote.setStatus(QuoteStatusEnum.ACPT.toString())
        quote = quoteCapitalizationTitleRepository.save(quote)

        def req = new PatchPayload()
                .data(new PatchPayloadData()
                        .status(PatchPayloadData.StatusEnum.ACKN)
                        .insurerQuoteId(quote.getQuoteId().toString())
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        def patchedQuote = quoteCapitalizationTitleService.patchQuote(req, consentId, quote.getClientId())

        then:
        noExceptionThrown()
        patchedQuote.quoteId == quote.quoteId
        patchedQuote.consentId == consentId
        patchedQuote.status == QuoteStatusEnum.ACKN.toString()
    }

    def "We can't acknowledge a quote that is not accepted"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteCapitalizationTitle(consentId)
        quote.setStatus(QuoteStatusEnum.EVAL.toString())
        quote = quoteCapitalizationTitleService.createQuote(quote)

        def req = new PatchPayload()
                .data(new PatchPayloadData()
                        .status(PatchPayloadData.StatusEnum.ACKN)
                        .insurerQuoteId(quote.getQuoteId().toString())
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        quoteCapitalizationTitleService.patchQuote(req, consentId, quote.getClientId())

        then:
        def e = thrown(HttpStatusException)
        e.getStatus() == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage().startsWith("NAO_INFORMADO")
    }

    def "We can cancel a quote"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuoteCapitalizationTitle(consentId)
        quote.setStatus(QuoteStatusEnum.EVAL.toString())
        quote = quoteCapitalizationTitleService.createQuote(quote)

        def req = new PatchPayload()
                .data(new PatchPayloadData()
                        .status(PatchPayloadData.StatusEnum.CANC)
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        def patchedQuote = quoteCapitalizationTitleService.patchQuote(req, consentId, quote.getClientId())

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
