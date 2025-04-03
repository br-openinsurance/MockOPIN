package com.raidiam.trustframework.mockinsurance.domain;

import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "quote_patrimonial_leads")
public class QuotePatrimonialLeadEntity extends QuoteEntity {

    public static QuotePatrimonialLeadEntity fromRequest(QuoteRequestPatrimonialLead req, String clientId) {
        QuotePatrimonialLeadEntity entity = new QuotePatrimonialLeadEntity();
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatus.StatusEnum.RCVD.toString());
        entity.setClientId(clientId);
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));
        entity.setCustomer(req.getData().getQuoteCustomer());

        return entity;
    }

    public ResponseQuoteLead toResponse() {
        return new ResponseQuoteLead()
                .data(new QuoteStatus()
                        .status(QuoteStatus.StatusEnum.fromValue(this.getStatus()))
                        .statusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()))
                );
    }

    public ResponseRevokeQuotePatch toRevokePatchResponse() {
        return new ResponseRevokeQuotePatch().data(new ResponseRevokeQuotePatchData()
                        .status(ResponseRevokeQuotePatchData.StatusEnum.CANC)
        );
    }

    @Override
    public boolean shouldReject() {
        return false;
    }
}
