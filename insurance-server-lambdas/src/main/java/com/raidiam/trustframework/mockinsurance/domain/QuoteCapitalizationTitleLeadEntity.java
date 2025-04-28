package com.raidiam.trustframework.mockinsurance.domain;

import org.hibernate.envers.Audited;

import com.raidiam.trustframework.mockinsurance.models.generated.QuoteRequestCapitalizationTitleLead;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatus;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteLead;
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
}
