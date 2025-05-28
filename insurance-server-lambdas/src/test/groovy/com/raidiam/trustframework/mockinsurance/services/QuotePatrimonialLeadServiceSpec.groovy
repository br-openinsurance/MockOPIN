package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.QuotePatrimonialLeadEntity
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
class QuotePatrimonialLeadServiceSpec extends CleanupSpecification {

    @Inject
    QuotePatrimonialLeadService quotePatrimonialLeadService

    def "We can create a quote patrimonial lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        QuotePatrimonialLeadEntity createdPatrimonialLead = TestEntityDataFactory.aQuotePatrimonialLead(consentId)

        when:
        def responsePatrimonialLead = quotePatrimonialLeadService.createQuote(createdPatrimonialLead)

        then:
        noExceptionThrown()
        responsePatrimonialLead != null
        responsePatrimonialLead.consentId == consentId
        responsePatrimonialLead.status == QuoteStatus.StatusEnum.RCVD.toString()
    }

    def "We can cancel a quote patrimonial lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def createdPatrimonialLead = TestEntityDataFactory.aQuotePatrimonialLead(consentId)
        createdPatrimonialLead = quotePatrimonialLeadService.createQuote(createdPatrimonialLead)

        def req = new RevokePatchPayload()
                .data(new RevokePatchPayloadData()
                        .author(new RevokePatchPayloadDataAuthor()
                                .identificationNumber("123456789")
                                .identificationType(RevokePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                ))

        when:
        def responsePatrimonialLead = quotePatrimonialLeadService.patchQuote(req, consentId, createdPatrimonialLead.getClientId())

        then:
        noExceptionThrown()
        responsePatrimonialLead.quoteId == createdPatrimonialLead.quoteId
        responsePatrimonialLead.consentId == consentId
        responsePatrimonialLead.status == QuoteStatusEnum.CANC.toString()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
