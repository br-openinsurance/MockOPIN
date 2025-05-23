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
@Table(name = "quote_life_pension_leads")
public class QuoteLifePensionLeadEntity extends QuoteEntity {

    public static QuoteLifePensionLeadEntity fromRequest(QuoteRequestLifePensionLead req, String clientId) {
        QuoteLifePensionLeadEntity entity = new QuoteLifePensionLeadEntity();
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