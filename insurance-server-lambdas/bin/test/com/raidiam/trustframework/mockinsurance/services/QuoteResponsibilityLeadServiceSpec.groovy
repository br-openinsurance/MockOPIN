package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.QuoteResponsibilityLeadEntity
import com.raidiam.trustframework.mockinsurance.models.generated.*
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class QuoteResponsibilityLeadServiceSpec extends CleanupSpecification {

    @Inject
    QuoteResponsibilityLeadService quoteResponsibilityLeadService

    def "We can create a quote responsibility lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        QuoteResponsibilityLeadEntity quoteResponsibilityLead = TestEntityDataFactory.aQuoteResponsibilityLead(consentId)

        when:
        def createdQuoteFinancialLead = quoteResponsibilityLeadService.createQuote(quoteResponsibilityLead)

        then:
        noExceptionThrown()
        createdQuoteFinancialLead != null
        createdQuoteFinancialLead.consentId == consentId
        createdQuoteFinancialLead.status == QuoteStatus.StatusEnum.RCVD.toString()
    }

    def "We can cancel a quote responsibility lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quoteResponsibilityLead = TestEntityDataFactory.aQuoteResponsibilityLead(consentId)
        def createdQuoteResponsibilityLead = quoteResponsibilityLeadService.createQuote(quoteResponsibilityLead)

        def req = new RevokePatchPayload()
                .data(new RevokePatchPayloadData()
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationNumber("123456789")
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                ))

        when:
        def responseResponsibilityLead = quoteResponsibilityLeadService.patchQuote(req, consentId, createdQuoteResponsibilityLead.getClientId())

        then:
        noExceptionThrown()
        responseResponsibilityLead.quoteId == createdQuoteResponsibilityLead.quoteId
        responseResponsibilityLead.consentId == consentId
        responseResponsibilityLead.status == QuoteStatusEnum.CANC.toString()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
