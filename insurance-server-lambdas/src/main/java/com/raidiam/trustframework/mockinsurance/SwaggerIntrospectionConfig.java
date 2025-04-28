package com.raidiam.trustframework.mockinsurance;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import io.micronaut.core.annotation.Introspected;

@Introspected(classes = {
        CreateConsent.class,
        UpdateConsent.class,
        ResponseConsent.class,
        QuoteRequestPatrimonialBusiness.class,
        ResponseQuotePatrimonialBusiness.class,
        RequestContractLifePension.class,
        ResponseQuoteLifePension.class,
        QuoteRequestPatrimonialHome.class,
        ResponseQuotePatrimonialHome.class,
        QuoteRequestPatrimonialCondominium.class,
        ResponseQuotePatrimonialCondominium.class,
        QuoteRequestPatrimonialDiverseRisks.class,
        ResponseQuotePatrimonialDiverseRisks.class,
        PatchQuotePayload.class,
        ResponseQuotePatch.class,
        QuoteRequestAutoLead.class,
        QuoteRequestAuto.class,
        ResponseQuoteAuto.class,
        RevokeQuotePatchPayload.class,
        ResponseRevokeQuotePatch.class,
        QuoteRequestCapitalizationTitleLead.class,
        QuoteRequestCapitalizationTitle.class,
        RequestCapitalizationTitleRaffle.class,
        ResponseCapitalizationTitleRaffle.class
})
public class SwaggerIntrospectionConfig {
}
