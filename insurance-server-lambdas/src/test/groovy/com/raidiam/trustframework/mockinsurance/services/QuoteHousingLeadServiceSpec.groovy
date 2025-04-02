package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.QuoteHousingLeadEntity
import com.raidiam.trustframework.mockinsurance.models.generated.*
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class QuoteHousingLeadServiceSpec extends CleanupSpecification {

    @Inject
    QuoteHousingLeadService quoteHousingLeadService

    def "We can create a quote housing lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        QuoteHousingLeadEntity createdHousingLead = TestEntityDataFactory.aQuoteHousingLead(consentId)

        when:
        def responseHousingLead = quoteHousingLeadService.createQuote(createdHousingLead)

        then:
        noExceptionThrown()
        responseHousingLead != null
        responseHousingLead.consentId == consentId
        responseHousingLead.status == QuoteStatus.StatusEnum.RCVD.toString()
    }

    def "We can cancel a quote housing lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def createdHousingLead = TestEntityDataFactory.aQuoteHousingLead(consentId)
        createdHousingLead = quoteHousingLeadService.createQuote(createdHousingLead)

        def req = new RevokeQuotePatchPayload()
                .data(new RevokeQuotePatchPayloadData()
                        .author(new RevokeQuotePatchPayloadDataAuthor()
                                .identificationNumber("123456789")
                                .identificationType(RevokeQuotePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                ))

        when:
        def responseHousingLead = quoteHousingLeadService.patchQuote(req, consentId, createdHousingLead.getClientId())

        then:
        noExceptionThrown()
        responseHousingLead.quoteId == createdHousingLead.quoteId
        responseHousingLead.consentId == consentId
        responseHousingLead.status == QuoteStatusEnum.CANC.toString()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
