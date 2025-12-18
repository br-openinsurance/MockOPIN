package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.models.generated.*
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

import java.time.LocalDate

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class QuotePatrimonialBusinessServiceSpec extends CleanupSpecification {

    @Inject
    QuotePatrimonialBusinessService quotePatrimonialBusinessService

    def "We can create a quote"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)

        when:
        def newQuote = quotePatrimonialBusinessService.createQuote(quote)

        then:
        noExceptionThrown()
        newQuote.quoteId != null
        newQuote.consentId == consentId
    }

    def "We can create a quote without max LMG"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)
        quote.data.v1.quoteData.maxLMG = null

        when:
        def newQuote = quotePatrimonialBusinessService.createQuote(quote)

        then:
        noExceptionThrown()
        newQuote.quoteId != null
        newQuote.consentId == consentId
    }

    def "We can fetch a quote that moves to ACPT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)
        quotePatrimonialBusinessRepository.save(quote)

        when:
        def quoteFetched = quotePatrimonialBusinessService.getQuote(consentId, quote.getClientId())

        then:
        noExceptionThrown()
        quoteFetched.quoteId == quote.quoteId
        quoteFetched.consentId == consentId
        quoteFetched.status == QuoteStatusEnum.ACPT.toString()
    }

    def "We can fetch a quote that moves to RJCT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)
        quote.getData().setV1(new QuotePatrimonialBusinessData().quoteData(new QuoteDataPatrimonialBusiness()))
        quotePatrimonialBusinessRepository.save(quote)

        when:
        def quoteFetched = quotePatrimonialBusinessService.getQuote(consentId, quote.getClientId())

        then:
        noExceptionThrown()
        quoteFetched.quoteId == quote.quoteId
        quoteFetched.consentId == consentId
        quoteFetched.status == QuoteStatusEnum.RJCT.toString()
    }

    def "A quote without a policy id is rejected"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)
        quote.data.v1.quoteData.setPolicyId(null)
        quotePatrimonialBusinessRepository.save(quote)

        when:
        def quoteFetched = quotePatrimonialBusinessService.getQuote(consentId, quote.getClientId())

        then:
        noExceptionThrown()
        quoteFetched.quoteId == quote.quoteId
        quoteFetched.consentId == consentId
        quoteFetched.status == QuoteStatusEnum.RJCT.toString()
    }

    def "A quote with termEndDate later than termStartDate is rejected"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)
        quote.data.v1.quoteData.setTermEndDate(LocalDate.of(2025, 1, 1))
        quote.data.v1.quoteData.setTermStartDate(LocalDate.of(2025, 1, 2))
        quotePatrimonialBusinessRepository.save(quote)

        when:
        def quoteFetched = quotePatrimonialBusinessService.getQuote(consentId, quote.getClientId())

        then:
        noExceptionThrown()
        quoteFetched.quoteId == quote.quoteId
        quoteFetched.consentId == consentId
        quoteFetched.status == QuoteStatusEnum.RJCT.toString()
    }

    def "A quote with null termEndDate or termStart date and non-null policyId is accepted"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)
        quote.data.v1.quoteData.setTermEndDate(null)
        quote.data.v1.quoteData.setTermStartDate(null)

        when:
        quotePatrimonialBusinessService.createQuote(quote)
        def quoteFetched = quotePatrimonialBusinessService.getQuote(consentId, quote.getClientId())

        then:
        noExceptionThrown()
        quoteFetched.quoteId == quote.quoteId
        quoteFetched.consentId == consentId
        quoteFetched.status == QuoteStatusEnum.ACPT.toString()
    }

    def "We can acknowledge a quote that is ACPT"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)
        quote.setStatus(QuoteStatusEnum.ACPT.toString())
        quote = quotePatrimonialBusinessRepository.save(quote)

        def req = new PatchPayload()
                .data(new PatchPayloadData()
                        .status(PatchPayloadData.StatusEnum.ACKN)
                        .insurerQuoteId(quote.getQuoteId().toString())
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        def patchedQuote = quotePatrimonialBusinessService.patchQuote(req, consentId, quote.getClientId())

        then:
        noExceptionThrown()
        patchedQuote.quoteId == quote.quoteId
        patchedQuote.consentId == consentId
        patchedQuote.status == QuoteStatusEnum.ACKN.toString()
    }

    def "We can't acknowledge a quote that is not accepted"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)
        quote.setStatus(QuoteStatusEnum.EVAL.toString())
        quote = quotePatrimonialBusinessService.createQuote(quote)

        def req = new PatchPayload()
                .data(new PatchPayloadData()
                        .status(PatchPayloadData.StatusEnum.ACKN)
                        .insurerQuoteId(quote.getQuoteId().toString())
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        quotePatrimonialBusinessService.patchQuote(req, consentId, quote.getClientId())

        then:
        def e = thrown(HttpStatusException)
        e.getStatus() == HttpStatus.UNPROCESSABLE_ENTITY
        e.getMessage().startsWith("NAO_INFORMADO")
    }

    def "We can cancel a quote"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)
        quote.setStatus(QuoteStatusEnum.EVAL.toString())
        quote = quotePatrimonialBusinessService.createQuote(quote)

        def req = new PatchPayload()
                .data(new PatchPayloadData()
                        .status(PatchPayloadData.StatusEnum.CANC)
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber(quote.getPersonCpf())))

        when:
        def patchedQuote = quotePatrimonialBusinessService.patchQuote(req, consentId, quote.getClientId())

        then:
        noExceptionThrown()
        patchedQuote.quoteId == quote.quoteId
        patchedQuote.consentId == consentId
        patchedQuote.status == QuoteStatusEnum.CANC.toString()
    }

    def "A quote with missing required fields for its max LMG details will throw a 400"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quote = TestEntityDataFactory.aQuotePatrimonialBusiness(consentId)
        quote.getData().setV1(new QuotePatrimonialBusinessData()
                .quoteData(new QuoteDataPatrimonialBusiness()
                .maxLMG(new AmountDetails()
                        .unit(new AmountDetailsUnit()
                                .code("code")
                                .description(AmountDetailsUnit.DescriptionEnum.ADP))
                        .unitTypeOthers("unitTypeOthers")
                )))

        when:
        quotePatrimonialBusinessService.createQuote(quote)

        then:
        def e = thrown(HttpStatusException)
        e.getStatus() == HttpStatus.BAD_REQUEST
        e.getMessage() == "NAO_INFORMADO: Amount missing from amount details"

        when:
        quote.data.v1.quoteData.maxLMG.setAmount("1000.00")
        quotePatrimonialBusinessService.createQuote(quote)

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
