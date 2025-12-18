package com.raidiam.trustframework.mockinsurance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raidiam.trustframework.mockinsurance.models.generated.*;
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
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name = "patrimonial_home_quotes")
public class QuotePatrimonialHomeEntity extends QuoteEntity {

    @Column(name = "data")
    @Type(JsonType.class)
    private QuoteData data;

    public static QuotePatrimonialHomeEntity fromRequest(QuoteRequestPatrimonialHome req, String clientId) {
        var entity = new QuotePatrimonialHomeEntity();

        entity.setClientId(clientId);
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatusEnum.RCVD.toString());
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));
        entity.setCustomer(req.getData().getQuoteCustomer());

        var data = new QuotePatrimonialHomeEntity.QuoteData();
        data.setV1(req.getData());
        entity.setData(data);
        return entity;
    }

    public static QuotePatrimonialHomeEntity fromRequestV2(QuoteRequestPatrimonialHomeV2 req, String clientId) {
        var entity = new QuotePatrimonialHomeEntity();

        entity.setClientId(clientId);
        entity.setConsentId(req.getData().getConsentId());
        entity.setStatus(QuoteStatusEnum.RCVD.toString());
        entity.setExpirationDateTime(InsuranceLambdaUtils.offsetDateToDate(req.getData().getExpirationDateTime()));
        entity.setCustomer(req.getData().getQuoteCustomer());

        var data = new QuotePatrimonialHomeEntity.QuoteData();
        data.setV2(req.getData());
        entity.setData(data);
        return entity;
    }

    public ResponseQuotePatrimonialHome toResponse() {
        var quoteData = new ResponseQuotePatrimonialHomeData();
        quoteData.setStatus(QuoteStatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var customer = new QuoteCustomer();
            customer.setIdentification(this.getData().getV1().getQuoteCustomer().getIdentificationData());
            customer.setQualification(this.getData().getV1().getQuoteCustomer().getQualificationData());
            customer.setComplimentaryInfo(this.getData().getV1().getQuoteCustomer().getComplimentaryInformationData());

            var premium = new QuoteResultPremium();
            premium.setPaymentsQuantity(1);
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

            var quote = new QuotePatrimonialHomeQuotes();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setQuoteDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));
            quote.setCoverages(List.of());
            quote.setPremiumInfo(premium);

            var quoteInfo = new QuotePatrimonialHome();
            quoteInfo.setQuoteCustomer(customer);
            quoteInfo.setQuoteData(this.getData().getV1().getQuoteData());
            quoteInfo.setQuoteCustomData(this.getData().getV1().getQuoteCustomData());
            quoteInfo.setQuotes(List.of(quote));

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason("The quote was rejected");
        }

        var resp = new ResponseQuotePatrimonialHome();
        resp.setData(quoteData);
        return resp;
    }

    public ResponseQuotePatrimonialHomeV2 toResponseV2() {
        var quoteData = new ResponseQuotePatrimonialHomeV2Data();
        quoteData.setStatus(QuoteStatusEnum.fromValue(this.getStatus()));
        quoteData.setStatusUpdateDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));

        if (QuoteStatusEnum.ACPT.toString().equals(this.getStatus())) {
            var customer = new QuoteCustomerV2();
            customer.setIdentification(this.getData().getV2().getQuoteCustomer().getIdentificationData());
            customer.setQualification(this.getData().getV2().getQuoteCustomer().getQualificationData());
            customer.setComplimentaryInfo(this.getData().getV2().getQuoteCustomer().getComplimentaryInformationData());

            var premium = new QuoteResultPremium();
            premium.setPaymentsQuantity(1);
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

            var quote = new QuotePatrimonialItem();
            quote.setInsurerQuoteId(this.getQuoteId().toString());
            quote.setQuoteDateTime(InsuranceLambdaUtils.dateToOffsetDate(this.getUpdatedAt()));
            quote.setCoverages(List.of());
            quote.setPremiumInfo(premium);

            var quoteInfo = new QuotePatrimonialHomeV2();
            quoteInfo.setQuoteCustomer(customer);
            quoteInfo.setQuoteData(this.getData().getV2().getQuoteData());
            quoteInfo.setQuoteCustomData(this.getData().getV2().getQuoteCustomData());
            quoteInfo.setQuotes(List.of(quote));

            quoteData.setQuoteInfo(quoteInfo);
        }

        if (QuoteStatusEnum.RJCT.toString().equals(this.getStatus())) {
            quoteData.setRejectionReason("The quote was rejected");
        }

        var resp = new ResponseQuotePatrimonialHomeV2();
        resp.setData(quoteData);
        return resp;
    }

    @Override
    public boolean shouldReject() {
        if (this.getData().getV1() != null) {
            return Optional.ofNullable(this.getData().getV1())
                    .map(QuotePatrimonialHomeData::getQuoteData)
                    .map(QuoteDataPatrimonialHome::getPolicyId).isEmpty();
        }
        return Optional.ofNullable(this.getData().getV2())
                .map(QuotePatrimonialHomeDataV2::getQuoteData)
                .map(QuoteDataPatrimonialHome::getPolicyId).isEmpty();
    }

    @Data
    public static class QuoteData implements Serializable {
        @JsonProperty("v1")
        private QuotePatrimonialHomeData v1;
        @JsonProperty("v2")
        private QuotePatrimonialHomeDataV2 v2;

        @JsonIgnore
        public AmountDetails getMaxLMG() {
            if (this.getV1() != null) {
                return this.getV1().getQuoteData().getMaxLMG();
            }
            return this.getV2().getQuoteData().getMaxLMG();
        }
    }
}
