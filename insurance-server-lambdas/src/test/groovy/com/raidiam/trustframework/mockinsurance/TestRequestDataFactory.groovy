package com.raidiam.trustframework.mockinsurance

import com.raidiam.trustframework.mockinsurance.models.generated.*
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils

import java.time.OffsetDateTime

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

}
