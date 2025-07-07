package com.raidiam.trustframework.mockinsurance

import com.raidiam.trustframework.mockinsurance.domain.*
import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.utils.PermissionGroup
import jakarta.inject.Singleton

import java.time.Duration
import java.time.Instant
import java.time.LocalDate

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
        return "urn:mockin:" + UUID.randomUUID().toString()
    }

    static aConsent(UUID accountHolderId, EnumConsentPermission... permissions) {
        def consent = new ConsentEntity()
        consent.setExpirationDateTime(Date.from(Instant.now() + Duration.ofDays(10)))
        consent.setStatus(EnumConsentStatus.AWAITING_AUTHORISATION.toString())
        consent.setCreationDateTime(Date.from(Instant.now()))
        consent.setStatusUpdateDateTime(Date.from(Instant.now()))
        consent.setConsentId(aConsentId())
        consent.setAccountHolderId(accountHolderId)
        if (permissions != null) {
            consent.setPermissions(Arrays.asList(permissions).stream().map {p -> p.name()}.toList())
        }
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

    static anEndorsement(String consentId){
        def endorsement = new EndorsementEntity()
        endorsement.setEndorsementId()
        endorsement.setConsentId(consentId)
        endorsement.setClientId("random_client_id")
        endorsement.setData(new CreateEndorsementData()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
            .requestDate(LocalDate.now().minusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        )
        endorsement
    }
  
    static ClaimNotificationDamageEntity aClaimNotificationDamage(String consentId) {
        aClaimNotificationDamage(null, null, consentId)
    }

    static ClaimNotificationDamageEntity aClaimNotificationDamage(String claimId, String consentId) {
        aClaimNotificationDamage(null, claimId, consentId)
    }

    static ClaimNotificationDamageEntity aClaimNotificationDamage(UUID claimId, String clientId, String consentId) {
        ClaimNotificationDamageEntity claim = new ClaimNotificationDamageEntity()
        ClaimNotificationDamageData data = new ClaimNotificationDamageData()
        data.setDocumentType(ClaimNotificationData.DocumentTypeEnum.CERTIFICADO)
        data.setPolicyId("111111")
        data.setGroupCertificateId("string")
        data.setInsuredObjectId(List.of("216731531723"))
        data.setOccurrenceDate(LocalDate.of(2022, 1, 1))
        data.setOccurrenceTime("22:18:54")
        data.setOccurrenceDescription("string")
        claim.setData(data)
        claim.setConsentId(consentId)
        claim.setClaimId(claimId)
        claim.setClientId(clientId)
        claim
    }

    static ClaimNotificationPersonEntity aClaimNotificationPerson(String consentId) {
        aClaimNotificationPerson(null, null, consentId)
    }

    static ClaimNotificationPersonEntity aClaimNotificationPerson(String claimId, String consentId) {
        aClaimNotificationPerson(null, claimId, consentId)
    }

    static ClaimNotificationPersonEntity aClaimNotificationPerson(UUID claimId, String clientId, String consentId) {
        ClaimNotificationPersonEntity claim = new ClaimNotificationPersonEntity()
        ClaimNotificationPersonData data = new ClaimNotificationPersonData()
        data.setDocumentType(ClaimNotificationData.DocumentTypeEnum.CERTIFICADO)
        data.setPolicyId("111111")
        data.setGroupCertificateId("string")
        data.setInsuredObjectId(List.of("216731531723"))
        data.setOccurrenceDate(LocalDate.of(2022, 1, 1))
        data.setOccurrenceTime("22:18:54")
        data.setOccurrenceDescription("string")
        claim.setData(data)
        claim.setConsentId(consentId)
        claim.setClaimId(claimId)
        claim.setClientId(clientId)
        claim
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


    static QuotePersonLeadEntity aQuotePersonLead(String consentId) {
        aQuotePersonLead(null, consentId)
    }

    static QuotePersonLeadEntity aQuotePersonLead(UUID quoteId, String consentId) {
        QuotePersonLeadEntity quote = new QuotePersonLeadEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "123456789"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote
    }

    static QuotePersonLifeEntity aQuotePersonLife(String consentId) {
        aQuotePersonLife(null, consentId)
    }

    static QuotePersonLifeEntity aQuotePersonLife(UUID quoteId, String consentId) {

        QuotePersonLifeEntity quote = new QuotePersonLifeEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "random_cpf"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()

        QuoteDataPersonLife quoteDataPersonLife = new QuoteDataPersonLife()

        quote.data = new QuoteRequestPersonLifeData()
        quote.data.setQuoteData(quoteDataPersonLife)

        return quote
    }

    static QuotePersonTravelEntity aQuotePersonTravel(String consentId) {
        aQuotePersonTravel(null, consentId)
    }

    static QuotePersonTravelEntity aQuotePersonTravel(UUID quoteId, String consentId) {

        QuotePersonTravelEntity quote = new QuotePersonTravelEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "random_cpf"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()

        QuoteDataPersonTravel quoteDataPersonTravel = new QuoteDataPersonTravel()

        quote.data = new QuoteRequestPersonTravelData()
        quote.data.setQuoteData(quoteDataPersonTravel)

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

    static QuoteCapitalizationTitleLeadEntity aQuoteCapitalizationTitleLead(String consentId) {
        aQuoteCapitalizationTitleLead(null, consentId)
    }

    static QuoteCapitalizationTitleLeadEntity aQuoteCapitalizationTitleLead(UUID quoteId, String consentId) {
        QuoteCapitalizationTitleLeadEntity quote = new QuoteCapitalizationTitleLeadEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "123456789"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote
    }

    static QuoteCapitalizationTitleEntity aQuoteCapitalizationTitle(String consentId) {
        aQuoteCapitalizationTitle(null, consentId)
    }

    static QuoteCapitalizationTitleEntity aQuoteCapitalizationTitle(UUID quoteId, String consentId) {
        QuoteCapitalizationTitleEntity quote = new QuoteCapitalizationTitleEntity()
        quote.quoteId = quoteId
        quote.consentId = consentId
        quote.status = QuoteStatusEnum.RCVD.toString()
        quote.personCpf = "123456789"
        quote.clientId = "random_client_id"
        quote.expirationDateTime = new Date()
        quote.data = new QuoteCapitalizationTitleData()
                .quoteData(new QuoteDataCapitalizationTitle()
                        .paymentType(QuoteDataCapitalizationTitle.PaymentTypeEnum.MENSAL)
                        .monthlyPayment(new AmountDetails()
                            .unitType(AmountDetails.UnitTypeEnum.PORCENTAGEM)
                            .amount("100")
                        )
                )
        quote
    }

    static CapitalizationTitleRaffleEntity aCapitalizationTitleRaffle() {
        aCapitalizationTitleRaffle(null)
    }

    static CapitalizationTitleRaffleEntity aCapitalizationTitleRaffle(UUID raffleId) {
        CapitalizationTitleRaffleEntity raffle = new CapitalizationTitleRaffleEntity()
        raffle.raffleId = raffleId
        raffle.clientId = "random_client_id"
        raffle.data = new RequestCapitalizationTitleRaffleData()
            .modality(RequestCapitalizationTitleRaffleData.ModalityEnum.TRADICIONAL)
            .susepProcessNumber("random_process_number")
            .raffleCustomData(new RaffleCustomData())
        raffle
    }

    static BusinessIdentificationEntity aBusinessIdentification(UUID accountHolderId, String cnpj) {
        BusinessIdentificationEntity entity = new BusinessIdentificationEntity()
        entity.setAccountHolderId(accountHolderId)
        entity.setCnpjNumber(cnpj)
        entity
    }

    static BusinessQualificationEntity aBusinessQualification(UUID accountHolderId) {
        BusinessQualificationEntity entity = new BusinessQualificationEntity()
        entity.setAccountHolderId(accountHolderId)
        entity
    }

    static PersonalIdentificationEntity aPersonalIdentification(UUID accountHolderId) {
        PersonalIdentificationEntity entity = new PersonalIdentificationEntity()
        entity.setAccountHolderId(accountHolderId)
        entity
    }

    static PersonalQualificationEntity aPersonalQualification(UUID accountHolderId) {
        PersonalQualificationEntity entity = new PersonalQualificationEntity()
        entity.setAccountHolderId(accountHolderId)
        entity
    }

    static aCapitalizationTitlePlan(UUID accountHolderId) {
        def plan = new CapitalizationTitlePlanEntity()
        plan.setAccountHolderId(accountHolderId)
        plan.setCapitalizationTitleId("random_capitalization_title")
        plan.setProductName("Test Capitalization Title Plan")
        plan
    }

    static aCapitalizationTitlePlanEvent(UUID planId, String eventType = 'SORTEIO') {
        def event = new CapitalizationTitlePlanEventEntity()
        event.setCapitalizationTitlePlanId(planId)
        event.setEventType(eventType)
        event.setRaffleDate(LocalDate.of(2023, 01, 30))
        event.setRaffleSettlementDate(LocalDate.of(2023, 01, 30))
        event.setRaffleAmount("8")
        event.setRaffleUnitType(AmountDetails.UnitTypeEnum.PORCENTAGEM.toString())
        event.setRaffleUnitTypeOthers("Horas")
        event.setRaffleUnitCode("R\$")
        event.setRaffleUnitDescription("BRL")
        event.setRaffleCurrency("BRL")
        event
    }

    static aCapitalizationTitlePlanSettlement(UUID planId) {
        def settlement = new CapitalizationTitlePlanSettlementEntity()
        settlement.setCapitalizationTitlePlanId(planId)
        settlement.setSettlementDueDate(LocalDate.of(2023, 01, 30))
        settlement.setSettlementPaymentDate(LocalDate.of(2023, 01, 30))
        settlement.setSettlementFinancialAmount("370")
        settlement.setSettlementFinancialUnitType(AmountDetails.UnitTypeEnum.PORCENTAGEM.toString())
        settlement.setSettlementFinancialUnitTypeOthers("Horas")
        settlement.setSettlementFinancialUnitCode("R\$")
        settlement.setSettlementFinancialUnitDescription("BRL")
        settlement.setSettlementFinancialCurrency("BRL")
        settlement
    }

    static aCapitalizationPlanSeries(UUID planId) {
        def series = new CapitalizationTitlePlanSeriesEntity()
        series.setCapitalizationTitlePlanId(planId)
        series.setModality("TRADICIONAL")
        series.setSusepProcessNumber("15414622222222222")
        series.setCommercialDenomination("Denominação comercial do produto")
        series.setSerieSize(5000000)
        series.setGracePeriodRedemption(48)
        series.setGracePeriodForFullRedemption(48)
        series.setUpdateIndex("IPCA")
        series.setUpdateIndexOthers("Índice de atualização Outros")
        series.setReadjustmentIndex("IPCA")
        series.setReadjustmentIndexOthers("Índice de atualização Outros")
        series.setBonusClause(false)
        series.setFrequency("PAGAMENTO_MENSAL")
        series.setFrequencyDescription("string")
        series.setInterestRate("10.00")
        series
    }

    static aCapitalizationPlanBroker(UUID seriesId) {
        def broker = new CapitalizationTitlePlanBrokerEntity()
        broker.setCapitalizationTitlePlanSeriesId(seriesId)
        broker.setBrokerDescription("string")
        broker.setSusepBrokerCode("123123123")
        broker
    }

    static aCapitalizationTitlePlanQuota(UUID seriesId) {
        def quota = new CapitalizationTitlePlanQuotaEntity()
        quota.setCapitalizationTitlePlanSeriesId(seriesId)
        quota.setQuota(10)
        quota.setCapitalizationQuota("0.000002")
        quota.setRaffleQuota("0.000002")
        quota.setChargingQuota("0.000002")
        quota
    }

    static aCapitalizationTitlePlanTitle(UUID seriesId) {
        def title = new CapitalizationTitlePlanTitleEntity()
        title.setCapitalizationTitlePlanSeriesId(seriesId)
        title.setRegistrationForm("string")
        title.setIssueTitleDate(LocalDate.of(2023, 01, 30))
        title.setTermStartDate(LocalDate.of(2023, 01, 30))
        title.setTermEndDate(LocalDate.of(2023, 01, 30))
        title.setRafflePremiumAmount("8")
        title.setRafflePremiumUnitType(AmountDetails.UnitTypeEnum.PORCENTAGEM.toString())
        title.setRafflePremiumUnitTypeOthers("Horas")
        title.setRafflePremiumUnitCode("R\$")
        title.setRafflePremiumUnitDescription("BRL")
        title.setRafflePremiumCurrency("BRL")
        title.setContributionAmount("370")
        title.setContributionUnitType(AmountDetails.UnitTypeEnum.PORCENTAGEM.toString())
        title.setContributionUnitTypeOthers("Horas")
        title.setContributionUnitCode("R\$")
        title.setContributionUnitDescription("BRL")
        title.setContributionCurrency("BRL")
        title
    }

    static aCapitalizationTitlePlanSubscriber(UUID titleId) {
        def subscriber = new CapitalizationTitlePlanSubscriberEntity()
        subscriber.setCapitalizationTitlePlanTitleId(titleId)
        subscriber.setSubscriberName("Nome do Subscritor")
        subscriber.setSubscriberDocumentType("OUTROS")
        subscriber.setSubscriberDocumentTypeOthers("string")
        subscriber.setSubscriberDocumentNumber("string")
        subscriber.setSubscriberAddress("Av Naburo Ykesaki, 1270")
        subscriber.setSubscriberAddressAdditionalInfo("Fundos")
        subscriber.setSubscriberTownName("Rio de Janeiro")
        subscriber.setSubscriberCountrySubDivision("RJ")
        subscriber.setSubscriberCountryCode("BRA")
        subscriber.setSubscriberPostcode("17500001")
        subscriber.setSubscriberCountryCallingCode("55")
        subscriber.setSubscriberAreaCode("11")
        subscriber.setSubscriberNumber("29875132")
        subscriber
    }

    static aCapitalizationTitlePlanHolder(UUID subscriberId) {
        def holder = new CapitalizationTitlePlanHolderEntity()
        holder.setCapitalizationTitleSubscriberId(subscriberId)
        holder.setHolderName("Nome do Titular")
        holder.setHolderDocumentType("OUTROS")
        holder.setHolderDocumentTypeOthers("string")
        holder.setHolderDocumentNumber("string")
        holder.setHolderAddress("Av Naburo Ykesaki, 1270")
        holder.setHolderAddressAdditionalInfo("Fundos")
        holder.setHolderTownName("Rio de Janeiro")
        holder.setHolderCountrySubdivision("RJ")
        holder.setHolderCountryCode("BRA")
        holder.setHolderPostcode("17500001")
        holder.setHolderRedemption(false)
        holder.setHolderRaffle(false)
        holder.setHolderCountryCallingCode("55")
        holder.setHolderAreaCode("11")
        holder.setHolderNumber("29875132")
        holder
    }

    static aCapitalizationTitlePlanTechnicalProvisions(UUID titleId) {
        def technicalProvisions = new CapitalizationTitlePlanTechnicalProvisionsEntity()
        technicalProvisions.setCapitalizationTitlePlanTitleId(titleId)
        technicalProvisions.setPmcAmount("100.00")
        technicalProvisions.setPmcUnitType(AmountDetails.UnitTypeEnum.PORCENTAGEM.toString())
        technicalProvisions.setPmcUnitTypeOthers("Horas")
        technicalProvisions.setPmcUnitCode("R\$")
        technicalProvisions.setPmcUnitDescription("BRL")
        technicalProvisions.setPmcCurrency("BRL")
        technicalProvisions.setPdbAmount("100.00")
        technicalProvisions.setPdbUnitType(AmountDetails.UnitTypeEnum.PORCENTAGEM.toString())
        technicalProvisions.setPdbUnitTypeOthers("Horas")
        technicalProvisions.setPdbUnitCode("R\$")
        technicalProvisions.setPdbUnitDescription("BRL")
        technicalProvisions.setPdbCurrency("BRL")
        technicalProvisions.setPrAmount("100.00")
        technicalProvisions.setPrUnitType(AmountDetails.UnitTypeEnum.PORCENTAGEM.toString())
        technicalProvisions.setPrUnitTypeOthers("Horas")
        technicalProvisions.setPrUnitCode("R\$")
        technicalProvisions.setPrUnitDescription("BRL")
        technicalProvisions.setPrCurrency("BRL")
        technicalProvisions.setPspAmount("100.00")
        technicalProvisions.setPspUnitType(AmountDetails.UnitTypeEnum.PORCENTAGEM.toString())
        technicalProvisions.setPspUnitTypeOthers("Horas")
        technicalProvisions.setPspUnitCode("R\$")
        technicalProvisions.setPspUnitDescription("BRL")
        technicalProvisions.setPspCurrency("BRL")
        technicalProvisions
    }

    static aFinancialRiskPolicy(UUID accountHolderId, List<UUID> insuredIds, List<UUID> beneficiaryIds,
                                List<UUID> principalIds, List<UUID> intermediaryIds, List<UUID> coinsurerIds) {
        def contract = new FinancialRiskPolicyEntity()
        contract.setAccountHolderId(accountHolderId)
        contract.setFinancialRiskId("random_financial_risk")
        contract.setProductName("Produto Exemplo")
        contract.setDocumentType(InsuranceFinancialRiskPolicyInfoData.DocumentTypeEnum.APOLICE_INDIVIDUAL.toString())
        contract.setSusepProcessNumber("string")
        contract.setGroupCertificateId("string")
        contract.setIssuanceType(InsuranceFinancialRiskPolicyInfoData.IssuanceTypeEnum.EMISSAO_PROPRIA.toString())
        contract.setIssuanceDate(LocalDate.of(2023, 01, 30))
        contract.setTermStartDate(LocalDate.of(2023, 01, 30))
        contract.setTermEndDate(LocalDate.of(2023, 01, 30))
        contract.setLeadInsurerCode("string")
        contract.setLeadInsurerPolicyId("string")
        contract.setMaxLMGAmount("27.90")
        contract.setMaxLMGUnitType(AmountDetails.UnitTypeEnum.PORCENTAGEM.toString())
        contract.setMaxLMGUnitTypeOthers("Horas")
        contract.setMaxLMGUnitCode("R\$")
        contract.setMaxLMGUnitDescription(AmountDetailsUnit.DescriptionEnum.BRL.toString())
        contract.setProposalId("string")
        contract.setBranchInfoIdentification("string")
        contract.setBranchInfoUserGroup("string")
        contract.setBranchInfoTechnicalSurplus("10.00")
        contract.setInsuredIds(insuredIds)
        contract.setBeneficiaryIds(beneficiaryIds)
        contract.setPrincipalIds(principalIds)
        contract.setIntermediaryIds(intermediaryIds)
        contract.setCoinsurerIds(coinsurerIds)
        contract
    }

    static aFinancialRiskPolicyInsured() {
        def insured = new PersonalInfoEntity()
        insured.setIdentification("12345678900")
        insured.setIdentificationType("CPF")
        insured.setIdentificationTypeOthers("RNE")
        insured.setName("Nome Sobrenome")
        insured.setBirthDate(LocalDate.of(1999, 06, 12))
        insured.setPostCode("10000000")
        insured.setEmail("string")
        insured.setCity("string")
        insured.setState("AC")
        insured.setCountry("BRA")
        insured.setAddress("string")
        insured
    }

    static aFinancialRiskPolicyBeneficiary() {
        def beneficiary = new BeneficiaryInfoEntity()
        beneficiary.setIdentification("12345678900")
        beneficiary.setIdentificationType("CPF")
        beneficiary.setIdentificationTypeOthers("RNE")
        beneficiary.setName("Nome Sobrenome")
        beneficiary
    }

    static aFinancialRiskPolicyPrincipal() {
        def principal = new PrincipalInfoEntity()
        principal.setIdentification("12345678900")
        principal.setIdentificationType("CPF")
        principal.setIdentificationTypeOthers("RNE")
        principal.setName("Nome Sobrenome")
        principal.setPostCode("10000000")
        principal.setEmail("string")
        principal.setCity("string")
        principal.setState("AC")
        principal.setCountry("BRA")
        principal.setAddress("string")
        principal
    }

    static aFinancialRiskPolicyIntermediary() {
        def intermediary = new IntermediaryEntity()
        intermediary.setIdentification("12345678900")
        intermediary.setIdentificationType("CPF")
        intermediary.setIdentificationTypeOthers("RNE")
        intermediary.setName("Nome Sobrenome")
        intermediary.setPostCode("10000000")
        intermediary.setCity("string")
        intermediary.setState("AC")
        intermediary.setCountry("BRA")
        intermediary.setAddress("string")
        intermediary
    }

    static aFinancialRiskPolicyInsuredObject(UUID planId) {
        def insuredObject = new FinancialRiskPolicyInsuredObjectEntity()
        insuredObject.setFinancialRiskPolicyId(planId)
        insuredObject.setIdentification("string")
        insuredObject.setType("CONTRATO")
        insuredObject.setTypeAdditionalInfo("string")
        insuredObject.setDescription("string")
        insuredObject.setAmount("751")
        insuredObject.setUnitType("PORCENTAGEM")
        insuredObject.setUnitTypeOthers("Horas")
        insuredObject.setUnitCode("R\$")
        insuredObject.setUnitDescription("BRL")
        insuredObject
    }

    static aFinancialRiskPolicyInsuredObjectCoverage(UUID insuredObjectId) {
        def coverage = new FinancialRiskPolicyInsuredObjectCoverageEntity()
        coverage.setFinancialRiskInsuredObjectId(insuredObjectId)
        coverage.setBranch("0111")
        coverage.setCode("PROTECAO_DE_BENS")
        coverage.setDescription("string")
        coverage.setInternalCode("string")
        coverage.setSusepProcessNumber("string")
        coverage.setLmiAmount("947556")
        coverage.setLmiUnitType("PORCENTAGEM")
        coverage.setLmiUnitTypeOthers("Horas")
        coverage.setLmiUnitCode("R\$")
        coverage.setLmiUnitDescription("BRL")
        coverage.setIsLMISublimit(true)
        coverage.setTermStartDate(LocalDate.of(2023, 01, 30))
        coverage.setTermEndDate(LocalDate.of(2023, 01, 30))
        coverage.setIsMainCoverage(true)
        coverage.setFeature("MASSIFICADOS")
        coverage.setType("PARAMETRICO")
        coverage.setGracePeriod(0)
        coverage.setGracePeriodicity("DIA")
        coverage.setGracePeriodCountingMethod("DIAS_UTEIS")
        coverage.setGracePeriodStartDate(LocalDate.of(2023, 01, 30))
        coverage.setGracePeriodEndDate(LocalDate.of(2023, 01, 30))
        coverage.setPremiumPeriodicity("MENSAL")
        coverage.setPremiumPeriodicityOthers("string")
        coverage
    }

    static aFinancialRiskPolicyCoverage(UUID planId, UUID deductibleId, UUID posId) {
        def coverage = new FinancialRiskPolicyCoverageEntity()
        coverage.setFinancialRiskPolicyId(planId)
        coverage.setDeductibleId(deductibleId)
        coverage.setPOSId(posId)
        coverage.setBranch("0111")
        coverage.setCode("PROTECAO_DE_BENS")
        coverage.setDescription("string")
        coverage
    }

    static aFinancialRiskPolicyCoverageDeductible() {
        def deductible = new DeductibleEntity()
        deductible.setType("DEDUTIVEL")
        deductible.setTypeAdditionalInfo("string")
        deductible.setAmount("692372.38")
        deductible.setUnitType("PORCENTAGEM")
        deductible.setUnitTypeOthers("Horas")
        deductible.setUnitCode("R\$")
        deductible.setUnitDescription("BRL")
        deductible.setPeriod(10)
        deductible.setPeriodicity("DIA")
        deductible.setPeriodCountingMethod("DIAS_UTEIS")
        deductible.setPeriodStartDate(LocalDate.of(2023, 01, 30))
        deductible.setPeriodEndDate(LocalDate.of(2023, 01, 31))
        deductible.setDescription("string")
        deductible
    }

    static aFinancialRiskPolicyCoveragePos() {
        def pos = new POSEntity()
        pos.setApplicationType("VALOR")
        pos.setDescription("string")
        pos.setMinValueAmount("01")
        pos.setMinValueUnitType("PORCENTAGEM")
        pos.setMinValueUnitTypeOthers("Horas")
        pos.setMinValueUnitCode("R\$")
        pos.setMinValueUnitDescription("BRL")
        pos.setMaxValueAmount("06.26")
        pos.setMaxValueUnitType("PORCENTAGEM")
        pos.setMaxValueUnitTypeOthers("Horas")
        pos.setMaxValueUnitCode("R\$")
        pos.setMaxValueUnitDescription("BRL")
        pos.setPercentageAmount("5.12")
        pos.setPercentageUnitType("PORCENTAGEM")
        pos.setPercentageUnitTypeOthers("Horas")
        pos.setPercentageUnitCode("R\$")
        pos.setPercentageUnitDescription("BRL")
        pos.setValueOthersAmount("616157")
        pos.setValueOthersUnitType("PORCENTAGEM")
        pos.setValueOthersUnitTypeOthers("Horas")
        pos.setValueOthersUnitCode("R\$")
        pos.setValueOthersUnitDescription("BRL")
        pos
    }

    static aFinancialRiskPolicyCoinsurer() {
        def coinsurer = new CoinsurerEntity()
        coinsurer.setIdentification("string")
        coinsurer.setCededPercentage("10.00")
        coinsurer
    }

    static aFinancialRiskPolicyClaim(UUID planId) {
        def claim = new FinancialRiskPolicyClaimEntity()
        claim.setFinancialRiskPolicyId(planId)
        claim.setIdentification("string")
        claim.setDocumentationDeliveryDate(LocalDate.of(2023, 01, 30))
        claim.setStatus("ABERTO")
        claim.setStatusAlterationDate(LocalDate.of(2023, 01, 30))
        claim.setOccurrenceDate(LocalDate.of(2023, 01, 30))
        claim.setWarningDate(LocalDate.of(2023, 01, 30))
        claim.setThirdPartyClaimDate(LocalDate.of(2023, 01, 30))
        claim.setAmount("28494")
        claim.setUnitType("PORCENTAGEM")
        claim.setUnitTypeOthers("Horas")
        claim.setUnitCode("R\$")
        claim.setUnitDescription("BRL")
        claim.setDenialJustification("RISCO_EXCLUIDO")
        claim.setDenialJustificationDescription("string")
        claim
    }

    static aFinancialRiskPolicyClaimCoverage(UUID claimId) {
        def coverage = new FinancialRiskPolicyClaimCoverageEntity()
        coverage.setFinancialRiskPolicyClaimId(claimId)
        coverage.setInsuredObjectId("string")
        coverage.setBranch("0111")
        coverage.setCode("PROTECAO_DE_BENS")
        coverage.setDescription("string")
        coverage.setWarningDate(LocalDate.of(2023, 01, 30))
        coverage.setThirdPartyClaimDate(LocalDate.of(2023, 01, 30))
        coverage
    }

    static aFinancialRiskPolicyPremium(UUID planId, List<UUID> paymentIds) {
        def premium = new FinancialRiskPolicyPremiumEntity()
        premium.setFinancialRiskPolicyId(planId)
        premium.setAmount("46")
        premium.setUnitType("PORCENTAGEM")
        premium.setUnitTypeOthers("Horas")
        premium.setUnitCode("R\$")
        premium.setUnitDescription("BRL")
        premium.setPaymentIds(paymentIds)
        premium
    }

    static aFinancialRiskPolicyPremiumCoverage(UUID premiumId) {
        def coverage = new FinancialRiskPolicyPremiumCoverageEntity()
        coverage.setFinancialRiskPolicyPremiumId(premiumId)
        coverage.setBranch("0111")
        coverage.setCode("PROTECAO_DE_BENS")
        coverage.setDescription("string")
        coverage.setPremiumAmount("100.00")
        coverage.setPremiumUnitType("PORCENTAGEM")
        coverage.setPremiumUnitTypeOthers("Horas")
        coverage.setPremiumUnitCode("R\$")
        coverage.setPremiumUnitDescription("BRL")
        coverage
    }

    static aFinancialRiskPolicyPremiumPayment() {
        def payment = new PaymentEntity()
        payment.setMovementDate(LocalDate.of(2023, 01, 30))
        payment.setMovementType("LIQUIDACAO_DE_PREMIO")
        payment.setMovementOrigin("EMISSAO_DIRETA")
        payment.setMovementPaymentsNumber("str")
        payment.setAmount("55595")
        payment.setUnitType("PORCENTAGEM")
        payment.setUnitTypeOthers("Horas")
        payment.setUnitCode("R\$")
        payment.setUnitDescription("BRL")
        payment.setMaturityDate(LocalDate.of(2023, 01, 30))
        payment.setTellerId("string")
        payment.setTellerIdType("CPF")
        payment.setTellerIdTypeOthers("RNE")
        payment.setTellerName("string")
        payment.setFinancialInstitutionCode("string")
        payment.setPaymentType("BOLETO")
        payment
    }

    static aHousingPolicy(UUID accountHolderId) {
        def contract = new HousingPolicyEntity()
        contract.setAccountHolderId(accountHolderId)
        contract.setHousingId("random_housing")
        contract
    }

    static aHousingPolicyClaim(UUID planId) {
        def claim = new HousingPolicyClaimEntity()
        claim.setHousingPolicyId(planId)
        claim
    }

    static aHousingPolicyPremium(UUID planId) {
        def premium = new HousingPolicyPremiumEntity()
        premium.setHousingPolicyId(planId)
        premium
    }

    static aResponsibilityPolicy(UUID accountHolderId) {
        def contract = new ResponsibilityPolicyEntity()
        contract.setAccountHolderId(accountHolderId)
        contract.setResponsibilityId("random_responsibility")
        contract
    }

    static aResponsibilityPolicyClaim(UUID planId) {
        def claim = new ResponsibilityPolicyClaimEntity()
        claim.setResponsibilityPolicyId(planId)
        claim
    }

    static aResponsibilityPolicyPremium(UUID planId) {
        def premium = new ResponsibilityPolicyPremiumEntity()
        premium.setResponsibilityPolicyId(planId)
        premium
    }

    static aPersonPolicy(UUID accountHolderId) {
        def contract = new PersonPolicyEntity()
        contract.setAccountHolderId(accountHolderId)
        contract.setPersonId("random_person")
        contract
    }

    static aPersonPolicyClaim(UUID planId) {
        def claim = new PersonPolicyClaimEntity()
        claim.setPersonPolicyId(planId)
        claim
    }

    static aPersonPolicyPremium(UUID planId) {
        def premium = new PersonPolicyPremiumEntity()
        premium.setPersonPolicyId(planId)
        premium
    }

    static aLifePensionContract(UUID accountHolderId) {
        def contract = new LifePensionContractEntity()
        contract.setAccountHolderId(accountHolderId)
        contract.setLifePensionId("random_life_pension")
        contract
    }

    static aLifePensionContractClaim(UUID planId) {
        def claim = new LifePensionContractClaimEntity()
        claim.setLifePensionContractId(planId)
        claim
    }

    static aLifePensionContractWithdrawal(UUID planId) {
        def withdrawal = new LifePensionContractWithdrawalEntity()
        withdrawal.setLifePensionContractId(planId)
        withdrawal
    }

    static aLifePensionContractPortability(UUID planId) {
        def portability = new LifePensionContractPortabilityInfoEntity()
        portability.setLifePensionContractId(planId)
        portability
    }

    static aLifePensionContractMovementContribution(UUID planId) {
        def contribution = new LifePensionContractMovementContributionEntity()
        contribution.setLifePensionContractId(planId)
        contribution
    }

    static aLifePensionContractMovementBenefit(UUID planId) {
        def benefit = new LifePensionContractMovementBenefitEntity()
        benefit.setLifePensionContractId(planId)
        benefit
    }

    static aPensionPlanContract(String planId, UUID accountHolderId) {
        def contract = new PensionPlanContractEntity()
        contract.setAccountHolderId(accountHolderId)
        contract.setPensionPlanContractId(planId)
        contract
    }

    static aPensionPlanContractClaim(String planId) {
        def claim = new PensionPlanContractClaimEntity()
        claim.setPensionPlanContractId(planId)
        claim
    }

    static aPensionPlanContractWithdrawal(String planId) {
        def withdrawal = new PensionPlanContractWithdrawalEntity()
        withdrawal.setPensionPlanContractId(planId)
        withdrawal
    }

    static aPensionPlanContractPortability(String planId) {
        def portability = new PensionPlanContractPortabilityInfoEntity()
        portability.setPensionPlanContractId(planId)
        portability
    }

    static aPensionPlanContractMovementContribution(String planId) {
        def contribution = new PensionPlanContractMovementContributionEntity()
        contribution.setPensionPlanContractId(planId)
        contribution
    }

    static aPensionPlanContractMovementBenefit(String planId) {
        def benefit = new PensionPlanContractMovementBenefitEntity()
        benefit.setPensionPlanContractId(planId)
        benefit
    }

    static aFinancialAssistanceContract(UUID accountHolderId) {
        def contract = new FinancialAssistanceContractEntity()
        contract.setAccountHolderId(accountHolderId)
        contract.setFinancialAssistanceContractId("random_life_pension")
        contract.setCertificateId("42")
        contract.setGroupContractId("42")
        contract.setSusepProcessNumber("12345")
        contract.setConceivedCreditValueAmount("100.00")
        contract.setConceivedCreditValueUnitType("PORCENTAGEM")
        contract.setConceivedCreditValueUnitTypeOthers("Horas")
        contract.setConceivedCreditValueUnitCode("R\$")
        contract.setConceivedCreditValueUnitDescription("BRL")
        contract.setConceivedCreditValueCurrency("BRL")
        contract.setCreditedLiquidValueAmount("100.00")
        contract.setCreditedLiquidValueUnitType("PORCENTAGEM")
        contract.setCreditedLiquidValueUnitTypeOthers("Horas")
        contract.setCreditedLiquidValueUnitCode("R\$")
        contract.setCreditedLiquidValueUnitDescription("BRL")
        contract.setCreditedLiquidValueCurrency("BRL")
        contract.setCounterInstallmentAmount("0.84")
        contract.setCounterInstallmentUnitType("PORCENTAGEM")
        contract.setCounterInstallmentUnitTypeOthers("Horas")
        contract.setCounterInstallmentUnitCode("R\$")
        contract.setCounterInstallmentUnitDescription("BRL")
        contract.setCounterInstallmentCurrency("BRL")
        contract.setCounterInstallmentPeriodicity("MENSAL")
        contract.setCounterInstallmentQuantity(4)
        contract.setCounterInstallmentFirstDate(LocalDate.of(2021, 05, 21))
        contract.setCounterInstallmentLastDate(LocalDate.of(2021, 05, 21))
        contract.setInterestRateAmount("88.70")
        contract.setInterestRateUnitType("PORCENTAGEM")
        contract.setInterestRateUnitTypeOthers("Horas")
        contract.setInterestRateUnitCode("R\$")
        contract.setInterestRateUnitDescription("BRL")
        contract.setInterestRateCurrency("BRL")
        contract.setEffectiveCostRateAmount("255579661.04")
        contract.setEffectiveCostRateUnitType("PORCENTAGEM")
        contract.setEffectiveCostRateUnitTypeOthers("Horas")
        contract.setEffectiveCostRateUnitCode("R\$")
        contract.setEffectiveCostRateUnitDescription("BRL")
        contract.setEffectiveCostRateCurrency("BRL")
        contract.setAmortizationPeriod(4)
        contract.setAcquittanceValueAmount("736280.46")
        contract.setAcquittanceValueUnitType("PORCENTAGEM")
        contract.setAcquittanceValueUnitTypeOthers("Horas")
        contract.setAcquittanceValueUnitCode("R\$")
        contract.setAcquittanceValueUnitDescription("BRL")
        contract.setAcquittanceValueCurrency("BRL")
        contract.setAcquittanceDate(LocalDate.of(2021, 05, 21))
        contract.setTaxesValueAmount("1856029.36")
        contract.setTaxesValueUnitType("PORCENTAGEM")
        contract.setTaxesValueUnitTypeOthers("Horas")
        contract.setTaxesValueUnitCode("R\$")
        contract.setTaxesValueUnitDescription("BRL")
        contract.setTaxesValueCurrency("BRL")
        contract.setExpensesValueAmount("016")
        contract.setExpensesValueUnitType("PORCENTAGEM")
        contract.setExpensesValueUnitTypeOthers("Horas")
        contract.setExpensesValueUnitCode("R\$")
        contract.setExpensesValueUnitDescription("BRL")
        contract.setExpensesValueCurrency("BRL")
        contract.setFinesValueAmount("123604.13")
        contract.setFinesValueUnitType("PORCENTAGEM")
        contract.setFinesValueUnitTypeOthers("Horas")
        contract.setFinesValueUnitCode("R\$")
        contract.setFinesValueUnitDescription("BRL")
        contract.setFinesValueCurrency("BRL")
        contract.setMonetaryUpdatesValueAmount("65.90")
        contract.setMonetaryUpdatesValueUnitType("PORCENTAGEM")
        contract.setMonetaryUpdatesValueUnitTypeOthers("Horas")
        contract.setMonetaryUpdatesValueUnitCode("R\$")
        contract.setMonetaryUpdatesValueUnitDescription("BRL")
        contract.setMonetaryUpdatesValueCurrency("BRL")
        contract.setAdministrativeFeesValueAmount("90.02")
        contract.setAdministrativeFeesValueUnitType("PORCENTAGEM")
        contract.setAdministrativeFeesValueUnitTypeOthers("Horas")
        contract.setAdministrativeFeesValueUnitCode("R\$")
        contract.setAdministrativeFeesValueUnitDescription("BRL")
        contract.setAdministrativeFeesValueCurrency("BRL")
        contract.setInterestValueAmount("100.00")
        contract.setInterestValueUnitType("PORCENTAGEM")
        contract.setInterestValueUnitTypeOthers("Horas")
        contract.setInterestValueUnitCode("R\$")
        contract.setInterestValueUnitDescription("BRL")
        contract.setInterestValueCurrency("BRL")
        contract
    }

    static aFinancialAssistanceContractMovement(String contractId) {
        def movement = new FinancialAssistanceContractMovementEntity()
        movement.setFinancialAssistanceContractId(contractId)
        movement.setUpdatedDebitAmount("291218.42")
        movement.setUpdatedDebitUnitType("PORCENTAGEM")
        movement.setUpdatedDebitUnitTypeOthers("Horas")
        movement.setUpdatedDebitUnitCode("R\$")
        movement.setUpdatedDebitUnitDescription("BRL")
        movement.setUpdatedDebitCurrency("BRL")
        movement.setRemainingCounterInstallmentsQuantity(4)
        movement.setRemainingUnpaidCounterInstallmentsQuantity(4)
        movement.setLifePensionPmBacAmount("910677.19")
        movement.setLifePensionPmBacUnitType("PORCENTAGEM")
        movement.setLifePensionPmBacUnitTypeOthers("Horas")
        movement.setLifePensionPmBacUnitCode("R\$")
        movement.setLifePensionPmBacUnitDescription("BRL")
        movement.setLifePensionPmBacCurrency("BRL")
        movement.setPensionPlanPmBacAmount("0546074035413.87")
        movement.setPensionPlanPmBacUnitType("PORCENTAGEM")
        movement.setPensionPlanPmBacUnitTypeOthers("Horas")
        movement.setPensionPlanPmBacUnitCode("R\$")
        movement.setPensionPlanPmBacUnitDescription("BRL")
        movement.setPensionPlanPmBacCurrency("BRL")
        movement
    }

    static aFinancialAssistanceContractInsured(String contractId) {
        def insured = new FinancialAssistanceContractInsuredEntity()
        insured.setFinancialAssistanceContractId(contractId)
        insured.setDocumentNumber("12345678910")
        insured.setDocumentType("CPF")
        insured.setDocumentTypeOthers("string")
        insured.setName("Juan Kaique Cláudio Fernandes")
        insured
    }

    static anAcceptanceAndBranchesAbroadPolicy(UUID accountHolderId) {
        def policy = new AcceptanceAndBranchesAbroadPolicyEntity()
        policy.setAccountHolderId(accountHolderId)
        policy.setInsuranceId("random_acceptance_and_branches_abroad")
        policy
    }

    static anAcceptanceAndBranchesAbroadClaim(UUID policyId, String identification = '123456789') {
        def claim = new AcceptanceAndBranchesAbroadClaimEntity()
        claim.setPolicyId(policyId)
        claim.setIdentification(identification)
        claim
    }

    static aPatrimonialPolicy(UUID accountHolderId, String branch) {
        def policy = new PatrimonialPolicyEntity()
        policy.setAccountHolderId(accountHolderId)
        policy.setInsuranceId("random_patrimonial")
        policy.setBranch(branch)
        policy
    }

    static aPatrimonialClaim(UUID policyId, String identification = '123456789') {
        def claim = new PatrimonialClaimEntity()
        claim.setPolicyId(policyId)
        claim.setIdentification(identification)
        claim
    }

    static aRuralPolicy(UUID accountHolderId) {
        def policy = new RuralPolicyEntity()
        policy.setAccountHolderId(accountHolderId)
        policy.setInsuranceId("random_rural")
        policy
    }

    static aRuralClaim(UUID policyId, String identification = '123456789') {
        def claim = new RuralClaimEntity()
        claim.setPolicyId(policyId)
        claim.setIdentification(identification)
        claim
    }

    static anAutoPolicy(UUID accountHolderId, String policyId = "auto-policy-1") {
        def policy = new AutoPolicyEntity()
        policy.setAutoPolicyId(policyId)
        policy.setAccountHolderId(accountHolderId)
        policy
    }

    static anAutoPolicyClaim(String policyId) {
        def claim = new AutoPolicyClaimEntity()
        claim.setAutoPolicyClaimId(policyId)
        claim
    }

    static aTransportPolicy(UUID accountHolderId, String policyId = "transport-policy-1") {
        def policy = new TransportPolicyEntity()
        policy.setTransportPolicyId(policyId)
        policy.setAccountHolderId(accountHolderId)
        policy
    }

    static aTransportPolicyClaim(String policyId) {
        def claim = new TransportPolicyClaimEntity()
        claim.setTransportPolicyClaimId(policyId)
        claim
    }
}
