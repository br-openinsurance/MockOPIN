package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.QuoteLifePensionLeadEntity
import com.raidiam.trustframework.mockinsurance.models.generated.*
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class QuoteLifePensionLeadServiceSpec extends CleanupSpecification {

    @Inject
    QuoteLifePensionLeadService quoteLifePensionLeadService

    def "We can create a quote housing lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        QuoteLifePensionLeadEntity createdLifePensionLead = TestEntityDataFactory.aQuoteLifePensionLead(consentId)

        when:
        def responseLifePensionLead = quoteLifePensionLeadService.createQuote(createdLifePensionLead)

        then:
        noExceptionThrown()
        responseLifePensionLead != null
        responseLifePensionLead.consentId == consentId
        responseLifePensionLead.status == QuoteStatus.StatusEnum.RCVD.toString()
    }

    def "We can cancel a quote housing lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def createdLifePensionLead = TestEntityDataFactory.aQuoteLifePensionLead(consentId)
        createdLifePensionLead = quoteLifePensionLeadService.createQuote(createdLifePensionLead)

        def req = new RevokeQuotePatchPayload()
                .data(new RevokeQuotePatchPayloadData()
                        .author(new RevokeQuotePatchPayloadDataAuthor()
                                .identificationNumber("123456789")
                                .identificationType(RevokeQuotePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                ))

        when:
        def responseLifePensionLead = quoteLifePensionLeadService.patchQuote(req, consentId, createdLifePensionLead.getClientId())

        then:
        noExceptionThrown()
        responseLifePensionLead.quoteId == createdLifePensionLead.quoteId
        responseLifePensionLead.consentId == consentId
        responseLifePensionLead.status == QuoteStatusEnum.CANC.toString()
    }

    def "enable cleanup"() {
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
