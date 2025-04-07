package com.raidiam.trustframework.mockinsurance

import com.raidiam.trustframework.mockinsurance.domain.*
import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.utils.PermissionGroup
import jakarta.inject.Singleton

import java.time.Duration
import java.time.Instant
import java.time.LocalDate;

@Singleton
class TestEntityDataFactory extends CleanupSpecification {

    static anAccountHolder (String identification, String rel) {
        def accountHolder = new AccountHolderEntity()
        accountHolder.setDocumentIdentification(identification)
        accountHolder.setDocumentRel(rel)
        accountHolder.setUserId('bob@test.com')
        accountHolder
    }

    static anAccountHolder () {
        long number = (long) Math.floor(Math.random() * 90000000000L) + 10000000000L
        anAccountHolder(Long.toString(number), "ABC")
    }

    static String aConsentId() {
        return "urn:mockin:" + UUID.randomUUID().toString();
    }

    static aConsent(UUID accountHolderId) {
        def consent = new ConsentEntity()
        consent.setExpirationDateTime(Date.from(Instant.now() + Duration.ofDays(10)))
        consent.setStatus(EnumConsentStatus.AWAITING_AUTHORISATION.toString())
        consent.setCreationDateTime(Date.from(Instant.now()))
        consent.setStatusUpdateDateTime(Date.from(Instant.now()))
        consent.setConsentId(aConsentId())
        consent.setAccountHolderId(accountHolderId)
        consent
    }

    static aConsent(UUID accountHolderId, String clientId) {
        def consent = new ConsentEntity()
        consent.setExpirationDateTime(Date.from(Instant.now() + Duration.ofDays(10)))
        consent.setStatus(EnumConsentStatus.AWAITING_AUTHORISATION.toString())
        consent.setCreationDateTime(Date.from(Instant.now()))
        consent.setStatusUpdateDateTime(Date.from(Instant.now()))
        consent.setConsentId(aConsentId())
        consent.setAccountHolderId(accountHolderId)
        consent.setClientId(clientId)
        consent.setPermissions(PermissionGroup.PERSONAL_REGISTRATION.getPermissions().stream().map {p -> p.toString()}.toList())
        consent
    }

    static QuotePatrimonialLeadEntity aQuotePatrimonialLead(String consentId) {
        aQuotePatrimonialLead(null, consentId)
    }

    static QuotePatrimonialLeadEntity aQuotePatrimonialLead(UUID quoteId, String consentId) {
        QuotePatrimonialLeadEntity quote = new QuotePatrimonialLeadEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "123456789"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote
    }

    static QuotePatrimonialBusinessEntity aQuotePatrimonialBusiness(String consentId) {
        aQuotePatrimonialBusiness(null, consentId)
    }

    static QuotePatrimonialBusinessEntity aQuotePatrimonialBusiness(UUID quoteId, String consentId) {
        QuotePatrimonialBusinessEntity quote = new QuotePatrimonialBusinessEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.data = new QuotePatrimonialBusinessData()
        quote.personCpf = "random_cpf"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote.data = new QuotePatrimonialBusinessData()
                .quoteData(new QuoteDataPatrimonialBusiness().policyId("random_policy_id")
                        .maxLMG(new AmountDetails()
                                .amount("1000")
                                .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                                .unit(new AmountDetailsUnit()
                                        .code("code")
                                        .description(AmountDetailsUnit.DescriptionEnum.ADP))
                                .unitTypeOthers("unitTypeOthers"))
                )
        quote
    }

    static QuotePatrimonialHomeEntity aQuotePatrimonialHome(String consentId) {
        aQuotePatrimonialHome(null, consentId)
    }

    static QuotePatrimonialHomeEntity aQuotePatrimonialHome(UUID quoteId, String consentId) {
        QuotePatrimonialHomeEntity quote = new QuotePatrimonialHomeEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.data = new QuotePatrimonialHomeData()
        quote.personCpf = "random_cpf"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote.data = new QuotePatrimonialHomeData()
                .quoteData(new QuoteDataPatrimonialHome().policyId("random_policy_id")
                        .maxLMG(new AmountDetails()
                                .amount("1000")
                                .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                                .unit(new AmountDetailsUnit()
                                        .code("code")
                                        .description(AmountDetailsUnit.DescriptionEnum.ADP))
                                .unitTypeOthers("unitTypeOthers"))
                )
        quote
    }

    static QuotePatrimonialCondominiumEntity aQuotePatrimonialCondominium(String consentId) {
        aQuotePatrimonialCondominium(null, consentId)
    }

    static QuotePatrimonialCondominiumEntity aQuotePatrimonialCondominium(UUID quoteId, String consentId) {
        QuotePatrimonialCondominiumEntity quote = new QuotePatrimonialCondominiumEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.data = new QuotePatrimonialCondominiumData()
        quote.personCpf = "random_cpf"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote.data = new QuotePatrimonialCondominiumData()
                .quoteData(new QuoteDataPatrimonialCondominium().policyId("random_policy_id")
                        .maxLMG(new AmountDetails()
                                .amount("1000")
                                .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                                .unit(new AmountDetailsUnit()
                                        .code("code")
                                        .description(AmountDetailsUnit.DescriptionEnum.ADP))
                                .unitTypeOthers("unitTypeOthers"))
                )
        quote
    }

    static QuotePatrimonialDiverseRisksEntity aQuotePatrimonialDiverseRisks(String consentId) {
        aQuotePatrimonialDiverseRisks(null, consentId)
    }

    static QuotePatrimonialDiverseRisksEntity aQuotePatrimonialDiverseRisks(UUID quoteId, String consentId) {
        QuotePatrimonialDiverseRisksEntity quote = new QuotePatrimonialDiverseRisksEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.data = new QuotePatrimonialDiverseRisksData()
        quote.personCpf = "random_cpf"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote.data = new QuotePatrimonialDiverseRisksData()
                .quoteData(new QuoteDataPatrimonialDiverseRisks().policyId("random_policy_id")
                        .maxLMG(new AmountDetails()
                                .amount("1000")
                                .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                                .unit(new AmountDetailsUnit()
                                        .code("code")
                                        .description(AmountDetailsUnit.DescriptionEnum.ADP))
                                .unitTypeOthers("unitTypeOthers"))
                )
        quote
    }

    static aWebhook() {
        aWebhook(UUID.randomUUID().toString())
    }

    static aWebhook(String clientId) {
        def webhook = new WebhookEntity()
        webhook.clientId = clientId
        webhook.webhookUri = "https://webhook"
        webhook
    }

    static QuoteFinancialRiskLeadEntity aQuoteFinancialRiskLead(String consentId) {
        aQuoteFinancialRiskLead(null, consentId)
    }

    static QuoteFinancialRiskLeadEntity aQuoteFinancialRiskLead(UUID quoteId, String consentId) {
        QuoteFinancialRiskLeadEntity quote = new QuoteFinancialRiskLeadEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "123456789"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote
    }

    static QuoteAcceptanceAndBranchesAbroadLeadEntity aQuoteAcceptanceAndBranchesAbroadLead(String consentId) {
        aQuoteAcceptanceAndBranchesAbroadLead(null, consentId)
    }

    static QuoteAcceptanceAndBranchesAbroadLeadEntity aQuoteAcceptanceAndBranchesAbroadLead(UUID quoteId, String consentId) {
        QuoteAcceptanceAndBranchesAbroadLeadEntity quote = new QuoteAcceptanceAndBranchesAbroadLeadEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "123456789"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote
    }
    static QuoteHousingLeadEntity aQuoteHousingLead(String consentId) {
        aQuoteHousingLead(null, consentId)
    }

    static QuoteHousingLeadEntity aQuoteHousingLead(UUID quoteId, String consentId) {
        QuoteHousingLeadEntity quote = new QuoteHousingLeadEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "123456789"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote
    }


    static QuoteLifePensionLeadEntity aQuoteLifePensionLead(String consentId) {
        aQuoteLifePensionLead(null, consentId)
    }

    static QuoteLifePensionLeadEntity aQuoteLifePensionLead(UUID quoteId, String consentId) {
        QuoteLifePensionLeadEntity quote = new QuoteLifePensionLeadEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "123456789"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote
    }

    static QuoteLifePensionEntity aQuoteLifePension(String consentId) {
        aQuoteLifePension(null, consentId)
    }

    static QuoteLifePensionEntity aQuoteLifePension(UUID quoteId, String consentId) {

        QuoteLifePensionEntity quote = new QuoteLifePensionEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "random_cpf"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()

        QuoteDataProductLifePension products = new QuoteDataProductLifePension()
        QuoteDataProductLifePensionInner product = new QuoteDataProductLifePensionInner()
        AllOfQuoteDataProductLifePensionInnerInitialContribution initial =
                new AllOfQuoteDataProductLifePensionInnerInitialContribution()

        initial.setAmount("2000")
        initial.setUnitType(AmountDetails.UnitTypeEnum.MONETARIO)

        product.setInitialContribution(initial)
        product.setIsContributeMonthly(true)
        product.setPlanType(QuoteDataProductLifePensionInner.PlanTypeEnum.PGBL)
        products.add(product)

        QuoteDataComplementaryIdentificationLifePension complementaryId =
                new QuoteDataComplementaryIdentificationLifePension()
        complementaryId.setIsNewPlanHolder(true)
        complementaryId.setIsIntendedForMinor(true)

        QuoteDataLifePension quoteDataLifePension = new QuoteDataLifePension()
        quoteDataLifePension.setPensionRiskCoverage(true)
        quoteDataLifePension.setIsPortabilityHiringQuote(true)
        quoteDataLifePension.setPortabilityPensionIdentifications(["123456"])
        quoteDataLifePension.setComplementaryIdentification(complementaryId)
        quoteDataLifePension.setProducts(products)

        quote.data = new RequestContractLifePensionData()
        quote.data.setQuoteData(quoteDataLifePension)

        return quote
    }

    static QuoteResponsibilityLeadEntity aQuoteResponsibilityLead(String consentId) {
        aQuoteResponsibilityLead(null, consentId)
    }

    static QuoteResponsibilityLeadEntity aQuoteResponsibilityLead(UUID quoteId, String consentId) {
        QuoteResponsibilityLeadEntity quote = new QuoteResponsibilityLeadEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "123456789"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote
    }

    static QuoteTransportLeadEntity aQuoteTransportLead(String consentId) {
        aQuoteTransportLead(null, consentId)
    }

    static QuoteTransportLeadEntity aQuoteTransportLead(UUID quoteId, String consentId) {
        QuoteTransportLeadEntity quote = new QuoteTransportLeadEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "123456789"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote
    }

    static QuoteRuralLeadEntity aQuoteRuralLead(String consentId) {
        aQuoteRuralLead(null, consentId)
    }

    static QuoteRuralLeadEntity aQuoteRuralLead(UUID quoteId, String consentId) {
        QuoteRuralLeadEntity quote = new QuoteRuralLeadEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "123456789"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote
    }

    static QuoteAutoLeadEntity aQuoteAutoLead(String consentId) {
        aQuoteAutoLead(null, consentId)
    }

    static QuoteAutoLeadEntity aQuoteAutoLead(UUID quoteId, String consentId) {
        QuoteAutoLeadEntity quote = new QuoteAutoLeadEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "123456789"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote
    }

    static QuoteAutoEntity aQuoteAuto(String consentId) {
        aQuoteAuto(null, consentId)
    }

    static QuoteAutoEntity aQuoteAuto(UUID quoteId, String consentId) {
        QuoteAutoEntity quote = new QuoteAutoEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "123456789"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote.data = new QuoteAutoData()
                .quoteData(new QuoteDataAuto()
                        .policyId("random_policy_id")
                        .termStartDate(LocalDate.now().minusYears(1))
                        .termEndDate(LocalDate.now().plusYears(1))
                )
        quote
    }
}
