package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "quote_capitalization_title_leads")
public class QuoteCapitalizationTitleLeadEntity extends QuoteEntity {

    @Override
    public boolean shouldReject() {
        return false;
    }

    public static QuoteCapitalizationTitleLeadEntity fromRequest(QuoteRequestCapitalizationTitleLead req, String clientId) {
        QuoteCapitalizationTitleLeadEntity entity = new QuoteCapitalizationTitleLeadEntity();
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatus.StatusEnum.RCVD.toString());
        entity.setClientId(clientId);
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));
        entity.setCustomer(req.getData().getQuoteCustomer());

        return entity;
    }

    public static QuoteCapitalizationTitleLeadEntity fromRequestV2(QuoteRequestCapitalizationTitleLeadV2 req, String clientId) {
        QuoteCapitalizationTitleLeadEntity entity = new QuoteCapitalizationTitleLeadEntity();
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatus.StatusEnum.RCVD.toString());
        entity.setClientId(clientId);
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));
        entity.setCustomer(req.getData().getQuoteCustomer());

        return entity;
    }

    public ResponseQuote toResponse() {
        return new ResponseQuote()
                .data(new QuoteStatus()
                        .status(QuoteStatus.StatusEnum.fromValue(this.getStatus()))
                        .statusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()))
                );
    }

    public ResponseRevokePatch toRevokePatchResponse() {
        return new ResponseRevokePatch().data(new ResponseRevokePatchData()
                        .status(ResponseRevokePatchData.StatusEnum.CANC)
        );
    }
}
