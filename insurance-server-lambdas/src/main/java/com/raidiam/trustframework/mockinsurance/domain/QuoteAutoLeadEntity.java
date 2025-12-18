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
@Table(name = "quote_auto_leads")
public class QuoteAutoLeadEntity extends QuoteEntity {

    @Override
    public boolean shouldReject() {
        return false;
    }

    public static QuoteAutoLeadEntity fromRequest(QuoteRequestAutoLead req, String clientId) {
        QuoteAutoLeadEntity entity = new QuoteAutoLeadEntity();
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatus.StatusEnum.RCVD.toString());
        entity.setClientId(clientId);
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));
        entity.setCustomer(req.getData().getQuoteCustomer());

        return entity;
    }

    public static QuoteAutoLeadEntity fromRequestV2(QuoteRequestAutoLeadV2 req, String clientId) {
        QuoteAutoLeadEntity entity = new QuoteAutoLeadEntity();
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
