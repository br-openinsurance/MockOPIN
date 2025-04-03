package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.TestEntityDataFactory
import com.raidiam.trustframework.mockinsurance.domain.QuoteFinancialRiskLeadEntity
import com.raidiam.trustframework.mockinsurance.models.generated.*
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class QuoteFinancialRiskLeadServiceSpec extends CleanupSpecification {

    @Inject
    QuoteFinancialRiskLeadService quoteFinancialRiskLeadService

    def "We can create a quote financial risk lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        QuoteFinancialRiskLeadEntity quoteFinancialLead = TestEntityDataFactory.aQuoteFinancialRiskLead(consentId)

        when:
        def createdQuoteFinancialLead = quoteFinancialRiskLeadService.createQuote(quoteFinancialLead)

        then:
        noExceptionThrown()
        createdQuoteFinancialLead != null
        createdQuoteFinancialLead.consentId == consentId
        createdQuoteFinancialLead.status == QuoteStatus.StatusEnum.RCVD.toString()
    }

    def "We can cancel a quote financial risk lead"() {
        given:
        def consentId = TestEntityDataFactory.aConsentId()
        def quoteFinancialLead = TestEntityDataFactory.aQuoteFinancialRiskLead(consentId)
        def createdQuoteFinancialLead = quoteFinancialRiskLeadService.createQuote(quoteFinancialLead)

        def req = new RevokeQuotePatchPayload()
                .data(new RevokeQuotePatchPayloadData()
                        .author(new RevokeQuotePatchPayloadDataAuthor()
                                .identificationNumber("123456789")
                                .identificationType(RevokeQuotePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                ))

        when:
        def responseFinancialLead = quoteFinancialRiskLeadService.patchQuote(req, consentId, createdQuoteFinancialLead.getClientId())

        then:
        noExceptionThrown()
        responseFinancialLead.quoteId == createdQuoteFinancialLead.quoteId
        responseFinancialLead.consentId == consentId
        responseFinancialLead.status == QuoteStatusEnum.CANC.toString()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}
