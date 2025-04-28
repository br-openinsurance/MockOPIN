package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.QuoteCapitalizationTitleLeadEntity
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatus
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatusEnum
import com.raidiam.trustframework.mockinsurance.models.generated.RevokeQuotePatchPayload
import com.raidiam.trustframework.mockinsurance.models.generated.RevokeQuotePatchPayloadData
import com.raidiam.trustframework.mockinsurance.models.generated.RevokeQuotePatchPayloadDataAuthor
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
public class QuoteCapitalizationTitleLeadServiceSpec extends CleanupSpecification {

    @Inject
    QuoteCapitalizationTitleLeadService quoteCapitalizationTitleLeadService

    def "We can create a quote capitalization title lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        QuoteCapitalizationTitleLeadEntity createdCapitalizationTitleLead = TestEntityDataFactory.aQuoteCapitalizationTitleLead(consentId)

        when:
        def responseCapitalizationTitleLead = quoteCapitalizationTitleLeadService.createQuote(createdCapitalizationTitleLead)

        then:
        noExceptionThrown()
        responseCapitalizationTitleLead != null
        responseCapitalizationTitleLead.consentId == consentId
        responseCapitalizationTitleLead.status == QuoteStatus.StatusEnum.RCVD.toString()
    }

    def "We can cancel a quote capitalization title lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def createdCapitalizationTitleLead = TestEntityDataFactory.aQuoteCapitalizationTitleLead(consentId)
        createdCapitalizationTitleLead = quoteCapitalizationTitleLeadService.createQuote(createdCapitalizationTitleLead)

        def req = new RevokeQuotePatchPayload()
                .data(new RevokeQuotePatchPayloadData()
                        .author(new RevokeQuotePatchPayloadDataAuthor()
                                .identificationNumber("123456789")
                                .identificationType(RevokeQuotePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                ))

        when:
        def responseCapitalizationTitleLead = quoteCapitalizationTitleLeadService.patchQuote(req, consentId, createdCapitalizationTitleLead.getClientId())

        then:
        noExceptionThrown()
        responseCapitalizationTitleLead.quoteId == createdCapitalizationTitleLead.quoteId
        responseCapitalizationTitleLead.consentId == consentId
        responseCapitalizationTitleLead.status == QuoteStatusEnum.CANC.toString()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
