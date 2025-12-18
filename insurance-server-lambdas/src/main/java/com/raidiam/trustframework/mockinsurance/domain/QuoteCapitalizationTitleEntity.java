package com.raidiam.trustframework.mockinsurance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raidiam.trustframework.mockinsurance.models.generated.AmountDetails.UnitTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatusCapitalizationTitleQuotes.ModalityEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.QuoteStatusCapitalizationTitleQuotes.PaymentTypeEnum;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteCapitalizationTitleData.StatusEnum;
import com.raidiam.trustframework.mockinsurance.utils.InsuranceLambdaUtils;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "capitalization_title_quotes")
public class QuoteCapitalizationTitleEntity extends QuoteEntity {

    @Column(name = "data")
    @Type(JsonType.class)
    private QuoteData data;

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

        var data = new QuoteData();
        data.setV1(req.getData());
        entity.setData(data);

        return entity;
    }

    public static QuoteCapitalizationTitleEntity fromRequestV2(QuoteRequestCapitalizationTitleV2 req, String clientId) {
        QuoteCapitalizationTitleEntity entity = new QuoteCapitalizationTitleEntity();
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatus.StatusEnum.RCVD.toString());
        entity.setClientId(clientId);
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));
        entity.setCustomer(req.getData().getQuoteCustomer());

        var data = new QuoteData();
        data.setV2(req.getData());
        entity.setData(data);

        return entity;
    }

    public ResponseQuoteCapitalizationTitle toResponse() {
        var quoteData = new ResponseQuoteCapitalizationTitleData();
        quoteData.setStatus(StatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var customer = new QuoteCustomer();
            customer.setIdentification(this.getData().getV1().getQuoteCustomer().getIdentificationData());
            customer.setQualification(this.getData().getV1().getQuoteCustomer().getQualificationData());
            customer.setComplimentaryInfo(this.getData().getV1().getQuoteCustomer().getComplimentaryInformationData());

            var quote = new QuoteStatusCapitalizationTitleQuotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setSusepProcessNumber("9456248756872356");
            quote.setPlanId("id");
            quote.setModality(ModalityEnum.valueOf(this.getData().getV1().getQuoteData().getModality().toString()));
            quote.setPeriod("36");
            quote.setGrouperCode("37846584765");
            quote.setRaffle(List.of());
            quote.setPaymentType(PaymentTypeEnum.valueOf(this.getData().getV1().getQuoteData().getPaymentType().toString()));

            var quoteInfo = new QuoteStatusCapitalizationTitle();
            quoteInfo.setQuoteCustomer(customer);
            quoteInfo.setQuoteData(this.getData().getV1().getQuoteData());
            quoteInfo.setQuoteCustomData(this.getData().getV1().getQuoteCustomData());
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

    public ResponseQuoteCapitalizationTitleV2 toResponseV2() {
        var quoteData = new ResponseQuoteCapitalizationTitleV2Data();
        quoteData.setStatus(ResponseQuoteCapitalizationTitleV2Data.StatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var customer = new QuoteCustomerV2();
            customer.setIdentification(this.getData().getV2().getQuoteCustomer().getIdentificationData());
            customer.setQualification(this.getData().getV2().getQuoteCustomer().getQualificationData());
            customer.setComplimentaryInfo(this.getData().getV2().getQuoteCustomer().getComplimentaryInformationData());

            var quote = new QuoteStatusCapitalizationTitleV2Quotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setSusepProcessNumber("9456248756872356");
            quote.setPlanId("id");
            quote.setModality(QuoteStatusCapitalizationTitleV2Quotes.ModalityEnum.valueOf(this.getData().getV2().getQuoteData().getModality().toString()));
            quote.setPeriod("36");
            quote.setGrouperCode("37846584765");
            quote.setRaffle(List.of());
            quote.setPaymentType(QuoteStatusCapitalizationTitleV2Quotes.PaymentTypeEnum.valueOf(this.getData().getV2().getQuoteData().getPaymentType().toString()));

            var quoteInfo = new QuoteStatusCapitalizationTitleV2();
            quoteInfo.setQuoteCustomer(customer);
            quoteInfo.setQuoteData(this.getData().getV2().getQuoteData());
            quoteInfo.setQuoteCustomData(this.getData().getV2().getQuoteCustomData());
            quoteInfo.setQuotes(List.of(quote));

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason("The quote was rejected");
        }

        var resp = new ResponseQuoteCapitalizationTitleV2();
        resp.setData(quoteData);
        return resp;
    }

    @Data
    public static class QuoteData implements Serializable {
        @JsonProperty("v1")
        private QuoteCapitalizationTitleData v1;
        @JsonProperty("v2")
        private QuoteCapitalizationTitleDataV2 v2;

        @JsonIgnore
        public QuoteDataCapitalizationTitle getQuoteData() {
            if (this.getV1().getQuoteData() != null) {
                return this.getV1().getQuoteData();
            }
            return this.getV2().getQuoteData();
        }
    }
}
