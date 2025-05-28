package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.QuoteTransportLeadEntity
import com.raidiam.trustframework.mockinsurance.models.generated.*
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class QuoteTransportLeadServiceSpec extends CleanupSpecification {

    @Inject
    QuoteTransportLeadService quoteTransportLeadService

    def "We can create a quote transport lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        QuoteTransportLeadEntity quoteTransportLead = TestEntityDataFactory.aQuoteTransportLead(consentId)

        when:
        def createdQuoteFinancialLead = quoteTransportLeadService.createQuote(quoteTransportLead)

        then:
        noExceptionThrown()
        createdQuoteFinancialLead != null
        createdQuoteFinancialLead.consentId == consentId
        createdQuoteFinancialLead.status == QuoteStatus.StatusEnum.RCVD.toString()
    }

    def "We can cancel a quote transport lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quoteTransportLead = TestEntityDataFactory.aQuoteTransportLead(consentId)
        def createdQuoteTransportLead = quoteTransportLeadService.createQuote(quoteTransportLead)

        def req = new RevokePatchPayload()
                .data(new RevokePatchPayloadData()
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationNumber("123456789")
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                ))

        when:
        def responseTransportLead = quoteTransportLeadService.patchQuote(req, consentId, createdQuoteTransportLead.getClientId())

        then:
        noExceptionThrown()
        responseTransportLead.quoteId == createdQuoteTransportLead.quoteId
        responseTransportLead.consentId == consentId
        responseTransportLead.status == QuoteStatusEnum.CANC.toString()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
