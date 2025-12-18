package com.raidiam.trustframework.mockinsurance.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
import com.raidiam.trustframework.mockinsurance.models.generated.ResponseQuoteAutoData.StatusEnum;
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
@Table(name = "auto_quotes")
public class QuoteAutoEntity extends QuoteEntity {

    @Column(name = "data")
    @Type(JsonType.class)
    private QuoteData data;

    @Override
    public boolean shouldReject() {
        if (this.getData().getV1() != null) {
            return this.getData().getV1().getQuoteData().getTermStartDate().isAfter(this.getData().getV1().getQuoteData().getTermEndDate());
        }
        return this.getData().getV2().getQuoteData().getTermStartDate().isAfter(this.getData().getV2().getQuoteData().getTermEndDate());
    }

    public static QuoteAutoEntity fromRequest(QuoteRequestAuto req, String clientId) {
        QuoteAutoEntity entity = new QuoteAutoEntity();
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

    public static QuoteAutoEntity fromRequestV2(QuoteRequestAutoV2 req, String clientId) {
        QuoteAutoEntity entity = new QuoteAutoEntity();
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

    public ResponseQuoteAuto toResponse() {
        var quoteData = new ResponseQuoteAutoData();
        quoteData.setStatus(StatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var customer = new QuoteCustomer();
            customer.setIdentification(this.getData().getV1().getQuoteCustomer().getIdentificationData());
            customer.setQualification(this.getData().getV1().getQuoteCustomer().getQualificationData());
            customer.setComplimentaryInfo(this.getData().getV1().getQuoteCustomer().getComplimentaryInformationData());

            var premium = new QuoteAutoResultPremium();
            premium.setPaymentsQuantity("1");
            premium.setTotalPremiumAmount(new AmountDetails()
                    .amount("100.00")
                    .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)));
            premium.setTotalNetAmount(new AmountDetails()
                    .amount("50.00")
                    .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)));
            premium.setIOF(new AmountDetails()
                    .amount("20.00")
                    .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)));
            premium.setCoverages(List.of());
            premium.setPayments(List.of(new QuoteResultPayment()
                    .amount(new AmountDetails()
                            .amount("100.00")
                            .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                            .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)))
                    .paymentType(QuoteResultPayment.PaymentTypeEnum.PIX)));

            var quote = new QuoteStatusAutoQuotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setSusepProcessNumbers(List.of());
            quote.setAssistances(List.of());
            quote.setCoverages(List.of());
            quote.setPremiumInfo(premium);

            var quoteInfo = new QuoteStatusAuto();
            quoteInfo.setQuoteCustomer(customer);
            quoteInfo.setQuoteData(this.getData().getV1().getQuoteData());
            quoteInfo.setQuoteCustomData(this.getData().getV1().getQuoteCustomData());
            quoteInfo.setQuotes(List.of(quote));

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason("The quote was rejected");
        }

        var resp = new ResponseQuoteAuto();
        resp.setData(quoteData);
        return resp;
    }

    public ResponseQuoteAutoV2 toResponseV2() {
        var quoteData = new ResponseQuoteAutoV2Data();
        quoteData.setStatus(ResponseQuoteAutoV2Data.StatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var customer = new QuoteCustomerV2();
            customer.setIdentification(this.getData().getV2().getQuoteCustomer().getIdentificationData());
            customer.setQualification(this.getData().getV2().getQuoteCustomer().getQualificationData());
            customer.setComplimentaryInfo(this.getData().getV2().getQuoteCustomer().getComplimentaryInformationData());

            var premium = new QuoteAutoResultPremium();
            premium.setPaymentsQuantity("1");
            premium.setTotalPremiumAmount(new AmountDetails()
                    .amount("100.00")
                    .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)));
            premium.setTotalNetAmount(new AmountDetails()
                    .amount("50.00")
                    .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)));
            premium.setIOF(new AmountDetails()
                    .amount("20.00")
                    .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                    .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)));
            premium.setCoverages(List.of());
            premium.setPayments(List.of(new QuoteResultPayment()
                    .amount(new AmountDetails()
                            .amount("100.00")
                            .unitType(AmountDetails.UnitTypeEnum.MONETARIO)
                            .unit(new AmountDetailsUnit().code("R$").description(AmountDetailsUnit.DescriptionEnum.BRL)))
                    .paymentType(QuoteResultPayment.PaymentTypeEnum.PIX)));

            var quote = new QuoteStatusAutoV2Quotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setSusepProcessNumbers(List.of());
            quote.setAssistances(List.of());
            quote.setCoverages(List.of());
            quote.setPremiumInfo(premium);

            var quoteInfo = new QuoteStatusAutoV2();
            quoteInfo.setQuoteCustomer(customer);
            quoteInfo.setQuoteData(this.getData().getV2().getQuoteData());
            quoteInfo.setQuoteCustomData(this.getData().getV2().getQuoteCustomData());
            quoteInfo.setQuotes(List.of(quote));

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason("The quote was rejected");
        }

        var resp = new ResponseQuoteAutoV2();
        resp.setData(quoteData);
        return resp;
    }

    @Data
    public static class QuoteData implements Serializable {
        @JsonProperty("v1")
        private QuoteAutoData v1;
        @JsonProperty("v2")
        private QuoteAutoDataV2 v2;
    }

}
