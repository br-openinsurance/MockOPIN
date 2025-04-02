package com.raidiam.trustframework.mockinsurance.domain;

import org.hibernate.envers.Audited;

import com.raidiam.trustframework.mockinsurance.models.generated.QuoteRequestAutoLead;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatus;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteAutoLead;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseRevokeQuotePatch;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseRevokeQuotePatchData;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;


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

    public ResponseQuoteAutoLead toResponse() {
        return new ResponseQuoteAutoLead()
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

}
