package com.raidiam.trustframework.mockinsurance.domain;

import java.util.List;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import com.raidiam.trustframework.mockinsurance.models.generated.QuoteCapitalizationTitleData;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteCustomer;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteDataCapitalizationTitle;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteRequestCapitalizationTitle;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatus;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatusCapitalizationTitle;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatusCapitalizationTitleQuotes;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatusEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteCapitalizationTitle;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteCapitalizationTitleData;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails.UnitTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatusCapitalizationTitleQuotes.PaymentTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatusCapitalizationTitleQuotes.ModalityEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteCapitalizationTitleData.StatusEnum;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "capitalization_title_quotes")
public class QuoteCapitalizationTitleEntity extends QuoteEntity {

    @Column(name = "data")
    @Type(JsonType.class)
    private QuoteCapitalizationTitleData data;

    @Override
    public boolean shouldReject() {
        var quoteData = this.getData().getQuoteData();
        return quoteData.getPaymentType().equals(QuoteDataCapitalizationTitle.PaymentTypeEnum.UNICO) &&
            quoteData.getSinglePayment().getAmount().equals("1000.00") &&
            quoteData.getSinglePayment().getUnitType().equals(UnitTypeEnum.PORCENTAGEM);
    }

    public static QuoteCapitalizationTitleEntity fromRequest(QuoteRequestCapitalizationTitle req, String clientId) {
        QuoteCapitalizationTitleEntity entity = new QuoteCapitalizationTitleEntity();
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatus.StatusEnum.RCVD.toString());
        entity.setClientId(clientId);
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));
        entity.setCustomer(req.getData().getQuoteCustomer());

        entity.setData(req.getData());

        return entity;
    }

    public ResponseQuoteCapitalizationTitle toResponse() {
        var quoteData = new ResponseQuoteCapitalizationTitleData();
        quoteData.setStatus(StatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var customer = new QuoteCustomer();
            customer.setIdentification(this.getData().getQuoteCustomer().getIdentificationData());
            customer.setQualification(this.getData().getQuoteCustomer().getQualificationData());
            customer.setComplimentaryInfo(this.getData().getQuoteCustomer().getComplimentaryInformationData());

            var quote = new QuoteStatusCapitalizationTitleQuotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setSusepProcessNumber("9456248756872356");
            quote.setPlanId("id");
            quote.setModality(ModalityEnum.valueOf(this.getData().getQuoteData().getModality().toString()));
            quote.setPeriod("36");
            quote.setGrouperCode("37846584765");
            quote.setRaffle(List.of());
            quote.setPaymentType(PaymentTypeEnum.valueOf(this.getData().getQuoteData().getPaymentType().toString()));

            var quoteInfo = new QuoteStatusCapitalizationTitle();
            quoteInfo.setQuoteCustomer(customer);
            quoteInfo.setQuoteData(this.getData().getQuoteData());
            quoteInfo.setQuoteCustomData(this.getData().getQuoteCustomData());
            quoteInfo.setQuotes(List.of(quote));

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason("The quote was rejected");
        }

        var resp = new ResponseQuoteCapitalizationTitle();
        resp.setData(quoteData);
        return resp;
    }
}
