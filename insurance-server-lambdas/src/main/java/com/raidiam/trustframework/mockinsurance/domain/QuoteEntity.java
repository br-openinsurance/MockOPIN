package com.raidiam.trustframework.mockinsurance.domain;


import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Audited
@MappedSuperclass
public abstract class QuoteEntity extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "quote_id", unique = true, nullable = false, updatable = false, insertable = false)
    private UUID quoteId;

    @Column(name = "consent_id", nullable = false)
    private String consentId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "person_cpf")
    private String personCpf;

    @Column(name = "business_cnpj")
    private String businessCnpj;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @EqualsAndHashCode.Exclude
    @Column(name = "expiration_date_time")
    private Date expirationDateTime;

    public abstract boolean shouldReject();

    public void setCustomer(QuoteCustomerData req) {
        this.setPersonCpf(req.getIdentificationData().getCpfNumber());
        if (req.getIdentificationData().getDocument() != null) {
            this.setBusinessCnpj(req.getIdentificationData().getDocument().getBusinesscnpjNumber());
        }
    }

    public ResponseQuotePatch toPatchResponse(String redirectLink) {
        var patchData = new ResponseQuotePatchData();
        patchData.setStatus(ResponseQuotePatchData.StatusEnum.fromValue(this.getStatus()));

        if (QuoteStatusEnum.ACKN.toString().equals(this.getStatus())) {
            patchData.setInsurerQuoteId(this.getQuoteId().toString());
            patchData.setProtocolNumber("12345678");
            patchData.setProtocolDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));
            patchData.setLinks(new ResponseQuotePatchDataLinks().redirect(redirectLink));
        }

        var resp = new ResponseQuotePatch();
        resp.setData(patchData);
        return resp;
    }
}
