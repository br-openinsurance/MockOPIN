package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.QuotePersonLeadEntity
import com.raidiam.trustframework.mockinsurance.models.generated.*
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class QuotePersonLeadServiceSpec extends CleanupSpecification {

    @Inject
    QuotePersonLeadService quotePersonLeadService

    def "We can create a quote person lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        QuotePersonLeadEntity createdPersonLead = TestEntityDataFactory.aQuotePersonLead(consentId)

        when:
        def responsePersonLead = quotePersonLeadService.createQuote(createdPersonLead)

        then:
        noExceptionThrown()
        responsePersonLead != null
        responsePersonLead.consentId == consentId
        responsePersonLead.status == QuoteStatus.StatusEnum.RCVD.toString()
    }

    def "We can cancel a quote person lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def createdPersonLead = TestEntityDataFactory.aQuotePersonLead(consentId)
        createdPersonLead = quotePersonLeadService.createQuote(createdPersonLead)

        def req = new RevokePatchPayload()
                .data(new RevokePatchPayloadData()
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationNumber("123456789")
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                ))

        when:
        def responsePersonLead = quotePersonLeadService.patchQuote(req, consentId, createdPersonLead.getClientId())

        then:
        noExceptionThrown()
        responsePersonLead.quoteId == createdPersonLead.quoteId
        responsePersonLead.consentId == consentId
        responsePersonLead.status == QuoteStatusEnum.CANC.toString()
    }

    def "enable cleanup"() {
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
