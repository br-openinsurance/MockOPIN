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
        plan
    }

    static aCapitalizationTitlePlanEvent(UUID planId, String eventType = 'SORTEIO') {
        def event = new CapitalizationTitlePlanEventEntity()
        event.setCapitalizationTitlePlanId(planId)
        event.setEventType(eventType)
        event
    }

    static aCapitalizationTitlePlanSettlement(UUID planId) {
        def settlement = new CapitalizationTitlePlanSettlementEntity()
        settlement.setCapitalizationTitlePlanId(planId)
        settlement
    }

    static aFinancialRiskPolicy(UUID accountHolderId) {
        def contract = new FinancialRiskPolicyEntity()
        contract.setAccountHolderId(accountHolderId)
        contract.setFinancialRiskId("random_financial_risk")
        contract
    }

    static aFinancialRiskPolicyClaim(UUID planId) {
        def claim = new FinancialRiskPolicyClaimEntity()
        claim.setFinancialRiskPolicyId(planId)
        claim
    }

    static aFinancialRiskPolicyPremium(UUID planId) {
        def premium = new FinancialRiskPolicyPremiumEntity()
        premium.setFinancialRiskPolicyId(planId)
        premium
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
        contract
    }

    static aFinancialAssistanceContractMovement(String contractId) {
        def movement = new FinancialAssistanceContractMovementEntity()
        movement.setFinancialAssistanceContractId(contractId)
        movement
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
