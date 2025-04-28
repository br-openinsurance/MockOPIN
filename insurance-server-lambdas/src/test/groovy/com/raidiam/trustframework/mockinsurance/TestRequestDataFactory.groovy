package com.raidiam.trustframework.mockinsurance

import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils

import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.LocalDate;

class TestRequestDataFactory {

    static createConsentRequest(
            String loggedUserIdentification,
            String loggedUserRel,
            OffsetDateTime expirationDateTime,
            List<EnumConsentPermission> permissions
    ) {
        CreateConsentData consentRequestData = new CreateConsentData()
                .loggedUser(new LoggedUser().document(new LoggedUserDocument().rel(loggedUserRel).identification(loggedUserIdentification)))
                .permissions(permissions)
                .expirationDateTime(expirationDateTime)

        return new CreateConsent().data(consentRequestData)
    }

    static createConsentRequest(
            String businessIdentityDocumentIdentification,
            String businessIdentityDocumentREL,
            String loggedUserIdentification,
            String loggedUserRel,
            OffsetDateTime expirationDateTime,
            List<EnumConsentPermission> permissions
    ) {
        CreateConsentData consentRequestData = new CreateConsentData()
                .businessEntity(
                        new BusinessEntity().document(
                                new BusinessEntityDocument().identification(businessIdentityDocumentIdentification)
                                        .rel(businessIdentityDocumentREL)))
                .loggedUser(new LoggedUser().document(new LoggedUserDocument().rel(loggedUserRel).identification(loggedUserIdentification)))
                .permissions(permissions)
                .expirationDateTime(expirationDateTime)

        return new CreateConsent().data(consentRequestData)
    }

    static createEndorsementRequest(){
        CreateEndorsementData endorsementData = new CreateEndorsementData()
            .policyId("random_policy_id")
            .insuredObjectId(new ArrayList<String>(List.of("123456")))
            .proposalId("random_proposal_id")
            .endorsementType(CreateEndorsementData.EndorsementTypeEnum.ALTERACAO)
            .requestDescription("description")
            .requestDate(LocalDate.now().minusMonths(1))
            .customData(new CreateEndorsementDataCustomData())
        return new CreateEndorsement().data(endorsementData)
    }
      
    static CreateClaimNotificationDamage createClaimNotificationDamageRequest() {
        return new CreateClaimNotificationDamage()
                .data(new ClaimNotificationDamageData()
                        .documentType(ClaimNotificationDamageData.DocumentTypeEnum.CERTIFICADO)
                        .policyId("111111")
                        .groupCertificateId("string")
                        .insuredObjectId(List.of("216731531723"))
                        .occurrenceTime("2022-01-01")
                        .occurrenceDescription("string")
                )
    }

    static CreateClaimNotificationPerson createClaimNotificationPersonRequest() {
        return new CreateClaimNotificationPerson()
                .data(new ClaimNotificationPersonData()
                        .documentType(ClaimNotificationPersonData.DocumentTypeEnum.CERTIFICADO)
                        .policyId("111111")
                        .groupCertificateId("string")
                        .insuredObjectId(List.of("216731531723"))
                        .occurrenceTime("2022-01-01")
                        .occurrenceDescription("string")
                )
    }

    static QuoteRequestPatrimonialLead createQuotePatrimonialLeadRequest() {
        return new QuoteRequestPatrimonialLead()
                .data(new QuoteRequestPatrimonialLeadData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteRequestPatrimonialLeadDataQuoteData())
                )
    }

    static QuoteRequestPatrimonialBusiness createQuotePatrimonialBusinessRequest() {
        QuoteRequestPatrimonialBusiness req = new QuoteRequestPatrimonialBusiness()
                .data(new QuotePatrimonialBusinessData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteDataPatrimonialBusiness()))
        req
    }

    static QuoteRequestPatrimonialHome createQuotePatrimonialHomeRequest() {
        QuoteRequestPatrimonialHome req = new QuoteRequestPatrimonialHome()
                .data(new QuotePatrimonialHomeData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteDataPatrimonialHome()))
        req
    }

    static QuoteRequestPatrimonialCondominium createQuotePatrimonialCondominiumRequest() {
        QuoteRequestPatrimonialCondominium req = new QuoteRequestPatrimonialCondominium()
                .data(new QuotePatrimonialCondominiumData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteDataPatrimonialCondominium()))
        req
    }

    static QuoteRequestPatrimonialDiverseRisks createQuotePatrimonialDiverseRisksRequest() {
        QuoteRequestPatrimonialDiverseRisks req = new QuoteRequestPatrimonialDiverseRisks()
                .data(new QuotePatrimonialDiverseRisksData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteDataPatrimonialDiverseRisks()))
        req
    }

    static QuoteRequestFinancialRiskLead createQuoteFinancialRiskLeadRequest() {
        return new QuoteRequestFinancialRiskLead()
                .data(new QuoteRequestFinancialRiskLeadData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteRequestFinancialRiskLeadDataQuoteData())
                )
    }

    static QuoteRequestHousingLead createQuoteHousingLeadRequest() {
        return new QuoteRequestHousingLead()
                .data(new QuoteRequestHousingLeadData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteRequestHousingLeadDataQuoteData())
                )
    }

    static QuoteRequestLifePensionLead createQuoteLifePensionLeadRequest() {
        return new QuoteRequestLifePensionLead()
                .data(new QuoteRequestLifePensionLeadData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteDataLifePension())
                )
    }

    static RequestContractLifePension createQuoteLifePensionRequest() {
        RequestContractLifePension req = new RequestContractLifePension()
                .data(new RequestContractLifePensionData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteDataLifePension()))
        req
    }

    static QuoteRequestPersonLead createQuotePersonLeadRequest() {
        return new QuoteRequestPersonLead()
                .data(new QuoteRequestPersonLeadData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteDataPersonLead())
                )
    }

    static QuoteRequestPersonLife createQuotePersonLifeRequest() {
        QuoteRequestPersonLife req = new QuoteRequestPersonLife()
                .data(new QuoteRequestPersonLifeData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteDataPersonLife()))
        req
    }

    static QuoteRequestPersonTravel createQuotePersonTravelRequest() {
        QuoteRequestPersonTravel req = new QuoteRequestPersonTravel()
                .data(new QuoteRequestPersonTravelData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteDataPersonTravel()))
        req
    }

    static QuoteRequestResponsibilityLead createQuoteResponsibilityLeadRequest() {
        return new QuoteRequestResponsibilityLead()
                .data(new QuoteRequestResponsibilityLeadData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteRequestResponsibilityLeadDataQuoteData())
                )
    }

    static PatchQuotePayload patchQuoteRequest(UUID quoteId) {
        PatchQuotePayload req = new PatchQuotePayload()
                .data(new PatchQuotePayloadData()
                        .status(PatchQuotePayloadData.StatusEnum.ACKN)
                        .insurerQuoteId(quoteId.toString())
                        .author(new RevokeQuotePatchPayloadDataAuthor()
                                .identificationType(RevokeQuotePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber("123456789")))
        req
    }

    static RevokeQuotePatchPayload revokeQuotePatchRequest(UUID quoteId) {
        RevokeQuotePatchPayload req = new RevokeQuotePatchPayload()
                .data(new RevokeQuotePatchPayloadData()
                        .author(new RevokeQuotePatchPayloadDataAuthor()
                                .identificationType(RevokeQuotePatchPayloadDataAuthor.IdentificationTypeEnum.CPF)
                                .identificationNumber("123456789"))
                )
        req
    }

    static QuoteRequestAcceptanceAndBranchesAbroadLead createQuoteAcceptanceAndBranchesAbroadLeadRequest() {
        return new QuoteRequestAcceptanceAndBranchesAbroadLead()
                .data(new QuoteAcceptanceAndBranchesAbroadLeadData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                )
    }

    static QuoteRequestTransportLead createQuoteTransportLeadRequest() {
        return new QuoteRequestTransportLead()
                .data(new QuoteRequestTransportLeadData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                )
    }

    static QuoteRequestRuralLead createQuoteRuralLeadRequest() {
        return new QuoteRequestRuralLead()
                .data(new QuoteRuralLeadData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                )
    }

    static QuoteRequestAutoLead createQuoteAutoLeadRequest() {
        return new QuoteRequestAutoLead()
                .data(new QuoteRequestAutoLeadData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")
                                )
                        )
                )
    }

    static QuoteRequestAuto createQuoteAutoRequest() {
        QuoteRequestAuto req = new QuoteRequestAuto()
                .data(new QuoteAutoData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteDataAuto()))
        req
    }

    static QuoteRequestCapitalizationTitleLead createQuoteCapitalizationTitleLeadRequest() {
        return new QuoteRequestCapitalizationTitleLead()
                .data(new QuoteRequestCapitalizationTitleLeadData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")
                                )
                        )
                )
    }

    static QuoteRequestCapitalizationTitle createQuoteCapitalizationTitleRequest() {
        QuoteRequestCapitalizationTitle req = new QuoteRequestCapitalizationTitle()
                .data(new QuoteCapitalizationTitleData()
                        .consentId(UUID.randomUUID().toString())
                        .expirationDateTime(InsuranceLambdaUtils.getOffsetDateTimeUTC())
                        .quoteCustomer(new QuoteCustomerData()
                                .identificationData((QuoteCustomerIdentificationData) new QuoteCustomerIdentificationData()
                                        .cpfNumber("123456789")))
                        .quoteData(new QuoteDataCapitalizationTitle()))
        req
    }

    static RequestCapitalizationTitleRaffle createCapitalizationTitleRaffleRequest() {
        RequestCapitalizationTitleRaffle req = new RequestCapitalizationTitleRaffle()
                .data(new RequestCapitalizationTitleRaffleData()
                        .modality(RequestCapitalizationTitleRaffleData.ModalityEnum.TRADICIONAL)
                        .susepProcessNumber("random_process_number")
                        .raffleCustomData(new RaffleCustomData())
                )
        req
    }
}
