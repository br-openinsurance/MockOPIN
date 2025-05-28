package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.QuoteAutoLeadEntity
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatus
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatusEnum
import com.raidiam.trustframework.mockinsurance.models.generated.RevokePatchPayload
import com.raidiam.trustframework.mockinsurance.models.generated.RevokePatchPayloadData
import com.raidiam.trustframework.mockinsurance.models.generated.RevokePatchPayloadDataAuthor
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
public class QuoteAutoLeadServiceSpec extends CleanupSpecification {

    @Inject
    QuoteAutoLeadService quoteAutoLeadService

    def "We can create a quote auto lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        QuoteAutoLeadEntity createdAutoLead = TestEntityDataFactory.aQuoteAutoLead(consentId)

        when:
        def responseAutoLead = quoteAutoLeadService.createQuote(createdAutoLead)

        then:
        noExceptionThrown()
        responseAutoLead != null
        responseAutoLead.consentId == consentId
        responseAutoLead.status == QuoteStatus.StatusEnum.RCVD.toString()
    }

    def "We can cancel a quote auto lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def createdAutoLead = TestEntityDataFactory.aQuoteAutoLead(consentId)
        createdAutoLead = quoteAutoLeadService.createQuote(createdAutoLead)

        def req = new RevokePatchPayload()
                .data(new RevokePatchPayloadData()
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationNumber("123456789")
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                ))

        when:
        def responseAutoLead = quoteAutoLeadService.patchQuote(req, consentId, createdAutoLead.getClientId())

        then:
        noExceptionThrown()
        responseAutoLead.quoteId == createdAutoLead.quoteId
        responseAutoLead.consentId == consentId
        responseAutoLead.status == QuoteStatusEnum.CANC.toString()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
